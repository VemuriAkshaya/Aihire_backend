package com.aihire.backend.controller;

import com.aihire.backend.dto.*;
import com.aihire.backend.response.ApiResponse;
import com.aihire.backend.service.JobService;
import com.aihire.backend.service.RecruiterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/recruiter")
@RequiredArgsConstructor
@Tag(name = "Recruiter Controller", description = "Endpoints for recruiters to post jobs, view applications, hire/reject candidates, and track metrics")
public class RecruiterController {

    private final RecruiterService recruiterService;
    private final JobService jobService;

    @PutMapping("/profile")
    @Operation(summary = "Update Recruiter Profile details")
    public ResponseEntity<ApiResponse<RecruiterProfileDto>> updateProfile(
            @RequestBody RecruiterProfileDto dto, Principal principal) {
        RecruiterProfileDto result = recruiterService.updateProfile(dto, principal.getName());
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", result));
    }

    @GetMapping("/profile")
    @Operation(summary = "View Recruiter Profile")
    public ResponseEntity<ApiResponse<RecruiterProfileDto>> getProfile(Principal principal) {
        RecruiterProfileDto result = recruiterService.getProfile(principal.getName());
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", result));
    }

    @PostMapping("/jobs")
    @Operation(summary = "Create/Post a new job listing")
    public ResponseEntity<ApiResponse<JobDto>> createJob(
            @Valid @RequestBody JobDto jobDto, Principal principal) {
        JobDto result = jobService.createJob(jobDto, principal.getName());
        return ResponseEntity.ok(ApiResponse.success("Job created successfully", result));
    }

    @PutMapping("/jobs/{id}")
    @Operation(summary = "Update an existing job listing")
    public ResponseEntity<ApiResponse<JobDto>> updateJob(
            @PathVariable Long id, @Valid @RequestBody JobDto jobDto, Principal principal) {
        JobDto result = jobService.updateJob(id, jobDto, principal.getName());
        return ResponseEntity.ok(ApiResponse.success("Job updated successfully", result));
    }

    @DeleteMapping("/jobs/{id}")
    @Operation(summary = "Delete/Close a job listing")
    public ResponseEntity<ApiResponse<Void>> deleteJob(
            @PathVariable Long id, Principal principal) {
        jobService.deleteJob(id, principal.getName());
        return ResponseEntity.ok(ApiResponse.success("Job deleted successfully"));
    }

    @GetMapping("/jobs")
    @Operation(summary = "View all jobs posted by current recruiter")
    public ResponseEntity<ApiResponse<List<JobDto>>> getMyJobs(Principal principal) {
        List<JobDto> result = jobService.getOwnJobs(principal.getName());
        return ResponseEntity.ok(ApiResponse.success("Jobs retrieved successfully", result));
    }

    @GetMapping("/applicants")
    @Operation(summary = "View all applicants who applied to recruiter's jobs")
    public ResponseEntity<ApiResponse<List<ApplicationDto>>> getApplicants(Principal principal) {
        List<ApplicationDto> result = recruiterService.getApplicants(principal.getName());
        return ResponseEntity.ok(ApiResponse.success("Applicants retrieved successfully", result));
    }

    @GetMapping("/applicants/search")
    @Operation(summary = "Search applicants by name, email, skills, or education")
    public ResponseEntity<ApiResponse<List<ApplicationDto>>> searchApplicants(
            @RequestParam String query, Principal principal) {
        List<ApplicationDto> result = recruiterService.searchApplicants(query, principal.getName());
        return ResponseEntity.ok(ApiResponse.success("Applicants search completed", result));
    }

    @PostMapping("/applications/{id}/hire")
    @Operation(summary = "Hire candidate and update status to HIRED")
    public ResponseEntity<ApiResponse<Void>> hireCandidate(
            @PathVariable Long id, Principal principal) {
        recruiterService.hireCandidate(id, principal.getName());
        return ResponseEntity.ok(ApiResponse.success("Candidate hired successfully"));
    }

    @PostMapping("/applications/{id}/reject")
    @Operation(summary = "Reject candidate and update status to REJECTED")
    public ResponseEntity<ApiResponse<Void>> rejectCandidate(
            @PathVariable Long id,
            @RequestParam(required = false) String reason,
            Principal principal) {
        recruiterService.rejectCandidate(id, reason, principal.getName());
        return ResponseEntity.ok(ApiResponse.success("Candidate rejected successfully"));
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get recruiter dashboard analytics")
    public ResponseEntity<ApiResponse<DashboardRecruiterDto>> getDashboard(Principal principal) {
        DashboardRecruiterDto result = recruiterService.getDashboardStats(principal.getName());
        return ResponseEntity.ok(ApiResponse.success("Dashboard metrics retrieved successfully", result));
    }
}
