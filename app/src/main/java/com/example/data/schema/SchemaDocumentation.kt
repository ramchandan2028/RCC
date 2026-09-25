package com.example.data.schema

object SchemaDocumentation {

    val SQL_DDL_POSTGRES = """
-- =========================================================
-- Study Management & Learning App - PostgreSQL Relational DDL
-- =========================================================

-- 1. User Profiles
CREATE TABLE user_profiles (
    user_id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    course_or_class VARCHAR(120) NOT NULL,
    daily_study_goal_hours NUMERIC(4, 2) NOT NULL DEFAULT 4.0 CHECK (daily_study_goal_hours >= 0),
    streak_count INT NOT NULL DEFAULT 0 CHECK (streak_count >= 0),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2. Subjects (Course Hierarchy)
CREATE TABLE subjects (
    subject_id BIGSERIAL PRIMARY KEY,
    subject_name VARCHAR(150) NOT NULL,
    user_id VARCHAR(64) NOT NULL,
    color_hex VARCHAR(9) DEFAULT '#2563EB',
    icon_name VARCHAR(50) DEFAULT 'MenuBook',
    CONSTRAINT fk_subjects_user FOREIGN KEY (user_id) 
        REFERENCES user_profiles(user_id) ON DELETE CASCADE
);
CREATE INDEX idx_subjects_user ON subjects(user_id);

-- 3. Chapters / Topics
CREATE TABLE chapters (
    chapter_id BIGSERIAL PRIMARY KEY,
    subject_id BIGINT NOT NULL,
    chapter_title VARCHAR(200) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'pending' 
        CHECK (status IN ('pending', 'completed')),
    order_index INT NOT NULL DEFAULT 0,
    CONSTRAINT fk_chapters_subject FOREIGN KEY (subject_id) 
        REFERENCES subjects(subject_id) ON DELETE CASCADE
);
CREATE INDEX idx_chapters_subject ON chapters(subject_id);

-- 4. Resources
CREATE TABLE resources (
    resource_id BIGSERIAL PRIMARY KEY,
    chapter_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    type VARCHAR(30) NOT NULL 
        CHECK (type IN ('PDF', 'text note', 'video link', 'formula sheet')),
    file_url_or_content TEXT NOT NULL,
    CONSTRAINT fk_resources_chapter FOREIGN KEY (chapter_id) 
        REFERENCES chapters(chapter_id) ON DELETE CASCADE
);
CREATE INDEX idx_resources_chapter ON resources(chapter_id);

-- 5. Questions (Practice & Quiz)
CREATE TABLE questions (
    question_id BIGSERIAL PRIMARY KEY,
    chapter_id BIGINT NOT NULL,
    question_text TEXT NOT NULL,
    option_a TEXT NOT NULL,
    option_b TEXT NOT NULL,
    option_c TEXT NOT NULL,
    option_d TEXT NOT NULL,
    correct_option CHAR(1) NOT NULL CHECK (correct_option IN ('A', 'B', 'C', 'D')),
    explanation TEXT NOT NULL,
    CONSTRAINT fk_questions_chapter FOREIGN KEY (chapter_id) 
        REFERENCES chapters(chapter_id) ON DELETE CASCADE
);
CREATE INDEX idx_questions_chapter ON questions(chapter_id);

-- 6. Quiz History
CREATE TABLE quiz_history (
    result_id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    chapter_id BIGINT,
    subject_name VARCHAR(150) NOT NULL,
    quiz_score INT NOT NULL CHECK (quiz_score >= 0),
    total_questions INT NOT NULL CHECK (total_questions > 0),
    timestamp TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_quiz_user FOREIGN KEY (user_id) 
        REFERENCES user_profiles(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_quiz_chapter FOREIGN KEY (chapter_id) 
        REFERENCES chapters(chapter_id) ON DELETE SET NULL
);
CREATE INDEX idx_quiz_user ON quiz_history(user_id);
CREATE INDEX idx_quiz_timestamp ON quiz_history(timestamp DESC);

-- 7. Study Tasks (Productivity)
CREATE TABLE study_tasks (
    task_id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    task_title VARCHAR(255) NOT NULL,
    due_date TIMESTAMPTZ NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'pending' 
        CHECK (status IN ('pending', 'done')),
    priority VARCHAR(10) NOT NULL DEFAULT 'medium' 
        CHECK (priority IN ('low', 'medium', 'high')),
    CONSTRAINT fk_tasks_user FOREIGN KEY (user_id) 
        REFERENCES user_profiles(user_id) ON DELETE CASCADE
);
CREATE INDEX idx_tasks_user ON study_tasks(user_id);
CREATE INDEX idx_tasks_due ON study_tasks(due_date ASC);

-- 8. Pomodoro / Focus Sessions
CREATE TABLE focus_sessions (
    session_id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    subject_id BIGINT NOT NULL,
    duration_minutes INT NOT NULL CHECK (duration_minutes > 0),
    date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sessions_user FOREIGN KEY (user_id) 
        REFERENCES user_profiles(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_sessions_subject FOREIGN KEY (subject_id) 
        REFERENCES subjects(subject_id) ON DELETE CASCADE
);
CREATE INDEX idx_sessions_user ON focus_sessions(user_id);
CREATE INDEX idx_sessions_subject ON focus_sessions(subject_id);

-- 9. Bookmarks
CREATE TABLE bookmarks (
    bookmark_id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    item_type VARCHAR(20) NOT NULL 
        CHECK (item_type IN ('chapter', 'resource', 'question')),
    item_id BIGINT NOT NULL,
    item_title VARCHAR(255) DEFAULT '',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_bookmarks_user FOREIGN KEY (user_id) 
        REFERENCES user_profiles(user_id) ON DELETE CASCADE,
    CONSTRAINT uq_user_bookmark UNIQUE (user_id, item_type, item_id)
);
CREATE INDEX idx_bookmarks_user ON bookmarks(user_id);
""".trimIndent()

