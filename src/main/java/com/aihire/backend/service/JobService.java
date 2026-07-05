package com.aihire.backend.service;

import com.aihire.backend.dto.JobDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface JobService {
    JobDto createJob(JobDto jobDto, String recruiterEmail);
    JobDto updateJob(Long id, JobDto jobDto, String recruiterEmail);
    void deleteJob(Long id, String recruiterEmail);
    List<JobDto> getOwnJobs(String recruiterEmail);
    JobDto getJobById(Long id);
    Page<JobDto> searchJobs(String title, String location, String company, String skills, String employmentType, String salary, String experience, String status, Pageable pageable);
}
