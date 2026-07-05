package com.aihire.backend.service;

import com.aihire.backend.dto.*;

import java.util.List;

public interface RecruiterService {
    RecruiterProfileDto updateProfile(RecruiterProfileDto dto, String email);
    RecruiterProfileDto getProfile(String email);
    List<ApplicationDto> getApplicants(String email);
    List<ApplicationDto> searchApplicants(String query, String email);
    void hireCandidate(Long applicationId, String email);
    void rejectCandidate(Long applicationId, String reason, String email);
    DashboardRecruiterDto getDashboardStats(String email);
}
