-- Sample data for Quiz App
-- This file is executed on application startup

-- Clear existing data (for dev purposes)
DELETE FROM answer;
DELETE FROM question;
DELETE FROM quiz_result;
DELETE FROM quiz_categories;
DELETE FROM quiz;
DELETE FROM category;

-- Insert categories
INSERT INTO category (id, name, description) VALUES
    (1, 'Programming', 'Questions about programming languages and concepts'),
    (2, 'Mathematics', 'Mathematical problems and concepts'),
    (3, 'Science', 'Scientific knowledge and discoveries'),
    (4, 'History', 'Historical events and figures'),
    (5, 'General Knowledge', 'Various topics and trivia');

-- =====================================================
-- QUIZ 1: Java Basics Quiz
-- =====================================================
INSERT INTO quiz (id, title, description, time_limit, shuffle_questions, shuffle_answers, negative_points, created_at, updated_at)
VALUES (1, 'Java Basics Quiz', 'Test your knowledge of Java programming fundamentals', 300, true, true, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Question 1: Single Choice
INSERT INTO question (id, text, type, points, order_index, quiz_id) 
VALUES (1, 'What is the correct way to declare a variable in Java?', 'SINGLE_CHOICE', 1, 1, 1);

INSERT INTO answer (id, text, is_correct, order_index, question_id) VALUES
    (1, 'int x = 5;', true, 1, 1),
    (2, 'x = 5 int;', false, 2, 1),
    (3, 'integer x = 5;', false, 3, 1),
    (4, 'var: x = 5;', false, 4, 1);

-- Question 2: Multiple Choice
INSERT INTO question (id, text, type, points, order_index, quiz_id) 
VALUES (2, 'Which of the following are primitive data types in Java? (Select all that apply)', 'MULTIPLE_CHOICE', 2, 2, 1);

INSERT INTO answer (id, text, is_correct, order_index, question_id) VALUES
    (5, 'int', true, 1, 2),
    (6, 'String', false, 2, 2),
    (7, 'boolean', true, 3, 2),
    (8, 'double', true, 4, 2),
    (9, 'Integer', false, 5, 2);

-- Question 3: True/False
INSERT INTO question (id, text, type, points, order_index, quiz_id) 
VALUES (3, 'Java is a compiled language that runs on the JVM.', 'TRUE_FALSE', 1, 3, 1);

INSERT INTO answer (id, text, is_correct, order_index, question_id) VALUES
    (10, 'True', true, 1, 3),
    (11, 'False', false, 2, 3);

-- Question 4: Short Answer
INSERT INTO question (id, text, type, points, order_index, quiz_id) 
VALUES (4, 'What keyword is used to create a new instance of a class?', 'SHORT_ANSWER', 1, 4, 1);

INSERT INTO answer (id, text, is_correct, order_index, question_id) VALUES
    (12, 'new', true, 1, 4);

-- Question 5: Single Choice
INSERT INTO question (id, text, type, points, order_index, quiz_id) 
VALUES (5, 'Which access modifier makes a member visible only within its own class?', 'SINGLE_CHOICE', 1, 5, 1);

INSERT INTO answer (id, text, is_correct, order_index, question_id) VALUES
    (13, 'public', false, 1, 5),
    (14, 'protected', false, 2, 5),
    (15, 'private', true, 3, 5),
    (16, 'default', false, 4, 5);

-- =====================================================
-- QUIZ 2: World Capitals
-- =====================================================
INSERT INTO quiz (id, title, description, time_limit, shuffle_questions, shuffle_answers, negative_points, created_at, updated_at)
VALUES (2, 'World Capitals Quiz', 'Test your knowledge of world capitals', 180, false, false, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Question 6
INSERT INTO question (id, text, type, points, order_index, quiz_id) 
VALUES (6, 'What is the capital of France?', 'SINGLE_CHOICE', 1, 1, 2);

INSERT INTO answer (id, text, is_correct, order_index, question_id) VALUES
    (17, 'London', false, 1, 6),
    (18, 'Paris', true, 2, 6),
    (19, 'Berlin', false, 3, 6),
    (20, 'Madrid', false, 4, 6);

-- Question 7
INSERT INTO question (id, text, type, points, order_index, quiz_id) 
VALUES (7, 'What is the capital of Japan?', 'SINGLE_CHOICE', 1, 2, 2);

INSERT INTO answer (id, text, is_correct, order_index, question_id) VALUES
    (21, 'Seoul', false, 1, 7),
    (22, 'Beijing', false, 2, 7),
    (23, 'Tokyo', true, 3, 7),
    (24, 'Osaka', false, 4, 7);

-- Question 8
INSERT INTO question (id, text, type, points, order_index, quiz_id) 
VALUES (8, 'What is the capital of Australia?', 'SINGLE_CHOICE', 1, 3, 2);

INSERT INTO answer (id, text, is_correct, order_index, question_id) VALUES
    (25, 'Sydney', false, 1, 8),
    (26, 'Melbourne', false, 2, 8),
    (27, 'Canberra', true, 3, 8),
    (28, 'Brisbane', false, 4, 8);

-- Question 9
INSERT INTO question (id, text, type, points, order_index, quiz_id) 
VALUES (9, 'Warsaw is the capital of Poland.', 'TRUE_FALSE', 1, 4, 2);

INSERT INTO answer (id, text, is_correct, order_index, question_id) VALUES
    (29, 'True', true, 1, 9),
    (30, 'False', false, 2, 9);

-- Question 10
INSERT INTO question (id, text, type, points, order_index, quiz_id) 
VALUES (10, 'What is the capital of Brazil?', 'SINGLE_CHOICE', 1, 5, 2);

INSERT INTO answer (id, text, is_correct, order_index, question_id) VALUES
    (31, 'Rio de Janeiro', false, 1, 10),
    (32, 'São Paulo', false, 2, 10),
    (33, 'Brasília', true, 3, 10),
    (34, 'Salvador', false, 4, 10);

-- =====================================================
-- QUIZ 3: Basic Math
-- =====================================================
INSERT INTO quiz (id, title, description, time_limit, shuffle_questions, shuffle_answers, negative_points, created_at, updated_at)
VALUES (3, 'Basic Math Quiz', 'Simple mathematics questions', 120, false, false, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Question 11
INSERT INTO question (id, text, type, points, order_index, quiz_id) 
VALUES (11, 'What is 7 + 8?', 'SHORT_ANSWER', 1, 1, 3);

INSERT INTO answer (id, text, is_correct, order_index, question_id) VALUES
    (35, '15', true, 1, 11);

-- Question 12
INSERT INTO question (id, text, type, points, order_index, quiz_id) 
VALUES (12, 'What is 12 × 12?', 'SHORT_ANSWER', 2, 2, 3);

INSERT INTO answer (id, text, is_correct, order_index, question_id) VALUES
    (36, '144', true, 1, 12);

-- Question 13
INSERT INTO question (id, text, type, points, order_index, quiz_id) 
VALUES (13, 'What is the square root of 81?', 'SINGLE_CHOICE', 1, 3, 3);

INSERT INTO answer (id, text, is_correct, order_index, question_id) VALUES
    (37, '7', false, 1, 13),
    (38, '8', false, 2, 13),
    (39, '9', true, 3, 13),
    (40, '10', false, 4, 13);

-- Question 14
INSERT INTO question (id, text, type, points, order_index, quiz_id) 
VALUES (14, '2 + 2 = 5', 'TRUE_FALSE', 1, 4, 3);

INSERT INTO answer (id, text, is_correct, order_index, question_id) VALUES
    (41, 'True', false, 1, 14),
    (42, 'False', true, 2, 14);

-- Question 15
INSERT INTO question (id, text, type, points, order_index, quiz_id) 
VALUES (15, 'What is 100 ÷ 4?', 'SINGLE_CHOICE', 1, 5, 3);

INSERT INTO answer (id, text, is_correct, order_index, question_id) VALUES
    (43, '20', false, 1, 15),
    (44, '25', true, 2, 15),
    (45, '30', false, 3, 15),
    (46, '35', false, 4, 15);

-- Link quizzes to categories
INSERT INTO quiz_categories (quiz_id, category_id) VALUES
    (1, 1),  -- Java Quiz -> Programming
    (2, 5),  -- Capitals -> General Knowledge
    (3, 2);  -- Math -> Mathematics

-- Reset sequences (for PostgreSQL)
SELECT setval('quiz_id_seq', (SELECT MAX(id) FROM quiz));
SELECT setval('question_id_seq', (SELECT MAX(id) FROM question));
SELECT setval('answer_id_seq', (SELECT MAX(id) FROM answer));
SELECT setval('category_id_seq', (SELECT MAX(id) FROM category));
