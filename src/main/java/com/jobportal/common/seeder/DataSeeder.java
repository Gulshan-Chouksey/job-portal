package com.jobportal.common.seeder;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.jobportal.application.entity.ApplicationStatus;
import com.jobportal.application.entity.JobApplication;
import com.jobportal.application.repository.ApplicationRepository;
import com.jobportal.auth.entity.Role;
import com.jobportal.auth.entity.User;
import com.jobportal.auth.repository.UserRepository;
import com.jobportal.candidate.entity.Candidate;
import com.jobportal.candidate.repository.CandidateRepository;
import com.jobportal.employer.entity.Employer;
import com.jobportal.employer.repository.EmployerRepository;
import com.jobportal.job.entity.Category;
import com.jobportal.job.entity.Job;
import com.jobportal.job.entity.JobStatus;
import com.jobportal.job.entity.SavedJob;
import com.jobportal.job.repository.CategoryRepository;
import com.jobportal.job.repository.JobRepository;
import com.jobportal.job.repository.SavedJobRepository;

import lombok.RequiredArgsConstructor;

/**
 * Seeds the database with sample data for development and testing.
 * Activated only when the "dev" profile is active.
 * All seeded users have the password: "password123"
 */
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepository;
    private final CandidateRepository candidateRepository;
    private final EmployerRepository employerRepository;
    private final JobRepository jobRepository;
    private final CategoryRepository categoryRepository;
    private final ApplicationRepository applicationRepository;
    private final SavedJobRepository savedJobRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already seeded. Skipping data seeder.");
            return;
        }

        log.info("Seeding database with sample data...");

        String encodedPassword = passwordEncoder.encode("password123");

        // --- Users ---
        User admin = createUser("Admin User", "admin@jobportal.com", encodedPassword, Role.ADMIN);
        User employer1User = createUser("John Smith", "john@techcorp.com", encodedPassword, Role.EMPLOYER);
        User employer2User = createUser("Sarah Johnson", "sarah@innovateinc.com", encodedPassword, Role.EMPLOYER);
        User candidate1User = createUser("Alice Brown", "alice@email.com", encodedPassword, Role.CANDIDATE);
        User candidate2User = createUser("Bob Wilson", "bob@email.com", encodedPassword, Role.CANDIDATE);
        User candidate3User = createUser("Charlie Davis", "charlie@email.com", encodedPassword, Role.CANDIDATE);

        userRepository.saveAll(List.of(admin, employer1User, employer2User,
                candidate1User, candidate2User, candidate3User));
        log.info("Seeded 6 users (1 admin, 2 employers, 3 candidates)");

        // --- Employers ---
        Employer employer1 = Employer.builder()
                .user(employer1User)
                .companyName("TechCorp Solutions")
                .companyDescription("A leading software development company specializing in enterprise solutions.")
                .companyWebsite("https://techcorp.com")
                .industry("Information Technology")
                .location("San Francisco, CA")
                .build();

        Employer employer2 = Employer.builder()
                .user(employer2User)
                .companyName("Innovate Inc.")
                .companyDescription("Innovative startup focused on AI and machine learning products.")
                .companyWebsite("https://innovateinc.com")
                .industry("Artificial Intelligence")
                .location("New York, NY")
                .build();

        employerRepository.saveAll(List.of(employer1, employer2));
        log.info("Seeded 2 employer profiles");

        // --- Candidates ---
        Candidate candidate1 = Candidate.builder()
                .user(candidate1User)
                .headline("Full Stack Java Developer")
                .summary("Experienced Java developer with 5 years of experience in Spring Boot, React, and cloud services.")
                .skills("Java, Spring Boot, React, AWS, Docker, PostgreSQL")
                .experienceYears(5)
                .education("B.Tech in Computer Science, MIT")
                .phone("+1-555-0101")
                .location("San Francisco, CA")
                .build();

        Candidate candidate2 = Candidate.builder()
                .user(candidate2User)
                .headline("Data Scientist & ML Engineer")
                .summary("Data scientist with expertise in Python, TensorFlow, and big data analytics.")
                .skills("Python, TensorFlow, PyTorch, SQL, Spark, Tableau")
                .experienceYears(3)
                .education("M.Sc in Data Science, Stanford University")
                .phone("+1-555-0102")
                .location("New York, NY")
                .build();

        Candidate candidate3 = Candidate.builder()
                .user(candidate3User)
                .headline("Frontend Developer")
                .summary("Creative frontend developer passionate about building beautiful and responsive UIs.")
                .skills("JavaScript, TypeScript, React, Angular, CSS, Figma")
                .experienceYears(2)
                .education("B.Sc in Software Engineering, UCLA")
                .phone("+1-555-0103")
                .location("Los Angeles, CA")
                .build();

        candidateRepository.saveAll(List.of(candidate1, candidate2, candidate3));
        log.info("Seeded 3 candidate profiles");

        // --- Categories ---
        Category backend = createCategory("Backend Development", "Server-side programming and APIs");
        Category frontend = createCategory("Frontend Development", "Client-side UI and UX development");
        Category dataSci = createCategory("Data Science", "Data analysis, ML, and AI");
        Category devops = createCategory("DevOps", "CI/CD, cloud infrastructure, and deployment");
        Category mobile = createCategory("Mobile Development", "iOS and Android app development");
        Category fullstack = createCategory("Full Stack", "End-to-end web application development");

        categoryRepository.saveAll(List.of(backend, frontend, dataSci, devops, mobile, fullstack));
        log.info("Seeded 6 categories");

        // --- Jobs ---
        Job job1 = createJob(employer1, "Senior Java Developer",
                "We're looking for an experienced Java developer to lead our backend team. " +
                "You'll design and implement microservices using Spring Boot and cloud-native technologies.",
                "San Francisco, CA", 120000, 160000, JobStatus.ACTIVE, Set.of(backend, fullstack));

        Job job2 = createJob(employer1, "React Frontend Developer",
                "Join our frontend team to build modern, responsive web applications using React and TypeScript.",
                "San Francisco, CA", 100000, 140000, JobStatus.ACTIVE, Set.of(frontend, fullstack));

        Job job3 = createJob(employer1, "DevOps Engineer",
                "Manage our CI/CD pipelines, Kubernetes clusters, and cloud infrastructure on AWS.",
                "Remote", 110000, 150000, JobStatus.ACTIVE, Set.of(devops, backend));

        Job job4 = createJob(employer2, "Machine Learning Engineer",
                "Build and deploy ML models for our AI-powered products. Experience with Python and TensorFlow required.",
                "New York, NY", 130000, 170000, JobStatus.ACTIVE, Set.of(dataSci));

        Job job5 = createJob(employer2, "Full Stack Developer",
                "Work on our core platform using Node.js, React, and PostgreSQL.",
                "New York, NY", 95000, 135000, JobStatus.ACTIVE, Set.of(fullstack, backend, frontend));

        Job job6 = createJob(employer2, "Mobile App Developer",
                "Develop cross-platform mobile applications using React Native.",
                "Remote", 100000, 140000, JobStatus.DRAFT, Set.of(mobile, frontend));

        Job job7 = createJob(employer1, "Junior Python Developer",
                "Entry-level position for Python enthusiasts. Great opportunity to learn and grow.",
                "San Francisco, CA", 70000, 90000, JobStatus.CLOSED, Set.of(backend));

        jobRepository.saveAll(List.of(job1, job2, job3, job4, job5, job6, job7));
        log.info("Seeded 7 jobs (5 active, 1 draft, 1 closed)");

        // --- Job Applications ---
        JobApplication app1 = createApplication(candidate1, job1, "I have 5 years of Java experience and would love to lead your backend team.", ApplicationStatus.SHORTLISTED);
        JobApplication app2 = createApplication(candidate1, job2, "I also have strong React skills and enjoy frontend work.", ApplicationStatus.PENDING);
        JobApplication app3 = createApplication(candidate2, job4, "My ML expertise aligns perfectly with this role.", ApplicationStatus.INTERVIEW);
        JobApplication app4 = createApplication(candidate2, job5, "I can contribute to both backend and data layers.", ApplicationStatus.REVIEWED);
        JobApplication app5 = createApplication(candidate3, job2, "Frontend is my passion. I'd be a great fit!", ApplicationStatus.PENDING);
        JobApplication app6 = createApplication(candidate3, job5, "I can handle the frontend portion of this full-stack role.", ApplicationStatus.OFFERED);

        applicationRepository.saveAll(List.of(app1, app2, app3, app4, app5, app6));
        log.info("Seeded 6 job applications");

        // --- Saved Jobs ---
        SavedJob saved1 = SavedJob.builder().candidate(candidate1).job(job3).build();
        SavedJob saved2 = SavedJob.builder().candidate(candidate1).job(job4).build();
        SavedJob saved3 = SavedJob.builder().candidate(candidate2).job(job1).build();
        SavedJob saved4 = SavedJob.builder().candidate(candidate3).job(job4).build();
        SavedJob saved5 = SavedJob.builder().candidate(candidate3).job(job3).build();

        savedJobRepository.saveAll(List.of(saved1, saved2, saved3, saved4, saved5));
        log.info("Seeded 5 saved jobs");

        log.info("Database seeding completed successfully!");
        log.info("===========================================");
        log.info("Sample login credentials (password: password123):");
        log.info("  Admin:     admin@jobportal.com");
        log.info("  Employer:  john@techcorp.com");
        log.info("  Employer:  sarah@innovateinc.com");
        log.info("  Candidate: alice@email.com");
        log.info("  Candidate: bob@email.com");
        log.info("  Candidate: charlie@email.com");
        log.info("===========================================");
    }

    private User createUser(String name, String email, String encodedPassword, Role role) {
        return User.builder()
                .name(name)
                .email(email)
                .password(encodedPassword)
                .role(role)
                .build();
    }

    private Category createCategory(String name, String description) {
        return Category.builder()
                .name(name)
                .description(description)
                .build();
    }

    private Job createJob(Employer employer, String title, String description,
                          String location, int salaryMin, int salaryMax,
                          JobStatus status, Set<Category> categories) {
        Job job = new Job();
        job.setEmployer(employer);
        job.setTitle(title);
        job.setDescription(description);
        job.setLocation(location);
        job.setSalaryMin(salaryMin);
        job.setSalaryMax(salaryMax);
        job.setStatus(status);
        job.setCategories(categories);
        return job;
    }

    private JobApplication createApplication(Candidate candidate, Job job,
                                              String coverLetter, ApplicationStatus status) {
        return JobApplication.builder()
                .candidate(candidate)
                .job(job)
                .coverLetter(coverLetter)
                .status(status)
                .build();
    }
}
