package net.creeperhost.polylib.fabric.datagen.providers;

import com.google.common.collect.Maps;
import net.creeperhost.polylib.PolyLib;
import net.creeperhost.polylib.fabric.datagen.ModuleType;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.data.CachedOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.nio.file.Path;
import java.util.Map;

public class PolyBlockTagProvider extends FabricTagProvider.BlockTagProvider
{
    private final ModuleType moduleType;
    private final Path basePath;
    private final Map<Block, TagKey<Block>> values = Maps.newLinkedHashMap();


    public PolyBlockTagProvider(FabricDataGenerator dataGenerator, ModuleType moduleType)
    {
        super(dataGenerator);
        basePath = Path.of("").toAbsolutePath().getParent().getParent();
        this.moduleType = moduleType;

        PolyLib.LOGGER.info("PolyBlockTagProvider created for " + dataGenerator.getModId() + " " + moduleType.name());
    }

    @Override
    protected void generateTags()
    {
        values.forEach((block, tagKey) -> tag(tagKey).add(block));
    }

    public void add(TagKey<Block> tagKey, Block block, ModuleType moduleType)
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
        return basePath.resolve(moduleType.name().toLowerCase() + "/src/generated/resources/data");
    }
}
