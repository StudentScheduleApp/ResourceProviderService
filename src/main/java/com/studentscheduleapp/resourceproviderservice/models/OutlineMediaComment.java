package com.studentscheduleapp.resourceproviderservice.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutlineMediaComment {
    private long id;
    private String text;
    private long userId;
    private long timestamp;
    private long mediaId;
    private long questionCommentId;
}
