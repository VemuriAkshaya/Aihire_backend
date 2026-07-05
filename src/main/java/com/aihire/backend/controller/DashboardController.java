package com.aihire.backend.controller;

import com.aihire.backend.dto.DashboardJobSeekerDto;
import com.aihire.backend.dto.DashboardRecruiterDto;
import com.aihire.backend.response.ApiResponse;
import com.aihire.backend.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/dashboards")
@RequiredArgsConstructor
@Tag(name = "Dashboard Controller", description = "Endpoints for fetching statistics and analytics for job seeker and recruiter dashboards")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/jobseeker")
    @Operation(summary = "Get Dashboard statistics for job seekers")
    public ResponseEntity<ApiResponse<DashboardJobSeekerDto>> getJobSeekerDashboard(Principal principal) {
        DashboardJobSeekerDto stats = dashboardService.getJobSeekerDashboard(principal.getName());
        return ResponseEntity.ok(ApiResponse.success("Job Seeker dashboard retrieved successfully", stats));
    }

    @GetMapping("/recruiter")
    @Operation(summary = "Get Dashboard statistics for recruiters")
    public ResponseEntity<ApiResponse<DashboardRecruiterDto>> getRecruiterDashboard(Principal principal) {
        DashboardRecruiterDto stats = dashboardService.getRecruiterDashboard(principal.getName());
        return ResponseEntity.ok(ApiResponse.success("Recruiter dashboard retrieved successfully", stats));
    }
}