    val NOSQL_STRUCTURE = """
// =========================================================
// Study Management & Learning App - NoSQL (Firestore) Schema
// =========================================================

/*
 Root Collections Hierarchy:
 users/{userId} (Document)
   ├── subjects/{subjectId} (Subcollection)
   │     └── chapters/{chapterId} (Subcollection)
   │           ├── resources/{resourceId} (Subcollection)
   │           └── questions/{questionId} (Subcollection)
   ├── study_tasks/{taskId} (Subcollection)
   ├── focus_sessions/{sessionId} (Subcollection)
   ├── quiz_history/{resultId} (Subcollection)
   └── bookmarks/{bookmarkId} (Subcollection)
*/

// Collection: users
// Document: users/{userId}
{
  "user_id": "usr_9981",
  "name": "Convent RCC",
  "email": "convent.rcc@university.edu",
  "course_or_class": "BCA",
  "daily_study_goal_hours": 4.0,
  "streak_count": 14,
  "created_at": "2026-08-15T09:00:00Z"
}

// Subcollection: users/{userId}/subjects/{subjectId}
{
  "subject_id": "subj_bca_101",
  "subject_name": "Basics of Android",
  "user_id": "usr_9981",
  "color_hex": "#10B981",
  "icon_name": "Android"
}

// Subcollection: .../chapters/{chapterId}
{
  "chapter_id": "chap_and_01",
  "subject_id": "subj_bca_101",
  "chapter_title": "Activity Lifecycle & Intents",
  "status": "completed", // "pending" | "completed"
  "order_index": 1
}

// Subcollection: .../resources/{resourceId}
{
  "resource_id": "res_and_01",
  "chapter_id": "chap_and_01",
  "title": "Android Activity Lifecycle Reference Sheet",
  "type": "formula sheet", // "PDF" | "text note" | "video link" | "formula sheet"
  "file_url_or_content": "Activity Created: onCreate() -> onStart() -> onResume()"
}

// Subcollection: .../questions/{questionId}
{
  "question_id": "q_hash_01",
  "chapter_id": "chap_dp01",
  "question_text": "What is the worst-case time complexity of inserting into a Hash Table with collisions?",
  "options": {
    "A": "O(1)",
    "B": "O(log n)",
    "C": "O(n)",
    "D": "O(n log n)"
  },
  "correct_option": "C",
  "explanation": "When all keys hash to the same bucket, the linked list chain degenerates to O(n)."
}

// Subcollection: users/{userId}/study_tasks/{taskId}
{
  "task_id": "task_202",
  "user_id": "usr_9981",
  "task_title": "Practice 10 LeetCode Medium DP Problems",
  "due_date": "2026-09-27T18:00:00Z",
  "status": "pending", // "pending" | "done"
  "priority": "high"
}

// Subcollection: users/{userId}/focus_sessions/{sessionId}
{
  "session_id": "sess_881",
  "user_id": "usr_9981",
  "subject_id": "subj_cs101",
  "duration_minutes": 50,
  "date": "2026-09-25T14:30:00Z"
}

// Subcollection: users/{userId}/quiz_history/{resultId}
{
  "result_id": "res_501",
  "user_id": "usr_9981",
  "chapter_id": "chap_dp01",
  "subject_name": "Data Structures & Algorithms",
  "quiz_score": 5,
  "total_questions": 5,
  "timestamp": "2026-09-24T16:20:00Z"
}

// Subcollection: users/{userId}/bookmarks/{bookmarkId}
{
  "bookmark_id": "bm_771",
  "user_id": "usr_9981",
  "item_type": "resource", // "chapter" | "resource" | "question"
  "item_id": 1,
  "item_title": "Time & Space Complexity Cheatsheet",
  "created_at": "2026-09-22T10:15:00Z"
}
""".trimIndent()

