package com.aihire.backend.service;

import com.aihire.backend.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface JobSeekerService {
    JobSeekerProfileDto updateProfile(JobSeekerProfileDto dto, String email);
    String uploadResume(MultipartFile file, String email);
    JobSeekerProfileDto getProfile(String email);
    ApplicationDto applyJob(Long jobId, String resumeUrl, String email);
    List<ApplicationDto> getMyApplications(String email);
    List<InterviewDto> getMyInterviews(String email);
    JobSeekerProfileDto updateSkills(String skills, String email);
    void deleteApplication(Long applicationId, String email);
}
