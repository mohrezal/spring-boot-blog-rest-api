package com.github.mohrezal.api.shared.services.deviceinfo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.mohrezal.api.support.constants.UserAgents;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

@ExtendWith(MockitoExtension.class)
class RequestInfoServiceImplTest {

    private static final String IP = "10.0.0.1";

    @InjectMocks private RequestInfoServiceImpl requestInfoService;

    @Test
    void parseDeviceName_whenGivenUserAgentIsEmpty_shouldReturnUnknowDevice() {
        assertEquals("Unknown Device", requestInfoService.parseDeviceName(null));
    }

    @Test
    void parseDeviceName_whenGivenUserAgentIsIPhone_shouldReturnIPhon() {
        assertEquals("iPhone", requestInfoService.parseDeviceName(UserAgents.IPHONE));
    }

    @Test
    void parseDeviceName_whenGivenUserAgentIsIPad_shouldReturnIPhon() {
        assertEquals("iPad", requestInfoService.parseDeviceName(UserAgents.IPAD));
    }

    @Test
    void parseDeviceName_whenGivenUserAgentIsAndroid_shouldReturnAndroid() {
        assertEquals("Android Device", requestInfoService.parseDeviceName(UserAgents.ANDROID));
    }

    @Test
    void parseDeviceName_whenGivenUserAgentIsMobile_shouldReturnAndroid() {
        assertEquals("Mobile Device", requestInfoService.parseDeviceName(UserAgents.MOBILE));
    }

    @Test
    void parseDeviceName_whenGivenUserAgentIsWindows_shouldReturnWindows() {
        assertEquals("Windows PC", requestInfoService.parseDeviceName(UserAgents.WINDOWS));
    }

    @Test
    void parseDeviceName_whenGivenUserAgentIsMac_shouldReturnMac() {
        assertEquals("Mac", requestInfoService.parseDeviceName(UserAgents.MAC));
    }

    @Test
    void parseDeviceName_whenGivenUserAgentIsLinux_shouldReturnLinux() {
        assertEquals("Linux PC", requestInfoService.parseDeviceName(UserAgents.LINUX));
    }

    @Test
    void getClientIp_whenXForwardedForIsNull_shouldReturnRemoteAddr() {
        var httpRequest = new MockHttpServletRequest();
        httpRequest.setRemoteAddr(IP);

        assertEquals(IP, requestInfoService.getClientIp(httpRequest));
    }

    @Test
    void getClientIp_whenXForwardedForIsEmpty_shouldReturnRemoteAddr() {
        var httpRequest = new MockHttpServletRequest();
        httpRequest.setRemoteAddr(IP);
        httpRequest.addHeader("X-Forwarded-For", "");

        assertEquals(IP, requestInfoService.getClientIp(httpRequest));
    }

    @Test
    void getClientIp_whenXForwardedForIsSpoofed_shouldReturnRemoteAddr() {
        var httpRequest = new MockHttpServletRequest();
        httpRequest.setRemoteAddr(IP);
        httpRequest.addHeader("X-Forwarded-For", "203.0.113.10, 198.51.100.1");

        assertEquals(IP, requestInfoService.getClientIp(httpRequest));
    }
}
