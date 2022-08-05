package net.creeperhost.polylib.generators;

import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.ModelProvider;

public abstract class PolyModelProvider extends ModelProvider
{
    protected final PolyDataGenerator dataGenerator;

    public PolyModelProvider(PolyDataGenerator dataGenerator) {
        super(dataGenerator);
        this.dataGenerator = dataGenerator;
    }

    public abstract void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator);

    public abstract void generateItemModels(ItemModelGenerators itemModelGenerator);

    public String getName() {
        return "Models";
    }
}
