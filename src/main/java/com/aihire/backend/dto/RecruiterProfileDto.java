package com.aihire.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecruiterProfileDto {
    private Long id;
    private String companyName;
    private String companyWebsite;
    private String companyLocation;
    private String designation;
    private String aboutCompany;
}
