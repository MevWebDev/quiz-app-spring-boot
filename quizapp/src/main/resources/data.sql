-- Sample data for Quiz App
-- This file is executed on application startup (spring.sql.init.mode=always)

-- Insert categories
INSERT INTO category (name, description) VALUES
    ('Programming', 'Questions about programming languages and concepts'),
    ('Mathematics', 'Mathematical problems and concepts'),
    ('Science', 'Scientific knowledge and discoveries'),
    ('History', 'Historical events and figures'),
    ('General Knowledge', 'Various topics and trivia')
ON CONFLICT DO NOTHING;

-- Insert sample quiz
INSERT INTO quiz (title, description, time_limit, shuffle_questions, shuffle_answers, negative_points, created_at, updated_at)
VALUES (
    'Java Basics Quiz',
    'Test your knowledge of Java programming fundamentals',
    300,
    true,
    true,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
)
ON CONFLICT DO NOTHING;

-- Users are now created by DataInitializer with properly encoded BCrypt passwords

-- Insert sample questions for the Java quiz (quiz_id = 1)
INSERT INTO question (text, type, points, order_index, quiz_id)
SELECT 'What is the correct way to declare a variable in Java?', 'SINGLE_CHOICE', 1, 1, q.id
FROM quiz q WHERE q.title = 'Java Basics Quiz'
ON CONFLICT DO NOTHING;

INSERT INTO question (text, type, points, order_index, quiz_id)
SELECT 'Which of the following are primitive data types in Java?', 'MULTIPLE_CHOICE', 2, 2, q.id
FROM quiz q WHERE q.title = 'Java Basics Quiz'
ON CONFLICT DO NOTHING;

INSERT INTO question (text, type, points, order_index, quiz_id)
SELECT 'Java is a compiled language.', 'TRUE_FALSE', 1, 3, q.id
FROM quiz q WHERE q.title = 'Java Basics Quiz'
ON CONFLICT DO NOTHING;
