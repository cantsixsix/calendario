package com.example.calendario;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class EventCodec {
    private EventCodec() {
    }

    public static String encode(List<CalendarEvent> events) {
        if (events == null || events.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (CalendarEvent event : events) {
            if (event.isHoliday()) {
                continue;
            }
            if (builder.length() > 0) {
                builder.append('\n');
            }
            builder.append(event.getDate());
            builder.append('\t');
            builder.append(encodeTitle(event.getTitle()));
        }
        return builder.toString();
    }

    public static List<CalendarEvent> decode(String payload) {
        List<CalendarEvent> events = new ArrayList<>();
        if (payload == null || payload.trim().isEmpty()) {
            return events;
        }
        String[] rows = payload.split("\\n");
        for (String row : rows) {
            int separator = row.indexOf('\t');
            if (separator <= 0 || separator == row.length() - 1) {
                continue;
            }
            try {
                LocalDate date = LocalDate.parse(row.substring(0, separator));
                String title = decodeTitle(row.substring(separator + 1));
                events.add(new CalendarEvent(date, title));
            } catch (RuntimeException ignored) {
                // Corrupted saved rows are ignored so one bad entry does not block the app.
            }
        }
        return events;
    }

    private static String encodeTitle(String title) {
        try {
            return URLEncoder.encode(title, "UTF-8");
        } catch (java.io.UnsupportedEncodingException exception) {
            throw new IllegalStateException(exception);
        }
    }

    private static String decodeTitle(String title) {
        try {
            return URLDecoder.decode(title, "UTF-8");
        } catch (java.io.UnsupportedEncodingException exception) {
            throw new IllegalStateException(exception);
        }
    }
}
