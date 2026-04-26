package com.casewire.service;

import com.casewire.dao.DBConnection;
import com.casewire.dao.ProgressDAO;
import com.casewire.dao.UserDAO;
import com.casewire.model.User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;

public class AuthService {

    private final UserDAO userDAO = new UserDAO();
    private final ProgressDAO progressDAO = new ProgressDAO();

    public User login(String username, String password) {
        String normalizedUsername = normalizeUsername(username);
        validateRequiredPassword(password);

        User user = userDAO.login(normalizedUsername, hashPassword(password));
        if (user == null) {
            throw new IllegalArgumentException("Invalid username or password.");
        }
        return user;
    }

    public User register(String fullName, String username, String password, String confirmPassword) {
        String normalizedUsername = normalizeUsername(username);
        validateRequiredPassword(password);

        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Password confirmation does not match.");
        }
        if (userDAO.usernameExists(normalizedUsername)) {
            throw new IllegalArgumentException("Username already exists.");
        }

        String normalizedFullName = normalizeOptionalText(fullName);
        String hashedPassword = hashPassword(password);

        try (Connection conn = DBConnection.getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            try {
                conn.setAutoCommit(false);
                User user = userDAO.createUser(normalizedUsername, hashedPassword, normalizedFullName, conn);
                progressDAO.initializeProgressForUser(user.getId(), conn);
                conn.commit();
                return user;
            } catch (SQLException e) {
                rollbackQuietly(conn);
                if (isDuplicateKey(e)) {
                    throw new IllegalArgumentException("Username already exists.");
                }
                throw new IllegalStateException("Unable to register user.", e);
            } finally {
                restoreAutoCommit(conn, originalAutoCommit);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to access the database for registration.", e);
        }
    }

    private String normalizeUsername(String username) {
        String normalized = normalizeOptionalText(username);
        if (normalized == null) {
            throw new IllegalArgumentException("Username cannot be empty.");
        }
        return normalized;
    }

    private void validateRequiredPassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }
    }

    private String normalizeOptionalText(String text) {
        if (text == null) {
            return null;
        }

        String normalized = text.trim();
        return normalized.isEmpty() ? null : normalized;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            for (byte b : hash) {
                builder.append(String.format("%02x", b));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available.", e);
        }
    }

    private void rollbackQuietly(Connection conn) {
        try {
            conn.rollback();
        } catch (SQLException ignored) {
        }
    }

    private void restoreAutoCommit(Connection conn, boolean originalAutoCommit) {
        try {
            conn.setAutoCommit(originalAutoCommit);
        } catch (SQLException ignored) {
        }
    }

    private boolean isDuplicateKey(SQLException e) {
        return "23000".equals(e.getSQLState()) || e.getErrorCode() == 1062;
    }
}
