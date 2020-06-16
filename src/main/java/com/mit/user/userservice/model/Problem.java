package com.mit.user.userservice.model;

import javax.persistence.*;

@Entity
public class Problem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String problemName;

    private String problemText;

    private long problemTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProblemName() {
        return problemName;
    }

    public void setProblemName(String problemName) {
        this.problemName = problemName;
    }

    public String getProblemText() {
        return problemText;
    }

    public void setProblemText(String problemText) {
        this.problemText = problemText;
    }

    public long getProblemTime() {
        return problemTime;
    }

    public void setProblemTime(long problemTime) {
        this.problemTime = problemTime;
    }
}
