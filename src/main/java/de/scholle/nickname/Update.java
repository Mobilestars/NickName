package de.scholle.nickname;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Level;

public class Update {

    private final JavaPlugin plugin;
    private boolean updateAvailable = false;
    private String newVersionString;

    public Update(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void checkForUpdates() {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    String projectId = "QnIMZwB9";
                    URL url = new URL("https://api.modrinth.com/v2/project/" + projectId + "/version");
                    HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("User-Agent", "Nickname-Updater/1.0");

                    int responseCode = connection.getResponseCode();
                    if (responseCode == 200) {
                        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                            StringBuilder content = new StringBuilder();
                            String line;
                            while ((line = in.readLine()) != null) content.append(line);

                            Gson gson = new Gson();
                            JsonArray versions = gson.fromJson(content.toString(), JsonArray.class);
                            if (versions != null && versions.size() > 0) {
                                JsonObject latestVersion = versions.get(0).getAsJsonObject();
                                String latestVersionNumber = latestVersion.get("version_number").getAsString();
                                String currentVersion = plugin.getDescription().getVersion();

                                if (!currentVersion.equals(latestVersionNumber)) {
                                    updateAvailable = true;
                                    newVersionString = latestVersionNumber;
                                    Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "========================================");
                                    Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "Nickname is outdated!");
                                    Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "Your version: " + ChatColor.RED + currentVersion);
                                    Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "Latest version: " + ChatColor.GREEN + newVersionString);
                                    Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "Download here: " + ChatColor.AQUA + "https://modrinth.com/plugin/" + projectId);
                                    Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "========================================");
                                } else {
                                    Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[Nickname] You are running the latest version (" + currentVersion + ").");
                                }
                            }
                        }
                    } else {
                        plugin.getLogger().warning("Failed to check for updates. Response code: " + responseCode);
                    }
                } catch (Exception e) {
                    plugin.getLogger().log(Level.SEVERE, "Could not check for updates!", e);
                }
            }
        }.runTaskAsynchronously(plugin);
    }
}
