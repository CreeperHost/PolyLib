package net.creeperhost.polylib.fabric.datagen.providers;

import net.creeperhost.polylib.PolyLib;
import net.creeperhost.polylib.fabric.datagen.ModuleType;
import net.creeperhost.polylib.fabric.datagen.PolyDataGen;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.KeyMapping;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class PolyLanguageProvider extends FabricLanguageProvider
{
    ModuleType moduleType;
    Map<String, String> values = new HashMap<>();

    public PolyLanguageProvider(FabricDataOutput dataOutput, ModuleType moduleType, CompletableFuture<HolderLookup.Provider> registryLookup)
    {
        super(dataOutput, "en_us", registryLookup);
        this.moduleType = moduleType;

        PolyLib.LOGGER.info("PolyLanguageProvider created for " + dataOutput.getModId() + " " + moduleType.name());
    }

    public void add(String key, String translation, ModuleType moduleType)
    {
        if (this.moduleType == moduleType)
        {
            PolyLib.LOGGER.info("Adding " + key + " for " + moduleType.name());
            this.values.put(key, translation);
        }
    }

    public void add(Item item, String translation, ModuleType moduleType)
    {
        add(item.getDescriptionId(), translation, moduleType);
    }

    public void add(Block block, String translation, ModuleType moduleType)
    {
        add(block.getDescriptionId(), translation, moduleType);
    }

    public void add(CreativeModeTab creativeModeTab, String translation, ModuleType moduleType)
    {
        add(creativeModeTab.getDisplayName().getString(), translation, moduleType);
    }

    public void add(KeyMapping keyMapping, String translation, ModuleType moduleType)
    {
        add(keyMapping.getName(), translation, moduleType);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder translationBuilder) {
        this.values.forEach((s, s2) ->
        {
            PolyLib.LOGGER.info("Running data gen for key " + s + " " + dataOutput.getOutputFolder());
            translationBuilder.add(s, s2);
        });
    }

    public Path appendPath(ModuleType moduleType)
    {
        return PolyDataGen.getPathFromModuleType(moduleType);
    }

    @Override
    public CompletableFuture<?> run(CachedOutput writer)
    {
        //If values is empty don't generate an empty array json file
        if (values.isEmpty()) return null;
        dataOutput.outputFolder = appendPath(moduleType);
        return super.run(writer);
    }
}
