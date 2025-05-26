-- ===============================================
-- ANSI SQL Using MySQL - Complete Exercise Solutions
-- Event Management System Database
-- ===============================================

-- 1. User Upcoming Events
-- Show a list of all upcoming events a user is registered for in their city, sorted by date.
SELECT 
    u.full_name,
    u.city,
    e.title,
    e.start_date,
    e.end_date
FROM Users u
JOIN Registrations r ON u.user_id = r.user_id
JOIN Events e ON r.event_id = e.event_id
WHERE e.status = 'upcoming' 
    AND e.city = u.city
ORDER BY e.start_date;

-- 2. Top Rated Events
-- Identify events with the highest average rating, considering only those that have received at least 10 feedback submissions.
SELECT 
    e.title,
    e.city,
    COUNT(f.feedback_id) as feedback_count,
    ROUND(AVG(f.rating), 2) as avg_rating
FROM Events e
JOIN Feedback f ON e.event_id = f.event_id
GROUP BY e.event_id, e.title, e.city
HAVING COUNT(f.feedback_id) >= 10
ORDER BY avg_rating DESC;

-- 3. Inactive Users
-- Retrieve users who have not registered for any events in the last 90 days.
SELECT 
    u.user_id,
    u.full_name,
    u.email,
    u.city
FROM Users u
LEFT JOIN Registrations r ON u.user_id = r.user_id
    AND r.registration_date >= DATE_SUB(CURDATE(), INTERVAL 90 DAY)
WHERE r.user_id IS NULL;

-- 4. Peak Session Hours
-- Count how many sessions are scheduled between 10 AM to 12 PM for each event.
SELECT 
    e.title,
    e.city,
    COUNT(s.session_id) as sessions_10am_to_12pm
FROM Events e
LEFT JOIN Sessions s ON e.event_id = s.event_id
    AND TIME(s.start_time) BETWEEN '10:00:00' AND '12:00:00'
GROUP BY e.event_id, e.title, e.city
ORDER BY sessions_10am_to_12pm DESC;

-- 5. Most Active Cities
-- List the top 5 cities with the highest number of distinct user registrations.
SELECT 
    u.city,
    COUNT(DISTINCT r.user_id) as distinct_registrations
FROM Users u
JOIN Registrations r ON u.user_id = r.user_id
GROUP BY u.city
ORDER BY distinct_registrations DESC
LIMIT 5;

-- 6. Event Resource Summary
-- Generate a report showing the number of resources (PDFs, images, links) uploaded for each event.
SELECT 
    e.title,
    e.city,
    COUNT(CASE WHEN res.resource_type = 'pdf' THEN 1 END) as pdf_count,
    COUNT(CASE WHEN res.resource_type = 'image' THEN 1 END) as image_count,
    COUNT(CASE WHEN res.resource_type = 'link' THEN 1 END) as link_count,
    COUNT(res.resource_id) as total_resources
FROM Events e
LEFT JOIN Resources res ON e.event_id = res.event_id
GROUP BY e.event_id, e.title, e.city
ORDER BY total_resources DESC;

-- 7. Low Feedback Alerts
-- List all users who gave feedback with a rating less than 3, along with their comments and associated event names.
SELECT 
    u.full_name,
    u.email,
    e.title as event_name,
    f.rating,
    f.comments,
    f.feedback_date
FROM Users u
JOIN Feedback f ON u.user_id = f.user_id
JOIN Events e ON f.event_id = e.event_id
WHERE f.rating < 3
ORDER BY f.feedback_date DESC;

-- 8. Sessions per Upcoming Event
-- Display all upcoming events with the count of sessions scheduled for them.
SELECT 
    e.title,
    e.city,
    e.start_date,
    COUNT(s.session_id) as session_count
FROM Events e
LEFT JOIN Sessions s ON e.event_id = s.event_id
WHERE e.status = 'upcoming'
GROUP BY e.event_id, e.title, e.city, e.start_date
ORDER BY session_count DESC;

-- 9. Organizer Event Summary
-- For each event organizer, show the number of events created and their current status.
SELECT 
    u.full_name as organizer_name,
    u.email,
    COUNT(CASE WHEN e.status = 'upcoming' THEN 1 END) as upcoming_events,
    COUNT(CASE WHEN e.status = 'completed' THEN 1 END) as completed_events,
    COUNT(CASE WHEN e.status = 'cancelled' THEN 1 END) as cancelled_events,
    COUNT(e.event_id) as total_events
