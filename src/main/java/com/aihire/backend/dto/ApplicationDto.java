package com.aihire.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationDto {
    private Long id;
    private Long jobId;
    private String jobTitle;
    private String companyName;
    private Long jobSeekerId;
    private String jobSeekerName;
    private String jobSeekerEmail;
    private String resumeUrl;
    private LocalDateTime appliedDate;
    private String status;
}
