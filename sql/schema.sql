-- ============================================================
-- CaseWire Database Schema + Seed Data
-- Clean reset script: re-run to recreate the database from scratch
-- ============================================================

DROP DATABASE IF EXISTS casewire;
CREATE DATABASE casewire;
USE casewire;

CREATE TABLE users (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    full_name  VARCHAR(100),
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS levels (
    id   INT PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS cases (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    level_id    INT          NOT NULL,
    title       VARCHAR(100) NOT NULL,
    description TEXT,
    intro       TEXT,
    FOREIGN KEY (level_id) REFERENCES levels(id)
);

CREATE TABLE IF NOT EXISTS suspects (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    case_id     INT          NOT NULL,
    name        VARCHAR(100) NOT NULL,
    role        VARCHAR(100),
    alibi       TEXT,
    description TEXT,
    FOREIGN KEY (case_id) REFERENCES cases(id)
);

CREATE TABLE IF NOT EXISTS evidence (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    case_id     INT          NOT NULL,
    title       VARCHAR(100) NOT NULL,
    type        VARCHAR(50),
    description TEXT,
    location    VARCHAR(100),
    why_matters TEXT,
    FOREIGN KEY (case_id) REFERENCES cases(id)
);

CREATE TABLE IF NOT EXISTS clue_connections (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    case_id         INT         NOT NULL,
    evidence_id_a   INT         NOT NULL,
    evidence_id_b   INT         NOT NULL,
    relation_type   VARCHAR(50) NOT NULL,
    explanation     TEXT,
    FOREIGN KEY (case_id)       REFERENCES cases(id),
    FOREIGN KEY (evidence_id_a) REFERENCES evidence(id),
    FOREIGN KEY (evidence_id_b) REFERENCES evidence(id)
);

CREATE TABLE IF NOT EXISTS solutions (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    case_id             INT          NOT NULL UNIQUE,
    correct_suspect_id  INT          NOT NULL,
    correct_motive      VARCHAR(200) NOT NULL,
    explanation         TEXT,
    FOREIGN KEY (case_id)            REFERENCES cases(id),
    FOREIGN KEY (correct_suspect_id) REFERENCES suspects(id)
);

CREATE TABLE IF NOT EXISTS solution_evidence (
    solution_id INT NOT NULL,
    evidence_id INT NOT NULL,
    PRIMARY KEY (solution_id, evidence_id),
    FOREIGN KEY (solution_id) REFERENCES solutions(id),
    FOREIGN KEY (evidence_id) REFERENCES evidence(id)
);

CREATE TABLE IF NOT EXISTS case_history (
    id                 INT AUTO_INCREMENT PRIMARY KEY,
    user_id            INT NOT NULL,
    case_id            INT NOT NULL,
    score              INT NOT NULL,
    time_taken_seconds INT NOT NULL,
    solved_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (case_id) REFERENCES cases(id)
);

CREATE TABLE IF NOT EXISTS player_progress (
    user_id     INT     NOT NULL,
    case_id     INT     NOT NULL,
    is_unlocked BOOLEAN NOT NULL DEFAULT FALSE,
    is_solved   BOOLEAN NOT NULL DEFAULT FALSE,
    score       INT     NOT NULL DEFAULT 0,
    PRIMARY KEY (user_id, case_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (case_id) REFERENCES cases(id)
);

CREATE TABLE IF NOT EXISTS player_discovered_connections (
    user_id       INT NOT NULL,
    connection_id INT NOT NULL,
    PRIMARY KEY (user_id, connection_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (connection_id) REFERENCES clue_connections(id)
);

INSERT INTO levels (id, name) VALUES
(1, 'Easy'),
(2, 'Medium'),
(3, 'Hard')
ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO cases (level_id, title, description, intro) VALUES
(1,
 'Stolen Phone',
 'A phone was stolen in a classroom.',
 'A student left a phone on the teacher desk after class. A few minutes later, the phone was gone. Three people were near the room. Find who took it.');

SET @case1 = LAST_INSERT_ID();

INSERT INTO suspects (case_id, name, role, alibi, description) VALUES
(@case1, 'Ali Khan', 'Student', 'Says he stayed near the classroom door after class.', 'Ali needed money and stayed close to the desk.'),
(@case1, 'Sara Noor', 'Student', 'Says she went to the library right away. The library log supports this.', 'Sara was in the room earlier but left before the theft.'),
(@case1, 'Mr Omar', 'Teacher', 'Says he was in the staff room. Two teachers confirm it.', 'Mr Omar came back and found the phone missing.');

INSERT INTO evidence (case_id, title, type, description, location, why_matters) VALUES
(@case1, 'Phone Fingerprint', 'Forensic', 'A clear fingerprint was found on the phone case.', 'Teacher desk', 'The print matches Ali.'),
(@case1, 'Hallway CCTV', 'Record', 'The hallway camera shows Ali taking the phone from the desk and putting it in his bag.', 'Hallway camera', 'It shows who took the phone.'),
(@case1, 'Library Log', 'Record', 'The library log shows Sara entered the library at the time of the theft.', 'Library', 'It supports Sara''s alibi.'),
(@case1, 'Debt Message', 'Document', 'A message on Ali''s phone says, "I need money today."', 'Ali''s phone', 'It gives Ali a simple money motive.');

INSERT INTO clue_connections (case_id, evidence_id_a, evidence_id_b, relation_type, explanation)
SELECT @case1, e1.id, e2.id, 'Match', 'The fingerprint and the camera both point to Ali.'
FROM evidence e1, evidence e2
WHERE e1.case_id = @case1 AND e1.title = 'Phone Fingerprint'
  AND e2.case_id = @case1 AND e2.title = 'Hallway CCTV';

INSERT INTO clue_connections (case_id, evidence_id_a, evidence_id_b, relation_type, explanation)
SELECT @case1, e1.id, e2.id, 'Related', 'The camera shows Ali at the desk, and the message shows he wanted money.'
FROM evidence e1, evidence e2
WHERE e1.case_id = @case1 AND e1.title = 'Hallway CCTV'
  AND e2.case_id = @case1 AND e2.title = 'Debt Message';

INSERT INTO solutions (case_id, correct_suspect_id, correct_motive, explanation)
SELECT @case1, s.id,
       'Money - Ali wanted quick money',
       'Ali took the phone. The camera and fingerprint both point to him. His message shows he wanted money.'
FROM suspects s WHERE s.case_id = @case1 AND s.name = 'Ali Khan';

SET @sol1 = LAST_INSERT_ID();

INSERT INTO solution_evidence (solution_id, evidence_id)
SELECT @sol1, e.id FROM evidence e
WHERE e.case_id = @case1 AND e.title IN ('Phone Fingerprint', 'Hallway CCTV', 'Debt Message');

INSERT INTO cases (level_id, title, description, intro) VALUES
(2,
 'Missing Office Cash',
 'Cash went missing from a small office drawer.',
 'At the end of the day, the office cash drawer was empty. Only three people were still near the office. Find who took the money.');

SET @case2 = LAST_INSERT_ID();

INSERT INTO suspects (case_id, name, role, alibi, description) VALUES
(@case2, 'Maya Reed', 'Office clerk', 'Says she only cleaned her desk before leaving.', 'Maya was worried because her rent was due that night.'),
(@case2, 'Ben Cole', 'Cleaner', 'Says he cleaned the hallway, not the office. The camera supports this.', 'Ben worked late but did not open the drawer.'),
(@case2, 'Nora Lane', 'Manager', 'Says she was in a meeting with two staff members. They confirm it.', 'Nora checks the cash every evening but was not alone near the drawer.');

INSERT INTO evidence (case_id, title, type, description, location, why_matters) VALUES
(@case2, 'Office Camera', 'Record', 'The office camera shows Maya opening the cash drawer after work.', 'Office wall camera', 'It shows Maya at the drawer.'),
(@case2, 'Drawer Key Log', 'Record', 'The drawer key log shows Maya''s key opened the drawer at 6:10 PM.', 'Office system', 'It links Maya to the drawer.'),
(@case2, 'Rent Text', 'Document', 'A text on Maya''s phone says, "I must pay my rent tonight."', 'Maya''s phone', 'It gives Maya a clear money motive.'),
(@case2, 'Meeting Note', 'Record', 'Two staff members say Nora was in a meeting when the cash was taken.', 'Meeting room', 'It supports Nora''s alibi.');

INSERT INTO clue_connections (case_id, evidence_id_a, evidence_id_b, relation_type, explanation)
SELECT @case2, e1.id, e2.id, 'Match', 'The camera and key log both show Maya opening the drawer.'
FROM evidence e1, evidence e2
WHERE e1.case_id = @case2 AND e1.title = 'Office Camera'
  AND e2.case_id = @case2 AND e2.title = 'Drawer Key Log';

INSERT INTO clue_connections (case_id, evidence_id_a, evidence_id_b, relation_type, explanation)
SELECT @case2, e1.id, e2.id, 'Related', 'Maya had money trouble, and the camera shows she had the chance.'
FROM evidence e1, evidence e2
WHERE e1.case_id = @case2 AND e1.title = 'Rent Text'
  AND e2.case_id = @case2 AND e2.title = 'Office Camera';

INSERT INTO solutions (case_id, correct_suspect_id, correct_motive, explanation)
SELECT @case2, s.id,
       'Money - Maya needed cash for rent',
       'Maya took the cash. The camera and key log show she opened the drawer. Her text explains why she did it.'
FROM suspects s WHERE s.case_id = @case2 AND s.name = 'Maya Reed';

SET @sol2 = LAST_INSERT_ID();

INSERT INTO solution_evidence (solution_id, evidence_id)
SELECT @sol2, e.id FROM evidence e
WHERE e.case_id = @case2 AND e.title IN ('Office Camera', 'Drawer Key Log', 'Rent Text');

INSERT INTO cases (level_id, title, description, intro) VALUES
(3,
 'Stolen Laptop',
 'A school laptop was taken from the office.',
 'After school, a laptop was missing from the office. Three people were near the building. Use the clues to find who took it.');

SET @case3 = LAST_INSERT_ID();

INSERT INTO suspects (case_id, name, role, alibi, description) VALUES
(@case3, 'Tom Hale', 'Student', 'Says he only waited outside for a friend.', 'Tom wanted money and knew the laptop could be sold.'),
(@case3, 'Lina Park', 'Office assistant', 'Says she locked the office and went home. The bus card log supports this.', 'Lina used the laptop earlier in the day but left before the theft.'),
(@case3, 'Paul Stone', 'Security guard', 'Says he stayed at the front gate. The gate log supports this.', 'Paul watched the gate and did not enter the office area.');

INSERT INTO evidence (case_id, title, type, description, location, why_matters) VALUES
(@case3, 'Office Camera', 'Record', 'The office camera shows Tom entering the office after school.', 'Office camera', 'It places Tom inside the office.'),
(@case3, 'Locker Charger', 'Physical', 'The missing laptop charger was found in Tom''s locker.', 'Tom''s locker', 'It links Tom to the stolen laptop.'),
(@case3, 'Sale Message', 'Document', 'A message on Tom''s phone says, "I can sell a laptop tonight."', 'Tom''s phone', 'It shows a plan to sell the laptop.'),
(@case3, 'Gate Log', 'Record', 'The gate log shows Paul stayed at the front gate during the theft.', 'School gate', 'It supports Paul''s alibi.');

INSERT INTO clue_connections (case_id, evidence_id_a, evidence_id_b, relation_type, explanation)
SELECT @case3, e1.id, e2.id, 'Match', 'The camera and the charger both point to Tom.'
FROM evidence e1, evidence e2
WHERE e1.case_id = @case3 AND e1.title = 'Office Camera'
  AND e2.case_id = @case3 AND e2.title = 'Locker Charger';

INSERT INTO clue_connections (case_id, evidence_id_a, evidence_id_b, relation_type, explanation)
SELECT @case3, e1.id, e2.id, 'Related', 'The sale message shows why Tom took the laptop.'
FROM evidence e1, evidence e2
WHERE e1.case_id = @case3 AND e1.title = 'Sale Message'
  AND e2.case_id = @case3 AND e2.title = 'Locker Charger';

INSERT INTO solutions (case_id, correct_suspect_id, correct_motive, explanation)
SELECT @case3, s.id,
       'Money - Tom wanted to sell the laptop',
       'Tom took the laptop. The camera shows him in the office, and the charger was in his locker. His message shows he planned to sell it.'
FROM suspects s WHERE s.case_id = @case3 AND s.name = 'Tom Hale';

SET @sol3 = LAST_INSERT_ID();

INSERT INTO solution_evidence (solution_id, evidence_id)
SELECT @sol3, e.id FROM evidence e
WHERE e.case_id = @case3 AND e.title IN ('Office Camera', 'Locker Charger', 'Sale Message');

INSERT INTO cases (level_id, title, description, intro) VALUES
(1,
 'Lost Wallet',
 'A wallet went missing in a school hall.',
 'A student left a wallet on a bench in the school hall. Ten minutes later, it was gone. Three people were near the bench. Find who took it.');

SET @case4 = LAST_INSERT_ID();

INSERT INTO suspects (case_id, name, role, alibi, description) VALUES
(@case4, 'Rami Saleh', 'Student', 'Says he only walked past the bench.', 'Rami wanted money for a game card.'),
(@case4, 'Huda Karim', 'Student', 'Says she was in the canteen. The cashier saw her there.', 'Huda passed the hall earlier but left before the theft.'),
(@case4, 'Mr Sami', 'Janitor', 'Says he was cleaning the stairs. Another worker confirms it.', 'Mr Sami was working in another part of the building.');

INSERT INTO evidence (case_id, title, type, description, location, why_matters) VALUES
(@case4, 'Hall Camera', 'Record', 'The hall camera shows Rami picking up the wallet from the bench.', 'School hall', 'It shows who took the wallet.'),
(@case4, 'Wallet Fingerprint', 'Forensic', 'A clear fingerprint on the wallet matches Rami.', 'Bench area', 'It links Rami to the wallet.'),
(@case4, 'Game Card Message', 'Document', 'A message on Rami''s phone says, "I need money for a game card today."', 'Rami''s phone', 'It gives Rami a money motive.');

INSERT INTO clue_connections (case_id, evidence_id_a, evidence_id_b, relation_type, explanation)
SELECT @case4, e1.id, e2.id, 'Match', 'The camera and fingerprint both point to Rami.'
FROM evidence e1, evidence e2
WHERE e1.case_id = @case4 AND e1.title = 'Hall Camera'
  AND e2.case_id = @case4 AND e2.title = 'Wallet Fingerprint';

INSERT INTO clue_connections (case_id, evidence_id_a, evidence_id_b, relation_type, explanation)
SELECT @case4, e1.id, e2.id, 'Related', 'The message shows why Rami took the wallet.'
FROM evidence e1, evidence e2
WHERE e1.case_id = @case4 AND e1.title = 'Hall Camera'
  AND e2.case_id = @case4 AND e2.title = 'Game Card Message';

INSERT INTO solutions (case_id, correct_suspect_id, correct_motive, explanation)
SELECT @case4, s.id,
       'Money - Rami wanted money for a game card',
       'Rami took the wallet. The camera and fingerprint both point to him. His message shows his reason.'
FROM suspects s WHERE s.case_id = @case4 AND s.name = 'Rami Saleh';

SET @sol4 = LAST_INSERT_ID();

INSERT INTO solution_evidence (solution_id, evidence_id)
SELECT @sol4, e.id FROM evidence e
WHERE e.case_id = @case4 AND e.title IN ('Hall Camera', 'Wallet Fingerprint', 'Game Card Message');

INSERT INTO cases (level_id, title, description, intro) VALUES
(2,
 'Stolen Bike',
 'A bike was taken from outside a small shop.',
 'A bike was locked outside a shop. Later, the lock was cut and the bike was gone. Three people were near the shop. Find who took it.');

SET @case5 = LAST_INSERT_ID();

INSERT INTO suspects (case_id, name, role, alibi, description) VALUES
(@case5, 'Nabil Jaber', 'Delivery worker', 'Says he only stopped to check his phone.', 'Nabil was angry because his own bike was broken.'),
(@case5, 'Salma Younes', 'Shop worker', 'Says she stayed inside the shop. The shop owner confirms it.', 'Salma saw the bike but did not leave the counter.'),
(@case5, 'Omar Faris', 'Customer', 'Says he left right after buying water. The receipt time supports this.', 'Omar was there for a short time only.');

INSERT INTO evidence (case_id, title, type, description, location, why_matters) VALUES
(@case5, 'Street Camera', 'Record', 'The street camera shows Nabil cutting the bike lock and riding away.', 'Outside shop', 'It shows the theft clearly.'),
(@case5, 'Cut Lock Tool', 'Physical', 'A small cutter was found in Nabil''s delivery bag.', 'Nabil''s bag', 'It could cut the bike lock.'),
(@case5, 'Angry Message', 'Document', 'A message on Nabil''s phone says, "I am angry. I will take a bike today."', 'Nabil''s phone', 'It shows anger and intent.');

INSERT INTO clue_connections (case_id, evidence_id_a, evidence_id_b, relation_type, explanation)
SELECT @case5, e1.id, e2.id, 'Match', 'The camera and tool both point to Nabil.'
FROM evidence e1, evidence e2
WHERE e1.case_id = @case5 AND e1.title = 'Street Camera'
  AND e2.case_id = @case5 AND e2.title = 'Cut Lock Tool';

INSERT INTO clue_connections (case_id, evidence_id_a, evidence_id_b, relation_type, explanation)
SELECT @case5, e1.id, e2.id, 'Related', 'The message shows why Nabil stole the bike.'
FROM evidence e1, evidence e2
WHERE e1.case_id = @case5 AND e1.title = 'Street Camera'
  AND e2.case_id = @case5 AND e2.title = 'Angry Message';

INSERT INTO solutions (case_id, correct_suspect_id, correct_motive, explanation)
SELECT @case5, s.id,
       'Anger - Nabil was angry about his broken bike',
       'Nabil stole the bike. The camera shows him, and the cutter was in his bag. His message explains his anger.'
FROM suspects s WHERE s.case_id = @case5 AND s.name = 'Nabil Jaber';

SET @sol5 = LAST_INSERT_ID();

INSERT INTO solution_evidence (solution_id, evidence_id)
SELECT @sol5, e.id FROM evidence e
WHERE e.case_id = @case5 AND e.title IN ('Street Camera', 'Cut Lock Tool', 'Angry Message');

INSERT INTO cases (level_id, title, description, intro) VALUES
(3,
 'Fake Expense Claim',
 'A worker submitted a fake taxi claim.',
 'A company paid for a taxi trip that may not be real. Three workers used expense forms that day. Check the clues and find who lied.');

SET @case6 = LAST_INSERT_ID();

INSERT INTO suspects (case_id, name, role, alibi, description) VALUES
(@case6, 'Lara Moss', 'Sales worker', 'Says she took a taxi home after a late meeting.', 'Lara wanted extra money this month.'),
(@case6, 'Fadi Noor', 'Accountant', 'Says he stayed in the office. Two workers confirm it.', 'Fadi checked forms but did not submit one.'),
(@case6, 'Mina Ross', 'Assistant', 'Says she went home by bus. Her bus card supports this.', 'Mina filed a normal food claim only.');

INSERT INTO evidence (case_id, title, type, description, location, why_matters) VALUES
(@case6, 'Building Camera', 'Record', 'The building camera shows Lara leaving with a friend in a private car, not a taxi.', 'Office exit', 'It breaks Lara''s story.'),
(@case6, 'Fake Receipt', 'Document', 'The taxi receipt number on Lara''s form does not exist in the taxi company system.', 'Expense form', 'It shows the receipt is fake.'),
(@case6, 'Money Message', 'Document', 'A message on Lara''s phone says, "I need extra money this month."', 'Lara''s phone', 'It gives Lara a money motive.');

INSERT INTO clue_connections (case_id, evidence_id_a, evidence_id_b, relation_type, explanation)
SELECT @case6, e1.id, e2.id, 'Match', 'The camera and fake receipt both show Lara lied.'
FROM evidence e1, evidence e2
WHERE e1.case_id = @case6 AND e1.title = 'Building Camera'
  AND e2.case_id = @case6 AND e2.title = 'Fake Receipt';

INSERT INTO clue_connections (case_id, evidence_id_a, evidence_id_b, relation_type, explanation)
SELECT @case6, e1.id, e2.id, 'Related', 'The message shows why Lara made the fake claim.'
FROM evidence e1, evidence e2
WHERE e1.case_id = @case6 AND e1.title = 'Fake Receipt'
  AND e2.case_id = @case6 AND e2.title = 'Money Message';

INSERT INTO solutions (case_id, correct_suspect_id, correct_motive, explanation)
SELECT @case6, s.id,
       'Money - Lara wanted extra money',
       'Lara made the fake claim. The camera shows she did not take a taxi, and the receipt is false. Her message shows the reason.'
FROM suspects s WHERE s.case_id = @case6 AND s.name = 'Lara Moss';

SET @sol6 = LAST_INSERT_ID();

INSERT INTO solution_evidence (solution_id, evidence_id)
SELECT @sol6, e.id FROM evidence e
WHERE e.case_id = @case6 AND e.title IN ('Building Camera', 'Fake Receipt', 'Money Message');
