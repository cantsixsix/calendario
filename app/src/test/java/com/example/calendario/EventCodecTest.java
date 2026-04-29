package com.example.calendario;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public final class EventCodecTest {
    @Test
    public void roundTripsEventsWithSpacesAccentsAndNewLines() {
        List<CalendarEvent> events = Arrays.asList(
                new CalendarEvent(LocalDate.of(2026, 4, 29), "Consulta medica"),
                new CalendarEvent(LocalDate.of(2026, 4, 30), "Aniversario da Ana\ncomprar bolo")
        );

        List<CalendarEvent> decoded = EventCodec.decode(EventCodec.encode(events));

        assertEquals(events, decoded);
    }

    @Test
    public void ignoresCorruptedRowsWhenDecodingSavedEvents() {
        String payload = "linha quebrada\n2026-04-29\tEvento+valido\n2026-99-99\tData+ruim";

        List<CalendarEvent> decoded = EventCodec.decode(payload);

        assertEquals(1, decoded.size());
        assertEquals(new CalendarEvent(LocalDate.of(2026, 4, 29), "Evento valido"), decoded.get(0));
    }
}
