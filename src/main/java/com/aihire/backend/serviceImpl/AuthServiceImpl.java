package com.aihire.backend.serviceImpl;

import com.aihire.backend.dto.*;
import com.aihire.backend.entity.JobSeekerProfile;
import com.aihire.backend.entity.RecruiterProfile;
import com.aihire.backend.entity.Role;
import com.aihire.backend.entity.User;
import com.aihire.backend.exception.BadRequestException;
import com.aihire.backend.exception.ResourceNotFoundException;
import com.aihire.backend.jwt.JwtTokenProvider;
import com.aihire.backend.mapper.EntityMapper;
import com.aihire.backend.repository.JobSeekerProfileRepository;
import com.aihire.backend.repository.RecruiterProfileRepository;
import com.aihire.backend.repository.UserRepository;
import com.aihire.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JobSeekerProfileRepository jobSeekerProfileRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final EntityMapper mapper;

    @Override
    @Transactional
    public AuthResponse registerJobSeeker(RegisterJobSeekerRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(Role.ROLE_JOB_SEEKER)
                .build();

        User savedUser = userRepository.save(user);

        JobSeekerProfile profile = JobSeekerProfile.builder()
                .user(savedUser)
                .build();
        jobSeekerProfileRepository.save(profile);

        return getAuthResponseForUser(savedUser);
    }

    @Override
    @Transactional
    public AuthResponse registerRecruiter(RegisterRecruiterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(Role.ROLE_RECRUITER)
                .build();

        User savedUser = userRepository.save(user);

        RecruiterProfile profile = RecruiterProfile.builder()
                .user(savedUser)
                .companyName(request.getCompanyName())
                .companyWebsite(request.getCompanyWebsite())
                .companyLocation(request.getCompanyLocation())
                .designation(request.getDesignation())
                .aboutCompany(request.getAboutCompany())
                .build();
        recruiterProfileRepository.save(profile);

        return getAuthResponseForUser(savedUser);
    }

    @Override
    public AuthResponse login(AuthRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            String token = tokenProvider.generateToken(authentication);
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            return AuthResponse.builder()
                    .token(token)
                    .role(user.getRole().name())
                    .email(user.getEmail())
                    .userId(user.getId())
                    .fullName(user.getFullName())
                    .build();
        } catch (Exception ex) {
            throw new BadRequestException("Invalid Credentials");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        UserProfileResponse response = UserProfileResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole().name())
                .build();

        if (user.getRole() == Role.ROLE_JOB_SEEKER) {
            JobSeekerProfile profile = jobSeekerProfileRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Job seeker profile not found"));
            response.setJobSeekerProfile(mapper.map(profile, JobSeekerProfileDto.class));
        } else if (user.getRole() == Role.ROLE_RECRUITER) {
            RecruiterProfile profile = recruiterProfileRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Recruiter profile not found"));
            response.setRecruiterProfile(mapper.map(profile, RecruiterProfileDto.class));
        }

        return response;
    }

    private AuthResponse getAuthResponseForUser(User user) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                new com.aihire.backend.security.UserPrincipal(user), null,
                new com.aihire.backend.security.UserPrincipal(user).getAuthorities()
        );
        String token = tokenProvider.generateToken(authenticationToken);

        return AuthResponse.builder()
                .token(token)
                .role(user.getRole().name())
                .email(user.getEmail())
                .userId(user.getId())
                .fullName(user.getFullName())
                .build();
    }
}
