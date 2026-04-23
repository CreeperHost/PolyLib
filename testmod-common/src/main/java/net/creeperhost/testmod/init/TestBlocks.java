package net.creeperhost.testmod.init;

import net.creeperhost.polylib.platform.Services;
import net.creeperhost.polylib.register.LazyBlock;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class TestBlocks {

    public static LazyBlock<Block> TEST_BLOCK = Services.REGISTER_HELPER.registerBlock(
            new LazyBlock<>(Identifier.fromNamespaceAndPath("testmod", "test_block"), Block::new, BlockBehaviour.Properties::of));

    public static void init() {}
}
