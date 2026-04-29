package com.example.calendario;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class CalendarState {
    private YearMonth visibleMonth;
    private LocalDate selectedDate;
    private final LocalDate today;
    private final Map<LocalDate, List<CalendarEvent>> eventsByDate;

    public CalendarState(LocalDate today, List<CalendarEvent> initialEvents) {
        if (today == null) {
            throw new IllegalArgumentException("today is required");
        }
        this.today = today;
        this.visibleMonth = YearMonth.from(today);
        this.selectedDate = today;
        this.eventsByDate = new LinkedHashMap<>();
        for (CalendarEvent event : initialEvents == null ? Collections.<CalendarEvent>emptyList() : initialEvents) {
            addEvent(event);
        }
    }

    public YearMonth getVisibleMonth() {
        return visibleMonth;
    }

    public LocalDate getSelectedDate() {
        return selectedDate;
    }

    public void goToPreviousMonth() {
        visibleMonth = visibleMonth.minusMonths(1);
        selectedDate = visibleMonth.atDay(Math.min(selectedDate.getDayOfMonth(), visibleMonth.lengthOfMonth()));
    }

    public void goToNextMonth() {
        visibleMonth = visibleMonth.plusMonths(1);
        selectedDate = visibleMonth.atDay(Math.min(selectedDate.getDayOfMonth(), visibleMonth.lengthOfMonth()));
    }

    public void goToMonth(YearMonth month) {
        if (month == null) {
            throw new IllegalArgumentException("month is required");
        }
        visibleMonth = month;
        selectedDate = visibleMonth.atDay(Math.min(selectedDate.getDayOfMonth(), visibleMonth.lengthOfMonth()));
    }

    public void goToToday() {
        visibleMonth = YearMonth.from(today);
        selectedDate = today;
    }

    public void selectDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("date is required");
        }
        selectedDate = date;
        visibleMonth = YearMonth.from(date);
    }

    public void addEvent(CalendarEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("event is required");
        }
        List<CalendarEvent> events = eventsByDate.get(event.getDate());
        if (events == null) {
            events = new ArrayList<>();
            eventsByDate.put(event.getDate(), events);
        }
        events.add(event);
    }

    public void addEvents(List<CalendarEvent> events) {
        for (CalendarEvent event : events == null ? Collections.<CalendarEvent>emptyList() : events) {
            addEvent(event);
        }
    }

    public List<CalendarEvent> getEventsForSelectedDate() {
        return getEventsForDate(selectedDate);
    }

    public List<CalendarEvent> getEventsForDate(LocalDate date) {
        List<CalendarEvent> events = eventsByDate.get(date);
        return events == null ? Collections.<CalendarEvent>emptyList() : Collections.unmodifiableList(events);
    }

    public List<CalendarEvent> getAllEvents() {
        List<CalendarEvent> allEvents = new ArrayList<>();
        for (List<CalendarEvent> events : eventsByDate.values()) {
            allEvents.addAll(events);
        }
        return allEvents;
    }

    public List<CalendarEvent> getUserEvents() {
        List<CalendarEvent> userEvents = new ArrayList<>();
        for (CalendarEvent event : getAllEvents()) {
            if (!event.isHoliday()) {
                userEvents.add(event);
            }
        }
        return userEvents;
    }

    public List<CalendarDay> buildMonthGrid() {
        LocalDate firstOfMonth = visibleMonth.atDay(1);
        int mondayOffset = firstOfMonth.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue();
        LocalDate firstGridDate = firstOfMonth.minusDays(mondayOffset);
        List<CalendarDay> days = new ArrayList<>(42);
        for (int i = 0; i < 42; i++) {
            LocalDate date = firstGridDate.plusDays(i);
            days.add(new CalendarDay(
                    date,
                    YearMonth.from(date).equals(visibleMonth),
                    date.equals(selectedDate),
                    date.equals(today),
                    !getEventsForDate(date).isEmpty()
            ));
        }
        return days;
    }

    public String getMonthTitle(Locale locale) {
        Locale safeLocale = locale == null ? Locale.getDefault() : locale;
        String month = visibleMonth.getMonth().getDisplayName(java.time.format.TextStyle.FULL, safeLocale);
        return capitalize(month) + " " + visibleMonth.getYear();
    }

    private static String capitalize(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        return value.substring(0, 1).toUpperCase(Locale.ROOT) + value.substring(1);
    }
}