    val ER_EXPLANATION = """
### Entity-Relationship Architecture Explanation

1. **User Profile (Central Anchor)**
   - `user_profiles` is the root entity.
   - **1-to-Many with Subjects**: A student manages multiple courses/subjects (`user_profiles.user_id -> subjects.user_id`).
   - **1-to-Many with Study Tasks**: Tasks belong directly to the user for scheduling and time management.
   - **1-to-Many with Focus Sessions**: Tracks Pomodoro study minutes logged by the user.
   - **1-to-Many with Quiz History**: Stores test performance and historical trends over time.
   - **1-to-Many with Bookmarks**: Holds quick shortcuts to chapters, questions, and resources.

2. **Course & Content Hierarchy (Strict Multi-level Tree)**
   - **Subjects -> Chapters (1 : N)**: A subject (e.g., "Operating Systems") contains multiple chapters/topics. Deleting a subject cascades to delete all its chapters.
   - **Chapters -> Resources (1 : N)**: A chapter groups related study materials (PDFs, formula sheets, markdown text notes, and video links).
   - **Chapters -> Questions (1 : N)**: Chapters serve as the question bank boundary, allowing students to take chapter-specific or comprehensive subject quizzes.

3. **Practice & Quiz (Evaluation Layer)**
   - `questions` belong to a `chapter_id`.
   - `quiz_history` records the user's attempts (`user_id`), linked to `chapter_id` with `ON DELETE SET NULL`, preserving performance records even if a chapter is archived.

4. **Productivity & Tracking (Workflow Layer)**
   - `study_tasks` provides actionable to-dos with due dates and priorities.
   - `focus_sessions` links `user_id` and `subject_id`, allowing aggregation of study time per subject vs. user daily study goals.
   - `bookmarks` uses polymorphic referencing (`item_type`: chapter/resource/question + `item_id`) with a UNIQUE constraint `(user_id, item_type, item_id)` to prevent duplicates.
""".trimIndent()

    val DUMMY_JSON_PAYLOADS = """
{
  "user_profile": {
    "user_id": "usr_9981",
    "name": "Convent RCC",
    "email": "convent.rcc@university.edu",
    "course_or_class": "BCA",
    "daily_study_goal_hours": 4.0,
    "streak_count": 14,
    "created_at": "2026-08-15T09:00:00Z"
  },
  "subject": {
    "subject_id": 1,
    "subject_name": "Basics of Android",
    "user_id": "usr_9981",
    "color_hex": "#10B981",
    "icon_name": "Android"
  },
  "chapter": {
    "chapter_id": 1,
    "subject_id": 1,
    "chapter_title": "Activity Lifecycle & Intents",
    "status": "completed",
    "order_index": 1
  },
  "resource": {
    "resource_id": 1,
    "chapter_id": 1,
    "title": "Time & Space Complexity Cheatsheet",
    "type": "formula sheet",
    "file_url_or_content": "Array Access: O(1)\nHash Search: Avg O(1), Worst O(n)"
  },
  "question": {
    "question_id": 1,
    "chapter_id": 1,
    "question_text": "What is the worst-case time complexity of inserting into a Hash Table with separate chaining when all keys collide?",
    "options": {
      "A": "O(1)",
      "B": "O(log n)",
      "C": "O(n)",
      "D": "O(n log n)"
    },
    "correct_option": "C",
    "explanation": "When all keys hash to the same bucket, searching and inserting degenerates into linear traversal of length n."
  },
  "quiz_history": {
    "result_id": 101,
    "user_id": "usr_9981",
    "chapter_id": 1,
    "subject_name": "Data Structures & Algorithms",
    "quiz_score": 5,
    "total_questions": 5,
    "timestamp": 1790327000000
  },
  "study_task": {
    "task_id": 1,
    "user_id": "usr_9981",
    "task_title": "Practice 10 LeetCode Medium DP Problems",
    "due_date": 1790500000000,
    "status": "pending",
    "priority": "high"
  },
  "focus_session": {
    "session_id": 1,
    "user_id": "usr_9981",
    "subject_id": 1,
    "duration_minutes": 50,
    "date": 1790310000000
  },
  "bookmark": {
    "bookmark_id": 1,
    "user_id": "usr_9981",
    "item_type": "resource",
    "item_id": 1,
    "item_title": "Time & Space Complexity Cheatsheet",
    "created_at": 1790070000000
  }
}
""".trimIndent()
}
