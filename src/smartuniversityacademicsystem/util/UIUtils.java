package smartuniversityacademicsystem.util;

import javafx.animation.*;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.*;
import javafx.util.Duration;
import javafx.util.Callback;

public class UIUtils {

    // ── Page transition ───────────────────────────────────────────────────────

    /** Fade + slide-up entrance for a content pane. */
    public static void animateIn(Node node) {
        node.setOpacity(0);
        node.setTranslateY(22);
        FadeTransition fade = new FadeTransition(Duration.millis(260), node);
        fade.setFromValue(0);
        fade.setToValue(1);
        TranslateTransition slide = new TranslateTransition(Duration.millis(260), node);
        slide.setFromY(22);
        slide.setToY(0);
        ParallelTransition pt = new ParallelTransition(fade, slide);
        pt.setInterpolator(Interpolator.EASE_OUT);
        pt.play();
    }

    // ── Staggered children ────────────────────────────────────────────────────

    /** Stagger-animate a list of nodes: each starts delayMs after the previous. */
    public static void staggerIn(java.util.List<? extends Node> nodes, int delayMs) {
        for (int i = 0; i < nodes.size(); i++) {
            final Node n = nodes.get(i);
            n.setOpacity(0);
            n.setTranslateY(18);
            FadeTransition fade = new FadeTransition(Duration.millis(240), n);
            fade.setFromValue(0);
            fade.setToValue(1);
            TranslateTransition slide = new TranslateTransition(Duration.millis(240), n);
            slide.setFromY(18);
            slide.setToY(0);
            ParallelTransition pt = new ParallelTransition(fade, slide);
            pt.setDelay(Duration.millis(i * delayMs));
            pt.play();
        }
    }

    // ── Card hover ────────────────────────────────────────────────────────────

    /** Gentle scale-up on hover for clickable cards. */
    public static void addCardHover(Region card) {
        ScaleTransition si = new ScaleTransition(Duration.millis(150), card);
        ScaleTransition so = new ScaleTransition(Duration.millis(150), card);
        card.setOnMouseEntered(e -> { si.setToX(1.035); si.setToY(1.035); si.play(); });
        card.setOnMouseExited(e  -> { so.setToX(1.0);   so.setToY(1.0);   so.play(); });
        card.setStyle(card.getStyle() + " -fx-cursor: default;");
    }

    // ── Animated ProgressBar ──────────────────────────────────────────────────

    /** Animate a ProgressBar from 0 → target over 800 ms with ease-out. */
    public static void animateProgress(ProgressBar bar, double target) {
        Timeline tl = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(bar.progressProperty(), 0.0, Interpolator.EASE_OUT)),
            new KeyFrame(Duration.millis(800),
                new KeyValue(bar.progressProperty(), target, Interpolator.EASE_OUT))
        );
        tl.play();
    }

    // ── Avatar circle ─────────────────────────────────────────────────────────

    /** Circle avatar showing the user's initials. */
    public static StackPane avatarCircle(String fullName, String hexColor) {
        Circle circle = new Circle(22, Color.web(hexColor));
        Label initLabel = new Label(initials(fullName));
        initLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        initLabel.setTextFill(Color.WHITE);
        StackPane sp = new StackPane(circle, initLabel);
        sp.setPrefSize(44, 44);
        sp.setMaxSize(44, 44);
        return sp;
    }

    private static String initials(String name) {
        if (name == null || name.isEmpty()) return "?";
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
    }

    // ── Toast notification ────────────────────────────────────────────────────

    /** Floating toast that auto-dismisses after ~3 s. Root must be a StackPane. */
    public static void toast(StackPane root, String message, boolean success) {
        Label t = new Label(message);
        t.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 13));
        t.setTextFill(Color.WHITE);
        t.setPadding(new Insets(12, 22, 12, 22));
        t.setStyle(
            "-fx-background-color: " + (success ? "#059669" : "#DC2626") + ";" +
            "-fx-background-radius: 10;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.45), 12, 0, 0, 3);"
        );
        t.setOpacity(0);
        StackPane.setAlignment(t, Pos.BOTTOM_CENTER);
        StackPane.setMargin(t, new Insets(0, 0, 30, 0));
        root.getChildren().add(t);

        FadeTransition fadeIn  = new FadeTransition(Duration.millis(200), t);
        fadeIn.setToValue(1);
        PauseTransition hold   = new PauseTransition(Duration.seconds(2.6));
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), t);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(ev -> root.getChildren().remove(t));

        new SequentialTransition(fadeIn, hold, fadeOut).play();
    }

    // ── Loading spinner ───────────────────────────────────────────────────────

    /** Indeterminate progress indicator for loading states. */
    public static ProgressIndicator spinner() {
        ProgressIndicator pi = new ProgressIndicator(-1);
        pi.setPrefSize(44, 44);
        pi.setStyle("-fx-progress-color: #2563EB;");
        return pi;
    }

    // ── Table row hover ───────────────────────────────────────────────────────

    /** Row factory that adds a subtle hover highlight to any TableView. */
    public static <T> Callback<TableView<T>, TableRow<T>> hoverRowFactory() {
        return tv -> {
            TableRow<T> row = new TableRow<T>();
            row.setOnMouseEntered(e -> {
                if (!row.isEmpty()) row.setStyle("-fx-background-color: #263248;");
            });
            row.setOnMouseExited(e -> row.setStyle(""));
            return row;
        };
    }

    // ── Sidebar top accent ────────────────────────────────────────────────────

    /** A thin colored stripe for the very top of a sidebar. */
    public static Region sidebarAccent(String hexColor) {
        Region r = new Region();
        r.setPrefHeight(3);
        r.setStyle("-fx-background-color: " + hexColor + ";");
        return r;
    }

    // ── Stat card builder ─────────────────────────────────────────────────────

    /**
     * Card with colored top accent, value label, descriptor label,
     * drop-shadow, and hover scale.
     */
    public static VBox statCard(String label, String value, String color, double prefWidth) {
        Region accent = new Region();
        accent.setPrefHeight(4);
        accent.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 12 12 0 0;");

        Label valLabel = new Label(value);
        valLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));
        valLabel.setTextFill(Color.web(color));

        Label lbl = new Label(label);
        lbl.setFont(Font.font("Segoe UI", 12));
        lbl.setTextFill(Color.web("#94A3B8"));

        VBox inner = new VBox(6, valLabel, lbl);
        inner.setPadding(new Insets(14, 18, 18, 18));

        VBox card = new VBox(0, accent, inner);
        card.setPrefWidth(prefWidth);
        card.setStyle(
            "-fx-background-color: #1E293B;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: #334155;" +
            "-fx-border-radius: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.30), 10, 0, 0, 3);"
        );
        addCardHover(card);
        return card;
    }
}
