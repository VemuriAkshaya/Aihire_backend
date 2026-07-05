package com.aihire.backend.repository;

import com.aihire.backend.entity.Application;
import com.aihire.backend.entity.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    
    List<Application> findByJobSeekerIdOrderByAppliedDateDesc(Long jobSeekerId);
    
    List<Application> findByJobIdOrderByAppliedDateDesc(Long jobId);
    
    List<Application> findByJobRecruiterIdOrderByAppliedDateDesc(Long recruiterId);
    
    boolean existsByJobIdAndJobSeekerId(Long jobId, Long jobSeekerId);
    
    long countByJobSeekerId(Long jobSeekerId);
    
    long countByJobSeekerIdAndStatus(Long jobSeekerId, ApplicationStatus status);
    
    long countByJobRecruiterId(Long recruiterId);
    
    long countByJobRecruiterIdAndStatus(Long recruiterId, ApplicationStatus status);

    @Query("SELECT a FROM Application a WHERE a.job.recruiter.id = :recruiterId AND " +
           "(:query IS NULL OR " +
           "LOWER(a.jobSeeker.fullName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(a.jobSeeker.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(a.jobSeeker.jobSeekerProfile.skills) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(a.jobSeeker.jobSeekerProfile.education) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Application> searchApplicants(
        @Param("recruiterId") Long recruiterId,
        @Param("query") String query
    );
}
