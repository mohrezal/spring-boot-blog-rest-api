package com.github.mohrezal.api.domains.users.scheduleds;

import com.github.mohrezal.api.domains.users.repositories.RefreshTokenRepository;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenCleanupTask {

    private final RefreshTokenRepository refreshTokenRepository;

    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void cleanupExpiredTokens() {
        OffsetDateTime threshold = OffsetDateTime.now().minusDays(30);
        log.info("Starting cleanup of expired refresh tokens older than {}", threshold);

        refreshTokenRepository.deleteExpiredTokens(threshold);
        log.info("Successfully cleaned up expired refresh tokens");
    }
}
