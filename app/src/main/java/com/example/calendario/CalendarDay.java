package com.example.calendario;

import java.time.LocalDate;

public final class CalendarDay {
    private final LocalDate date;
    private final boolean currentMonth;
    private final boolean selected;
    private final boolean today;
    private final boolean hasEvents;

    public CalendarDay(
            LocalDate date,
            boolean currentMonth,
            boolean selected,
            boolean today,
            boolean hasEvents
    ) {
        this.date = date;
        this.currentMonth = currentMonth;
        this.selected = selected;
        this.today = today;
        this.hasEvents = hasEvents;
    }

    public LocalDate getDate() {
        return date;
    }

    public boolean isCurrentMonth() {
        return currentMonth;
    }

    public boolean isSelected() {
        return selected;
    }

    public boolean isToday() {
        return today;
    }

    public boolean hasEvents() {
        return hasEvents;
    }
}
