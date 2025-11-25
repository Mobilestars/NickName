package de.scholle.nickname;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class Language {

    private final JavaPlugin plugin;

    public Language(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public JsonObject getPlayerLanguage(Player player) {
        String locale = player.getLocale().replace("-", "_").toLowerCase();

        String fileName;
        if (locale.length() >= 5)
            fileName = locale.substring(0, 2) + "_" + locale.substring(3).toUpperCase() + ".json";
        else
            fileName = "en_US.json";

        File langFolder = new File(plugin.getDataFolder(), "lang");
        if (!langFolder.exists()) langFolder.mkdirs();

        File langFile = new File(langFolder, fileName);
        if (!langFile.exists()) langFile = new File(langFolder, "en_US.json");

        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(langFile), StandardCharsets.UTF_8)) {
            JsonParser parser = new JsonParser();
            return parser.parse(reader).getAsJsonObject();
        } catch (Exception e) {
            return new JsonObject();
        }
    }

    public String get(Player player, String key) {
        JsonObject lang = getPlayerLanguage(player);
        if (lang.has(key)) return lang.get(key).getAsString();
        return "{" + key + "}";
    }

    public String get(Player player, String key, Map<String, String> replacements) {
        String text = get(player, key);
        for (Map.Entry<String, String> entry : replacements.entrySet())
            text = text.replace("%" + entry.getKey() + "%", entry.getValue());
        return text;
    }
}
