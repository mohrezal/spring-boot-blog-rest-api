package com.github.mohrezal.springbootblogrestapi.shared.services.deviceinfo;

import jakarta.servlet.http.HttpServletRequest;

public interface RequestInfoService {
    String parseDeviceName(String userAgent);

    String getClientIp(HttpServletRequest request);
}
