package net.creeperhost.polylib.fabric.datagen.providers;

import net.creeperhost.polylib.PolyLib;
import net.creeperhost.polylib.fabric.datagen.ModuleType;
import net.creeperhost.polylib.fabric.datagen.PolyDataGen;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.CachedOutput;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class PolyAdvancementProvider extends FabricAdvancementProvider
{
    private final ModuleType moduleType;
    private List<Advancement> values = new ArrayList<>();

    public PolyAdvancementProvider(FabricDataGenerator dataGenerator, FabricDataOutput fabricDataOutput, ModuleType moduleType)
    {
        super(fabricDataOutput);
        this.moduleType = moduleType;

        PolyLib.LOGGER.info("PolyAdvancementProvider created for " + dataGenerator.getModId() + " " + moduleType.name());
    }

    @Override
    public void generateAdvancement(Consumer<Advancement> consumer)
    {
        values.forEach(advancement ->
        {
            PolyLib.LOGGER.info("Running data gen for advancement " + advancement.getId() + " " + output.getOutputFolder());
            consumer.accept(advancement);
        });
    }

    public void add(Advancement advancement, ModuleType moduleType)
    {
        if (this.moduleType == moduleType) values.add(advancement);
    }

    @Override
    public CompletableFuture<?> run(CachedOutput writer)
    {
//        if (values.isEmpty()) return;
//        dataGenerator.outputFolder = appendPath(moduleType);
        return super.run(writer);
    }

    public Path appendPath(ModuleType moduleType)
    {
        return PolyDataGen.getPathFromModuleType(moduleType).resolve("data");
    }
}
