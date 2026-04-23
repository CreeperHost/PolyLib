package net.creeperhost.testmod.init;

import net.creeperhost.polylib.registry.PolyRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;

public class TestBlocks {

    public static final PolyRegistry<Block> BLOCKS = PolyRegistry.create(Registries.BLOCK, "testmod");

    public static final Supplier<Block> TEST_BLOCK = BLOCKS.register("test_block", "Test Block", () -> new Block(BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("testmod", "test_block")))));

    public static void init() {
        BLOCKS.init();
    }
}
