package net.creeperhost.polylib.testmod;

import net.creeperhost.polylib.platform.Services;

import net.creeperhost.polylib.registry.PolyRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;

public class TestModCommon
{
    public static final PolyRegistry<Block> BLOCKS = PolyRegistry.create(Registries.BLOCK, "polylib");
    public static final Supplier<Block> TEST_BLOCK = BLOCKS.register("test_block", () -> new Block(BlockBehaviour.Properties.of()));
    public static void init()
    {
        BLOCKS.init();

        if (Services.PLATFORM.isClient()) {
            TestModClientCommon.init();
        }
    }
}
