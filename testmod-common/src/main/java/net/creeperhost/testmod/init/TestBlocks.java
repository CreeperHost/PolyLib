package net.creeperhost.testmod.init;

import net.creeperhost.polylib.registry.PolyRegistry;
import net.creeperhost.testmod.TestModCommon;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public class TestBlocks {

    public static final PolyRegistry<Block> BLOCKS = PolyRegistry.create(Registries.BLOCK, TestModCommon.MOD_ID);

    public static final Supplier<Block> TEST_BLOCK = BLOCKS.registerBlock("test_block", "Test Block", Block::new);

    public static void init() {
        BLOCKS.init();
    }
}
