package net.creeperhost.polylib.config;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import net.creeperhost.polylib.Constants;
import net.creeperhost.polylib.platform.Services;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.atomic.AtomicReference;

public class ConfigBuilder
{
    String CONFIG_NAME;
    AtomicReference<ConfigData> CONFIG_DATA = new AtomicReference<>();
    Path CONFIG_PATH;
    Class<?> CONFIG_DATA_CLASS;

    Jankson JANKSON = Jankson.builder().build();

    public ConfigBuilder(@NotNull String configName, @NotNull Path configPath, @NotNull Class<?> clazz)
    {
        this.CONFIG_NAME = configName;
        this.CONFIG_PATH = configPath;
        this.CONFIG_DATA_CLASS = clazz;
        if (CONFIG_PATH.toFile().exists())
        {
            load();
        } else
        {
            save();
        }
    }

    public ConfigBuilder(@NotNull String configName, @NotNull Path configPath, ConfigData data)
    {
        this.CONFIG_NAME = configName;
        this.CONFIG_PATH = configPath;
        this.CONFIG_DATA_CLASS = data.getClass();
        if (CONFIG_PATH.toFile().exists())
        {
            load(data);
        } else
        {
            save(data);
        }
    }

    public ConfigBuilder(@NotNull String configName, @NotNull Class<?> clazz)
    {
        this.CONFIG_NAME = configName;
        this.CONFIG_PATH = Services.PLATFORM.getConfigFolder().resolve(configName + ".json5");
        this.CONFIG_DATA_CLASS = clazz;
        if (CONFIG_PATH.toFile().exists())
        {
            load();
        } else
        {
            save();
        }
    }

    public void load()
    {
        try
        {
            JsonObject jObject = JANKSON.load(CONFIG_PATH.toFile());
            ConfigData newData = (ConfigData) JANKSON.fromJson(jObject, CONFIG_DATA_CLASS);
            CONFIG_DATA.set(newData);
        }
        catch (Exception e)
        {
            Constants.LOG.warn("Failed to load config {}, backing it up and writing defaults", CONFIG_PATH, e);
            recoverInvalidConfig();
        }
    }

    public void load(ConfigData data)
    {
        try
        {
            JsonObject jObject = JANKSON.load(CONFIG_PATH.toFile());
            ConfigData newData = (ConfigData) JANKSON.fromJson(jObject, CONFIG_DATA_CLASS);
            data = newData;
            CONFIG_DATA.set(newData);
        }
        catch (Exception e)
        {
            Constants.LOG.warn("Failed to load config {}, backing it up and writing defaults", CONFIG_PATH, e);
            recoverInvalidConfig();
        }
    }

    public void save()
    {
        try
        {
            saveOrThrow();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void save(ConfigData data)
    {
        CONFIG_DATA.set(data);
        save();
    }

    /** Saves the current object, reporting failure so an editor can retain its pending changes. */
    public void saveOrThrow() throws IOException
    {
        if (CONFIG_DATA.get() == null) {
            try {
                CONFIG_DATA.set(createDefaultData());
            } catch (ReflectiveOperationException e) {
                throw new IOException("Cannot create defaults for " + CONFIG_NAME, e);
            }
        }
        String serialized = saveConfig();
        Path destination = CONFIG_PATH.toAbsolutePath();
        Files.createDirectories(destination.getParent());
        Path temporary = Files.createTempFile(destination.getParent(), "polylib-config-", ".tmp");
        try {
            Files.writeString(temporary, serialized, StandardCharsets.UTF_8);
            Files.move(temporary, destination, StandardCopyOption.REPLACE_EXISTING);
        } finally {
            Files.deleteIfExists(temporary);
        }
    }


    public String saveConfig()
    {
        ConfigData conf = CONFIG_DATA.get();
        JsonElement elem = JANKSON.toJson(conf);
        return elem.toJson(true, true);
    }

    public String getConfigName()
    {
        return CONFIG_NAME;
    }

    public Path getConfigPath()
    {
        return CONFIG_PATH;
    }

    public ConfigData getConfigData()
    {
        return CONFIG_DATA.get();
    }

    private ConfigData createDefaultData() throws ReflectiveOperationException
    {
        return (ConfigData) CONFIG_DATA_CLASS.getDeclaredConstructor().newInstance();
    }

    private void recoverInvalidConfig()
    {
        try
        {
            if (CONFIG_PATH.toFile().exists())
            {
                Path backup = CONFIG_PATH.resolveSibling(CONFIG_PATH.getFileName() + ".invalid");
                Files.move(CONFIG_PATH, backup, StandardCopyOption.REPLACE_EXISTING);
            }
            CONFIG_DATA.set(createDefaultData());
            save();
        }
        catch (Exception recoverException)
        {
            recoverException.printStackTrace();
        }
    }
}
