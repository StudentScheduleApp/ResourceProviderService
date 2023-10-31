package com.studentscheduleapp.resourceproviderservice.services;

import com.studentscheduleapp.resourceproviderservice.repos.*;
import com.studentscheduleapp.resourceproviderservice.models.LessonTemplate;
import com.studentscheduleapp.resourceproviderservice.models.ScheduleTemplate;
import com.studentscheduleapp.resourceproviderservice.models.SpecificLesson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Service
public class ScheduleService {
    private static final long WEEK_LENGTH = 604800000L;
    private static final SimpleDateFormat format = new SimpleDateFormat("dd HH:mm");


    @Autowired
    private LessonTemplateRepository lessonTemplateRepository;
    @Autowired
    private SpecificLessonRepository specificLessonRepository;
    @Autowired
    private ScheduleTemplateRepository scheduleTemplateRepository;
    @Autowired
    private OutlineRepository outlineRepository;
    @Autowired
    private OutlineMediaRepository outlineMediaRepository;
    @Autowired
    private OutlineMediaCommentRepository outlineMediaCommentRepository;

    private ArrayList<SpecificLesson> scheduleLessons(long startTimestamp, long endTimestamp, List<LessonTemplate> schedule, long groupId) {
        final Calendar weekStartCalendar = Calendar.getInstance();
        weekStartCalendar.setTimeInMillis(startTimestamp);
        weekStartCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        weekStartCalendar.set(Calendar.HOUR, 0);
        weekStartCalendar.set(Calendar.MINUTE, 0);
        weekStartCalendar.set(Calendar.SECOND, 0);
        weekStartCalendar.set(Calendar.MILLISECOND, 0);
        final long weekStartTime = weekStartCalendar.getTimeInMillis() - 86400000L/2;
        int week = 0;
        ArrayList<SpecificLesson> generatedLessons = new ArrayList<>();
        while ((weekStartTime + WEEK_LENGTH*week) < endTimestamp) {
            for (LessonTemplate lesson: schedule) {
                long lessonTime = weekStartTime + week*WEEK_LENGTH + lesson.getTime();
                if (lessonTime < startTimestamp) continue;
                if (lessonTime > endTimestamp) break;
                SpecificLesson newLesson = new SpecificLesson(0, groupId, lesson.getLessonId(),
                        lessonTime, false, lesson.getComment());
                generatedLessons.add(newLesson);
            }
            week++;
        }
        return generatedLessons;
    }

    public void updateSchedule(long scheduleId) throws Exception {
      //  ScheduleTemplate st = scheduleTemplateRepository.getById(scheduleId);
      //  ArrayList<LessonTemplate> lts = (ArrayList<LessonTemplate>) lessonTemplateRepository.getByScheduleTemplateId(st.getId());
      //  ArrayList<SpecificLesson> sls = scheduleLessons(st.getTimeStart(), st.getTimeStop(), lts, st.getGroupId());
      //  ArrayList<SpecificLesson> slsold = (ArrayList<SpecificLesson>) specificLessonRepository.getByGroupId(st.getGroupId());
      //  for (SpecificLesson sl : slsold){
      //      if(sl.getTime() > System.currentTimeMillis()) {
      //          specificLessonRepository.delete(sl.getId());
      //          ArrayList<SpecificLessonMedia> slms = (ArrayList<SpecificLessonMedia>) specificLessonMediaRepository.findSpecificLessonMediaBySpecificLessonId(sl.getId()).get();
      //          for (SpecificLessonMedia slm : slms){
      //              specificLessonMediaCommentRepository.deleteByMediaId(slm.getId());
      //          }
      //          specificLessonMediaRepository.deleteSpecificLessonMediaBySpecificLessonId(sl.getId());
      //      }
      //  }
      //  for (SpecificLesson sl : sls){
      //      specificLessonRepository.save(sl);
      //  }
    }

}
