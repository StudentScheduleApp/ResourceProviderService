package com.studentscheduleapp.resourceproviderservice.properties.services;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class DatabaseServiceProperties {

    @Value("${databaseservice.uri}")
    private String uri;

    //CustomLessons
    @Value("${databaseservice.path.customLesson.getById}")
    private String getCustomLessonByIdPath;
    @Value("${databaseservice.path.customLesson.getByGroupId}")
    private String getCustomLessonByGroupIdPath;
    @Value("${databaseservice.path.customLesson.save}")
    private String saveCustomLessonPath;
    @Value("${databaseservice.path.customLesson.delete}")
    private String deleteCustomLessonPath;

    //Groups
    @Value("${databaseservice.path.group.getById}")
    private String getGroupByIdPath;
    @Value("${databaseservice.path.group.save}")
    private String saveGroupPath;
    @Value("${databaseservice.path.group.delete}")
    private String deleteGroupPath;

    //LessonTemplate
    @Value("${databaseservice.path.lessonTemplate.getById}")
    private String getLessonTemplateByIdPath;
    @Value("${databaseservice.path.lessonTemplate.getByScheduleTemplateId}")
    private String getLessonTemplateByScheduleTemplateIdPath;
    @Value("${databaseservice.path.lessonTemplate.save}")
    private String saveLessonTemplatePath;
    @Value("${databaseservice.path.lessonTemplate.delete}")
    private String deleteLessonTemplatePath;

    //Member
    @Value("${databaseservice.path.member.getById}")
    private String getMemberByIdPath;
    @Value("${databaseservice.path.member.getByGroupId}")
    private String getMemberByGroupIdPath;
    @Value("${databaseservice.path.member.getByUserId}")
    private String getMemberByUserIdPath;
    @Value("${databaseservice.path.member.save}")
    private String saveMemberPath;
    @Value("${databaseservice.path.member.delete}")
    private String deleteMemberPath;

    //OutlineMediaComment
    @Value("${databaseservice.path.outlineMediaComment.getById}")
    private String getOutlineMediaCommentByIdPath;
    @Value("${databaseservice.path.outlineMediaComment.getByOutlineMediaId}")
    private String getOutlineMediaCommentByOutlineMediaIdPath;
    @Value("${databaseservice.path.outlineMediaComment.save}")
    private String saveOutlineMediaCommentPath;
    @Value("${databaseservice.path.outlineMediaComment.delete}")
    private String deleteOutlineMediaCommentPath;

    //OutlineMedia
    @Value("${databaseservice.path.outlineMedia.getById}")
    private String getOutlineMediaByIdPath;
    @Value("${databaseservice.path.outlineMedia.getByOutlineId}")
    private String getOutlineMediaByOutlineIdPath;
    @Value("${databaseservice.path.outlineMedia.save}")
    private String saveOutlineMediaPath;
    @Value("${databaseservice.path.outlineMedia.delete}")
    private String deleteOutlineMediaPath;

    //Outline
    @Value("${databaseservice.path.outline.getById}")
    private String getOutlineByIdPath;
    @Value("${databaseservice.path.outline.getBySpecificLessonId}")
    private String getOutlineBySpecificLessonIdPath;
    @Value("${databaseservice.path.outline.getByUserId}")
    private String getOutlineByUserIdPath;
    @Value("${databaseservice.path.outline.save}")
    private String saveOutlinePath;
    @Value("${databaseservice.path.outline.delete}")
    private String deleteOutlinePath;

    //ScheduleTemplate
    @Value("${databaseservice.path.scheduleTemplate.getById}")
    private String getScheduleTemplateByIdPath;
    @Value("${databaseservice.path.scheduleTemplate.getByGroupId}")
    private String getScheduleTemplateByGroupIdPath;
    @Value("${databaseservice.path.scheduleTemplate.save}")
    private String saveScheduleTemplatePath;
    @Value("${databaseservice.path.scheduleTemplate.delete}")
    private String deleteScheduleTemplatePath;

    //SpecificLesson
    @Value("${databaseservice.path.specificLesson.getById}")
    private String getSpecificLessonByIdPath;
    @Value("${databaseservice.path.specificLesson.getByGroupId}")
    private String getSpecificLessonByGroupIdPath;
    @Value("${databaseservice.path.specificLesson.save}")
    private String saveSpecificLessonPath;
    @Value("${databaseservice.path.specificLesson.delete}")
    private String deleteSpecificLessonPath;

    //User
    @Value("${databaseservice.path.user.getById}")
    private String getUserByIdPath;
    @Value("${databaseservice.path.user.getByEmail}")
    private String getUserByEmailPath;
    @Value("${databaseservice.path.user.save}")
    private String saveUserPath;
    @Value("${databaseservice.path.user.delete}")
    private String deleteUserPath;


}
