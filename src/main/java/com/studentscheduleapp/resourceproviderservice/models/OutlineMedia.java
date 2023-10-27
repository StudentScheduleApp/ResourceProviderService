package com.studentscheduleapp.resourceproviderservice.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutlineMedia {
    private long id;
    private long timestamp;
    private long outlineId;
    private String imageUrl;

}
