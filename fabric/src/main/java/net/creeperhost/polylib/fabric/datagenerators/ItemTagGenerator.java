package net.creeperhost.polylib.fabric.datagenerators;

import net.creeperhost.polylib.generators.PolyLibDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ItemTagGenerator extends FabricTagProvider<Item>
{
    public ItemTagGenerator(FabricDataGenerator dataGenerator)
    {
        super(dataGenerator, Registry.ITEM);
    }

    @Override
    protected void generateTags()
    {
        PolyLibDataGenerator.ITEM_TAGS.forEach((tagKey, block) -> getOrCreateTagBuilder(tagKey).add(block));
    }
}
