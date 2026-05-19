package com.skillsphere.skillsphere.config;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.skillsphere.skillsphere.enrollment.entity.Enrollment;
import com.skillsphere.skillsphere.enrollment.repository.EnrollmentRepository;
import com.skillsphere.skillsphere.learning.entity.Category;
import com.skillsphere.skillsphere.learning.entity.Course;
import com.skillsphere.skillsphere.learning.entity.CourseSection;
import com.skillsphere.skillsphere.learning.entity.Lesson;
import com.skillsphere.skillsphere.learning.enums.LessonType;
import com.skillsphere.skillsphere.learning.repository.CategoryRepository;
import com.skillsphere.skillsphere.learning.repository.CourseRepository;
import com.skillsphere.skillsphere.learning.repository.CourseSectionRepository;
import com.skillsphere.skillsphere.learning.repository.LessonRepository;
import com.skillsphere.skillsphere.progress.entity.LessonProgress;
import com.skillsphere.skillsphere.progress.repository.LessonProgressRepository;
import com.skillsphere.skillsphere.review.entity.Review;
import com.skillsphere.skillsphere.review.repository.ReviewRepository;
import com.skillsphere.skillsphere.user.entity.Instructor;
import com.skillsphere.skillsphere.user.entity.Role;
import com.skillsphere.skillsphere.user.entity.User;
import com.skillsphere.skillsphere.user.repository.InstructorRepository;
import com.skillsphere.skillsphere.user.repository.UserRepository;

