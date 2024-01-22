package com.studentscheduleapp.resourceproviderservice.properties.services;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class ImageServiceProperties {

    @Value("${imageservice.uri}")
    private String uri;

    @Value("${imageservice.path.upload}")
    private String getUploadPath;
    @Value("${imageservice.path.delete}")
    private String getDeletePath;



}
