package com.aihire.backend.service;

import com.aihire.backend.dto.*;

public interface AuthService {
    AuthResponse registerJobSeeker(RegisterJobSeekerRequest request);
    AuthResponse registerRecruiter(RegisterRecruiterRequest request);
    AuthResponse login(AuthRequest request);
    UserProfileResponse getProfile(String email);
}
