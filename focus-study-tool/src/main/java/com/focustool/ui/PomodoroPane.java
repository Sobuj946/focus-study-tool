package com.focustool.ui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.text.Font;
import javafx.util.Duration;

/**
 * Standard Pomodoro timer: focus session followed by a break, repeating.
 * Lives in the sidebar so it's visible no matter which tab (PDF/Browser) is active.
 */
public class PomodoroPane extends VBox {

    private static final int DEFAULT_FOCUS_MIN = 25;
    private static final int DEFAULT_BREAK_MIN = 5;

    private final Label bigClock = new Label("25:00");
    private final Label modeLabel = new Label("Focus session");
    private final Label sessionCountLabel = new Label("Completed sessions: 0");
    private final Spinner<Integer> focusSpinner = new Spinner<>(5, 90, DEFAULT_FOCUS_MIN);
    private final Spinner<Integer> breakSpinner = new Spinner<>(1, 30, DEFAULT_BREAK_MIN);

    private Timeline timeline;
    private int secondsLeft;
    private boolean onBreak = false;
    private int completedSessions = 0;
    private boolean running = false;

    public PomodoroPane() {
        setPadding(new Insets(12));
        setSpacing(10);
        setPrefWidth(220);
        setAlignment(Pos.TOP_CENTER);
        setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #ddd; -fx-border-width: 0 1 0 0;");

        Label title = new Label("Pomodoro Timer");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        bigClock.setFont(Font.font(36));
        modeLabel.setStyle("-fx-text-fill: #555;");

        Button startPauseBtn = new Button("Start");
        startPauseBtn.setMaxWidth(Double.MAX_VALUE);
        startPauseBtn.setOnAction(e -> {
            if (running) {
                pause();
                startPauseBtn.setText("Resume");
            } else {
                start();
                startPauseBtn.setText("Pause");
            }
        });

        Button resetBtn = new Button("Reset");
        resetBtn.setMaxWidth(Double.MAX_VALUE);
        resetBtn.setOnAction(e -> {
            reset();
            startPauseBtn.setText("Start");
        });

        Label focusLabel = new Label("Focus (min)");
        focusSpinner.setEditable(true);
        focusSpinner.setMaxWidth(Double.MAX_VALUE);

        Label breakLabel = new Label("Break (min)");
        breakSpinner.setEditable(true);
        breakSpinner.setMaxWidth(Double.MAX_VALUE);

        // Changing spinners resets the current (not-yet-started) countdown to match.
        focusSpinner.valueProperty().addListener((obs, o, n) -> {
            if (!running && !onBreak) resetClockDisplay();
        });

        getChildren().addAll(
                title,
                bigClock,
                modeLabel,
                startPauseBtn,
                resetBtn,
                new Separator(),
                focusLabel, focusSpinner,
                breakLabel, breakSpinner,
                new Separator(),
                sessionCountLabel
        );

        resetClockDisplay();
    }

    private void resetClockDisplay() {
        onBreak = false;
        secondsLeft = focusSpinner.getValue() * 60;
        modeLabel.setText("Focus session");
        updateClockLabel();
    }

    private void start() {
        running = true;
        if (timeline != null) timeline.stop();
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> tick()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void pause() {
        running = false;
        if (timeline != null) timeline.stop();
    }

    private void reset() {
        running = false;
        if (timeline != null) timeline.stop();
        resetClockDisplay();
    }

    private void tick() {
        secondsLeft--;
        if (secondsLeft <= 0) {
            switchMode();
        }
        updateClockLabel();
    }

    private void switchMode() {
        if (!onBreak) {
            completedSessions++;
            sessionCountLabel.setText("Completed sessions: " + completedSessions);
            onBreak = true;
            secondsLeft = breakSpinner.getValue() * 60;
            modeLabel.setText("Break time");
        } else {
            onBreak = false;
            secondsLeft = focusSpinner.getValue() * 60;
            modeLabel.setText("Focus session");
        }
        // Simple audible/visual nudge at the transition; avoids external sound files.
        java.awt.Toolkit.getDefaultToolkit().beep();
    }

    private void updateClockLabel() {
        int m = secondsLeft / 60;
        int s = secondsLeft % 60;
        bigClock.setText(String.format("%02d:%02d", m, s));
    }
}
