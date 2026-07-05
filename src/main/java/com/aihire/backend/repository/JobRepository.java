package com.aihire.backend.repository;

import com.aihire.backend.entity.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    
    List<Job> findByRecruiterIdOrderByPostedDateDesc(Long recruiterId);
    
    long countByRecruiterId(Long recruiterId);

    @Query("SELECT j FROM Job j WHERE " +
           "(:title IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
           "(:company IS NULL OR LOWER(j.company) LIKE LOWER(CONCAT('%', :company, '%'))) AND " +
           "(:skills IS NULL OR LOWER(j.skillsRequired) LIKE LOWER(CONCAT('%', :skills, '%'))) AND " +
           "(:employmentType IS NULL OR LOWER(j.employmentType) LIKE LOWER(CONCAT('%', :employmentType, '%'))) AND " +
           "(:salary IS NULL OR LOWER(j.salary) LIKE LOWER(CONCAT('%', :salary, '%'))) AND " +
           "(:experience IS NULL OR LOWER(j.experience) LIKE LOWER(CONCAT('%', :experience, '%'))) AND " +
           "(:status IS NULL OR LOWER(j.status) = LOWER(:status))")
    Page<Job> searchJobs(
        @Param("title") String title,
        @Param("location") String location,
        @Param("company") String company,
        @Param("skills") String skills,
        @Param("employmentType") String employmentType,
        @Param("salary") String salary,
        @Param("experience") String experience,
        @Param("status") String status,
        Pageable pageable
    );
}
