package com.github.mohrezal.api.support.constants;

public final class UserAgents {

    private UserAgents() {}

    public static final String IPHONE =
            "Mozilla/5.0 (iPhone; CPU iPhone OS 16_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML,"
                    + " like Gecko) Version/16.0 Mobile/15E148 Safari/604.1";
    public static final String IPAD =
            "Mozilla/5.0 (iPad; CPU OS 16_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko)"
                    + " Version/16.0 Mobile/15E148 Safari/604.1";

    public static final String ANDROID =
            "Mozilla/5.0 (Linux; Android 13; Pixel 7) AppleWebKit/537.36 (KHTML, like Gecko)"
                    + " Chrome/116.0.0.0 Mobile Safari/537.36";

    public static final String MOBILE = "Mozilla/5.0 (Mobile; rv:91.0) Gecko/91.0 Firefox/91.0";

    public static final String WINDOWS =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko)"
                    + " Chrome/116.0.0.0 Safari/537.36";
    public static final String MAC =
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko)"
                    + " Chrome/116.0.0.0 Safari/537.36";
    public static final String LINUX =
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko)"
                    + " Chrome/116.0.0.0 Safari/537.36";

    public static final String UNKNOWN = "SomeRandomBrowser/1.0";
}
