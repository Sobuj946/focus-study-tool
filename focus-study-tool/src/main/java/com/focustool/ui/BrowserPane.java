package com.focustool.ui;

import com.focustool.util.WhitelistManager;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * A restricted browser: only domains in the whitelist can load.
 * Anything else gets cancelled before it renders, and a "blocked" page
 * is shown instead. Manage allowed sites from the sidebar.
 */
public class BrowserPane extends BorderPane {

    private final WhitelistManager whitelist;
    private final WebView webView = new WebView();
    private final WebEngine engine = webView.getEngine();
    private final TextField addressBar = new TextField();
    private String lastAllowedUrl = "about:blank";

    public BrowserPane(WhitelistManager whitelist) {
        this.whitelist = whitelist;
        setPadding(new Insets(8));

        setTop(buildAddressBar());
        setCenter(webView);
        setRight(buildWhitelistPanel());

        // Block mechanism: watch the location itself and cancel loads that
        // aren't on the whitelist before they render.
        engine.locationProperty().addListener((obs, oldUrl, newUrl) -> {
            if (newUrl == null || newUrl.isEmpty()) return;
            if (newUrl.startsWith("data:")) return; // our own blocked-page content

            if (whitelist.isAllowed(newUrl)) {
                lastAllowedUrl = newUrl;
                addressBar.setText(newUrl);
            } else {
                engine.getLoadWorker().cancel();
                showBlockedPage(newUrl);
            }
        });

        showBlockedPage(null);
    }

    private ToolBar buildAddressBar() {
        addressBar.setPromptText("study site URL, e.g. https://wikipedia.org");
        addressBar.setOnAction(e -> navigate(addressBar.getText()));

        Button go = new Button("Go");
        go.setOnAction(e -> navigate(addressBar.getText()));

        HBox.setHgrow(addressBar, Priority.ALWAYS);
        ToolBar bar = new ToolBar(addressBar, go);
        return bar;
    }

    private void navigate(String url) {
        if (url == null || url.isBlank()) return;
        String target = url.trim();
        if (!target.startsWith("http://") && !target.startsWith("https://")) {
            target = "https://" + target;
        }
        if (whitelist.isAllowed(target)) {
            engine.load(target);
        } else {
            showBlockedPage(target);
        }
    }

    private void showBlockedPage(String attemptedUrl) {
        String reason = attemptedUrl == null ? "" :
                "<p style='color:#888'>Blocked address: " + escape(attemptedUrl) + "</p>";
        String html = "<html><body style='font-family:sans-serif;text-align:center;margin-top:80px;color:#333'>"
                + "<h1 style='color:#c0392b'>Site Blocked</h1>"
                + "<p>This site isn't on your study whitelist, so it's blocked while Focus Mode is on.</p>"
                + reason
                + "<p>Add it from the panel on the right if you actually need it for studying.</p>"
                + "</body></html>";
        engine.loadContent(html);
    }

    private String escape(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private VBox buildWhitelistPanel() {
        Label title = new Label("Allowed study sites");
        title.setStyle("-fx-font-weight: bold;");

        ListView<String> listView = new ListView<>(whitelist.getDomains());
        listView.setPrefWidth(200);
        listView.setPrefHeight(220);

        TextField newDomain = new TextField();
        newDomain.setPromptText("e.g. moodle.mysite.edu");

        Button addBtn = new Button("Add");
        addBtn.setMaxWidth(Double.MAX_VALUE);
        addBtn.setOnAction(e -> {
            whitelist.addDomain(newDomain.getText());
            newDomain.clear();
        });

        Button removeBtn = new Button("Remove selected");
        removeBtn.setMaxWidth(Double.MAX_VALUE);
        removeBtn.setOnAction(e -> {
            String selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null) whitelist.removeDomain(selected);
        });

        VBox box = new VBox(8, title, listView, newDomain, addBtn, removeBtn);
        box.setPadding(new Insets(0, 0, 0, 12));
        box.setPrefWidth(220);
        return box;
    }
}
