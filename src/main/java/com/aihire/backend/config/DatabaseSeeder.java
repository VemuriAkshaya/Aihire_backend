package com.aihire.backend.config;

import com.aihire.backend.entity.*;
import com.aihire.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final JobSeekerProfileRepository jobSeekerProfileRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        seedJobSeeker();
        seedRecruiter();
    }

    private void seedJobSeeker() {
        String email = "jobseeker@gmail.com";
        if (!userRepository.existsByEmail(email)) {
            User user = User.builder()
                    .fullName("John Seeker")
                    .email(email)
                    .password(passwordEncoder.encode("123456"))
                    .phone("1234567890")
                    .role(Role.ROLE_JOB_SEEKER)
                    .build();

            User savedUser = userRepository.save(user);

            JobSeekerProfile profile = JobSeekerProfile.builder()
                    .user(savedUser)
                    .education("Bachelor of Science in Computer Science")
                    .experience("2 years as Java Developer")
                    .skills("Java, Spring Boot, JPA, MySQL")
                    .location("New York, USA")
                    .about("Passionate backend engineer who loves coding in Java.")
                    .build();
            jobSeekerProfileRepository.save(profile);
            log.info("Job Seeker seeded successfully");
        }
    }

    private void seedRecruiter() {
        String email = "recruiter@gmail.com";
        if (!userRepository.existsByEmail(email)) {
            User user = User.builder()
                    .fullName("Alice Recruiter")
                    .email(email)
                    .password(passwordEncoder.encode("123456"))
                    .phone("9876543210")
                    .role(Role.ROLE_RECRUITER)
                    .build();

            User savedUser = userRepository.save(user);

            RecruiterProfile profile = RecruiterProfile.builder()
                    .user(savedUser)
                    .companyName("Tech Innovators Inc.")
                    .companyWebsite("https://techinnovators.example.com")
                    .companyLocation("San Francisco, USA")
                    .designation("Technical Recruiter")
                    .aboutCompany("Leading provider of AI and cloud consulting services.")
                    .build();
            recruiterProfileRepository.save(profile);
            log.info("Recruiter seeded successfully");
        }
    }
}
