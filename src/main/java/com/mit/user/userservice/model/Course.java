package com.mit.user.userservice.model;

import javax.persistence.Entity;
import javax.persistence.*;
import java.util.List;

@Entity
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "course_id")
    private Long id;

    @Column(name = "course_name")
    private String courseName;

    @Column(name = "course_main_picture")
    private String courseMainPictureUrl;

//    @OneToMany
//    @JoinTable(name = "course_problems",
//            uniqueConstraints = {@UniqueConstraint(columnNames = {"problems_id", "course_course_id"})},
//            joinColumns = @JoinColumn(name = "course_course_id"),
//            inverseJoinColumns = @JoinColumn(name = "problems_id"))
//    private List<Problem> problems;
//
//    @ManyToMany(mappedBy = "courses")
//    private List<User> users;

    @Column(name = "course_description")
    private String courseDescription;

    @Column(name = "course_duration")
    private String courseDuration;

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

    public void setId(Long id) {
        this.id = id;
    }

    public String getCourseMainPictureUrl() {
        return courseMainPictureUrl;
    }

    public void setCourseMainPictureUrl(String courseMainPictureUrl) {
        this.courseMainPictureUrl = courseMainPictureUrl;
    }

    //    public List<Problem> getProblems() {
//        return problems;
//    }
//
//    public void setProblems(List<Problem> problems) {
//        this.problems = problems;
//    }
//
//    public List<User> getUsers() {
//        return users;
//    }
//
//    public void setUsers(List<User> users) {
//        this.users = users;
//    }
}
