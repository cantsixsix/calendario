package com.example.calendario;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public final class CalendarStateTest {
    @Test
    public void buildsFixedSixWeekGridStartingOnMonday() {
        CalendarState state = new CalendarState(LocalDate.of(2026, 4, 29), Collections.emptyList());

        List<CalendarDay> grid = state.buildMonthGrid();

        assertEquals(42, grid.size());
        assertEquals(LocalDate.of(2026, 3, 30), grid.get(0).getDate());
        assertEquals(LocalDate.of(2026, 5, 10), grid.get(41).getDate());
        assertFalse(grid.get(0).isCurrentMonth());
        assertTrue(grid.get(2).isCurrentMonth());
    }

    @Test
    public void nextMonthClampsSelectedDayWhenTargetMonthIsShorter() {
        CalendarState state = new CalendarState(LocalDate.of(2024, 1, 31), Collections.emptyList());

        state.goToNextMonth();

        assertEquals(YearMonth.of(2024, 2), state.getVisibleMonth());
        assertEquals(LocalDate.of(2024, 2, 29), state.getSelectedDate());
    }

    @Test
    public void choosingMonthAndYearClampsSelectedDay() {
        CalendarState state = new CalendarState(LocalDate.of(2026, 3, 31), Collections.emptyList());

        state.goToMonth(YearMonth.of(2026, 2));

        assertEquals(YearMonth.of(2026, 2), state.getVisibleMonth());
        assertEquals(LocalDate.of(2026, 2, 28), state.getSelectedDate());
    }

    @Test
    public void goToTodayRestoresCurrentMonthAndSelectedDate() {
        CalendarState state = new CalendarState(LocalDate.of(2026, 4, 29), Collections.emptyList());

        state.goToMonth(YearMonth.of(2030, 12));
        state.goToToday();

        assertEquals(YearMonth.of(2026, 4), state.getVisibleMonth());
        assertEquals(LocalDate.of(2026, 4, 29), state.getSelectedDate());
    }

    @Test
    public void selectingADateChangesVisibleMonth() {
        CalendarState state = new CalendarState(LocalDate.of(2026, 4, 29), Collections.emptyList());

        state.selectDate(LocalDate.of(2026, 12, 24));

        assertEquals(YearMonth.of(2026, 12), state.getVisibleMonth());
        assertEquals(LocalDate.of(2026, 12, 24), state.getSelectedDate());
    }

    @Test
    public void eventsAreReturnedOnlyForSelectedDate() {
        CalendarState state = new CalendarState(
                LocalDate.of(2026, 4, 29),
                Arrays.asList(
                        new CalendarEvent(LocalDate.of(2026, 4, 29), "Dentista"),
                        new CalendarEvent(LocalDate.of(2026, 4, 30), "Reuniao")
                )
        );

        assertEquals(1, state.getEventsForSelectedDate().size());
        assertEquals("Dentista", state.getEventsForSelectedDate().get(0).getTitle());
    }

    @Test
    public void gridMarksDaysWithEvents() {
        CalendarState state = new CalendarState(
                LocalDate.of(2026, 4, 29),
                Collections.singletonList(new CalendarEvent(LocalDate.of(2026, 5, 1), "Feriado"))
        );
        state.selectDate(LocalDate.of(2026, 5, 1));

        CalendarDay firstOfMay = state.buildMonthGrid().stream()
                .filter(day -> day.getDate().equals(LocalDate.of(2026, 5, 1)))
                .findFirst()
                .orElseThrow(AssertionError::new);

        assertTrue(firstOfMay.hasEvents());
        assertTrue(firstOfMay.isSelected());
    }

    @Test
    public void monthTitleUsesPortugueseLocaleAndCapitalizesMonth() {
        CalendarState state = new CalendarState(LocalDate.of(2026, 4, 29), Collections.emptyList());

        assertEquals("Abril 2026", state.getMonthTitle(new Locale("pt", "BR")));
    }
}
