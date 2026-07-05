package com.aihire.backend.serviceImpl;

import com.aihire.backend.dto.*;
import com.aihire.backend.entity.*;
import com.aihire.backend.exception.BadRequestException;
import com.aihire.backend.exception.ResourceNotFoundException;
import com.aihire.backend.mapper.EntityMapper;
import com.aihire.backend.repository.*;
import com.aihire.backend.service.NotificationService;
import com.aihire.backend.service.RecruiterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecruiterServiceImpl implements RecruiterService {

    private final UserRepository userRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;
    private final InterviewRepository interviewRepository;
    private final NotificationService notificationService;
    private final EntityMapper mapper;

    @Override
    @Transactional
    public RecruiterProfileDto updateProfile(RecruiterProfileDto dto, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        RecruiterProfile profile = recruiterProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter profile not found"));

        profile.setCompanyName(dto.getCompanyName());
        profile.setCompanyWebsite(dto.getCompanyWebsite());
        profile.setCompanyLocation(dto.getCompanyLocation());
        profile.setDesignation(dto.getDesignation());
        profile.setAboutCompany(dto.getAboutCompany());

        RecruiterProfile updated = recruiterProfileRepository.save(profile);
        return mapper.map(updated, RecruiterProfileDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public RecruiterProfileDto getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        RecruiterProfile profile = recruiterProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter profile not found"));

        return mapper.map(profile, RecruiterProfileDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationDto> getApplicants(String email) {
        User recruiter = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter not found"));

        List<Application> apps = applicationRepository.findByJobRecruiterIdOrderByAppliedDateDesc(recruiter.getId());
        return apps.stream().map(this::convertApplicationToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationDto> searchApplicants(String query, String email) {
        User recruiter = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter not found"));

        List<Application> apps = applicationRepository.searchApplicants(recruiter.getId(), query);
        return apps.stream().map(this::convertApplicationToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void hireCandidate(Long applicationId, String email) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        if (!app.getJob().getRecruiter().getEmail().equalsIgnoreCase(email)) {
            throw new BadRequestException("You are not authorized to manage this application");
        }

        app.setStatus(ApplicationStatus.HIRED);
        applicationRepository.save(app);

        // Auto-create notification for candidate
        notificationService.createNotification(
                app.getJobSeeker(),
                "Hired!",
                "Congratulations! You have been HIRED for the position of '" + app.getJob().getTitle() + "' at " + app.getJob().getCompany() + "."
        );
    }

    @Override
    @Transactional
    public void rejectCandidate(Long applicationId, String reason, String email) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        if (!app.getJob().getRecruiter().getEmail().equalsIgnoreCase(email)) {
            throw new BadRequestException("You are not authorized to manage this application");
        }

        app.setStatus(ApplicationStatus.REJECTED);
        applicationRepository.save(app);

        // Auto-create notification for candidate
        String rejectMessage = "Thank you for applying for the position of '" + app.getJob().getTitle() + "' at " + app.getJob().getCompany() + ". " +
                "Unfortunately, the company has decided not to proceed with your application at this time.";
        if (reason != null && !reason.isBlank()) {
            rejectMessage += " Reason provided: " + reason;
        }

        notificationService.createNotification(
                app.getJobSeeker(),
                "Application Status Update",
                rejectMessage
        );
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardRecruiterDto getDashboardStats(String email) {
        User recruiter = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter not found"));

        Long recruiterId = recruiter.getId();

        long totalJobs = jobRepository.countByRecruiterId(recruiterId);
        long applicants = applicationRepository.countByJobRecruiterId(recruiterId);
        long interviews = interviewRepository.countByApplicationJobRecruiterId(recruiterId);
        long hired = applicationRepository.countByJobRecruiterIdAndStatus(recruiterId, ApplicationStatus.HIRED);
        long rejected = applicationRepository.countByJobRecruiterIdAndStatus(recruiterId, ApplicationStatus.REJECTED);

        return DashboardRecruiterDto.builder()
                .totalJobs(totalJobs)
                .applicants(applicants)
                .interviews(interviews)
                .hired(hired)
                .rejected(rejected)
                .build();
    }

    private ApplicationDto convertApplicationToDto(Application app) {
        return ApplicationDto.builder()
                .id(app.getId())
                .jobId(app.getJob().getId())
                .jobTitle(app.getJob().getTitle())
                .companyName(app.getJob().getCompany())
                .jobSeekerId(app.getJobSeeker().getId())
                .jobSeekerName(app.getJobSeeker().getFullName())
                .jobSeekerEmail(app.getJobSeeker().getEmail())
                .resumeUrl(app.getResumeUrl())
                .appliedDate(app.getAppliedDate())
                .status(app.getStatus().name())
                .build();
    }
}
