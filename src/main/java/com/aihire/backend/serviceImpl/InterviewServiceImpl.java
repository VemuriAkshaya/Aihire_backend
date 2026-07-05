package com.aihire.backend.serviceImpl;

import com.aihire.backend.dto.InterviewDto;
import com.aihire.backend.entity.*;
import com.aihire.backend.exception.BadRequestException;
import com.aihire.backend.exception.ResourceNotFoundException;
import com.aihire.backend.mapper.EntityMapper;
import com.aihire.backend.repository.ApplicationRepository;
import com.aihire.backend.repository.InterviewRepository;
import com.aihire.backend.repository.UserRepository;
import com.aihire.backend.service.InterviewService;
import com.aihire.backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InterviewServiceImpl implements InterviewService {

    private final InterviewRepository interviewRepository;
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final EntityMapper mapper;

    @Override
    @Transactional
    public InterviewDto createInterview(InterviewDto dto, String recruiterEmail) {
        Application application = applicationRepository.findById(dto.getApplicationId())
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        if (!application.getJob().getRecruiter().getEmail().equalsIgnoreCase(recruiterEmail)) {
            throw new BadRequestException("You are not authorized to schedule interviews for this application");
        }

        application.setStatus(ApplicationStatus.INTERVIEW_SCHEDULED);
        applicationRepository.save(application);

        Interview interview = Interview.builder()
                .application(application)
                .interviewType(InterviewType.valueOf(dto.getInterviewType().toUpperCase()))
                .date(dto.getDate())
                .time(dto.getTime())
                .meetingLink(dto.getMeetingLink())
                .venue(dto.getVenue())
                .interviewerName(dto.getInterviewerName())
                .notes(dto.getNotes())
                .status("SCHEDULED")
                .build();

        Interview saved = interviewRepository.save(interview);

        String message = "A new " + saved.getInterviewType().name() + " interview has been scheduled for the position of '" + 
                application.getJob().getTitle() + "' at " + application.getJob().getCompany() + 
                " on " + saved.getDate() + " at " + saved.getTime() + ".";
        if (saved.getInterviewType() == InterviewType.ONLINE && saved.getMeetingLink() != null) {
            message += " Meeting Link: " + saved.getMeetingLink();
        } else if (saved.getInterviewType() == InterviewType.OFFLINE && saved.getVenue() != null) {
            message += " Venue: " + saved.getVenue();
        }

        notificationService.createNotification(
                application.getJobSeeker(),
                "Interview Scheduled",
                message
        );

        return convertToDto(saved);
    }

    @Override
    @Transactional
    public InterviewDto updateInterview(Long id, InterviewDto dto, String recruiterEmail) {
        Interview interview = interviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found"));

        if (!interview.getApplication().getJob().getRecruiter().getEmail().equalsIgnoreCase(recruiterEmail)) {
            throw new BadRequestException("You are not authorized to update this interview");
        }

        interview.setInterviewType(InterviewType.valueOf(dto.getInterviewType().toUpperCase()));
        interview.setDate(dto.getDate());
        interview.setTime(dto.getTime());
        interview.setMeetingLink(dto.getMeetingLink());
        interview.setVenue(dto.getVenue());
        interview.setInterviewerName(dto.getInterviewerName());
        interview.setNotes(dto.getNotes());
        if (dto.getStatus() != null) {
            interview.setStatus(dto.getStatus());
        }

        Interview updated = interviewRepository.save(interview);

        String message = "Your interview for '" + interview.getApplication().getJob().getTitle() + "' at " + 
                interview.getApplication().getJob().getCompany() + " has been updated. New details: " + 
                updated.getDate() + " at " + updated.getTime() + " (Mode: " + updated.getInterviewType().name() + ").";

        notificationService.createNotification(
                interview.getApplication().getJobSeeker(),
                "Interview Details Updated",
                message
        );

        return convertToDto(updated);
    }

    @Override
    @Transactional
    public void deleteInterview(Long id, String recruiterEmail) {
        Interview interview = interviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found"));

        if (!interview.getApplication().getJob().getRecruiter().getEmail().equalsIgnoreCase(recruiterEmail)) {
            throw new BadRequestException("You are not authorized to cancel this interview");
        }

        notificationService.createNotification(
                interview.getApplication().getJobSeeker(),
                "Interview Cancelled",
                "Your scheduled interview for the position '" + interview.getApplication().getJob().getTitle() + 
                "' at " + interview.getApplication().getJob().getCompany() + " has been cancelled."
        );

        interviewRepository.delete(interview);
    }

    @Override
    @Transactional(readOnly = true)
    public InterviewDto getInterviewById(Long id, String userEmail) {
        Interview interview = interviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found"));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean isSeeker = interview.getApplication().getJobSeeker().getId().equals(user.getId());
        boolean isRecruiter = interview.getApplication().getJob().getRecruiter().getId().equals(user.getId());

        if (!isSeeker && !isRecruiter) {
            throw new BadRequestException("You are not authorized to view this interview");
        }

        return convertToDto(interview);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InterviewDto> getAllInterviews(String recruiterEmail) {
        User recruiter = userRepository.findByEmail(recruiterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter not found"));

        List<Interview> list = interviewRepository.findByApplicationJobRecruiterIdOrderByDateDescTimeDesc(recruiter.getId());
        return list.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    private InterviewDto convertToDto(Interview intv) {
        return InterviewDto.builder()
                .id(intv.getId())
                .applicationId(intv.getApplication().getId())
                .jobId(intv.getApplication().getJob().getId())
                .jobTitle(intv.getApplication().getJob().getTitle())
                .candidateId(intv.getApplication().getJobSeeker().getId())
                .candidateName(intv.getApplication().getJobSeeker().getFullName())
                .candidateEmail(intv.getApplication().getJobSeeker().getEmail())
                .interviewType(intv.getInterviewType().name())
                .date(intv.getDate())
                .time(intv.getTime())
                .meetingLink(intv.getMeetingLink())
                .venue(intv.getVenue())
                .interviewerName(intv.getInterviewerName())
                .notes(intv.getNotes())
                .status(intv.getStatus())
                .build();
    }
}
