package smartuniversityacademicsystem.view;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import smartuniversityacademicsystem.model.TimetableEntry;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Reusable weekly timetable calendar grid with day filter.
 * Call buildWithFilter(entries) to get a VBox containing the ComboBox + grid.
 * Call build(entries) to get just the ScrollPane grid (no filter bar).
 */
public class TimetableGridView {

    private static final String[] DAYS = {
        "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"
    };

    private static final String[][] SLOTS = {
        {"08:00", "10:00"},
        {"10:00", "12:00"},
        {"14:00", "16:00"},
        {"16:00", "18:00"}
    };

    private static final String[] CARD_COLORS = {
        "#2563EB", "#059669", "#7C3AED", "#D97706",
        "#0891B2", "#DC2626", "#65A30D", "#DB2777"
    };

    // ── Public API ────────────────────────────────────────────────────────────

    /** Returns a VBox with a day-filter ComboBox on top and the grid below. */
    public VBox buildWithFilter(List<TimetableEntry> entries) {
        ComboBox<String> dayFilter = new ComboBox<>();
        dayFilter.setItems(FXCollections.observableArrayList(
            "All Days", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"
        ));
        dayFilter.setValue("All Days");
        dayFilter.setStyle(
            "-fx-background-color: #1E293B; -fx-text-fill: #F1F5F9;" +
            "-fx-border-color: #475569; -fx-border-radius: 8; -fx-background-radius: 8;"
        );

        StackPane gridHolder = new StackPane(build(entries));
        VBox.setVgrow(gridHolder, Priority.ALWAYS);

        dayFilter.setOnAction(e -> {
            String selected = dayFilter.getValue();
            List<TimetableEntry> filtered = "All Days".equals(selected)
                ? entries
                : entries.stream()
                    .filter(t -> t.getDayOfWeek().equalsIgnoreCase(selected))
                    .collect(Collectors.toList());
            gridHolder.getChildren().setAll(build(filtered, selected));
        });

        Label filterLabel = new Label("Filter by day:");
        filterLabel.setTextFill(Color.web("#94A3B8"));
        filterLabel.setFont(Font.font("Segoe UI", 12));

        HBox filterBar = new HBox(10, filterLabel, dayFilter);
        filterBar.setAlignment(Pos.CENTER_LEFT);

        VBox wrapper = new VBox(10, filterBar, gridHolder);
        VBox.setVgrow(gridHolder, Priority.ALWAYS);
        return wrapper;
    }

    /** Returns just the ScrollPane grid (used internally and for backwards compat). */
    public ScrollPane build(List<TimetableEntry> entries) {
        return build(entries, null);
    }

    // ── Grid builder ──────────────────────────────────────────────────────────

    private ScrollPane build(List<TimetableEntry> entries, String dayFilter) {
        // When filtering to a single day, only show that day's column
        String[] visibleDays = (dayFilter == null || "All Days".equals(dayFilter))
            ? DAYS
            : new String[]{normalizeDay(dayFilter)};

        GridPane grid = new GridPane();
        grid.setHgap(6);
        grid.setVgap(6);
        grid.setPadding(new Insets(16));
        grid.setStyle("-fx-background-color: #0F172A;");

        // Column constraints: time label + one per visible day
        grid.getColumnConstraints().add(colConstraint(90, false));
        for (int i = 0; i < visibleDays.length; i++) {
            grid.getColumnConstraints().add(colConstraint(150, true));
        }

        // Row constraints
        grid.getRowConstraints().add(rowConstraint(38));
        for (int s = 0; s < SLOTS.length; s++) {
            grid.getRowConstraints().add(rowConstraint(90));
        }

        // Header row
        grid.add(headerCell(""), 0, 0);
        for (int d = 0; d < visibleDays.length; d++) {
            grid.add(headerCell(visibleDays[d]), d + 1, 0);
        }

        // Time labels + empty cells
        for (int s = 0; s < SLOTS.length; s++) {
            grid.add(timeLabel(SLOTS[s][0] + "\n" + SLOTS[s][1]), 0, s + 1);
            for (int d = 0; d < visibleDays.length; d++) {
                grid.add(emptyCell(), d + 1, s + 1);
            }
        }

        // Place course cards
        java.util.Map<String, Integer> colorMap = new java.util.LinkedHashMap<>();
        int nextColor = 0;
        for (TimetableEntry entry : entries) {
            int col = dayToCol(entry.getDayOfWeek(), visibleDays);
            int row = timeToRow(entry.getStartTime());
            if (col < 0 || row < 0) continue;

            if (!colorMap.containsKey(entry.getCourseCode())) {
                colorMap.put(entry.getCourseCode(), nextColor++ % CARD_COLORS.length);
            }

            // Replace empty placeholder at this cell
            grid.getChildren().removeIf(node ->
                GridPane.getColumnIndex(node) != null &&
                GridPane.getRowIndex(node) != null &&
                GridPane.getColumnIndex(node) == col &&
                GridPane.getRowIndex(node) == row &&
                node instanceof StackPane
            );
            grid.add(courseCard(entry, CARD_COLORS[colorMap.get(entry.getCourseCode())]), col, row);
        }

        if (entries.isEmpty()) {
            Label empty = new Label("No timetable entries for this selection.");
            empty.setTextFill(Color.web("#64748B"));
            empty.setFont(Font.font("Segoe UI", 13));
            grid.add(empty, 1, 2);
        }

        ScrollPane scroll = new ScrollPane(grid);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: #0F172A; -fx-background: #0F172A;");
        return scroll;
    }

