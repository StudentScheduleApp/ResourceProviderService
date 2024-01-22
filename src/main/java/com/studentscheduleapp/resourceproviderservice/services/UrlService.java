package com.studentscheduleapp.resourceproviderservice.services;

import org.springframework.stereotype.Service;

@Service
public class UrlService {

    public String getNameFromImageUrl(String url){
        return url.split("/")[url.split("/").length - 1];
    }

}
