package com.eztech.fitrans.constants;

import java.util.stream.Stream;

public enum ProfilePriorityEnum {
    LOW(1), MEDIUM(2), HIGH(3);

    private int priority;

    private ProfilePriorityEnum(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    public static ProfilePriorityEnum of(int priority) {
        return Stream.of(ProfilePriorityEnum.values())
                .filter(p -> p.getPriority() == priority)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
