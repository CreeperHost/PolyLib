package net.creeperhost.polylib.fabric.datagen.providers;

import com.google.common.collect.Maps;
import net.creeperhost.polylib.PolyLib;
import net.creeperhost.polylib.fabric.datagen.ModuleType;
import net.creeperhost.polylib.fabric.datagen.PolyDataGen;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.data.CachedOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class PolyBlockLootProvider extends FabricBlockLootTableProvider
{
    private final ModuleType moduleType;
    private final Map<Block, LootTable.Builder> values = Maps.newHashMap();

    public PolyBlockLootProvider(FabricDataGenerator dataGenerator, FabricDataOutput dataOutput, ModuleType moduleType)
    {
        super(dataOutput);
        this.moduleType = moduleType;

        PolyLib.LOGGER.info("PolyBlockLootProvider created for " + dataGenerator.getModId() + " " + moduleType.name());
    }


    public void addDropOther(Block block, ItemLike itemLike, ModuleType moduleType)
    {
        if (this.moduleType == moduleType) this.values.put(block, createSingleItemTable(itemLike));
    }

    public void addSelfDrop(Block block, ModuleType moduleType)
    {
        addDropOther(block, block, moduleType);
    }

    @Override
    public CompletableFuture<?> run(CachedOutput writer)
    {
        //TODO
        //If values is empty don't generate an empty array json file
//        if (values.isEmpty()) return;
//        dataGenerator.outputFolder = appendPath(moduleType);
        return super.run(writer);
    }

    public Path appendPath(ModuleType moduleType)
    {
        return PolyDataGen.getPathFromModuleType(moduleType);
    }

    @Override
    public void generate()
    {
        values.forEach((block, builder) ->
        {
            PolyLib.LOGGER.info("Running data gen for block loot table " + block.getDescriptionId() + " " + getFabricDataOutput().getOutputFolder());
            add(block, builder);
        });
    }

    @Override
    public void accept(BiConsumer<ResourceLocation, LootTable.Builder> resourceLocationBuilderBiConsumer)
    {

    }
}
