package com.aihire.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardJobSeekerDto {
    private long totalApplications;
    private long interviews;
    private long selected;
    private long rejected;
}
