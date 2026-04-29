package com.example.calendario;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;

public final class MainActivity extends Activity {
    private static final String PREFS_NAME = "calendar_prefs";
    private static final String EVENTS_KEY = "events";

    private final Locale brazil = new Locale("pt", "BR");
    private final DateTimeFormatter fullDateFormatter =
            DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(brazil);

    private CalendarState state;
    private SharedPreferences preferences;
    private TextView titleView;
    private TextView subtitleView;
    private GridLayout daysGrid;
    private TextView selectedDateView;
    private TextView eventCountView;
    private LinearLayout eventList;
    private EditText eventInput;
    private int dayCellHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        List<CalendarEvent> savedEvents = EventCodec.decode(preferences.getString(EVENTS_KEY, ""));
        state = new CalendarState(LocalDate.now(), savedEvents);
        int currentYear = LocalDate.now().getYear();
        state.addEvents(BrazilianHolidays.betweenYears(currentYear - 80, currentYear + 80));
        setContentView(buildContent());
        render();
    }

    private LinearLayout buildContent() {
        LinearLayout root = vertical();
        root.setBackgroundColor(color(R.color.bg));
        applySafeAreaPadding(root);

        LinearLayout content = vertical();
        TextView eyebrow = new TextView(this);
        eyebrow.setText("Agenda");
        eyebrow.setTextColor(color(R.color.accent_dark));
        eyebrow.setTextSize(14);
        eyebrow.setTypeface(Typeface.DEFAULT_BOLD);
        content.addView(eyebrow);

        titleView = new TextView(this);
        titleView.setTextColor(color(R.color.ink));
        titleView.setTextSize(compactScreen() ? 26 : 30);
        titleView.setTypeface(Typeface.DEFAULT_BOLD);
        titleView.setPadding(0, dp(2), 0, 0);
        content.addView(titleView);

        subtitleView = new TextView(this);
        subtitleView.setTextColor(color(R.color.muted));
        subtitleView.setTextSize(14);
        subtitleView.setPadding(0, dp(2), 0, dp(14));
        content.addView(subtitleView);

        content.addView(buildCalendarPanel(), matchWrapParams());
        content.addView(buildEventPanel(), matchWrapParams());

        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(false);
        scrollView.setClipToPadding(false);
        scrollView.addView(content);
        root.addView(scrollView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1
        ));
        root.addView(buildEventForm(), matchWrapParams());

        return root;
    }

    private LinearLayout buildCalendarPanel() {
        LinearLayout panel = vertical();
        panel.setPadding(dp(12), dp(12), dp(12), dp(12));
        panel.setBackground(cardBackground(Color.WHITE, color(R.color.line), dp(1), dp(10)));

        LinearLayout actions = horizontal();
        actions.setGravity(Gravity.CENTER_VERTICAL);

        Button previous = smallButton("<");
        previous.setContentDescription("Mes anterior");
        previous.setOnClickListener(view -> {
            state.goToPreviousMonth();
            render();
        });

        Button chooseMonth = primaryButton(compactScreen() ? "Mes e ano" : "Escolher mes/ano");
        chooseMonth.setOnClickListener(view -> showMonthYearPicker());
        chooseMonth.setLayoutParams(new LinearLayout.LayoutParams(0, dp(46), 1));

        Button next = smallButton(">");
        next.setContentDescription("Proximo mes");
        next.setOnClickListener(view -> {
            state.goToNextMonth();
            render();
        });

        actions.addView(previous);
        actions.addView(chooseMonth);
        actions.addView(next);
        panel.addView(actions);

        Button today = secondaryButton("Hoje");
        today.setOnClickListener(view -> {
            state.goToToday();
            render();
        });
        LinearLayout.LayoutParams todayParams = matchWrapParams();
        todayParams.setMargins(0, dp(8), 0, dp(10));
        panel.addView(today, todayParams);

        LinearLayout weekHeader = horizontal();
        weekHeader.setPadding(0, 0, 0, dp(6));
        String[] weekDays = {"Seg", "Ter", "Qua", "Qui", "Sex", "Sab", "Dom"};
        for (String day : weekDays) {
            TextView dayView = new TextView(this);
            dayView.setText(day);
            dayView.setGravity(Gravity.CENTER);
            dayView.setTextColor(color(R.color.muted));
            dayView.setTextSize(11);
            dayView.setTypeface(Typeface.DEFAULT_BOLD);
            dayView.setLayoutParams(new LinearLayout.LayoutParams(0, dp(24), 1));
            weekHeader.addView(dayView);
        }
        panel.addView(weekHeader);

        daysGrid = new GridLayout(this);
        daysGrid.setColumnCount(7);
        daysGrid.setRowCount(6);
        dayCellHeight = calculateDayCellHeight();
        panel.addView(daysGrid, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                (dayCellHeight + dp(4)) * 6
        ));

        return panel;
    }

    private LinearLayout buildEventPanel() {
        LinearLayout panel = vertical();
        panel.setPadding(0, dp(14), 0, 0);

        LinearLayout eventHeader = horizontal();
        eventHeader.setGravity(Gravity.CENTER_VERTICAL);

        selectedDateView = new TextView(this);
        selectedDateView.setTextColor(color(R.color.ink));
        selectedDateView.setTextSize(18);
        selectedDateView.setTypeface(Typeface.DEFAULT_BOLD);
        selectedDateView.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        eventHeader.addView(selectedDateView);

        eventCountView = new TextView(this);
        eventCountView.setTextColor(color(R.color.accent_dark));
        eventCountView.setTextSize(12);
        eventCountView.setTypeface(Typeface.DEFAULT_BOLD);
        eventCountView.setGravity(Gravity.CENTER);
        eventCountView.setPadding(dp(10), dp(6), dp(10), dp(6));
        eventCountView.setBackground(cardBackground(color(R.color.accent_soft), color(R.color.accent_soft), 0, dp(99)));
        eventHeader.addView(eventCountView);
        panel.addView(eventHeader);

        eventList = vertical();
        eventList.setPadding(0, dp(10), 0, 0);
        panel.addView(eventList, matchWrapParams());

        return panel;
    }

    private LinearLayout buildEventForm() {
        LinearLayout form = horizontal();
        form.setGravity(Gravity.CENTER_VERTICAL);
        form.setPadding(0, dp(10), 0, 0);

        eventInput = new EditText(this);
        eventInput.setHint("Adicionar evento");
        eventInput.setSingleLine(true);
        eventInput.setTextColor(color(R.color.ink));
        eventInput.setHintTextColor(color(R.color.muted));
        eventInput.setPadding(dp(14), 0, dp(12), 0);
        eventInput.setBackground(cardBackground(Color.WHITE, color(R.color.line), dp(1), dp(8)));
        eventInput.setLayoutParams(new LinearLayout.LayoutParams(0, dp(52), 1));

        Button addButton = primaryButton("+");
        addButton.setTextSize(24);
        addButton.setContentDescription("Adicionar evento");
        addButton.setOnClickListener(view -> addEvent());

        LinearLayout.LayoutParams addParams = new LinearLayout.LayoutParams(dp(56), dp(52));
        addParams.setMargins(dp(8), 0, 0, 0);
        form.addView(eventInput);
        form.addView(addButton, addParams);

        return form;
    }

    private void showMonthYearPicker() {
        LinearLayout pickerLayout = horizontal();
        pickerLayout.setPadding(dp(18), dp(10), dp(18), 0);
        pickerLayout.setGravity(Gravity.CENTER);

        NumberPicker monthPicker = new NumberPicker(this);
        String[] months = new String[]{
                "Janeiro", "Fevereiro", "Marco", "Abril", "Maio", "Junho",
                "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
        };
        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setDisplayedValues(months);
        monthPicker.setValue(state.getVisibleMonth().getMonthValue());
        monthPicker.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        NumberPicker yearPicker = new NumberPicker(this);
        int currentYear = LocalDate.now().getYear();
        yearPicker.setMinValue(currentYear - 80);
        yearPicker.setMaxValue(currentYear + 80);
        yearPicker.setValue(state.getVisibleMonth().getYear());
        yearPicker.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        pickerLayout.addView(monthPicker);
        pickerLayout.addView(yearPicker);

        new AlertDialog.Builder(this)
                .setTitle("Escolher mes e ano")
                .setView(pickerLayout)
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Aplicar", (dialog, which) -> {
                    state.goToMonth(YearMonth.of(yearPicker.getValue(), monthPicker.getValue()));
                    render();
                })
                .show();
    }

    private void render() {
        titleView.setText(state.getMonthTitle(brazil));
        subtitleView.setText("Toque em um dia para ver ou adicionar eventos");
        selectedDateView.setText(fullDateFormatter.format(state.getSelectedDate()));
        renderDays();
        renderEvents();
    }

    private void renderDays() {
        daysGrid.removeAllViews();
        List<CalendarDay> days = state.buildMonthGrid();
        for (CalendarDay day : days) {
            TextView cell = new TextView(this);
            cell.setGravity(Gravity.CENTER);
            cell.setText(String.valueOf(day.getDate().getDayOfMonth()) + (day.hasEvents() ? "\n●" : ""));
            cell.setTextSize(day.hasEvents() ? 13 : 15);
            cell.setTypeface(day.isSelected() || day.isToday() ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
            cell.setTextColor(dayTextColor(day));
            cell.setBackground(dayBackground(day));
            cell.setOnClickListener(view -> {
                state.selectDate(day.getDate());
                render();
            });

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = dayCellHeight;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(dp(2), dp(2), dp(2), dp(2));
            daysGrid.addView(cell, params);
        }
    }

    private void renderEvents() {
        eventList.removeAllViews();
        List<CalendarEvent> events = state.getEventsForSelectedDate();
        eventCountView.setText(events.size() == 1 ? "1 evento" : events.size() + " eventos");
        if (events.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("Sem eventos neste dia.");
            empty.setTextColor(color(R.color.muted));
            empty.setTextSize(15);
            empty.setGravity(Gravity.CENTER);
            empty.setPadding(dp(12), dp(20), dp(12), dp(20));
            empty.setBackground(cardBackground(Color.WHITE, color(R.color.line), dp(1), dp(8)));
            eventList.addView(empty, matchWrapParams());
            return;
        }
        for (CalendarEvent event : events) {
            TextView row = new TextView(this);
            row.setText(event.getTitle());
            row.setTextColor(event.isHoliday() ? color(R.color.accent_dark) : color(R.color.ink));
            row.setTextSize(16);
            row.setTypeface(event.isHoliday() ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
            row.setPadding(dp(14), dp(13), dp(14), dp(13));
            row.setBackground(event.isHoliday()
                    ? cardBackground(color(R.color.accent_soft), color(R.color.accent_soft), 0, dp(8))
                    : cardBackground(Color.WHITE, color(R.color.line), dp(1), dp(8)));
            LinearLayout.LayoutParams params = matchWrapParams();
            params.setMargins(0, 0, 0, dp(8));
            eventList.addView(row, params);
        }
    }

    private int dayTextColor(CalendarDay day) {
        if (day.isSelected()) {
            return Color.WHITE;
        }
        if (!day.isCurrentMonth()) {
            return color(R.color.muted);
        }
        if (day.hasEvents()) {
            return color(R.color.accent_dark);
        }
        return color(R.color.ink);
    }

    private GradientDrawable dayBackground(CalendarDay day) {
        if (day.isSelected()) {
            return cardBackground(color(R.color.accent), color(R.color.accent), 0, dp(10));
        }
        if (day.isToday()) {
            return cardBackground(color(R.color.accent_soft), color(R.color.accent), dp(1), dp(10));
        }
        if (!day.isCurrentMonth()) {
            return cardBackground(color(R.color.muted_soft), color(R.color.muted_soft), 0, dp(10));
        }
        if (day.hasEvents()) {
            return cardBackground(Color.WHITE, color(R.color.accent_soft), dp(2), dp(10));
        }
        return cardBackground(Color.WHITE, color(R.color.line), dp(1), dp(10));
    }

    private void addEvent() {
        String title = eventInput.getText().toString().trim();
        if (title.isEmpty()) {
            Toast.makeText(this, "Escreva o nome do evento", Toast.LENGTH_SHORT).show();
            return;
        }
        state.addEvent(new CalendarEvent(state.getSelectedDate(), title));
        preferences.edit().putString(EVENTS_KEY, EventCodec.encode(state.getUserEvents())).apply();
        eventInput.setText("");
        render();
    }

    private Button smallButton(String text) {
        Button button = secondaryButton(text);
        button.setTextSize(18);
        button.setTypeface(Typeface.DEFAULT_BOLD);
        button.setMinWidth(dp(48));
        button.setMinHeight(dp(46));
        button.setLayoutParams(new LinearLayout.LayoutParams(dp(52), dp(46)));
        return button;
    }

    private Button primaryButton(String text) {
        Button button = baseButton(text);
        button.setTextColor(Color.WHITE);
        button.setBackground(cardBackground(color(R.color.accent), color(R.color.accent), 0, dp(8)));
        return button;
    }

    private Button secondaryButton(String text) {
        Button button = baseButton(text);
        button.setTextColor(color(R.color.accent_dark));
        button.setBackground(cardBackground(Color.WHITE, color(R.color.line), dp(1), dp(8)));
        return button;
    }

    private Button baseButton(String text) {
        Button button = new Button(this);
        button.setText(text);
        button.setAllCaps(false);
        button.setTextSize(14);
        button.setTypeface(Typeface.DEFAULT_BOLD);
        button.setPadding(dp(8), 0, dp(8), 0);
        return button;
    }

    private LinearLayout vertical() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        return layout;
    }

    private LinearLayout horizontal() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        return layout;
    }

    private LinearLayout.LayoutParams matchWrapParams() {
        return new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
    }

    private GradientDrawable cardBackground(int fill, int stroke, int strokeWidth, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(fill);
        drawable.setCornerRadius(radius);
        if (strokeWidth > 0) {
            drawable.setStroke(strokeWidth, stroke);
        }
        return drawable;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private int calculateDayCellHeight() {
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int availableWidth = screenWidth - dp(32) - dp(24);
        int target = availableWidth / 7 - dp(4);
        return clamp(target, dp(36), dp(48));
    }

    private boolean compactScreen() {
        return getResources().getDisplayMetrics().widthPixels < dp(380);
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private void applySafeAreaPadding(View view) {
        int horizontalPadding = dp(16);
        int topPadding = dp(14);
        int bottomPadding = dp(12);
        view.setPadding(horizontalPadding, topPadding, horizontalPadding, bottomPadding);
        view.setOnApplyWindowInsetsListener((target, insets) -> {
            target.setPadding(
                    horizontalPadding + insets.getSystemWindowInsetLeft(),
                    topPadding + insets.getSystemWindowInsetTop(),
                    horizontalPadding + insets.getSystemWindowInsetRight(),
                    bottomPadding + insets.getSystemWindowInsetBottom()
            );
            return insets;
        });
        view.post(view::requestApplyInsets);
    }

    private int color(int resourceId) {
        return getResources().getColor(resourceId, getTheme());
    }
}
