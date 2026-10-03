package org.example.payrollmanagment;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserDAO {

    private static final String FILE_PATH = "users.dat";

    public UserDAO() {
        ensureFileExists();
    }

    private void ensureFileExists() {
        try {
            Path path = Paths.get(FILE_PATH);
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to initialize user storage file.", e);
        }
    }

    public boolean userExists(String username) {
        return findByUsername(username) != null;
    }

    public User findByUsername(String username) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(FILE_PATH), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                User user = User.fromFileLine(line);
                if (user != null && user.getUsername().equalsIgnoreCase(username)) {
                    return user;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading user data.", e);
        }
        return null;
    }

    public boolean registerUser(String username, String rawPassword, String fullName, String email) {
        if (userExists(username)) {
            return false;
        }
        String hashed = hashPassword(rawPassword);
        User user = new User(username, hashed, fullName, email);

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(FILE_PATH, true), StandardCharsets.UTF_8))) {
            writer.write(user.toFileLine());
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Error saving new user.", e);
        }
        return true;
    }

    public User authenticate(String username, String rawPassword) {
        User user = findByUsername(username);
        if (user == null) {
            return null;
        }
        String hashedAttempt = hashPassword(rawPassword);
        return hashedAttempt.equals(user.getPassword()) ? user : null;
    }

    private String hashPassword(String rawPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available.", e);
        }
    }
}