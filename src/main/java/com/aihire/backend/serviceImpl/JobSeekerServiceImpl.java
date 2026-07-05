package com.aihire.backend.serviceImpl;

import com.aihire.backend.dto.*;
import com.aihire.backend.entity.*;
import com.aihire.backend.exception.BadRequestException;
import com.aihire.backend.exception.ResourceNotFoundException;
import com.aihire.backend.mapper.EntityMapper;
import com.aihire.backend.repository.*;
import com.aihire.backend.service.JobSeekerService;
import com.aihire.backend.service.NotificationService;
import com.aihire.backend.util.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobSeekerServiceImpl implements JobSeekerService {

    private final UserRepository userRepository;
    private final JobSeekerProfileRepository jobSeekerProfileRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;
    private final InterviewRepository interviewRepository;
    private final NotificationService notificationService;
    private final FileUtils fileUtils;
    private final EntityMapper mapper;

    @Override
    @Transactional
    public JobSeekerProfileDto updateProfile(JobSeekerProfileDto dto, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        JobSeekerProfile profile = jobSeekerProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Job seeker profile not found"));

        profile.setEducation(dto.getEducation());
        profile.setExperience(dto.getExperience());
        profile.setSkills(dto.getSkills());
        profile.setLocation(dto.getLocation());
        profile.setAbout(dto.getAbout());
        profile.setProfileImage(dto.getProfileImage());
        if (dto.getResumeUrl() != null) {
            profile.setResumeUrl(dto.getResumeUrl());
        }

        JobSeekerProfile updated = jobSeekerProfileRepository.save(profile);
        return mapper.map(updated, JobSeekerProfileDto.class);
    }

    @Override
    @Transactional
    public String uploadResume(MultipartFile file, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        JobSeekerProfile profile = jobSeekerProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Job seeker profile not found"));

        String resumeUrl = fileUtils.saveResume(file);
        profile.setResumeUrl(resumeUrl);
        jobSeekerProfileRepository.save(profile);

        return resumeUrl;
    }

    @Override
    @Transactional(readOnly = true)
    public JobSeekerProfileDto getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        JobSeekerProfile profile = jobSeekerProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Job seeker profile not found"));

        return mapper.map(profile, JobSeekerProfileDto.class);
    }

    @Override
    @Transactional
    public ApplicationDto applyJob(Long jobId, String resumeUrl, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with ID: " + jobId));

        if (applicationRepository.existsByJobIdAndJobSeekerId(jobId, user.getId())) {
            throw new BadRequestException("You have already applied for this job");
        }

        // Use profile resume if not explicitly sent in application
        String finalResumeUrl = resumeUrl;
        if (finalResumeUrl == null || finalResumeUrl.isBlank()) {
            JobSeekerProfile profile = jobSeekerProfileRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Job seeker profile not found"));
            finalResumeUrl = profile.getResumeUrl();
            if (finalResumeUrl == null || finalResumeUrl.isBlank()) {
                throw new BadRequestException("Please upload a resume first before applying");
            }
        }

        Application application = Application.builder()
                .job(job)
                .jobSeeker(user)
                .resumeUrl(finalResumeUrl)
                .status(ApplicationStatus.APPLIED)
                .build();

        Application saved = applicationRepository.save(application);

        // Auto-create notification for candidate
        notificationService.createNotification(
                user,
                "Application Submitted",
                "You have successfully applied for the job '" + job.getTitle() + "' at " + job.getCompany() + "."
        );

        // Auto-create notification for recruiter
        notificationService.createNotification(
                job.getRecruiter(),
                "New Applicant",
                user.getFullName() + " has applied for the job '" + job.getTitle() + "'."
        );

        return convertApplicationToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationDto> getMyApplications(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Application> apps = applicationRepository.findByJobSeekerIdOrderByAppliedDateDesc(user.getId());
        return apps.stream().map(this::convertApplicationToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InterviewDto> getMyInterviews(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Interview> interviews = interviewRepository.findByApplicationJobSeekerIdOrderByDateDescTimeDesc(user.getId());
        return interviews.stream().map(this::convertInterviewToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public JobSeekerProfileDto updateSkills(String skills, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        JobSeekerProfile profile = jobSeekerProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Job seeker profile not found"));

        profile.setSkills(skills);
        JobSeekerProfile updated = jobSeekerProfileRepository.save(profile);
        return mapper.map(updated, JobSeekerProfileDto.class);
    }

    @Override
    @Transactional
    public void deleteApplication(Long applicationId, String email) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        if (!app.getJobSeeker().getEmail().equalsIgnoreCase(email)) {
            throw new BadRequestException("You are not authorized to delete this application");
        }

        applicationRepository.delete(app);
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

    private InterviewDto convertInterviewToDto(Interview intv) {
        return InterviewDto.builder()
                .id(intv.getId())
                .applicationId(intv.getApplication().getId())
                .jobId(intv.getApplication().getJob().getId())
                .jobTitle(intv.getApplication().getJob().getTitle())
                .candidateId(intv.getApplication().getJobSeeker().getId())
                .candidateName(intv.getApplication().getJobSeeker().getFullName())
                .candidateEmail(intv.getApplication().getJobSeeker().getEmail())
                .interviewType(intv.getInterviewType().name())
                .date(intv.getDate())
                .time(intv.getTime())
                .meetingLink(intv.getMeetingLink())
                .venue(intv.getVenue())
                .interviewerName(intv.getInterviewerName())
                .notes(intv.getNotes())
                .status(intv.getStatus())
                .build();
    }
}
