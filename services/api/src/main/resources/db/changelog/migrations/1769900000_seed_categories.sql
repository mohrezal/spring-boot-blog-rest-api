--liquibase formatted sql
--changeset mohammadreza:seed_categories context:seed splitStatements:false

INSERT INTO categories (id, name, slug, description, created_at, updated_at)
SELECT * FROM (VALUES
    (uuid_generate_v4(), 'Technology', 'technology', 'Latest technology trends and innovations', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (uuid_generate_v4(), 'Programming', 'programming', 'Programming languages, paradigms, and best practices', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (uuid_generate_v4(), 'Web Development', 'web-development', 'Frontend and backend web development', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (uuid_generate_v4(), 'Mobile Development', 'mobile-development', 'iOS, Android, and cross-platform mobile apps', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (uuid_generate_v4(), 'DevOps', 'devops', 'CI/CD, infrastructure, and deployment strategies', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (uuid_generate_v4(), 'Data Science', 'data-science', 'Data analysis, visualization, and machine learning', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (uuid_generate_v4(), 'Artificial Intelligence', 'artificial-intelligence', 'AI, deep learning, and neural networks', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (uuid_generate_v4(), 'Cybersecurity', 'cybersecurity', 'Security practices, vulnerabilities, and protection', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (uuid_generate_v4(), 'Cloud Computing', 'cloud-computing', 'AWS, Azure, GCP, and cloud architecture', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (uuid_generate_v4(), 'Software Engineering', 'software-engineering', 'Design patterns, architecture, and methodologies', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (uuid_generate_v4(), 'Career', 'career', 'Career advice, interviews, and professional growth', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (uuid_generate_v4(), 'Tutorials', 'tutorials', 'Step-by-step guides and how-to articles', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (uuid_generate_v4(), 'News', 'news', 'Industry news and announcements', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (uuid_generate_v4(), 'Opinion', 'opinion', 'Perspectives and commentary on tech topics', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (uuid_generate_v4(), 'Reviews', 'reviews', 'Product and tool reviews', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
) AS seed(id, name, slug, description, created_at, updated_at)
WHERE NOT EXISTS (SELECT 1 FROM categories LIMIT 1);

--rollback DELETE FROM categories WHERE slug IN ('technology', 'programming', 'web-development', 'mobile-development', 'devops', 'data-science', 'artificial-intelligence', 'cybersecurity', 'cloud-computing', 'software-engineering', 'career', 'tutorials', 'news', 'opinion', 'reviews');
