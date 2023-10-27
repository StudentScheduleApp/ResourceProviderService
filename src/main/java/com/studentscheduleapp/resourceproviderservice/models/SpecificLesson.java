package com.studentscheduleapp.resourceproviderservice.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpecificLesson {
    private long id;
    private long groupId;
    private long lessonId;
    private long time;
    private Boolean canceled;
    private String comment;

}
