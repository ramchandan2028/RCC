package com.example.data.local

import com.example.data.local.dao.StudyDao
import com.example.data.local.entity.Bookmark
import com.example.data.local.entity.Chapter
import com.example.data.local.entity.FocusSession
import com.example.data.local.entity.QuizQuestion
import com.example.data.local.entity.QuizResult
import com.example.data.local.entity.StudyResource
import com.example.data.local.entity.StudyTask
import com.example.data.local.entity.Subject
import com.example.data.local.entity.UserProfile

object DatabaseSeeder {

    suspend fun seedIfNeeded(dao: StudyDao) {
        val existing = dao.getUserProfileDirect()
        if (existing == null || existing.courseOrClass != "BCA") {
            seedAll(dao)
        }
    }

    suspend fun seedAll(dao: StudyDao) {
        val now = System.currentTimeMillis()
        val oneDayMillis = 24L * 60 * 60 * 1000

        // 1. User Profile
        val user = UserProfile(
            userId = "usr_default_01",
            name = "Convent RCC",
            email = "convent.rcc@university.edu",
            courseOrClass = "BCA",
            dailyStudyGoalHours = 4.0,
            streakCount = 14,
            createdAt = now - (30L * oneDayMillis)
        )
        dao.insertOrUpdateProfile(user)

        // 2. Subjects (BCA Curriculum)
        val subjects = listOf(
            Subject(
                subjectId = 1,
                subjectName = "Basics of Android",
                userId = "usr_default_01",
                colorHex = "#10B981",
                iconName = "Android"
            ),
            Subject(
                subjectId = 2,
                subjectName = "Web Designing-II",
                userId = "usr_default_01",
                colorHex = "#0EA5E9",
                iconName = "Language"
            ),
            Subject(
                subjectId = 3,
                subjectName = "Data Science Implementation using Python",
                userId = "usr_default_01",
                colorHex = "#F59E0B",
                iconName = "Analytics"
            ),
            Subject(
                subjectId = 4,
                subjectName = "Software Engineering",
                userId = "usr_default_01",
                colorHex = "#8B5CF6",
                iconName = "Engineering"
            ),
            Subject(
                subjectId = 5,
                subjectName = "Understanding .NET Framework with C#",
                userId = "usr_default_01",
                colorHex = "#2563EB",
                iconName = "Terminal"
            )
        )
        dao.insertSubjects(subjects)

        // 3. Chapters
        val chapters = listOf(
            // Basics of Android
            Chapter(
                chapterId = 1,
                subjectId = 1,
                chapterTitle = "Activity Lifecycle & Intents",
                status = "completed",
                orderIndex = 1
            ),
            Chapter(
                chapterId = 2,
                subjectId = 1,
                chapterTitle = "Jetpack Compose UI & State Management",
                status = "completed",
                orderIndex = 2
            ),
            Chapter(
                chapterId = 3,
                subjectId = 1,
                chapterTitle = "Room Database & Kotlin Coroutines",
                status = "pending",
                orderIndex = 3
            ),
            // Web Designing-II
            Chapter(
                chapterId = 4,
                subjectId = 2,
                chapterTitle = "CSS Flexbox & Responsive Grid Layouts",
                status = "completed",
                orderIndex = 1
            ),
            Chapter(
                chapterId = 5,
                subjectId = 2,
                chapterTitle = "JavaScript ES6+, DOM Manipulation & Events",
                status = "completed",
                orderIndex = 2
            ),
            Chapter(
                chapterId = 6,
                subjectId = 2,
                chapterTitle = "Async JS, Fetch API & JSON Handling",
                status = "pending",
                orderIndex = 3
            ),
            // Data Science Implementation using Python
            Chapter(
                chapterId = 7,
                subjectId = 3,
                chapterTitle = "NumPy Arrays & Mathematical Operations",
                status = "completed",
                orderIndex = 1
            ),
            Chapter(
                chapterId = 8,
                subjectId = 3,
                chapterTitle = "Pandas DataFrames, Data Cleaning & GroupBy",
                status = "completed",
                orderIndex = 2
            ),
            Chapter(
                chapterId = 9,
                subjectId = 3,
                chapterTitle = "Data Visualization with Matplotlib & Seaborn",
                status = "pending",
                orderIndex = 3
            ),
            // Software Engineering
            Chapter(
                chapterId = 10,
                subjectId = 4,
                chapterTitle = "SDLC Models & Agile Scrum Methodology",
                status = "completed",
                orderIndex = 1
            ),
            Chapter(
                chapterId = 11,
                subjectId = 4,
                chapterTitle = "Requirement Engineering, SRS & UML Diagrams",
                status = "completed",
                orderIndex = 2
            ),
            Chapter(
                chapterId = 12,
                subjectId = 4,
                chapterTitle = "Software Testing: Unit, Integration & System Testing",
                status = "pending",
                orderIndex = 3
            ),
            // Understanding .NET Framework with C#
            Chapter(
                chapterId = 13,
                subjectId = 5,
                chapterTitle = "CLR Architecture, Assemblies & CTS",
                status = "completed",
                orderIndex = 1
            ),
            Chapter(
                chapterId = 14,
                subjectId = 5,
                chapterTitle = "C# OOP, Interfaces, Delegates & Events",
                status = "completed",
                orderIndex = 2
            ),
            Chapter(
                chapterId = 15,
                subjectId = 5,
                chapterTitle = "LINQ Queries & Entity Framework Core",
                status = "pending",
                orderIndex = 3
            )
        )
        dao.insertChapters(chapters)

        // 4. Resources
        val resources = listOf(
            StudyResource(
                resourceId = 1,
                chapterId = 1,
                title = "Android Activity Lifecycle Reference Sheet",
                type = "formula sheet",
                fileUrlOrContent = "• Activity Created: onCreate() -> onStart() -> onResume()\n• Activity Paused: onPause() (visible but partially obscured)\n• Activity Stopped: onStop() (hidden in background)\n• Activity Destroyed: onDestroy()\n• Activity Restarted: onRestart() -> onStart() -> onResume()"
            ),
            StudyResource(
                resourceId = 2,
                chapterId = 4,
                title = "CSS Flexbox & Grid Core Cheatsheet",
                type = "formula sheet",
                fileUrlOrContent = "/* Flexbox Container */\ndisplay: flex;\nflex-direction: row | column;\njustify-content: flex-start | center | space-between | space-around;\nalign-items: stretch | center | flex-start | flex-end;\n\n/* Grid Container */\ndisplay: grid;\ngrid-template-columns: repeat(auto-fit, minmax(250px, 1fr));\ngap: 16px;"
            ),
            StudyResource(
                resourceId = 3,
                chapterId = 7,
                title = "NumPy & Pandas Quick Reference Guide",
                type = "formula sheet",
                fileUrlOrContent = "# NumPy Operations\nimport numpy as np\narr = np.array([1, 2, 3])\nmean_val = np.mean(arr)\nstd_val = np.std(arr)\n\n# Pandas Operations\nimport pandas as pd\ndf = pd.read_csv('data.csv')\nclean_df = df.dropna().drop_duplicates()\ngrouped = clean_df.groupby('category').mean()"
            ),
            StudyResource(
                resourceId = 4,
                chapterId = 10,
                title = "Agile Scrum Framework & Ceremonies",
                type = "text note",
                fileUrlOrContent = "• Scrum Roles: Product Owner, Scrum Master, Development Team\n• Artifacts: Product Backlog, Sprint Backlog, Increment (Definition of Done)\n• Ceremonies:\n  1. Sprint Planning (defining sprint goal)\n  2. Daily Scrum (15-min standup on blockers)\n  3. Sprint Review (demoing increment)\n  4. Sprint Retrospective (team process improvements)"
            ),
            StudyResource(
                resourceId = 5,
                chapterId = 13,
                title = ".NET Common Language Runtime (CLR) Architecture",
                type = "formula sheet",
                fileUrlOrContent = "• C# Compilation Pipeline:\n  Source Code (.cs) -> Roslyn Compiler -> CIL (Common Intermediate Language) + Metadata -> JIT Compiler (Just-In-Time) -> Native CPU Machine Code\n\n• Core CLR Services:\n  1. Memory Management (Garbage Collector with Gen 0, 1, 2)\n  2. Type Safety Enforcement (CTS - Common Type System)\n  3. Exception Handling Engine\n  4. Multi-threading and task scheduling"
            )
        )
        dao.insertResources(resources)

        // 5. Questions
        val questions = listOf(
            QuizQuestion(
                questionId = 1,
                chapterId = 1,
                questionText = "Which Android Activity callback is called immediately when the Activity enters the foreground and becomes interactive with the user?",
                optionA = "onCreate()",
                optionB = "onStart()",
                optionC = "onResume()",
                optionD = "onRestart()",
                correctOption = "C",
                explanation = "onResume() is invoked when the activity starts interacting with the user. At this point, the activity is at the top of the activity stack and captures all user inputs."
            ),
            QuizQuestion(
                questionId = 2,
                chapterId = 4,
                questionText = "In CSS Flexbox, which property is used to align flex items along the cross axis?",
                optionA = "justify-content",
                optionB = "align-items",
                optionC = "flex-direction",
                optionD = "align-content",
                correctOption = "B",
                explanation = "align-items controls alignment along the cross axis (perpendicular to the main axis), whereas justify-content aligns items along the main axis."
            ),
            QuizQuestion(
                questionId = 3,
                chapterId = 8,
                questionText = "Which Pandas function is used to remove missing or NaN values from a DataFrame?",
                optionA = "df.fillna()",
                optionB = "df.dropna()",
                optionC = "df.drop_duplicates()",
                optionD = "df.isnull()",
                correctOption = "B",
                explanation = "dropna() removes rows or columns with missing (NaN) values, while fillna() replaces missing values with a specified default."
            ),
            QuizQuestion(
                questionId = 4,
                chapterId = 10,
                questionText = "Which Software Engineering SDLC model executes iterative development cycles called 'Sprints'?",
                optionA = "Waterfall Model",
                optionB = "V-Model",
                optionC = "Agile Scrum",
                optionD = "Big Bang Model",
                correctOption = "C",
                explanation = "Agile Scrum organizes work into short, fixed-duration iterations called Sprints (usually 2 to 4 weeks long)."
            ),
            QuizQuestion(
                questionId = 5,
                chapterId = 13,
                questionText = "In the .NET Framework, what is the role of the Common Language Runtime (CLR)?",
                optionA = "To design visual UI forms",
                optionB = "Execution engine that provides garbage collection, type safety, and JIT compilation",
                optionC = "A database query engine",
                optionD = "A web server host for IIS",
                correctOption = "B",
                explanation = "The CLR is the core execution engine of .NET that compiles intermediate language (CIL) to native machine code via JIT and manages memory through the Garbage Collector."
            ),
            QuizQuestion(
                questionId = 6,
                chapterId = 14,
                questionText = "In C#, what is a 'delegate'?",
                optionA = "A keyword to inherit multiple classes",
                optionB = "A type-safe reference pointer to a method with a specific signature",
                optionC = "A database migration script",
                optionD = "An unmanaged C++ memory block",
                correctOption = "B",
                explanation = "A delegate in C# is a reference type that holds a reference to a method with a matching signature and return type, enabling event handling and callback functions."
            )
        )
        dao.insertQuestions(questions)

        // 6. Quiz History
        val quizHistory = listOf(
            QuizResult(
                resultId = 1,
                userId = "usr_default_01",
                chapterId = 1,
                subjectName = "Basics of Android",
                quizScore = 5,
                totalQuestions = 5,
                timestamp = now - (2L * oneDayMillis)
            ),
            QuizResult(
                resultId = 2,
                userId = "usr_default_01",
                chapterId = 7,
                subjectName = "Data Science Implementation using Python",
                quizScore = 4,
                totalQuestions = 5,
                timestamp = now - (1L * oneDayMillis)
            )
        )
        dao.insertQuizResults(quizHistory)

        // 7. Study Tasks
        val studyTasks = listOf(
            StudyTask(
                taskId = 1,
                userId = "usr_default_01",
                taskTitle = "Build Android Activity lifecycle state visualizer in Compose",
                dueDate = now + (2L * oneDayMillis),
                status = "pending",
                priority = "high"
            ),
            StudyTask(
                taskId = 2,
                userId = "usr_default_01",
                taskTitle = "Practice NumPy vectorization and Pandas cleaning queries",
                dueDate = now + (1L * oneDayMillis),
                status = "pending",
                priority = "high"
            ),
            StudyTask(
                taskId = 3,
                userId = "usr_default_01",
                taskTitle = "Create responsive CSS Grid & Flexbox navigation header",
                dueDate = now - (1L * oneDayMillis),
                status = "done",
                priority = "medium"
            ),
            StudyTask(
                taskId = 4,
                userId = "usr_default_01",
                taskTitle = "Draft SRS functional requirements document for BCA project",
                dueDate = now + (3L * oneDayMillis),
                status = "pending",
                priority = "medium"
            ),
            StudyTask(
                taskId = 5,
                userId = "usr_default_01",
                taskTitle = "Write C# delegate and LINQ filter demonstration code",
                dueDate = now + (4L * oneDayMillis),
                status = "pending",
                priority = "low"
            )
        )
        dao.insertTasks(studyTasks)

        // 8. Focus Sessions
        val focusSessions = listOf(
            FocusSession(
                sessionId = 1,
                userId = "usr_default_01",
                subjectId = 1, // Basics of Android
                durationMinutes = 50,
                date = now - (4L * 60 * 60 * 1000)
            ),
            FocusSession(
                sessionId = 2,
                userId = "usr_default_01",
                subjectId = 3, // Data Science with Python
                durationMinutes = 25,
                date = now - (2L * 60 * 60 * 1000)
            ),
            FocusSession(
                sessionId = 3,
                userId = "usr_default_01",
                subjectId = 2, // Web Designing-II
                durationMinutes = 45,
                date = now - oneDayMillis
            )
        )
        dao.insertFocusSessions(focusSessions)

        // 9. Bookmarks
        val bookmarks = listOf(
            Bookmark(
                bookmarkId = 1,
                userId = "usr_default_01",
                itemType = "resource",
                itemId = 1,
                itemTitle = "Android Activity Lifecycle Reference Sheet",
                createdAt = now - (3L * oneDayMillis)
            ),
            Bookmark(
                bookmarkId = 2,
                userId = "usr_default_01",
                itemType = "resource",
                itemId = 2,
                itemTitle = "CSS Flexbox & Grid Core Cheatsheet",
                createdAt = now - (2L * oneDayMillis)
            ),
            Bookmark(
                bookmarkId = 3,
                userId = "usr_default_01",
                itemType = "question",
                itemId = 5,
                itemTitle = "Question: .NET CLR Architecture & Garbage Collection",
                createdAt = now - (1L * oneDayMillis)
            )
        )
        for (b in bookmarks) {
            dao.insertBookmark(b)
        }
    }
}
