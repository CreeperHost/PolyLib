package net.creeperhost.polylib.fabric.datagenerators;

import net.creeperhost.polylib.generators.PolyLibDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;

@Deprecated(forRemoval = true)
public class BlockTagGenerator extends FabricTagProvider<Block>
{
    public BlockTagGenerator(FabricDataGenerator dataGenerator)
    {
        super(dataGenerator, Registry.BLOCK);
    }

    @Override
    protected void generateTags()
    {
        PolyLibDataGenerator.BLOCK_TAGS.forEach((tagKey, block) -> getOrCreateTagBuilder(tagKey).add(block));
    }
}
