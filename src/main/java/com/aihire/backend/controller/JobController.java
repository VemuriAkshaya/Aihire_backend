package com.aihire.backend.controller;

import com.aihire.backend.dto.JobDto;
import com.aihire.backend.response.ApiResponse;
import com.aihire.backend.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@Tag(name = "Job Controller", description = "Public endpoints for searching and viewing job listings")
public class JobController {

    private final JobService jobService;

    @GetMapping("/search")
    @Operation(summary = "Search jobs with filters, pagination, and sorting")
    public ResponseEntity<ApiResponse<Page<JobDto>>> searchJobs(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String company,
            @RequestParam(required = false) String skills,
            @RequestParam(required = false) String employmentType,
            @RequestParam(required = false) String salary,
            @RequestParam(required = false) String experience,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "postedDate,desc") String sort) {

        String[] sortParts = sort.split(",");
        Sort.Direction direction = Sort.Direction.DESC;
        String property = "postedDate";

        if (sortParts.length == 2) {
            property = sortParts[0];
            direction = Sort.Direction.fromString(sortParts[1]);
        } else if (sortParts.length == 1 && !sortParts[0].isBlank()) {
            property = sortParts[0];
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, property));
        Page<JobDto> result = jobService.searchJobs(title, location, company, skills, employmentType, salary, experience, status, pageable);
        return ResponseEntity.ok(ApiResponse.success("Jobs retrieved successfully", result));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get job details by job ID")
    public ResponseEntity<ApiResponse<JobDto>> getJobById(@PathVariable Long id) {
        JobDto job = jobService.getJobById(id);
        return ResponseEntity.ok(ApiResponse.success("Job retrieved successfully", job));
    }
}
