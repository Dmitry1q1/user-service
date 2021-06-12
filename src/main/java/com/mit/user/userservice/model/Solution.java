package com.mit.user.userservice.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class Solution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "user_id")
    private long userId;

    @Column(name = "problems_id")
    private long problemId;

    @Column(name = "solution_date")
    private LocalDateTime solutionDate;

    @Column(name = "solution_text")
    private String solutionText;

    @Column(name = "solution_status")
    private String solutionStatus;

    @Column(name = "status_description")
    private String statusDescription;

    @Column(name = "programming_language_id")
    private Long programmingLanguageId;

    public Long getProgrammingLanguageId() {
        return programmingLanguageId;
    }

    public void setProgrammingLanguageId(Long programmingLanguageId) {
        this.programmingLanguageId = programmingLanguageId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getProblemId() {
        return problemId;
    }

    public void setProblemId(long problemId) {
        this.problemId = problemId;
    }

    public LocalDateTime getSolutionDate() {
        return solutionDate;
    }

    public void setSolutionDate(LocalDateTime solutionDate) {
        this.solutionDate = solutionDate;
    }

    public String getSolutionText() {
        return solutionText;
    }

    public void setSolutionText(String solutionText) {
        this.solutionText = solutionText;
    }

    public String getSolutionStatus() {
        return solutionStatus;
    }

    public void setSolutionStatus(String solutionStatus) {
        this.solutionStatus = solutionStatus;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }
}
