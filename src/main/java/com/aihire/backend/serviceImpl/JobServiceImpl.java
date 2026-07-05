package com.aihire.backend.serviceImpl;

import com.aihire.backend.dto.JobDto;
import com.aihire.backend.entity.Job;
import com.aihire.backend.entity.User;
import com.aihire.backend.exception.BadRequestException;
import com.aihire.backend.exception.ResourceNotFoundException;
import com.aihire.backend.mapper.EntityMapper;
import com.aihire.backend.repository.JobRepository;
import com.aihire.backend.repository.UserRepository;
import com.aihire.backend.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final EntityMapper mapper;

    @Override
    @Transactional
    public JobDto createJob(JobDto jobDto, String recruiterEmail) {
        User recruiter = userRepository.findByEmail(recruiterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter not found"));

        Job job = mapper.map(jobDto, Job.class);
        job.setRecruiter(recruiter);
        if (job.getStatus() == null) {
            job.setStatus("ACTIVE");
        }

        Job savedJob = jobRepository.save(job);
        return convertToDto(savedJob);
    }

    @Override
    @Transactional
    public JobDto updateJob(Long id, JobDto jobDto, String recruiterEmail) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        if (!job.getRecruiter().getEmail().equalsIgnoreCase(recruiterEmail)) {
            throw new BadRequestException("You are not authorized to update this job");
        }

        job.setTitle(jobDto.getTitle());
        job.setDescription(jobDto.getDescription());
        job.setCompany(jobDto.getCompany());
        job.setLocation(jobDto.getLocation());
        job.setSalary(jobDto.getSalary());
        job.setExperience(jobDto.getExperience());
        job.setEmploymentType(jobDto.getEmploymentType());
        job.setSkillsRequired(jobDto.getSkillsRequired());
        job.setLastDate(jobDto.getLastDate());
        if (jobDto.getStatus() != null) {
            job.setStatus(jobDto.getStatus());
        }

        Job updatedJob = jobRepository.save(job);
        return convertToDto(updatedJob);
    }

    @Override
    @Transactional
    public void deleteJob(Long id, String recruiterEmail) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        if (!job.getRecruiter().getEmail().equalsIgnoreCase(recruiterEmail)) {
            throw new BadRequestException("You are not authorized to delete this job");
        }

        jobRepository.delete(job);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobDto> getOwnJobs(String recruiterEmail) {
        User recruiter = userRepository.findByEmail(recruiterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter not found"));

        List<Job> jobs = jobRepository.findByRecruiterIdOrderByPostedDateDesc(recruiter.getId());
        return jobs.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public JobDto getJobById(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with ID: " + id));
        return convertToDto(job);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobDto> searchJobs(String title, String location, String company, String skills, String employmentType, String salary, String experience, String status, Pageable pageable) {
        String searchStatus = status != null ? status : "ACTIVE";
        Page<Job> jobsPage = jobRepository.searchJobs(title, location, company, skills, employmentType, salary, experience, searchStatus, pageable);
        return jobsPage.map(this::convertToDto);
    }

    private JobDto convertToDto(Job job) {
        JobDto dto = mapper.map(job, JobDto.class);
        if (job.getRecruiter() != null) {
            dto.setRecruiterId(job.getRecruiter().getId());
            dto.setRecruiterName(job.getRecruiter().getFullName());
        }
        return dto;
    }
}
