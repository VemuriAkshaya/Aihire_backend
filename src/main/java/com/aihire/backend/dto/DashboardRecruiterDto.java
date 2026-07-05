package com.aihire.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardRecruiterDto {
    private long totalJobs;
    private long applicants;
    private long interviews;
    private long hired;
    private long rejected;
}
