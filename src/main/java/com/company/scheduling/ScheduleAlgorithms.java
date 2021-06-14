package com.company.scheduling;

public enum ScheduleAlgorithms {

    PriorityLLF {
        final ScheduleStrategy scheduleStrategy = new LLFScheduleStrategy();

        @Override
        public ScheduleStrategy getStrategy() {
            return scheduleStrategy;
        }
    };

    public abstract ScheduleStrategy getStrategy();
}
