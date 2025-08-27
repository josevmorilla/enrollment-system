package com.champlain.courseservice.presentationlayer;

import com.champlain.courseservice.dataaccesslayer.Course;

import java.util.UUID;

public record CourseResponseModel(String courseId,
                                  String courseNumber,
                                  String courseName,
                                  Integer numHours,
                                  Double numCredits,
                                  String department) {

}
