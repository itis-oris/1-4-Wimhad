-- SkillSwap Database Complete Setup Script
-- This script creates the database, tables, and inserts mock data

-- Connect to PostgreSQL and create database (run this as superuser)
-- CREATE DATABASE skillswap;
-- \c skillswap;

-- Drop existing tables if they exist (in reverse order of dependencies)
DROP TABLE IF EXISTS reviews CASCADE;
DROP TABLE IF EXISTS offer_requests CASCADE;
DROP TABLE IF EXISTS requests CASCADE;
DROP TABLE IF EXISTS offers CASCADE;
DROP TABLE IF EXISTS user_skills CASCADE;
DROP TABLE IF EXISTS skills CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Drop sequences if they exist
DROP SEQUENCE IF EXISTS users_id_seq CASCADE;
DROP SEQUENCE IF EXISTS skills_id_seq CASCADE;
DROP SEQUENCE IF EXISTS offers_id_seq CASCADE;
DROP SEQUENCE IF EXISTS requests_id_seq CASCADE;
DROP SEQUENCE IF EXISTS reviews_id_seq CASCADE;

-- 1. USERS TABLE
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    bio TEXT,
    phone VARCHAR(20),
    location VARCHAR(100),
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. SKILLS TABLE
CREATE TABLE skills (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    category VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. USER_SKILLS TABLE (Many-to-Many relationship)
CREATE TABLE user_skills (
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    skill_id INTEGER REFERENCES skills(id) ON DELETE CASCADE,
    proficiency_level VARCHAR(20) DEFAULT 'INTERMEDIATE',
    years_experience INTEGER DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, skill_id)
);

