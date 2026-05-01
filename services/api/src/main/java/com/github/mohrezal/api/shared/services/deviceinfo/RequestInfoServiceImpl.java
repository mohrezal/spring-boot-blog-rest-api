package com.github.mohrezal.api.shared.services.deviceinfo;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class RequestInfoServiceImpl implements RequestInfoService {

    @Override
    public String parseDeviceName(String userAgent) {
        if (userAgent == null || userAgent.isEmpty()) {
            return "Unknown Device";
        }

        if (userAgent.contains("iPhone")) {
            return "iPhone";
        }

        if (userAgent.contains("iPad")) {
            return "iPad";
        }

        if (userAgent.contains("Android")) {
            return "Android Device";
        }

        if (userAgent.contains("Mobile")) {
            return "Mobile Device";
        }

        if (userAgent.contains("Windows")) {
            return "Windows PC";
        }

        if (userAgent.contains("Mac")) {
            return "Mac";
        }

        if (userAgent.contains("Linux")) {
            return "Linux PC";
        }

        return "Unknown Device";
    }

    @Override
    public String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isEmpty()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
