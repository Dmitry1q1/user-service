package com.mit.user.userservice.model;

import javax.persistence.Entity;
import javax.persistence.*;
import java.util.List;

@Entity
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "course_id")
    private long id;

    @Column(name = "course_name")
    private String courseName;

    @OneToMany
    @JoinTable(name = "course_problems",
            uniqueConstraints = {@UniqueConstraint(columnNames = {"problems_id","course_course_id"})},
            joinColumns = @JoinColumn(name = "course_course_id"),
            inverseJoinColumns = @JoinColumn(name = "problems_id"))
    private List<Problem> problems;

    @ManyToMany(mappedBy = "courses")
    private List<User> users;

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

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