@Component
@Order(2)
public class CourseDataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseSectionRepository sectionRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private LessonProgressRepository lessonProgressRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (courseRepository.count() > 0) {
            return; // Already seeded
        }

        // 1. Seed Instructors & their Users
        Instructor vikram = createInstructor("Dr. Vikram Rao", "vikram@skillsphere.com", "Principal Data Architect", "A pioneer in relational schemas and zero-downtime migrations with 15+ years of database engineering experience.", 15);
        Instructor sneha = createInstructor("Sneha Kulkarni", "sneha@skillsphere.com", "Senior Data Engineer", "Expert in streaming pipelines and Apache Airflow orchestration with massive scale workloads.", 10);
        Instructor ananya = createInstructor("Ananya Iyer", "ananya@skillsphere.com", "Security Engineer", "Certified security expert focusing on OWASP top 10, threat modeling, and secure software development lifecycle.", 8);
        Instructor ravi = createInstructor("Ravi Menon", "ravi@skillsphere.com", "Lead Data Scientist", "Passionate educator specializing in inferential statistics, machine learning pipelines, and predictive analytics.", 12);
        Instructor kavya = createInstructor("Kavya Rao", "kavya@skillsphere.com", "Engineering Manager", "Leader and coach helping developers transition to leadership roles using practical engineering governance.", 14);
        Instructor hr = createInstructor("HR Team", "hr@skillsphere.com", "People & Culture", "SkillSphere central people development team focused on organizational excellence and code of conduct.", 5);

        // 2. Fetch or seed Categories
        Category schemaCat = getOrCreateCategory("Schema", "schema");
        Category etlCat = getOrCreateCategory("ETL", "etl");
        Category securityCat = getOrCreateCategory("Cybersecurity", "cybersecurity");
        Category dsCat = getOrCreateCategory("Data Science", "data-science");
        Category leadCat = getOrCreateCategory("Leadership", "leadership");
        Category mandatoryCat = getOrCreateCategory("Mandatory", "mandatory");

        // 3. Seed Courses
        
        // Course 1: Schema Design Mastery
        Course schemaCourse = Course.builder()
                .slug("schema-design-mastery")
                .title("Schema Design Mastery")
                .tagline("Design resilient, scalable data schemas used in production systems.")
                .description("A deep-dive into the principles and patterns of schema design for relational and NoSQL databases. You'll learn normalization, denormalization trade-offs, indexing strategies, and how to evolve schemas without downtime.")
                .level("Advanced")
                .category(schemaCat)
                .instructor(vikram)
                .durationMinutes(720)
                .isMandatory(false)
                .isPublished(true)
                .enrolledCount(1842)
                .rating(4.8)
                .reviewCount(312)
                .certificateEnabled(true)
                .lastUpdatedAt(LocalDateTime.now().minusMonths(1))
                .whatYouLearn(List.of(
                        "Master all normal forms from 1NF through BCNF",
                        "Design schemas optimized for read-heavy vs write-heavy workloads",
                        "Implement safe zero-downtime schema migrations",
                        "Use foreign keys, constraints, and triggers effectively",
                        "Apply indexing strategies for complex query patterns",
                        "Model many-to-many and hierarchical relationships"
                ))
                .requirements(List.of(
                        "Basic SQL knowledge (SELECT, JOIN, GROUP BY)",
                        "Familiarity with at least one RDBMS (PostgreSQL, MySQL, etc.)"
                ))
                .build();
        schemaCourse = courseRepository.save(schemaCourse);

        // Course 2: Ingestion & ETL Pipeline
        Course etlCourse = Course.builder()
                .slug("etl-pipeline")
                .title("Ingestion & ETL Pipeline")
                .tagline("Build robust data ingestion pipelines from source to warehouse.")
                .description("Learn to design and operate production ETL pipelines that reliably move and transform data at scale. Covers batch and streaming patterns, error handling, monitoring, and orchestration with Apache Airflow.")
                .level("Intermediate")
                .category(etlCat)
                .instructor(sneha)
                .durationMinutes(900)
                .isMandatory(false)
                .isPublished(true)
                .enrolledCount(1240)
                .rating(4.6)
                .reviewCount(198)
                .certificateEnabled(true)
                .lastUpdatedAt(LocalDateTime.now().minusMonths(2))
                .whatYouLearn(List.of(
                        "Understand Extract, Transform, Load concepts end to end",
                        "Build batch and streaming pipelines",
                        "Handle errors, retries, and dead-letter queues",
                        "Orchestrate workflows with Apache Airflow",
                        "Monitor pipeline health and set up alerting"
                ))
                .requirements(List.of(
                        "Python programming basics",
                        "Basic SQL",
                        "Familiarity with cloud storage (S3 / GCS)"
                ))
                .build();
        etlCourse = courseRepository.save(etlCourse);

        // Course 3: Cybersecurity Fundamentals
        Course securityCourse = Course.builder()
                .slug("cybersecurity-fundamentals")
                .title("Cybersecurity Fundamentals")
                .tagline("Essential security concepts every developer and engineer must know.")
                .description("A beginner-friendly overview of cybersecurity principles covering threat modeling, common attack vectors, authentication best practices, and secure coding guidelines relevant for software teams.")
                .level("Beginner")
                .category(securityCat)
                .instructor(ananya)
                .durationMinutes(480)
                .isMandatory(false)
                .isPublished(true)
                .enrolledCount(2100)
                .rating(4.5)
                .reviewCount(156)
                .certificateEnabled(true)
                .lastUpdatedAt(LocalDateTime.now().minusMonths(1))
                .whatYouLearn(List.of(
                        "Understand the CIA triad and core security principles",
                        "Identify common attack vectors (OWASP Top 10)",
                        "Implement secure authentication and session management",
                        "Apply secure coding practices in daily development",
                        "Conduct basic threat modeling"
                ))
                .requirements(List.of(
                        "No prior security experience required",
                        "Basic understanding of web applications"
                ))
                .build();
        securityCourse = courseRepository.save(securityCourse);

        // Course 4: Data Science Foundations
        Course dsCourse = Course.builder()
                .slug("data-science-foundations")
                .title("Data Science Foundations")
                .tagline("Statistics, Python, and ML fundamentals for data practitioners.")
                .description("From descriptive statistics to building your first ML model, this course gives data engineers and analysts the toolkit to work confidently with data science workflows.")
                .level("Intermediate")
                .category(dsCat)
                .instructor(ravi)
                .durationMinutes(1080)
                .isMandatory(false)
                .isPublished(true)
                .enrolledCount(1560)
                .rating(4.7)
                .reviewCount(289)
                .certificateEnabled(true)
                .lastUpdatedAt(LocalDateTime.now().minusMonths(3))
                .whatYouLearn(List.of(
                        "Apply descriptive and inferential statistics",
                        "Manipulate data with Pandas and NumPy",
                        "Build and evaluate regression and classification models",
                        "Visualize data with Matplotlib and Seaborn",
                        "Understand overfitting, regularization, and cross-validation"
                ))
                .requirements(List.of(
                        "Python basics",
                        "Basic linear algebra is helpful but not required"
                ))
                .build();
        dsCourse = courseRepository.save(dsCourse);

        // Course 5: Leadership for Developers
        Course leadCourse = Course.builder()
                .slug("leadership-for-developers")
                .title("Leadership for Developers")
                .tagline("Grow from IC to technical leader with proven frameworks.")
                .description("Practical leadership skills for senior engineers and tech leads — how to run effective 1-on-1s, give feedback, manage up, build team culture, and grow other engineers.")
                .level("Intermediate")
                .category(leadCat)
                .instructor(kavya)
                .durationMinutes(360)
                .isMandatory(false)
                .isPublished(true)
                .enrolledCount(3200)
                .rating(4.9)
                .reviewCount(421)
                .certificateEnabled(true)
                .lastUpdatedAt(LocalDateTime.now().minusWeeks(2))
                .whatYouLearn(List.of(
                        "Lead without authority as a senior IC",
                        "Run effective meetings and 1-on-1s",
                        "Give actionable, timely feedback",
                        "Resolve conflicts and build psychological safety",
                        "Manage stakeholders and communicate up"
                ))
                .requirements(List.of(
                        "2+ years of software engineering experience"
                ))
                .build();
        leadCourse = courseRepository.save(leadCourse);

        // Course 6: Work Ethics & Compliance
        Course complianceCourse = Course.builder()
                .slug("work-ethics-compliance")
                .title("Work Ethics & Compliance")
                .tagline("Mandatory training covering company policies and professional standards.")
                .description("Company-wide mandatory training covering the code of conduct, workplace ethics, data privacy regulations, and professional standards expected of all employees.")
                .level("Mandatory")
                .category(mandatoryCat)
                .instructor(hr)
                .durationMinutes(120)
                .isMandatory(true)
                .isPublished(true)
                .enrolledCount(5400)
                .rating(4.2)
                .reviewCount(89)
                .certificateEnabled(true)
                .lastUpdatedAt(LocalDateTime.now().minusMonths(4))
                .whatYouLearn(List.of(
                        "Understand the company code of conduct",
                        "Comply with data privacy regulations (GDPR, DPDP)",
                        "Identify and report workplace misconduct",
                        "Follow secure information handling procedures"
                ))
                .requirements(List.of(
                        "All employees — no prerequisites"
                ))
                .build();
        complianceCourse = courseRepository.save(complianceCourse);

        // 4. Seed Curriculum Sections & Lessons
        
        // --- Schema Design Mastery Curriculum ---
        CourseSection s1 = createSection(schemaCourse, "Foundations of Schema Design", 1);
        Lesson l1 = createLesson(s1, "What is a Schema?", LessonType.VIDEO, 372, "https://www.youtube.com/embed/T9bzgXuT-ds", true, 1);
        Lesson l2 = createLesson(s1, "Relational vs Document Models", LessonType.VIDEO, 588, "https://www.youtube.com/embed/T9bzgXuT-ds", true, 2);
        Lesson l3 = createLesson(s1, "Reading: Schema Design Patterns", LessonType.READING, 600, null, false, 3);
        Lesson l4 = createLesson(s1, "Knowledge Check Quiz", LessonType.QUIZ, 300, null, false, 4);

        CourseSection s2 = createSection(schemaCourse, "Normalization & Normal Forms", 2);
        Lesson l5 = createLesson(s2, "First & Second Normal Form", LessonType.VIDEO, 680, "https://www.youtube.com/embed/T9bzgXuT-ds", false, 1);
        Lesson l6 = createLesson(s2, "Third Normal Form & BCNF", LessonType.VIDEO, 785, "https://www.youtube.com/embed/T9bzgXuT-ds", false, 2);
        Lesson l7 = createLesson(s2, "When to Denormalize", LessonType.VIDEO, 524, "https://www.youtube.com/embed/T9bzgXuT-ds", false, 3);

        CourseSection s3 = createSection(schemaCourse, "Indexing Strategies", 3);
        Lesson l8 = createLesson(s3, "B-Tree vs Hash Indexes", LessonType.VIDEO, 630, "https://www.youtube.com/embed/T9bzgXuT-ds", false, 1);
        Lesson l9 = createLesson(s3, "Composite & Partial Indexes", LessonType.VIDEO, 555, "https://www.youtube.com/embed/T9bzgXuT-ds", false, 2);
        Lesson l10 = createLesson(s3, "Index Lab Exercise", LessonType.ASSIGNMENT, 1800, null, false, 3);

        CourseSection s4 = createSection(schemaCourse, "Schema Migrations", 4);
        Lesson l11 = createLesson(s4, "Zero-Downtime Migration Patterns", LessonType.VIDEO, 862, "https://www.youtube.com/embed/T9bzgXuT-ds", false, 1);
        Lesson l12 = createLesson(s4, "Versioning Your Schema", LessonType.VIDEO, 490, "https://www.youtube.com/embed/T9bzgXuT-ds", false, 2);

        CourseSection s5 = createSection(schemaCourse, "Advanced Patterns", 5);
        Lesson l13 = createLesson(s5, "EAV Model & When to Avoid It", LessonType.VIDEO, 715, "https://www.youtube.com/embed/T9bzgXuT-ds", false, 1);
        Lesson l14 = createLesson(s5, "Polymorphic Associations", LessonType.VIDEO, 580, "https://www.youtube.com/embed/T9bzgXuT-ds", false, 2);
        Lesson l15 = createLesson(s5, "Module Assessment", LessonType.QUIZ, 1200, null, false, 3);

        CourseSection s6 = createSection(schemaCourse, "Final Project & Certification", 6);
        Lesson l16 = createLesson(s6, "Final Assignment Brief", LessonType.READING, 300, null, false, 1);
        Lesson l17 = createLesson(s6, "Final Assignment Submission", LessonType.ASSIGNMENT, 7200, null, false, 2);

        // --- Ingestion & ETL Pipeline Curriculum ---
        CourseSection etlS1 = createSection(etlCourse, "ETL Fundamentals", 1);
        Lesson el1 = createLesson(etlS1, "What is ETL?", LessonType.VIDEO, 430, "https://www.youtube.com/embed/T9bzgXuT-ds", true, 1);
        Lesson el2 = createLesson(etlS1, "ETL vs ELT", LessonType.VIDEO, 525, "https://www.youtube.com/embed/T9bzgXuT-ds", false, 2);

        CourseSection etlS2 = createSection(etlCourse, "Data Extraction Patterns", 2);
        Lesson el3 = createLesson(etlS2, "Full Load vs Incremental Load", LessonType.VIDEO, 620, "https://www.youtube.com/embed/T9bzgXuT-ds", false, 1);
        Lesson el4 = createLesson(etlS2, "Change Data Capture (CDC)", LessonType.VIDEO, 750, "https://www.youtube.com/embed/T9bzgXuT-ds", false, 2);

        CourseSection etlS3 = createSection(etlCourse, "Transformation & Loading", 3);
        Lesson el5 = createLesson(etlS3, "Data Cleansing Techniques", LessonType.VIDEO, 595, "https://www.youtube.com/embed/T9bzgXuT-ds", false, 1);
        Lesson el6 = createLesson(etlS3, "Slowly Changing Dimensions", LessonType.VIDEO, 660, "https://www.youtube.com/embed/T9bzgXuT-ds", false, 2);
        Lesson el7 = createLesson(etlS3, "Loading Strategies: Upsert, Merge", LessonType.VIDEO, 510, "https://www.youtube.com/embed/T9bzgXuT-ds", false, 3);

        CourseSection etlS4 = createSection(etlCourse, "Orchestration with Airflow", 4);
        Lesson el8 = createLesson(etlS4, "Airflow DAGs & Operators", LessonType.VIDEO, 825, "https://www.youtube.com/embed/T9bzgXuT-ds", false, 1);
        Lesson el9 = createLesson(etlS4, "Scheduling & Dependencies", LessonType.VIDEO, 610, "https://www.youtube.com/embed/T9bzgXuT-ds", false, 2);

        // --- Cybersecurity Curriculum ---
        CourseSection secS1 = createSection(securityCourse, "Security Foundations", 1);
        createLesson(secS1, "CIA Triad Explained", LessonType.VIDEO, 480, "https://www.youtube.com/embed/T9bzgXuT-ds", true, 1);
        createLesson(secS1, "Threat Modeling Basics", LessonType.VIDEO, 615, "https://www.youtube.com/embed/T9bzgXuT-ds", false, 2);

        CourseSection secS2 = createSection(securityCourse, "OWASP Top 10", 2);
        createLesson(secS2, "Injection Attacks", LessonType.VIDEO, 570, "https://www.youtube.com/embed/T9bzgXuT-ds", false, 1);
        createLesson(secS2, "Broken Authentication", LessonType.VIDEO, 535, "https://www.youtube.com/embed/T9bzgXuT-ds", false, 2);
        createLesson(secS2, "XSS & CSRF", LessonType.VIDEO, 680, "https://www.youtube.com/embed/T9bzgXuT-ds", false, 3);

        // --- Leadership Curriculum ---
        CourseSection leadS1 = createSection(leadCourse, "The IC-to-Leader Mindset", 1);
        Lesson ll1 = createLesson(leadS1, "Why Leadership is a Skill", LessonType.VIDEO, 450, "https://www.youtube.com/embed/T9bzgXuT-ds", true, 1);
        Lesson ll2 = createLesson(leadS1, "Leading Without Authority", LessonType.VIDEO, 555, "https://www.youtube.com/embed/T9bzgXuT-ds", false, 2);

        CourseSection leadS2 = createSection(leadCourse, "Communication & Feedback", 2);
        Lesson ll3 = createLesson(leadS2, "Giving Effective Feedback", LessonType.VIDEO, 600, "https://www.youtube.com/embed/T9bzgXuT-ds", false, 1);
        Lesson ll4 = createLesson(leadS2, "Running 1-on-1s", LessonType.VIDEO, 525, "https://www.youtube.com/embed/T9bzgXuT-ds", false, 2);

        CourseSection leadS3 = createSection(leadCourse, "Team & Culture", 3);
        Lesson ll5 = createLesson(leadS3, "Building Psychological Safety", LessonType.VIDEO, 660, "https://www.youtube.com/embed/T9bzgXuT-ds", false, 1);
        Lesson ll6 = createLesson(leadS3, "Conflict Resolution", LessonType.VIDEO, 570, "https://www.youtube.com/embed/T9bzgXuT-ds", false, 2);

        CourseSection leadS4 = createSection(leadCourse, "Stakeholder Management", 4);
        Lesson ll7 = createLesson(leadS4, "Managing Up", LessonType.VIDEO, 500, "https://www.youtube.com/embed/T9bzgXuT-ds", false, 1);
        Lesson ll8 = createLesson(leadS4, "Final Assignment", LessonType.ASSIGNMENT, 3600, null, false, 2);

        // --- Compliance Curriculum ---
        CourseSection compS1 = createSection(complianceCourse, "Code of Conduct", 1);
        createLesson(compS1, "Company Values & Ethics", LessonType.VIDEO, 720, "https://www.youtube.com/embed/T9bzgXuT-ds", true, 1);
        createLesson(compS1, "Data Privacy & GDPR Basics", LessonType.VIDEO, 630, "https://www.youtube.com/embed/T9bzgXuT-ds", false, 2);
        createLesson(compS1, "Compliance Quiz", LessonType.QUIZ, 600, null, false, 3);

        // 5. Seed default user enrollments & lesson progress
        Optional<User> userOpt = userRepository.findByEmail("user@skillsphere.com");
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // Enroll in Schema Design Mastery (68% complete - 11/17 lessons)
            Enrollment e1 = Enrollment.builder()
                    .user(user)
                    .course(schemaCourse)
                    .enrolledAt(LocalDateTime.now().minusWeeks(2))
                    .progressPct(68)
                    .completed(false)
                    .isWishlist(false)
                    .build();
            enrollmentRepository.save(e1);

            // Complete first 11 lessons
            List<Lesson> schemaLessons = List.of(l1, l2, l3, l4, l5, l6, l7, l8, l9, l10, l11);
            for (Lesson les : schemaLessons) {
                lessonProgressRepository.save(LessonProgress.builder()
                        .user(user)
                        .lesson(les)
                        .completed(true)
                        .completedAt(LocalDateTime.now().minusDays(3))
                        .watchTimeSeconds(les.getDurationSeconds())
                        .build());
            }

            // Enroll in Ingestion & ETL Pipeline (33% complete - 3/9 lessons)
            Enrollment e2 = Enrollment.builder()
                    .user(user)
                    .course(etlCourse)
                    .enrolledAt(LocalDateTime.now().minusWeeks(1))
                    .progressPct(33)
                    .completed(false)
                    .isWishlist(false)
                    .build();
            enrollmentRepository.save(e2);

            // Complete first 3 lessons
            List<Lesson> etlLessons = List.of(el1, el2, el3);
            for (Lesson les : etlLessons) {
                lessonProgressRepository.save(LessonProgress.builder()
                        .user(user)
                        .lesson(les)
                        .completed(true)
                        .completedAt(LocalDateTime.now().minusDays(2))
                        .watchTimeSeconds(les.getDurationSeconds())
                        .build());
            }

            // Enroll in Leadership for Developers (75% complete - 6/8 lessons)
            Enrollment e3 = Enrollment.builder()
                    .user(user)
                    .course(leadCourse)
                    .enrolledAt(LocalDateTime.now().minusWeeks(3))
                    .progressPct(75)
                    .completed(false)
                    .isWishlist(false)
                    .build();
            enrollmentRepository.save(e3);

            // Complete 6 out of 8 lessons (ll1, ll2, ll3, ll4, ll5, ll7)
            List<Lesson> leadLessons = List.of(ll1, ll2, ll3, ll4, ll5, ll7);
            for (Lesson les : leadLessons) {
                lessonProgressRepository.save(LessonProgress.builder()
                        .user(user)
                        .lesson(les)
                        .completed(true)
                        .completedAt(LocalDateTime.now().minusDays(4))
                        .watchTimeSeconds(les.getDurationSeconds())
                        .build());
            }
        }

        // 6. Seed Reviews
        seedReviews(schemaCourse);
    }

    private Instructor createInstructor(String name, String email, String title, String bio, int years) {
        User user = User.builder()
                .name(name)
                .email(email)
                .ldap(email.split("@")[0])
                .password(passwordEncoder.encode("instructor123"))
                .roles(Set.of(Role.INSTRUCTOR))
                .isActive(true)
                .build();
        user = userRepository.save(user);

        Instructor inst = Instructor.builder()
                .user(user)
                .title(title)
                .bio(bio)
                .experienceYears(years)
                .build();
        return instructorRepository.save(inst);
    }

    private CourseSection createSection(Course course, String title, int position) {
        CourseSection sec = CourseSection.builder()
                .course(course)
                .title(title)
                .position(position)
                .build();
        return sectionRepository.save(sec);
    }

    private Lesson createLesson(CourseSection section, String title, LessonType type, int seconds, String url, boolean free, int position) {
        Lesson les = Lesson.builder()
                .section(section)
                .title(title)
                .type(type)
                .durationSeconds(seconds)
                .videoUrl(url)
                .isFree(free)
                .isPublished(true)
                .position(position)
                .description("In this lesson, you will learn comprehensive strategies on " + title.toLowerCase() + " following modern industry guidelines.")
                .content("# " + title + "\n\nWelcome to this detailed reading resource. Follow these industry principles closely.")
                .build();
        return lessonRepository.save(les);
    }

    private void seedReviews(Course course) {
        // Priya S.
        User priya = User.builder()
                .name("Priya S.")
                .email("priya@skillsphere.com")
                .ldap("priya")
                .roles(Set.of(Role.LEARNER))
                .build();
        userRepository.save(priya);
        Review r1 = new Review();
        r1.setUser(priya);
        r1.setCourse(course);
        r1.setRating(5);
        r1.setComment("Excellent course — very hands-on and practical. Filled gaps I didn't even know I had.");
        reviewRepository.save(r1);

        // Ravi M.
        User raviM = User.builder()
                .name("Ravi M.")
                .email("ravim@skillsphere.com")
                .ldap("ravim")
                .roles(Set.of(Role.LEARNER))
                .build();
        userRepository.save(raviM);
        Review r2 = new Review();
        r2.setUser(raviM);
        r2.setCourse(course);
        r2.setRating(5);
        r2.setComment("The best structured course I've taken on this topic. Highly recommended for any developer.");
        reviewRepository.save(r2);

        // Ananya I.
        User ananyaI = User.builder()
                .name("Ananya I.")
                .email("ananyai@skillsphere.com")
                .ldap("ananyai")
                .roles(Set.of(Role.LEARNER))
                .build();
        userRepository.save(ananyaI);
        Review r3 = new Review();
        r3.setUser(ananyaI);
        r3.setCourse(course);
        r3.setRating(4);
        r3.setComment("Great content overall. Some sections felt a bit rushed but the core material is excellent.");
        reviewRepository.save(r3);
    }

    private Category getOrCreateCategory(String name, String slug) {
        return categoryRepository.findByName(name)
                .orElseGet(() -> categoryRepository.save(Category.builder()
                        .name(name)
                        .slug(slug)
                        .isDeleted(false)
                        .build()));
    }
}
