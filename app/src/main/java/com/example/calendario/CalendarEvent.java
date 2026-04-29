package com.example.calendario;

import java.time.LocalDate;
import java.util.Objects;

public final class CalendarEvent {
    private final LocalDate date;
    private final String title;
    private final boolean holiday;

    public CalendarEvent(LocalDate date, String title) {
        this(date, title, false);
    }

    public CalendarEvent(LocalDate date, String title, boolean holiday) {
        if (date == null) {
            throw new IllegalArgumentException("date is required");
        }
        String cleanTitle = title == null ? "" : title.trim();
        if (cleanTitle.isEmpty()) {
            throw new IllegalArgumentException("title is required");
        }
        this.date = date;
        this.title = cleanTitle;
        this.holiday = holiday;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public boolean isHoliday() {
        return holiday;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof CalendarEvent)) {
            return false;
        }
        CalendarEvent that = (CalendarEvent) other;
        return holiday == that.holiday && date.equals(that.date) && title.equals(that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, title, holiday);
    }
}
