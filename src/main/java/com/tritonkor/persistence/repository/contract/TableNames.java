package com.tritonkor.persistence.repository.contract;

public enum TableNames {
    USERS("users"),
    TESTS("tests"),
    TAGS("tags"),
    QUESTIONS("questions"),
    ANSWERS("answers"),
    RESULTS("results"),
    REPORTS("reports"),
    USER_TEST("user_test");

    private final String name;

    TableNames(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
