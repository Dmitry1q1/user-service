package com.mit.user.userservice.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum SolutionStatus {
    READY_TO_COMPILE("READY_TO_COMPILE"),
    ON_COMPILE("ON_COMPILE"),
    COMPILATION_ERROR("COMPILATION_ERROR"),
    RUNTIME_ERROR("RUNTIME_ERROR"),
    WRONG_ANSWER("WRONG_ANSWER"),
    PROBLEM_SOLVED("PROBLEM_SOLVED");

    @Getter
    public String value;

}
