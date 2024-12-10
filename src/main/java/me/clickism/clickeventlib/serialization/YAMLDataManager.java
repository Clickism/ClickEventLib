package me.clickism.clickeventlib.serialization;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Simple YAML data manager.
 */
public class YAMLDataManager extends DataManager {

    private FileConfiguration config;

    /**
     * Creates a new YAMLDataManager instance.
     *
     * @param plugin    plugin
     * @param directory directory of the file
     * @param fileName  fileName, i.E: "config.yml"
     * @throws IOException if an I/O error occurs
     */
    public YAMLDataManager(JavaPlugin plugin, @NotNull File directory, String fileName) throws IOException {
        super(plugin, directory, fileName);
        saveDefaultConfig();
    }

    @Override
    public void load() {
        config = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Saves the configuration to the file.
     */
    public void saveConfig() {
        try {
            config.save(file);
        } catch (IOException exception) {
            plugin.getLogger().severe("\"" + file.getName() + "\" config couldn't be saved.");
        }
    }

    private void saveDefaultConfig() throws IOException {
        String path = getTrimmedPath(plugin, file);
        try {
            plugin.saveResource(path, false);
        } catch (IllegalArgumentException exception) {
            file.createNewFile();
        }

        config = YamlConfiguration.loadConfiguration(file);
        InputStream defaultStream = plugin.getResource(path);

        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            config.setDefaults(defaultConfig);
            saveConfig();
            defaultStream.close();
        }
    }

    /**
     * Get the file configuration.
     * This is NOT guaranteed to return the same instance every time.
     *
     * @return file configuration
     */
    public FileConfiguration getConfig() {
        return config;
    }
}
