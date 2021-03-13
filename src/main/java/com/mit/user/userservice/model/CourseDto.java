package com.mit.user.userservice.model;

import java.util.List;

public class CourseDto {
    private long id;
    private String courseName;
    private String courseDescription;
    private String courseDuration;
    private List<Problem> problems;
//    private List<User> users;
    private List<Long> courseAuthorsId;

    public List<Long> getCourseAuthorsId() {
        return courseAuthorsId;
    }

    public void setCourseAuthorsId(List<Long> courseAuthorsId) {
        this.courseAuthorsId = courseAuthorsId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public List<Problem> getProblems() {
        return problems;
    }

    public void setProblems(List<Problem> problems) {
        this.problems = problems;
    }

//    public List<User> getUsers() {
//        return users;
//    }
//
//    public void setUsers(List<User> users) {
//        this.users = users;
//    }

    public String getCourseDescription() {
        return courseDescription;
    }

    public void setCourseDescription(String courseDescription) {
        this.courseDescription = courseDescription;
    }

    public String getCourseDuration() {
        return courseDuration;
    }

    public void setCourseDuration(String courseDuration) {
        this.courseDuration = courseDuration;
    }
}
