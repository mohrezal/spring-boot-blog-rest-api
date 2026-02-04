package com.github.mohrezal.api.shared.services.deviceinfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.github.mohrezal.api.support.constants.UserAgents;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RequestInfoServiceImplTest {

    private static final String IP = "10.0.0.1";

    @Mock private HttpServletRequest request;

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
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn(IP);

        assertEquals(IP, requestInfoService.getClientIp(request));
    }

    @Test
    void getClientIp_whenXForwardedForIsEmpty_shouldReturnRemoteAddr() {
        when(request.getHeader("X-Forwarded-For")).thenReturn("");
        when(request.getRemoteAddr()).thenReturn(IP);

        assertEquals(IP, requestInfoService.getClientIp(request));
    }

    @Test
    void getClientIp_whenXForwardedForHasSingleIp_shouldReturnThatIp() {
        when(request.getHeader("X-Forwarded-For")).thenReturn(IP);

        assertEquals(IP, requestInfoService.getClientIp(request));
    }

    @Test
    void getClientIp_whenXForwardedForHasMultipleIps_shouldReturnFirstIp() {
        when(request.getHeader("X-Forwarded-For")).thenReturn(IP + ", 10.0.0.2, 10.0.0.3");

        assertEquals(IP, requestInfoService.getClientIp(request));
    }

    @Test
    void getClientIp_whenXForwardedForHasSpaces_shouldReturnTrimmedIp() {
        when(request.getHeader("X-Forwarded-For")).thenReturn("  " + IP + "  ");

        assertEquals(IP, requestInfoService.getClientIp(request));
    }

    @Test
    void getClientIp_whenXForwardedForHasMultipleIpsWithSpaces_shouldReturnFirstTrimmedIp() {
        when(request.getHeader("X-Forwarded-For")).thenReturn(" " + IP + " , 10.0.0.2 ");

        assertEquals(IP, requestInfoService.getClientIp(request));
    }
}
