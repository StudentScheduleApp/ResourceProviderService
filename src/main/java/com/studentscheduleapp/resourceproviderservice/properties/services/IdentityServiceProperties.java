package com.studentscheduleapp.resourceproviderservice.properties.services;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class IdentityServiceProperties {

    @Value("${identityservice.uri}")
    private String uri;

    @Value("${identityservice.path.user.login}")
    private String getLoginPath;
    @Value("${identityservice.path.user.refresh}")
    private String getRefreshPath;
    @Value("${identityservice.path.user.register}")
    private String getRegisterPath;
    @Value("${identityservice.path.user.verify}")
    private String getVerifyPath;
    @Value("${identityservice.path.user.authorize}")
    private String getAuthorizePath;




}
