package com.aihire.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobSeekerProfileDto {
    private Long id;
    private String education;
    private String experience;
    private String skills;
    private String resumeUrl;
    private String location;
    private String about;
    private String profileImage;
}
