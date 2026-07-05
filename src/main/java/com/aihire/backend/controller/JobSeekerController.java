package com.aihire.backend.controller;

import com.aihire.backend.dto.*;
import com.aihire.backend.response.ApiResponse;
import com.aihire.backend.service.JobSeekerService;
import com.aihire.backend.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/jobseeker")
@RequiredArgsConstructor
@Tag(name = "Job Seeker Controller", description = "Endpoints for job seekers to update profiles, upload resumes, apply to jobs, view applications, and manage notifications")
public class JobSeekerController {

    private final JobSeekerService jobSeekerService;
    private final NotificationService notificationService;

    @PutMapping("/profile")
    @Operation(summary = "Update Job Seeker Profile details")
    public ResponseEntity<ApiResponse<JobSeekerProfileDto>> updateProfile(
            @RequestBody JobSeekerProfileDto dto, Principal principal) {
        JobSeekerProfileDto result = jobSeekerService.updateProfile(dto, principal.getName());
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", result));
    }

    @GetMapping("/profile")
    @Operation(summary = "View current Job Seeker Profile")
    public ResponseEntity<ApiResponse<JobSeekerProfileDto>> getProfile(Principal principal) {
        JobSeekerProfileDto result = jobSeekerService.getProfile(principal.getName());
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", result));
    }

    @PostMapping("/profile/resume")
    @Operation(summary = "Upload resume file (PDF, DOC, DOCX up to 5MB)")
    public ResponseEntity<ApiResponse<String>> uploadResume(
            @RequestParam("file") MultipartFile file, Principal principal) {
        String url = jobSeekerService.uploadResume(file, principal.getName());
        return ResponseEntity.ok(ApiResponse.success("Resume uploaded successfully", url));
    }

    @PostMapping("/apply/{jobId}")
    @Operation(summary = "Apply to a job listing")
    public ResponseEntity<ApiResponse<ApplicationDto>> applyJob(
            @PathVariable Long jobId,
            @RequestParam(required = false) String resumeUrl,
            Principal principal) {
        ApplicationDto result = jobSeekerService.applyJob(jobId, resumeUrl, principal.getName());
        return ResponseEntity.ok(ApiResponse.success("Application submitted successfully", result));
    }

    @GetMapping("/applications")
    @Operation(summary = "View all own job applications")
    public ResponseEntity<ApiResponse<List<ApplicationDto>>> getMyApplications(Principal principal) {
        List<ApplicationDto> result = jobSeekerService.getMyApplications(principal.getName());
        return ResponseEntity.ok(ApiResponse.success("Applications retrieved successfully", result));
    }

    @GetMapping("/interviews")
    @Operation(summary = "View scheduled interviews")
    public ResponseEntity<ApiResponse<List<InterviewDto>>> getMyInterviews(Principal principal) {
        List<InterviewDto> result = jobSeekerService.getMyInterviews(principal.getName());
        return ResponseEntity.ok(ApiResponse.success("Interviews retrieved successfully", result));
    }

    @GetMapping("/notifications")
    @Operation(summary = "View user notifications")
    public ResponseEntity<ApiResponse<List<NotificationDto>>> getMyNotifications(Principal principal) {
        List<NotificationDto> result = notificationService.getUserNotifications(principal.getName());
        return ResponseEntity.ok(ApiResponse.success("Notifications retrieved successfully", result));
    }

    @PutMapping("/skills")
    @Operation(summary = "Update list of skills")
    public ResponseEntity<ApiResponse<JobSeekerProfileDto>> updateSkills(
            @RequestParam String skills, Principal principal) {
        JobSeekerProfileDto result = jobSeekerService.updateSkills(skills, principal.getName());
        return ResponseEntity.ok(ApiResponse.success("Skills updated successfully", result));
    }

    @DeleteMapping("/applications/{id}")
    @Operation(summary = "Withdraw/Delete a job application")
    public ResponseEntity<ApiResponse<Void>> deleteApplication(
            @PathVariable Long id, Principal principal) {
        jobSeekerService.deleteApplication(id, principal.getName());
        return ResponseEntity.ok(ApiResponse.success("Application withdrawn successfully"));
    }
}
