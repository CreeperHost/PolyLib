package net.creeperhost.polylib.fabric.datagen.providers;

import com.google.common.collect.Maps;
import net.creeperhost.polylib.PolyLib;
import net.creeperhost.polylib.fabric.datagen.ModuleType;
import net.creeperhost.polylib.fabric.datagen.PolyDataGen;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.data.CachedOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Map;

public class PolyItemTagProvider extends FabricTagProvider.ItemTagProvider
{
    private final ModuleType moduleType;
    private final Map<Item, TagKey<Item>> values = Maps.newLinkedHashMap();

    public PolyItemTagProvider(FabricDataGenerator dataGenerator, @Nullable BlockTagProvider blockTagProvider, ModuleType moduleType)
    {
        super(dataGenerator, blockTagProvider);
        this.moduleType = moduleType;

        PolyLib.LOGGER.info("PolyItemTagProvider created for " + dataGenerator.getModId() + " " + moduleType.name());
    }

    @Override
    protected void generateTags()
    {
        values.forEach((item, tagKey) ->
        {
            PolyLib.LOGGER.info("Running data gen for item tag " + item.getDescriptionId() + " " + getFabricDataGenerator().getOutputFolder());
            tag(tagKey).add(item);
        });
    }

    public void add(TagKey<Item> tagKey, Item block, ModuleType moduleType)
    {
        if (this.moduleType == moduleType)
        {
            values.put(block, tagKey);
        }
    }

    @Override
    public void run(CachedOutput cachedOutput)
    {
        //If values is empty don't generate an empty array json file
        if (values.isEmpty()) return;
        pathProvider.root = appendPath(moduleType);
        super.run(cachedOutput);
    }

    public Path appendPath(ModuleType moduleType)
    {
        return PolyDataGen.getPathFromModuleType(moduleType).resolve("data");
    }
}