FROM Users u
LEFT JOIN Events e ON u.user_id = e.organizer_id
GROUP BY u.user_id, u.full_name, u.email
HAVING COUNT(e.event_id) > 0
ORDER BY total_events DESC;

-- 10. Feedback Gap
-- Identify events that had registrations but received no feedback at all.
SELECT 
    e.title,
    e.city,
    e.status,
    COUNT(r.registration_id) as registration_count
FROM Events e
JOIN Registrations r ON e.event_id = r.event_id
LEFT JOIN Feedback f ON e.event_id = f.event_id
WHERE f.event_id IS NULL
GROUP BY e.event_id, e.title, e.city, e.status
ORDER BY registration_count DESC;

-- 11. Daily New User Count
-- Find the number of users who registered each day in the last 7 days.
SELECT 
    registration_date,
    COUNT(user_id) as new_users
FROM Users
WHERE registration_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)
GROUP BY registration_date
ORDER BY registration_date DESC;

-- 12. Event with Maximum Sessions
-- List the event(s) with the highest number of sessions.
SELECT 
    e.title,
    e.city,
    e.status,
    COUNT(s.session_id) as session_count
FROM Events e
LEFT JOIN Sessions s ON e.event_id = s.event_id
GROUP BY e.event_id, e.title, e.city, e.status
HAVING COUNT(s.session_id) = (
    SELECT MAX(session_count)
    FROM (
        SELECT COUNT(s2.session_id) as session_count
        FROM Events e2
        LEFT JOIN Sessions s2 ON e2.event_id = s2.event_id
        GROUP BY e2.event_id
    ) max_sessions
);

-- 13. Average Rating per City
-- Calculate the average feedback rating of events conducted in each city.
SELECT 
    e.city,
    COUNT(f.feedback_id) as total_feedback,
    ROUND(AVG(f.rating), 2) as avg_rating
FROM Events e
JOIN Feedback f ON e.event_id = f.event_id
GROUP BY e.city
ORDER BY avg_rating DESC;

-- 14. Most Registered Events
-- List top 3 events based on the total number of user registrations.
SELECT 
    e.title,
    e.city,
    e.status,
    COUNT(r.registration_id) as registration_count
FROM Events e
JOIN Registrations r ON e.event_id = r.event_id
GROUP BY e.event_id, e.title, e.city, e.status
ORDER BY registration_count DESC
LIMIT 3;

-- 15. Event Session Time Conflict
-- Identify overlapping sessions within the same event.
SELECT 
    e.title as event_name,
    s1.title as session1,
    s1.start_time as session1_start,
    s1.end_time as session1_end,
    s2.title as session2,
    s2.start_time as session2_start,
    s2.end_time as session2_end
FROM Events e
JOIN Sessions s1 ON e.event_id = s1.event_id
JOIN Sessions s2 ON e.event_id = s2.event_id
WHERE s1.session_id < s2.session_id
    AND (
        (s1.start_time < s2.end_time AND s1.end_time > s2.start_time)
    );

-- 16. Unregistered Active Users
-- Find users who created an account in the last 30 days but haven't registered for any events.
SELECT 
    u.user_id,
    u.full_name,
    u.email,
    u.city,
    u.registration_date
FROM Users u
LEFT JOIN Registrations r ON u.user_id = r.user_id
WHERE u.registration_date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)
    AND r.user_id IS NULL
ORDER BY u.registration_date DESC;

-- 17. Multi-Session Speakers
-- Identify speakers who are handling more than one session across all events.
SELECT 
    speaker_name,
    COUNT(session_id) as session_count,
    COUNT(DISTINCT event_id) as events_count
FROM Sessions
GROUP BY speaker_name
HAVING COUNT(session_id) > 1
ORDER BY session_count DESC;

-- 18. Resource Availability Check
-- List all events that do not have any resources uploaded.
SELECT 
    e.event_id,
    e.title,
    e.city,
    e.status
FROM Events e
LEFT JOIN Resources r ON e.event_id = r.event_id
WHERE r.event_id IS NULL
ORDER BY e.start_date;

-- 19. Completed Events with Feedback Summary
-- For completed events, show total registrations and average feedback rating.
SELECT 
    e.title,
    e.city,
    COUNT(DISTINCT r.registration_id) as total_registrations,
    COUNT(f.feedback_id) as feedback_count,
    ROUND(AVG(f.rating), 2) as avg_rating
