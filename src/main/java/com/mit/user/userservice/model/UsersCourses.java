package com.mit.user.userservice.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.*;
import java.io.Serializable;

@Entity
public class UsersCourses implements Serializable {
    @Id
    @Column(name = "user_id", nullable = false)
    private long userId;
    @Id
    @Column(name = "course_id", nullable = false)
    private long courseId;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }
}
