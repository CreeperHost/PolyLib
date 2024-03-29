package net.creeperhost.polylib.fabric.datagen.providers;

import net.creeperhost.polylib.PolyLib;
import net.creeperhost.polylib.fabric.datagen.ModuleType;
import net.creeperhost.polylib.fabric.datagen.PolyDataGen;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.data.models.BlockModelGenerators.createSimpleBlock;

public class PolyModelProvider extends FabricModelProvider
{
    private final ModuleType moduleType;
    private Map<Item, ModelTemplate> itemValues = new HashMap<>();
    private Map<Block, MultiVariantGenerator> blockValues = new HashMap<>();
    private FabricDataOutput dataOutput;


    public PolyModelProvider(FabricDataOutput dataOutput, ModuleType moduleType)
    {
        super(dataOutput);
        this.moduleType = moduleType;
        this.dataOutput = dataOutput;
        updatePaths();
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator)
    {
        blockValues.forEach((block, blockStateGenerator) ->
        {
            PolyLib.LOGGER.info(block.getDescriptionId() + " " + dataOutput.getOutputFolder());
            blockStateModelGenerator.createTrivialCube(block);
        });
    }

    public void addBlockModel(Block block, MultiVariantGenerator blockStateGenerator, ModuleType moduleType)
    {
        if (this.moduleType == moduleType) blockValues.put(block, blockStateGenerator);
    }

    public void addSimpleBlockModel(Block block, ResourceLocation textureLocation, ModuleType moduleType)
    {
        MultiVariantGenerator multiVariantGenerator = createSimpleBlock(block, textureLocation);
        addBlockModel(block, multiVariantGenerator, moduleType);
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator)
    {
        itemValues.forEach((item, modelTemplate) ->
        {
            itemModelGenerator.generateFlatItem(item, ModelTemplates.FLAT_ITEM);
        });
    }

    public void addItemModel(Item item, ModelTemplate modelTemplate, ModuleType moduleType)
    {
        if (this.moduleType == moduleType)
        {
            PolyLib.LOGGER.info("Adding item model for " + item.getDescriptionId() + " " + dataOutput.getOutputFolder());
            itemValues.put(item, modelTemplate);
        }
    }

    public void addSimpleItemModel(Item item, ModuleType moduleType)
    {
        addItemModel(item, ModelTemplates.FLAT_ITEM, moduleType);
    }

    public void addSimpleToolModel(Item item, ModuleType moduleType)
    {
        addItemModel(item, ModelTemplates.FLAT_HANDHELD_ITEM, moduleType);
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cachedOutput)
    {
        updatePaths();
        return super.run(cachedOutput);
    }

    public void updatePaths()
    {
        dataOutput.outputFolder = appendPath(moduleType);
        blockStatePathProvider.root = appendPath(moduleType);
        modelPathProvider.root = appendPath(moduleType);
    }

    public Path appendPath(ModuleType moduleType)
    {
        return PolyDataGen.getPathFromModuleType(moduleType).resolve("assets");
    }
}