    // ── Cell builders ─────────────────────────────────────────────────────────

    private Label headerCell(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        lbl.setTextFill(Color.web("#94A3B8"));
        lbl.setAlignment(Pos.CENTER);
        lbl.setMaxWidth(Double.MAX_VALUE);
        lbl.setPadding(new Insets(6));
        lbl.setStyle(
            "-fx-background-color: #1E293B; -fx-background-radius: 6;" +
            "-fx-border-color: #334155; -fx-border-radius: 6;"
        );
        return lbl;
    }

    private Label timeLabel(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 11));
        lbl.setTextFill(Color.web("#94A3B8"));
        lbl.setAlignment(Pos.CENTER);
        lbl.setMaxWidth(Double.MAX_VALUE);
        lbl.setMaxHeight(Double.MAX_VALUE);
        lbl.setPadding(new Insets(6));
        lbl.setStyle(
            "-fx-background-color: #1E293B; -fx-background-radius: 8;" +
            "-fx-border-color: #334155; -fx-border-radius: 8;"
        );
        return lbl;
    }

    private StackPane emptyCell() {
        StackPane p = new StackPane();
        p.setMaxWidth(Double.MAX_VALUE);
        p.setMaxHeight(Double.MAX_VALUE);
        p.setStyle(
            "-fx-background-color: #1E293B; -fx-background-radius: 8;" +
            "-fx-border-color: #1E3A5F; -fx-border-radius: 8;"
        );
        return p;
    }

    private VBox courseCard(TimetableEntry entry, String color) {
        Label code  = new Label(entry.getCourseCode());
        code.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        code.setTextFill(Color.WHITE);

        Label name  = new Label(entry.getCourseName());
        name.setFont(Font.font("Segoe UI", 11));
        name.setTextFill(Color.web("#CBD5E1"));
        name.setWrapText(true);

        Label venue = new Label(entry.getVenue());
        venue.setFont(Font.font("Segoe UI", 10));
        venue.setTextFill(Color.web("#94A3B8"));

        VBox card = new VBox(3, code, name, venue);
        card.setPadding(new Insets(8));
        card.setMaxWidth(Double.MAX_VALUE);
        card.setMaxHeight(Double.MAX_VALUE);
        card.setStyle(
            "-fx-background-color: " + color + "22;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: " + color + ";" +
            "-fx-border-radius: 8; -fx-border-width: 1.5;"
        );

        Region accent = new Region();
        accent.setPrefWidth(4);
        accent.setMaxHeight(Double.MAX_VALUE);
        accent.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 4 0 0 4;");

        HBox wrapper = new HBox(accent, card);
        HBox.setHgrow(card, Priority.ALWAYS);
        wrapper.setMaxWidth(Double.MAX_VALUE);
        wrapper.setMaxHeight(Double.MAX_VALUE);
        return new VBox(wrapper);
    }

    // ── Mapping helpers ───────────────────────────────────────────────────────

    private int dayToCol(String day, String[] visibleDays) {
        for (int i = 0; i < visibleDays.length; i++) {
            if (visibleDays[i].equalsIgnoreCase(day)) return i + 1;
        }
        return -1;
    }

    private int timeToRow(String startTime) {
        for (int i = 0; i < SLOTS.length; i++) {
            if (SLOTS[i][0].equals(startTime)) return i + 1;
        }
        return -1;
    }

    private String normalizeDay(String day) {
        if (day == null) return "";
        return day.substring(0, 1).toUpperCase() + day.substring(1).toLowerCase();
    }

    private ColumnConstraints colConstraint(double min, boolean grow) {
        ColumnConstraints cc = new ColumnConstraints();
        cc.setMinWidth(min);
        if (grow) cc.setHgrow(Priority.ALWAYS);
        return cc;
    }

    private RowConstraints rowConstraint(double height) {
        RowConstraints rc = new RowConstraints();
        rc.setMinHeight(height);
        rc.setPrefHeight(height);
        return rc;
    }
}
