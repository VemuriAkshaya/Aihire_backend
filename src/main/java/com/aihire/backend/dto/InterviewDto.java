package com.aihire.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewDto {
    private Long id;

    @NotNull(message = "Application ID is required")
    private Long applicationId;

    private Long jobId;
    private String jobTitle;
    private Long candidateId;
    private String candidateName;
    private String candidateEmail;

    @NotNull(message = "Interview type is required (ONLINE or OFFLINE)")
    private String interviewType; // ONLINE, OFFLINE

    @NotNull(message = "Interview date is required")
    private LocalDate date;

    @NotNull(message = "Interview time is required")
    private LocalTime time;

    private String meetingLink;
    private String venue;
    private String interviewerName;
    private String notes;
    private String status; // SCHEDULED, COMPLETED, CANCELLED
}
