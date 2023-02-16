package net.creeperhost.polylib.fabric.datagen.providers;

import com.google.common.collect.Maps;
import net.creeperhost.polylib.PolyLib;
import net.creeperhost.polylib.fabric.datagen.ModuleType;
import net.creeperhost.polylib.fabric.datagen.PolyDataGen;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.data.CachedOutput;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public class PolyBlockLootProvider extends FabricBlockLootTableProvider
{
    private final ModuleType moduleType;
    private final Map<Block, LootTable.Builder> values = Maps.newHashMap();

    public PolyBlockLootProvider(FabricDataGenerator dataGenerator, ModuleType moduleType)
    {
        super(dataGenerator);
        this.moduleType = moduleType;

        PolyLib.LOGGER.info("PolyBlockLootProvider created for " + dataGenerator.getModId() + " " + moduleType.name());
    }

    @Override
    protected void generateBlockLootTables()
    {
        values.forEach((block, builder) ->
        {
            PolyLib.LOGGER.info("Running data gen for block loot table " + block.getDescriptionId() + " " + dataGenerator.getOutputFolder());
            add(block, builder);
        });
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
    public void run(CachedOutput writer) throws IOException
    {
        //If values is empty don't generate an empty array json file
        if (values.isEmpty()) return;
        dataGenerator.outputFolder = appendPath(moduleType);
        super.run(writer);
    }

    public Path appendPath(ModuleType moduleType)
    {
        return PolyDataGen.getPathFromModuleType(moduleType);
    }
}
