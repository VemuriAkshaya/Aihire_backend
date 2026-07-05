package com.aihire.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "jobs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(nullable = false)
    private String company;

    @Column(nullable = false)
    private String location;

    private String salary;

    private String experience;

    private String employmentType;

    @Column(columnDefinition = "TEXT")
    private String skillsRequired;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime postedDate;

    private LocalDate lastDate;

    @Column(nullable = false)
    private String status; // ACTIVE, CLOSED

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruiter_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User recruiter;
}
