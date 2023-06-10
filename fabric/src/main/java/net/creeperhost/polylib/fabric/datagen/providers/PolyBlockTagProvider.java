package net.creeperhost.polylib.fabric.datagen.providers;

import com.google.common.collect.Maps;
import net.creeperhost.polylib.PolyLib;
import net.creeperhost.polylib.fabric.datagen.ModuleType;
import net.creeperhost.polylib.fabric.datagen.PolyDataGen;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class PolyBlockTagProvider extends FabricTagProvider.BlockTagProvider
{
    private final ModuleType moduleType;
    private final Map<Block, TagKey<Block>> values = Maps.newLinkedHashMap();

    public PolyBlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture, ModuleType moduleType)
    {
        super(output, registriesFuture);
        this.moduleType = moduleType;
    }

    public void add(TagKey<Block> tagKey, Block block, ModuleType moduleType)
    {
        if (this.moduleType == moduleType)
        {
            values.put(block, tagKey);
        }
    }

    @Override
    public CompletableFuture<?> run(@NotNull CachedOutput cachedOutput)
    {
        //If values is empty don't generate an empty array json file
        if (values.isEmpty()) return null;
        pathProvider.root = appendPath(moduleType);
        return super.run(cachedOutput);
    }

    public Path appendPath(ModuleType moduleType)
    {
        return PolyDataGen.getPathFromModuleType(moduleType).resolve("data");
    }

    @Override
    protected void addTags(HolderLookup.Provider arg)
    {
        values.forEach((block, tagKey) -> tag(tagKey).add(reverseLookup(block)));
    }
}
