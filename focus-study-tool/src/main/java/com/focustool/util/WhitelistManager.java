package com.focustool.util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.net.URI;
import java.nio.file.*;
import java.util.List;

/**
 * Keeps track of which domains are allowed inside the embedded browser.
 * Anything not on this list (facebook.com, youtube.com, etc.) gets blocked.
 * List is persisted to whitelist.txt next to the app so it survives restarts.
 */
public class WhitelistManager {

    private static final Path FILE_PATH = Paths.get(System.getProperty("user.home"),
            ".focus-study-tool", "whitelist.txt");

    private final ObservableList<String> domains = FXCollections.observableArrayList();

    public WhitelistManager() {
        load();
    }

    private void load() {
        try {
            if (Files.exists(FILE_PATH)) {
                List<String> lines = Files.readAllLines(FILE_PATH);
                for (String line : lines) {
                    String d = line.trim().toLowerCase();
                    if (!d.isEmpty()) domains.add(d);
                }
                return;
            }
        } catch (IOException ignored) {
            // fall through to defaults
        }
        // sensible study-only defaults on first run
        domains.addAll(
                "wikipedia.org",
                "scholar.google.com",
                "docs.google.com",
                "drive.google.com"
        );
        save();
    }

    public void save() {
        try {
            Files.createDirectories(FILE_PATH.getParent());
            Files.write(FILE_PATH, domains);
        } catch (IOException e) {
            System.err.println("Could not save whitelist: " + e.getMessage());
        }
    }

    public ObservableList<String> getDomains() {
        return domains;
    }

    public void addDomain(String domain) {
        String d = domain.trim().toLowerCase().replaceFirst("^www\\.", "");
        if (!d.isEmpty() && !domains.contains(d)) {
            domains.add(d);
            save();
        }
    }

    public void removeDomain(String domain) {
        domains.remove(domain);
        save();
    }

    /**
     * Checks whether a URL's host matches or is a subdomain of any whitelisted domain.
     */
    public boolean isAllowed(String url) {
        try {
            URI uri = URI.create(url);
            String host = uri.getHost();
            if (host == null) {
                // about:blank, data: URIs used for our own "blocked" page, etc.
                return true;
            }
            host = host.toLowerCase();
            for (String allowed : domains) {
                if (host.equals(allowed) || host.endsWith("." + allowed)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
