package com.example.calendario;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

public final class BrazilianHolidaysTest {
    @Test
    public void includesBrazilianNationalHolidaysFor2026() {
        List<CalendarEvent> holidays = BrazilianHolidays.forYear(2026);

        assertTrue(holidays.contains(new CalendarEvent(LocalDate.of(2026, 1, 1), "Confraternizacao Universal", true)));
        assertTrue(holidays.contains(new CalendarEvent(LocalDate.of(2026, 4, 3), "Paixao de Cristo", true)));
        assertTrue(holidays.contains(new CalendarEvent(LocalDate.of(2026, 4, 21), "Tiradentes", true)));
        assertTrue(holidays.contains(new CalendarEvent(LocalDate.of(2026, 11, 20), "Consciencia Negra", true)));
        assertTrue(holidays.contains(new CalendarEvent(LocalDate.of(2026, 12, 25), "Natal", true)));
    }

    @Test
    public void includesCommonFederalOptionalDatesFor2026() {
        List<CalendarEvent> holidays = BrazilianHolidays.forYear(2026);

        assertTrue(holidays.contains(new CalendarEvent(LocalDate.of(2026, 2, 16), "Carnaval - ponto facultativo", true)));
        assertTrue(holidays.contains(new CalendarEvent(LocalDate.of(2026, 2, 17), "Carnaval - ponto facultativo", true)));
        assertTrue(holidays.contains(new CalendarEvent(LocalDate.of(2026, 2, 18), "Quarta-feira de Cinzas - ponto facultativo", true)));
        assertTrue(holidays.contains(new CalendarEvent(LocalDate.of(2026, 6, 4), "Corpus Christi - ponto facultativo", true)));
    }

    @Test
    public void generatesHolidaysForInclusiveYearRange() {
        List<CalendarEvent> holidays = BrazilianHolidays.betweenYears(2026, 2027);

        assertEquals(BrazilianHolidays.forYear(2026).size() + BrazilianHolidays.forYear(2027).size(), holidays.size());
    }
}
