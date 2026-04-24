package net.creeperhost.testmod.init;

import net.creeperhost.polylib.registry.PolyRegistry;
import net.creeperhost.testmod.TestModCommon;
import net.creeperhost.testmod.blocks.inventorytestblock.BlockEntityInventoryTest;
import net.creeperhost.testmod.blocks.inventorytestblock.BlockInventoryTest;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Set;
import java.util.function.Supplier;

import java.util.function.Supplier;

public class TestBlocks {

    public static final PolyRegistry<Block> BLOCKS = PolyRegistry.create(Registries.BLOCK, TestModCommon.MOD_ID);
    public static final PolyRegistry<BlockEntityType<?>> BLOCK_ENTITIES = PolyRegistry.create(Registries.BLOCK_ENTITY_TYPE, TestModCommon.MOD_ID);

    public static final Supplier<Block> TEST_BLOCK = BLOCKS.registerBlock("test_block", "Test Block", BlockInventoryTest::new);
    public static final Supplier<BlockEntityType<BlockEntityInventoryTest>> TEST_BLOCK_ENTITY = BLOCK_ENTITIES.register("test_block_entity",
            () -> new BlockEntityType<>(BlockEntityInventoryTest::new, Set.of(TEST_BLOCK.get())));

    public static void init() {
        BLOCKS.init();
        BLOCK_ENTITIES.init();
    }
}
