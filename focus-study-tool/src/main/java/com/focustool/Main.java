package com.focustool;

import com.focustool.ui.BrowserPane;
import com.focustool.ui.PdfViewerPane;
import com.focustool.ui.PomodoroPane;
import com.focustool.util.WhitelistManager;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Focus Study Tool
 * ------------------
 * A distraction-free study app:
 *  - Left sidebar: Pomodoro timer, always visible.
 *  - "Study PDF" tab: open and read PDF files.
 *  - "Study Web" tab: an embedded browser that ONLY loads whitelisted
 *    study domains (Wikipedia, Google Scholar, etc. by default) —
 *    everything else (Facebook, YouTube, ...) is blocked automatically.
 *
 * There is no way to open a regular system browser from inside this app,
 * so as long as the student stays in this window, they stay on-task.
 */
public class Main extends Application {

    @Override
    public void start(Stage stage) {
        WhitelistManager whitelist = new WhitelistManager();

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab pdfTab = new Tab("Study PDF", new PdfViewerPane());
        Tab browserTab = new Tab("Study Web", new BrowserPane(whitelist));
        tabPane.getTabs().addAll(pdfTab, browserTab);

        BorderPane root = new BorderPane();
        root.setLeft(new PomodoroPane());
        root.setCenter(tabPane);

        Scene scene = new Scene(root, 1100, 720);
        stage.setTitle("Focus Study Tool");
        stage.setScene(scene);

        // Optional: uncomment to force fullscreen and make it harder to
        // switch away from the app (student can still Alt+Tab to the OS
        // unless you also apply OS-level kiosk settings).
        // stage.setFullScreen(true);
        // stage.setFullScreenExitHint("");

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
