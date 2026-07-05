package com.aihire.backend.controller;

import com.aihire.backend.dto.InterviewDto;
import com.aihire.backend.response.ApiResponse;
import com.aihire.backend.service.InterviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/interviews")
@RequiredArgsConstructor
@Tag(name = "Interview Controller", description = "Endpoints for scheduling, updating, cancelling, and viewing candidate interviews")
public class InterviewController {

    private final InterviewService interviewService;

    @PostMapping
    @Operation(summary = "Schedule a new interview")
    public ResponseEntity<ApiResponse<InterviewDto>> createInterview(
            @Valid @RequestBody InterviewDto dto, Principal principal) {
        InterviewDto result = interviewService.createInterview(dto, principal.getName());
        return ResponseEntity.ok(ApiResponse.success("Interview scheduled successfully", result));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing interview's details")
    public ResponseEntity<ApiResponse<InterviewDto>> updateInterview(
            @PathVariable Long id, @Valid @RequestBody InterviewDto dto, Principal principal) {
        InterviewDto result = interviewService.updateInterview(id, dto, principal.getName());
        return ResponseEntity.ok(ApiResponse.success("Interview updated successfully", result));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel / delete a scheduled interview")
    public ResponseEntity<ApiResponse<Void>> deleteInterview(
            @PathVariable Long id, Principal principal) {
        interviewService.deleteInterview(id, principal.getName());
        return ResponseEntity.ok(ApiResponse.success("Interview cancelled successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "View details of a specific interview")
    public ResponseEntity<ApiResponse<InterviewDto>> getInterviewById(
            @PathVariable Long id, Principal principal) {
        InterviewDto result = interviewService.getInterviewById(id, principal.getName());
        return ResponseEntity.ok(ApiResponse.success("Interview retrieved successfully", result));
    }

    @GetMapping
    @Operation(summary = "View all scheduled interviews (Recruiter only)")
    public ResponseEntity<ApiResponse<List<InterviewDto>>> getAllInterviews(Principal principal) {
        List<InterviewDto> result = interviewService.getAllInterviews(principal.getName());
        return ResponseEntity.ok(ApiResponse.success("Interviews retrieved successfully", result));
    }
}
