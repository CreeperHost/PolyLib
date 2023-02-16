package net.creeperhost.polylib.config;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import dev.architectury.platform.Platform;

import javax.annotation.Nonnull;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;

public class ConfigBuilder
{
    String CONFIG_NAME;
    AtomicReference<ConfigData> CONFIG_DATA = new AtomicReference<>();
    Path CONFIG_PATH;
    Class<?> CONFIG_DATA_CLASS;

    Jankson JANKSON = Jankson.builder().build();

    public ConfigBuilder(@Nonnull String configName, @Nonnull Path configPath, @Nonnull Class<?> clazz)
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

    public ConfigBuilder(@Nonnull String configName, @Nonnull Path configPath, ConfigData data)
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

    public ConfigBuilder(@Nonnull String configName, @Nonnull Class<?> clazz)
    {
        this.CONFIG_NAME = configName;
        this.CONFIG_PATH = Platform.getConfigFolder().resolve(configName + ".json");
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
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }

    public void save()
    {
        try
        {
            ConfigData data = (ConfigData) CONFIG_DATA_CLASS.newInstance();
            CONFIG_DATA.set(data);

            FileWriter fileWriter = new FileWriter(CONFIG_PATH.toFile());
            fileWriter.write(saveConfig());
            fileWriter.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void save(ConfigData data)
    {
        try
        {
            //            ConfigData data = (ConfigData) CONFIG_DATA_CLASS.newInstance();
            CONFIG_DATA.set(data);

            FileWriter fileWriter = new FileWriter(CONFIG_PATH.toFile());
            fileWriter.write(saveConfig());
            fileWriter.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
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
}
