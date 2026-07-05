package com.aihire.backend.service;

import com.aihire.backend.dto.InterviewDto;

import java.util.List;

public interface InterviewService {
    InterviewDto createInterview(InterviewDto dto, String recruiterEmail);
    InterviewDto updateInterview(Long id, InterviewDto dto, String recruiterEmail);
    void deleteInterview(Long id, String recruiterEmail);
    InterviewDto getInterviewById(Long id, String userEmail);
    List<InterviewDto> getAllInterviews(String recruiterEmail);
}
