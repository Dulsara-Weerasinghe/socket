package com.example.websocket.repo;


import com.example.websocket.entity.LoginChallenge;
import com.example.websocket.model.LoginChallengeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface LoginChallengeRepository extends JpaRepository<LoginChallenge, String> {
    List<LoginChallenge> findByUserIdAndStatus(String userId, LoginChallengeStatus status);
    List<LoginChallenge> findByStatusAndExpiresAtBefore(LoginChallengeStatus status, Instant time);

}