FROM Events e
LEFT JOIN Registrations r ON e.event_id = r.event_id
LEFT JOIN Feedback f ON e.event_id = f.event_id
WHERE e.status = 'completed'
GROUP BY e.event_id, e.title, e.city
ORDER BY avg_rating DESC;

-- 20. User Engagement Index
-- For each user, calculate how many events they attended and how many feedbacks they submitted.
SELECT 
    u.full_name,
    u.email,
    u.city,
    COUNT(DISTINCT r.event_id) as events_registered,
    COUNT(f.feedback_id) as feedback_submitted,
    ROUND(
        CASE 
            WHEN COUNT(DISTINCT r.event_id) = 0 THEN 0
            ELSE COUNT(f.feedback_id) / COUNT(DISTINCT r.event_id) * 100
        END, 2
    ) as engagement_percentage
FROM Users u
LEFT JOIN Registrations r ON u.user_id = r.user_id
LEFT JOIN Feedback f ON u.user_id = f.user_id
GROUP BY u.user_id, u.full_name, u.email, u.city
ORDER BY engagement_percentage DESC;

-- 21. Top Feedback Providers
-- List top 5 users who have submitted the most feedback entries.
SELECT 
    u.full_name,
    u.email,
    u.city,
    COUNT(f.feedback_id) as feedback_count,
    ROUND(AVG(f.rating), 2) as avg_rating_given
FROM Users u
JOIN Feedback f ON u.user_id = f.user_id
GROUP BY u.user_id, u.full_name, u.email, u.city
ORDER BY feedback_count DESC
LIMIT 5;

-- 22. Duplicate Registrations Check
-- Detect if a user has been registered more than once for the same event.
SELECT 
    u.full_name,
    u.email,
    e.title,
    COUNT(r.registration_id) as registration_count
FROM Users u
JOIN Registrations r ON u.user_id = r.user_id
JOIN Events e ON r.event_id = e.event_id
GROUP BY u.user_id, e.event_id, u.full_name, u.email, e.title
HAVING COUNT(r.registration_id) > 1
ORDER BY registration_count DESC;

-- 23. Registration Trends
-- Show a month-wise registration count trend over the past 12 months.
SELECT 
    YEAR(r.registration_date) as year,
    MONTH(r.registration_date) as month,
    MONTHNAME(r.registration_date) as month_name,
    COUNT(r.registration_id) as registration_count
FROM Registrations r
WHERE r.registration_date >= DATE_SUB(CURDATE(), INTERVAL 12 MONTH)
GROUP BY YEAR(r.registration_date), MONTH(r.registration_date)
ORDER BY year DESC, month DESC;

-- 24. Average Session Duration per Event
-- Compute the average duration (in minutes) of sessions in each event.
SELECT 
    e.title,
    e.city,
    COUNT(s.session_id) as total_sessions,
    ROUND(AVG(TIMESTAMPDIFF(MINUTE, s.start_time, s.end_time)), 2) as avg_duration_minutes
FROM Events e
LEFT JOIN Sessions s ON e.event_id = s.event_id
GROUP BY e.event_id, e.title, e.city
HAVING COUNT(s.session_id) > 0
ORDER BY avg_duration_minutes DESC;

-- 25. Events Without Sessions
-- List all events that currently have no sessions scheduled under them.
SELECT 
    e.event_id,
    e.title,
    e.city,
    e.status,
    e.start_date,
    e.end_date
FROM Events e
LEFT JOIN Sessions s ON e.event_id = s.event_id
WHERE s.event_id IS NULL
ORDER BY e.start_date;

-- ===============================================
-- Additional Utility Queries for Data Verification
-- ===============================================

-- Quick data overview
SELECT 'Users' as table_name, COUNT(*) as record_count FROM Users
UNION ALL
SELECT 'Events', COUNT(*) FROM Events
UNION ALL
SELECT 'Sessions', COUNT(*) FROM Sessions
UNION ALL
SELECT 'Registrations', COUNT(*) FROM Registrations
UNION ALL
SELECT 'Feedback', COUNT(*) FROM Feedback
UNION ALL
SELECT 'Resources', COUNT(*) FROM Resources;

-- Event status distribution
SELECT 
    status,
    COUNT(*) as count,
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM Events), 2) as percentage
FROM Events
GROUP BY status;