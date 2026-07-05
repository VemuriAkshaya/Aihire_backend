package com.aihire.backend.serviceImpl;

import com.aihire.backend.dto.DashboardJobSeekerDto;
import com.aihire.backend.dto.DashboardRecruiterDto;
import com.aihire.backend.entity.ApplicationStatus;
import com.aihire.backend.entity.User;
import com.aihire.backend.exception.ResourceNotFoundException;
import com.aihire.backend.repository.ApplicationRepository;
import com.aihire.backend.repository.InterviewRepository;
import com.aihire.backend.repository.JobRepository;
import com.aihire.backend.repository.UserRepository;
import com.aihire.backend.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;
    private final InterviewRepository interviewRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardJobSeekerDto getJobSeekerDashboard(String email) {
        User seeker = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        long totalApplications = applicationRepository.countByJobSeekerId(seeker.getId());
        long interviews = interviewRepository.countByApplicationJobSeekerId(seeker.getId());
        // For job seeker, selected can represent SELECTED or HIRED state
        long selected = applicationRepository.countByJobSeekerIdAndStatus(seeker.getId(), ApplicationStatus.SELECTED)
                + applicationRepository.countByJobSeekerIdAndStatus(seeker.getId(), ApplicationStatus.HIRED);
        long rejected = applicationRepository.countByJobSeekerIdAndStatus(seeker.getId(), ApplicationStatus.REJECTED);

        return DashboardJobSeekerDto.builder()
                .totalApplications(totalApplications)
                .interviews(interviews)
                .selected(selected)
                .rejected(rejected)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardRecruiterDto getRecruiterDashboard(String email) {
        User recruiter = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        long totalJobs = jobRepository.countByRecruiterId(recruiter.getId());
        long applicants = applicationRepository.countByJobRecruiterId(recruiter.getId());
        long interviews = interviewRepository.countByApplicationJobRecruiterId(recruiter.getId());
        long hired = applicationRepository.countByJobRecruiterIdAndStatus(recruiter.getId(), ApplicationStatus.HIRED);
        long rejected = applicationRepository.countByJobRecruiterIdAndStatus(recruiter.getId(), ApplicationStatus.REJECTED);

        return DashboardRecruiterDto.builder()
                .totalJobs(totalJobs)
                .applicants(applicants)
                .interviews(interviews)
                .hired(hired)
                .rejected(rejected)
                .build();
    }
}
