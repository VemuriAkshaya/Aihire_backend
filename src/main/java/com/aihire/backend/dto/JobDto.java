package com.aihire.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobDto {
    private Long id;

    @NotBlank(message = "Job title is required")
    private String title;

    @NotBlank(message = "Job description is required")
    private String description;

    @NotBlank(message = "Company is required")
    private String company;

    @NotBlank(message = "Location is required")
    private String location;

    private String salary;
    private String experience;
    private String employmentType;
    private String skillsRequired;
    private LocalDateTime postedDate;
    private LocalDate lastDate;
    private String status; // ACTIVE, CLOSED
    
    private Long recruiterId;
    private String recruiterName;
}
