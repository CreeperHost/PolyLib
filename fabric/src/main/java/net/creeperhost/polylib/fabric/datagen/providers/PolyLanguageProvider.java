package net.creeperhost.polylib.fabric.datagen.providers;

import net.creeperhost.polylib.PolyLib;
import net.creeperhost.polylib.fabric.datagen.ModuleType;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.data.CachedOutput;
import net.minecraft.world.item.Item;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class PolyLanguageProvider extends FabricLanguageProvider
{
    private final Path basePath;

    ModuleType moduleType;
    Map<String, String> values = new HashMap<>();

    public PolyLanguageProvider(FabricDataGenerator dataGenerator, ModuleType moduleType)
    {
        super(dataGenerator, "en_us");
        this.moduleType = moduleType;
        basePath = Path.of("").toAbsolutePath().getParent().getParent().getParent();

        PolyLib.LOGGER.info("PolyLanguageProvider created for " + dataGenerator.getModId() + " " + moduleType.name());
    }

    public void add(String key, String translation, ModuleType moduleType)
    {
        if(this.moduleType == moduleType)
        {
            PolyLib.LOGGER.info("Adding " + key + " for " + moduleType.name());
            this.values.put(key, translation);
        }
    }

    public void add(Item item, String translation, ModuleType moduleType)
    {
        add(item.getDescriptionId(), translation, moduleType);
    }

    @Override
    public void generateTranslations(TranslationBuilder translationBuilder)
    {
        this.values.forEach((s, s2) ->
        {
            PolyLib.LOGGER.info("Running data gen for key " + s + " " + dataGenerator.getOutputFolder());
            translationBuilder.add(s, s2);
        });
    }

    public Path appendPath(ModuleType moduleType)
    {
        return basePath.resolve(moduleType.name().toLowerCase() + "/src/generated/resources");
    }

    @Override
    public void run(CachedOutput writer) throws IOException
    {
        //If values is empty don't generate an empty array json file
        if(values.isEmpty()) return;
        dataGenerator.outputFolder = appendPath(moduleType);
        super.run(writer);
    }
}
