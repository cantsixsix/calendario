package com.example.calendario;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class BrazilianHolidays {
    private BrazilianHolidays() {
    }

    public static List<CalendarEvent> betweenYears(int startYear, int endYear) {
        List<CalendarEvent> holidays = new ArrayList<>();
        for (int year = startYear; year <= endYear; year++) {
            holidays.addAll(forYear(year));
        }
        return holidays;
    }

    public static List<CalendarEvent> forYear(int year) {
        List<CalendarEvent> holidays = new ArrayList<>();
        LocalDate easter = easterSunday(year);

        holidays.add(holiday(year, 1, 1, "Confraternizacao Universal"));
        holidays.add(new CalendarEvent(easter.minusDays(48), "Carnaval - ponto facultativo", true));
        holidays.add(new CalendarEvent(easter.minusDays(47), "Carnaval - ponto facultativo", true));
        holidays.add(new CalendarEvent(easter.minusDays(46), "Quarta-feira de Cinzas - ponto facultativo", true));
        holidays.add(new CalendarEvent(easter.minusDays(2), "Paixao de Cristo", true));
        holidays.add(holiday(year, 4, 21, "Tiradentes"));
        holidays.add(holiday(year, 5, 1, "Dia Mundial do Trabalho"));
        holidays.add(new CalendarEvent(easter.plusDays(60), "Corpus Christi - ponto facultativo", true));
        holidays.add(holiday(year, 9, 7, "Independencia do Brasil"));
        holidays.add(holiday(year, 10, 12, "Nossa Senhora Aparecida"));
        holidays.add(holiday(year, 11, 2, "Finados"));
        holidays.add(holiday(year, 11, 15, "Proclamacao da Republica"));
        holidays.add(holiday(year, 11, 20, "Consciencia Negra"));
        holidays.add(holiday(year, 12, 25, "Natal"));
        return holidays;
    }

    private static CalendarEvent holiday(int year, int month, int day, String title) {
        return new CalendarEvent(LocalDate.of(year, month, day), title, true);
    }

    private static LocalDate easterSunday(int year) {
        int a = year % 19;
        int b = year / 100;
        int c = year % 100;
        int d = b / 4;
        int e = b % 4;
        int f = (b + 8) / 25;
        int g = (b - f + 1) / 3;
        int h = (19 * a + b - d - g + 15) % 30;
        int i = c / 4;
        int k = c % 4;
        int l = (32 + 2 * e + 2 * i - h - k) % 7;
        int m = (a + 11 * h + 22 * l) / 451;
        int month = (h + l - 7 * m + 114) / 31;
        int day = ((h + l - 7 * m + 114) % 31) + 1;
        return LocalDate.of(year, month, day);
    }
}
