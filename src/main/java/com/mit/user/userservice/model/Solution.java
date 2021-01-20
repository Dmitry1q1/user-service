package com.mit.user.userservice.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Solution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    public Solution(long userId, long problemId, LocalDateTime solutionDate, String solutionText, String solutionStatus, String statusDescription) {
        this.userId = userId;
        this.problemId = problemId;
        this.solutionDate = solutionDate;
        this.solutionText = solutionText;
        this.solutionStatus = solutionStatus;
        this.statusDescription = statusDescription;
    }

    //    @OneToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @Column(name = "user_id")
    private long userId;

//    @OneToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "problems_id", referencedColumnName = "problems_id")
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
