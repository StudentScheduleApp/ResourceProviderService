package com.studentscheduleapp.resourceproviderservice.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Group {
    private long id;
    private long chatId;
    private long avaUrl;
    private String name;

}