-- 4. OFFERS TABLE
CREATE TABLE offers (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    skill_id INTEGER REFERENCES skills(id),
    title VARCHAR(100) NOT NULL,
    description TEXT,
    price NUMERIC(10,2),
    currency VARCHAR(10) DEFAULT 'RUB',
    duration_hours INTEGER,
    location_type VARCHAR(20) DEFAULT 'ONLINE', -- ONLINE, IN_PERSON, HYBRID
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 5. REQUESTS TABLE
CREATE TABLE requests (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    skill_id INTEGER REFERENCES skills(id),
    title VARCHAR(100) NOT NULL,
    description TEXT,
    budget_min NUMERIC(10,2),
    budget_max NUMERIC(10,2),
    currency VARCHAR(10) DEFAULT 'RUB',
    urgency VARCHAR(20) DEFAULT 'NORMAL', -- LOW, NORMAL, HIGH, URGENT
    status VARCHAR(20) DEFAULT 'OPEN', -- OPEN, IN_PROGRESS, COMPLETED, CANCELLED
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 6. OFFER_REQUESTS TABLE (Many-to-Many relationship)
CREATE TABLE offer_requests (
    offer_id INTEGER REFERENCES offers(id) ON DELETE CASCADE,
    request_id INTEGER REFERENCES requests(id) ON DELETE CASCADE,
    status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, ACCEPTED, REJECTED, COMPLETED
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (offer_id, request_id)
);

-- 7. REVIEWS TABLE
CREATE TABLE reviews (
    id SERIAL PRIMARY KEY,
    reviewer_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    reviewed_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    offer_id INTEGER REFERENCES offers(id) ON DELETE SET NULL,
    request_id INTEGER REFERENCES requests(id) ON DELETE SET NULL,
    rating INTEGER CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    is_public BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_offers_user_id ON offers(user_id);
CREATE INDEX idx_offers_skill_id ON offers(skill_id);
CREATE INDEX idx_offers_is_active ON offers(is_active);
CREATE INDEX idx_requests_user_id ON requests(user_id);
CREATE INDEX idx_requests_skill_id ON requests(skill_id);
CREATE INDEX idx_requests_status ON requests(status);
CREATE INDEX idx_reviews_reviewed_id ON reviews(reviewed_id);
CREATE INDEX idx_reviews_rating ON reviews(rating);

-- Create updated_at trigger function
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create triggers for updated_at
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_offers_updated_at BEFORE UPDATE ON offers FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_requests_updated_at BEFORE UPDATE ON requests FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_offer_requests_updated_at BEFORE UPDATE ON offer_requests FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ============================================================================
-- INSERT MOCK DATA
-- ============================================================================

-- 1. INSERT USERS
INSERT INTO users (username, email, password_hash, full_name, bio, phone, location, role) VALUES
('alice_dev', 'alice.smith@email.com', '$2a$10$vRWzZfDJKYugfBG4eOv.pudCDC0fjUC22d7a6lyUwwn9foQxC6HEK', 'Alice Smith', 'Full-stack developer with 5 years of experience in Java and React. Passionate about teaching and helping others learn programming.', '+7-900-123-45-67', 'Moscow, Russia', 'USER'),
('bob_teacher', 'bob.johnson@email.com', '$2a$10$vRWzZfDJKYugfBG4eOv.pudCDC0fjUC22d7a6lyUwwn9foQxC6HEK', 'Bob Johnson', 'Experienced English teacher and translator. Native English speaker with 8 years of teaching experience.', '+7-900-234-56-78', 'St. Petersburg, Russia', 'USER'),
('maria_artist', 'maria.petrova@email.com', '$2a$10$vRWzZfDJKYugfBG4eOv.pudCDC0fjUC22d7a6lyUwwn9foQxC6HEK', 'Maria Petrova', 'Professional photographer and graphic designer. Specializing in portrait and event photography.', '+7-900-345-67-89', 'Kazan, Russia', 'USER'),
('dmitry_chef', 'dmitry.ivanov@email.com', '$2a$10$vRWzZfDJKYugfBG4eOv.pudCDC0fjUC22d7a6lyUwwn9foQxC6HEK', 'Dmitry Ivanov', 'Chef with 12 years of experience in Italian and French cuisine. Love teaching cooking techniques.', '+7-900-456-78-90', 'Novosibirsk, Russia', 'USER'),
('anna_music', 'anna.sidorova@email.com', '$2a$10$vRWzZfDJKYugfBG4eOv.pudCDC0fjUC22d7a6lyUwwn9foQxC6HEK', 'Anna Sidorova', 'Classical pianist and music teacher. Graduated from Moscow Conservatory.', '+7-900-567-89-01', 'Moscow, Russia', 'USER'),
('sergey_fitness', 'sergey.kuznetsov@email.com', '$2a$10$vRWzZfDJKYugfBG4eOv.pudCDC0fjUC22d7a6lyUwwn9foQxC6HEK', 'Sergey Kuznetsov', 'Certified personal trainer and nutritionist. Helping people achieve their fitness goals.', '+7-900-678-90-12', 'Yekaterinburg, Russia', 'USER'),
('elena_design', 'elena.volkova@email.com', '$2a$10$vRWzZfDJKYugfBG4eOv.pudCDC0fjUC22d7a6lyUwwn9foQxC6HEK', 'Elena Volkova', 'UI/UX designer with expertise in web and mobile applications. Passionate about user-centered design.', '+7-900-789-01-23', 'Moscow, Russia', 'USER'),
('admin', 'admin@skillswap.com', '$2a$10$vRWzZfDJKYugfBG4eOv.pudCDC0fjUC22d7a6lyUwwn9foQxC6HEK', 'System Administrator', 'Platform administrator', '+7-900-000-00-00', 'Moscow, Russia', 'ADMIN');

-- 2. INSERT SKILLS
INSERT INTO skills (name, description, category) VALUES
('Java Programming', 'Object-oriented programming language for building enterprise applications', 'Programming'),
('Python Programming', 'Versatile programming language for data science, web development, and automation', 'Programming'),
('Web Development', 'HTML, CSS, JavaScript for creating websites and web applications', 'Programming'),
('React Development', 'JavaScript library for building user interfaces and single-page applications', 'Programming'),
('English Teaching', 'Teaching English as a foreign language to various age groups', 'Education'),
('Russian Translation', 'Professional translation services between English and Russian', 'Language'),
('Photography', 'Professional photography including portrait, event, and landscape', 'Arts'),
('Graphic Design', 'Creating visual content using digital tools and design principles', 'Arts'),
('Cooking', 'Culinary arts including various cuisines and cooking techniques', 'Lifestyle'),
('Piano Lessons', 'Teaching piano playing for beginners and intermediate students', 'Music'),
('Guitar Lessons', 'Acoustic and electric guitar instruction for all skill levels', 'Music'),
('Personal Training', 'Fitness training and workout programs for individuals', 'Fitness'),
('Nutrition Consulting', 'Diet planning and nutritional advice for health goals', 'Health'),
('UI/UX Design', 'User interface and user experience design for digital products', 'Design'),
('Data Analysis', 'Statistical analysis and data visualization using various tools', 'Analytics'),
('Project Management', 'Planning, organizing, and managing projects and teams', 'Business'),
('Content Writing', 'Creating engaging content for websites, blogs, and marketing', 'Writing'),
('Video Editing', 'Professional video editing and post-production services', 'Media');

-- 3. INSERT USER_SKILLS
INSERT INTO user_skills (user_id, skill_id, proficiency_level, years_experience) VALUES
-- Alice's skills
(1, 1, 'EXPERT', 5), -- Java Programming
(1, 3, 'ADVANCED', 4), -- Web Development
(1, 4, 'INTERMEDIATE', 3), -- React Development

-- Bob's skills
(2, 5, 'EXPERT', 8), -- English Teaching
(2, 6, 'ADVANCED', 6), -- Russian Translation

-- Maria's skills
(3, 7, 'EXPERT', 7), -- Photography
(3, 8, 'ADVANCED', 5), -- Graphic Design

-- Dmitry's skills
(4, 9, 'EXPERT', 12), -- Cooking

-- Anna's skills
(5, 10, 'EXPERT', 10), -- Piano Lessons
(5, 11, 'ADVANCED', 8), -- Guitar Lessons

-- Sergey's skills
(6, 12, 'EXPERT', 6), -- Personal Training
(6, 13, 'ADVANCED', 4), -- Nutrition Consulting

-- Elena's skills
(7, 14, 'EXPERT', 8), -- UI/UX Design
(7, 3, 'ADVANCED', 6), -- Web Development

-- Admin skills
(8, 1, 'INTERMEDIATE', 3), -- Java Programming
(8, 16, 'ADVANCED', 5); -- Project Management

-- 4. INSERT OFFERS
INSERT INTO offers (user_id, skill_id, title, description, price, currency, duration_hours, location_type) VALUES
-- Alice's offers
(1, 1, 'Java Programming Tutoring', 'Learn Java from basics to advanced concepts. Perfect for beginners and intermediate developers. We will cover OOP, collections, streams, and Spring framework.', 1500, 'RUB', 2, 'ONLINE'),
(1, 3, 'Web Development Course', 'Complete web development course covering HTML, CSS, JavaScript, and modern frameworks. Build real projects from scratch.', 2000, 'RUB', 3, 'HYBRID'),
(1, 4, 'React Development Workshop', 'Hands-on React workshop for building modern web applications. Learn hooks, state management, and best practices.', 1800, 'RUB', 2, 'ONLINE'),

-- Bob's offers
(2, 5, 'English Conversation Classes', 'Improve your English speaking skills through conversation practice. Suitable for intermediate and advanced learners.', 800, 'RUB', 1, 'ONLINE'),
(2, 6, 'Professional Translation Services', 'High-quality translation between English and Russian. Specializing in business documents, academic papers, and creative content.', 1200, 'RUB', 1, 'ONLINE'),

-- Maria's offers
(3, 7, 'Portrait Photography Session', 'Professional portrait photography session. Includes 2 hours of shooting and 10 edited photos. Perfect for LinkedIn profiles or personal use.', 3000, 'RUB', 2, 'IN_PERSON'),
(3, 8, 'Logo and Brand Design', 'Custom logo and brand identity design. Includes consultation, multiple concepts, and final files in various formats.', 5000, 'RUB', 4, 'ONLINE'),

-- Dmitry's offers
(4, 9, 'Italian Cooking Masterclass', 'Learn authentic Italian cooking techniques. We will prepare pasta, risotto, and traditional sauces. All ingredients included.', 2500, 'RUB', 3, 'IN_PERSON'),

-- Anna's offers
(5, 10, 'Piano Lessons for Beginners', 'Individual piano lessons for beginners. Learn proper technique, music theory, and play your favorite songs.', 1000, 'RUB', 1, 'IN_PERSON'),
(5, 11, 'Guitar Lessons', 'Learn acoustic or electric guitar. Suitable for all ages and skill levels. We will cover chords, strumming, and popular songs.', 800, 'RUB', 1, 'HYBRID'),

-- Sergey's offers
(6, 12, 'Personal Training Session', 'One-on-one fitness training session. Customized workout plan based on your goals and fitness level.', 1500, 'RUB', 1, 'IN_PERSON'),
(6, 13, 'Nutrition Consultation', 'Personalized nutrition plan and dietary advice. Includes meal planning and progress tracking.', 1200, 'RUB', 1, 'ONLINE'),

-- Elena's offers
(7, 14, 'UI/UX Design Consultation', 'Professional consultation for your digital product design. Improve user experience and interface design.', 2000, 'RUB', 2, 'ONLINE'),
(7, 3, 'Website Design and Development', 'Complete website design and development service. From concept to deployment with modern technologies.', 15000, 'RUB', 20, 'ONLINE');

-- 5. INSERT REQUESTS
INSERT INTO requests (user_id, skill_id, title, description, budget_min, budget_max, currency, urgency) VALUES
-- Requests from various users
(2, 1, 'Need Java Help for University Project', 'I need help with a Java project for my university course. It involves creating a simple banking system with basic operations.', 500, 1500, 'RUB', 'HIGH'),
(3, 5, 'English Speaking Practice', 'Looking for conversation practice to improve my English speaking skills. I am intermediate level and want to become more fluent.', 300, 800, 'RUB', 'NORMAL'),
(4, 7, 'Professional Headshots Needed', 'I need professional headshots for my business profile. Looking for high-quality photos with professional editing.', 2000, 4000, 'RUB', 'NORMAL'),
(5, 9, 'Learn to Cook Russian Dishes', 'Want to learn traditional Russian cooking. Interested in borscht, pelmeni, and other classic dishes.', 1000, 2500, 'RUB', 'LOW'),
(6, 14, 'Website Redesign Consultation', 'Need help redesigning my company website to improve user experience and conversion rates.', 3000, 8000, 'RUB', 'HIGH'),
(7, 12, 'Fitness Training for Weight Loss', 'Looking for a personal trainer to help me lose weight and get in shape. I am a beginner with no previous experience.', 800, 2000, 'RUB', 'NORMAL'),
(1, 6, 'Document Translation Needed', 'Need to translate a business contract from English to Russian. Document is about 10 pages long.', 800, 1500, 'RUB', 'URGENT'),
(2, 8, 'Logo Design for Startup', 'Starting a new tech company and need a professional logo design. Looking for something modern and memorable.', 2000, 6000, 'RUB', 'HIGH');

-- 6. INSERT OFFER_REQUESTS (matching offers to requests)
INSERT INTO offer_requests (offer_id, request_id, status) VALUES
-- Alice's Java offer matches Bob's Java request
(1, 1, 'ACCEPTED'),
-- Bob's English offer matches Maria's English request
(4, 2, 'PENDING'),
-- Maria's photography offer matches Dmitry's headshot request
(6, 3, 'ACCEPTED'),
-- Dmitry's cooking offer matches Anna's Russian cooking request
(9, 4, 'PENDING'),
-- Elena's UI/UX offer matches Sergey's website redesign request
(13, 5, 'ACCEPTED'),
-- Sergey's training offer matches Elena's fitness request
(12, 6, 'PENDING'),
-- Bob's translation offer matches Alice's translation request
(5, 7, 'COMPLETED'),
-- Maria's design offer matches Bob's logo request
(7, 8, 'ACCEPTED');

-- 7. INSERT REVIEWS
INSERT INTO reviews (reviewer_id, reviewed_id, offer_id, rating, comment, is_public) VALUES
-- Reviews for completed transactions
(2, 1, 1, 5, 'Excellent Java tutor! Alice explained complex concepts very clearly and helped me complete my university project successfully. Highly recommended!', TRUE),
(1, 2, 5, 5, 'Bob provided excellent translation services. The document was translated accurately and delivered on time. Very professional!', TRUE),
(4, 3, 6, 5, 'Maria is an amazing photographer! The headshots turned out perfect and she made me feel very comfortable during the session.', TRUE),
(7, 6, 12, 4, 'Sergey is a great trainer. He created a personalized workout plan and helped me stay motivated. I can already see results!', TRUE),
(6, 7, 13, 5, 'Elena provided valuable insights for our website redesign. Her expertise in UI/UX design helped us improve our conversion rates significantly.', TRUE),
(3, 2, 4, 4, 'Bob is a patient and encouraging English teacher. Our conversation practice sessions have really improved my speaking confidence.', TRUE),
(5, 4, 9, 5, 'Dmitry is a fantastic cooking instructor! I learned so much about Italian cuisine and the food was absolutely delicious.', TRUE),
(1, 7, 14, 4, 'Elena designed a beautiful website for my business. She was professional, creative, and delivered exactly what I wanted.', TRUE);

-- Add some general user reviews (not tied to specific offers)
INSERT INTO reviews (reviewer_id, reviewed_id, rating, comment, is_public) VALUES
(2, 1, 5, 'Alice is very knowledgeable and always willing to help. Great community member!', TRUE),
(3, 2, 5, 'Bob is reliable and professional. I would definitely work with him again.', TRUE),
(4, 3, 5, 'Maria has great artistic vision and attention to detail.', TRUE),
(5, 4, 5, 'Dmitry is passionate about cooking and it shows in his teaching.', TRUE),
(6, 5, 4, 'Anna is a talented musician and excellent teacher.', TRUE),
(7, 6, 5, 'Sergey is dedicated to helping people achieve their fitness goals.', TRUE),
(1, 7, 5, 'Elena is creative and professional in her design work.', TRUE);

-- password is test123
