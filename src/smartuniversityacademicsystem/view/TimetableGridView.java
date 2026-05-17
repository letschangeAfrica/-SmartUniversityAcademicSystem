package smartuniversityacademicsystem.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import smartuniversityacademicsystem.model.TimetableEntry;

import java.util.List;

/**
 * Reusable weekly timetable calendar grid.
 * Displays courses as coloured cards in a Mon-Fri / time-slot grid.
 */
public class TimetableGridView {

    private static final String[] DAYS = {
        "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"
    };

    // Displayed time bands (rows)
    private static final String[][] SLOTS = {
        {"08:00", "10:00"},
        {"10:00", "12:00"},
        {"14:00", "16:00"},
        {"16:00", "18:00"}
    };

    // Card accent colours cycling per unique course code
    private static final String[] CARD_COLORS = {
        "#2563EB", "#059669", "#7C3AED", "#D97706",
        "#0891B2", "#DC2626", "#65A30D", "#DB2777"
    };

    public ScrollPane build(List<TimetableEntry> entries) {
        GridPane grid = new GridPane();
        grid.setHgap(6);
        grid.setVgap(6);
        grid.setPadding(new Insets(16));
        grid.setStyle("-fx-background-color: #0F172A;");

        // Column constraints
        ColumnConstraints timeCol = new ColumnConstraints(90);
        grid.getColumnConstraints().add(timeCol);
        for (int d = 0; d < DAYS.length; d++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setHgrow(Priority.ALWAYS);
            cc.setMinWidth(130);
            grid.getColumnConstraints().add(cc);
        }

        // Row constraints
        grid.getRowConstraints().add(rowConstraint(40)); // header
        for (int s = 0; s < SLOTS.length; s++) {
            grid.getRowConstraints().add(rowConstraint(90));
        }

        // ── Header row ────────────────────────────────────────────────────────
        grid.add(headerCell(""), 0, 0);
        for (int d = 0; d < DAYS.length; d++) {
            grid.add(headerCell(DAYS[d]), d + 1, 0);
        }

        // ── Time slot labels ──────────────────────────────────────────────────
        for (int s = 0; s < SLOTS.length; s++) {
            Label timeLabel = new Label(SLOTS[s][0] + "\n" + SLOTS[s][1]);
            timeLabel.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 11));
            timeLabel.setTextFill(Color.web("#94A3B8"));
            timeLabel.setAlignment(Pos.CENTER);
            timeLabel.setMaxWidth(Double.MAX_VALUE);
            timeLabel.setMaxHeight(Double.MAX_VALUE);
            timeLabel.setStyle(
                "-fx-background-color: #1E293B; -fx-background-radius: 8;" +
                "-fx-border-color: #334155; -fx-border-radius: 8;"
            );
            timeLabel.setPadding(new Insets(6));
            grid.add(timeLabel, 0, s + 1);

            // Empty cells for all days in this slot
            for (int d = 0; d < DAYS.length; d++) {
                grid.add(emptyCell(), d + 1, s + 1);
            }
        }

        // ── Place timetable entries as cards ──────────────────────────────────
        // Track unique course codes for consistent colour assignment
        java.util.Map<String, Integer> colorIndex = new java.util.LinkedHashMap<>();
        int nextColor = 0;

        for (TimetableEntry entry : entries) {
            int col = dayToCol(entry.getDayOfWeek());
            int row = timeToRow(entry.getStartTime());
            if (col < 0 || row < 0) continue;

            if (!colorIndex.containsKey(entry.getCourseCode())) {
                colorIndex.put(entry.getCourseCode(), nextColor % CARD_COLORS.length);
                nextColor++;
            }
            String color = CARD_COLORS[colorIndex.get(entry.getCourseCode())];

            // Remove the empty placeholder and insert the course card
            grid.getChildren().removeIf(node ->
                GridPane.getColumnIndex(node) != null &&
                GridPane.getRowIndex(node) != null &&
                GridPane.getColumnIndex(node) == col &&
                GridPane.getRowIndex(node) == row &&
                !(node instanceof Label && ((Label)node).getText().contains(":"))
            );

            grid.add(courseCard(entry, color), col, row);
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
        lbl.setPadding(new Insets(4, 8, 4, 8));
        lbl.setStyle(
            "-fx-background-color: #1E293B; -fx-background-radius: 6;" +
            "-fx-border-color: #334155; -fx-border-radius: 6;"
        );
        return lbl;
    }

    private StackPane emptyCell() {
        StackPane pane = new StackPane();
        pane.setMaxWidth(Double.MAX_VALUE);
        pane.setMaxHeight(Double.MAX_VALUE);
        pane.setStyle(
            "-fx-background-color: #1E293B; -fx-background-radius: 8;" +
            "-fx-border-color: #1E3A5F; -fx-border-radius: 8;"
        );
        return pane;
    }

    private VBox courseCard(TimetableEntry entry, String color) {
        Label codeLabel = new Label(entry.getCourseCode());
        codeLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        codeLabel.setTextFill(Color.WHITE);

        Label nameLabel = new Label(entry.getCourseName());
        nameLabel.setFont(Font.font("Segoe UI", 11));
        nameLabel.setTextFill(Color.web("#CBD5E1"));
        nameLabel.setWrapText(true);

        Label venueLabel = new Label(entry.getVenue());
        venueLabel.setFont(Font.font("Segoe UI", 10));
        venueLabel.setTextFill(Color.web("#94A3B8"));

        VBox card = new VBox(3, codeLabel, nameLabel, venueLabel);
        card.setPadding(new Insets(8));
        card.setMaxWidth(Double.MAX_VALUE);
        card.setMaxHeight(Double.MAX_VALUE);
        card.setStyle(
            "-fx-background-color: " + color + "22; " +   // 22 = ~13% opacity
            "-fx-background-radius: 8;" +
            "-fx-border-color: " + color + ";" +
            "-fx-border-radius: 8;" +
            "-fx-border-width: 1.5;"
        );

        // left accent bar
        Region accent = new Region();
        accent.setPrefWidth(4);
        accent.setMaxHeight(Double.MAX_VALUE);
        accent.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 4 0 0 4;");

        HBox wrapper = new HBox(accent, card);
        wrapper.setMaxWidth(Double.MAX_VALUE);
        wrapper.setMaxHeight(Double.MAX_VALUE);
        return new VBox(wrapper); // wrap in VBox so GridPane respects sizing
    }

    // ── Mapping helpers ───────────────────────────────────────────────────────

    private int dayToCol(String day) {
        for (int i = 0; i < DAYS.length; i++) {
            if (DAYS[i].equalsIgnoreCase(day)) return i + 1;
        }
        return -1;
    }

    private int timeToRow(String startTime) {
        for (int i = 0; i < SLOTS.length; i++) {
            if (SLOTS[i][0].equals(startTime)) return i + 1;
        }
        return -1;
    }

    private RowConstraints rowConstraint(double height) {
        RowConstraints rc = new RowConstraints();
        rc.setMinHeight(height);
        rc.setPrefHeight(height);
        return rc;
    }
}
