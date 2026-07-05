package com.aihire.backend.service;

import com.aihire.backend.dto.DashboardJobSeekerDto;
import com.aihire.backend.dto.DashboardRecruiterDto;

public interface DashboardService {
    DashboardJobSeekerDto getJobSeekerDashboard(String email);
    DashboardRecruiterDto getRecruiterDashboard(String email);
}
