package net.creeperhost.testmod.init;

import net.creeperhost.polylib.registry.PolyRegistry;
import net.creeperhost.testmod.TestModCommon;
import net.creeperhost.testmod.blocks.creativepower.CreativePowerBlock;
import net.creeperhost.testmod.blocks.creativepower.CreativePowerBlockEntity;
import net.creeperhost.testmod.blocks.fluidtank.FluidTankBlock;
import net.creeperhost.testmod.blocks.fluidtank.FluidTankBlockEntity;
import net.creeperhost.testmod.blocks.inventorytestblock.BlockEntityInventoryTest;
import net.creeperhost.testmod.blocks.inventorytestblock.BlockInventoryTest;
import net.creeperhost.testmod.blocks.multiblock.BlockTestMultiblockBlock;
import net.creeperhost.testmod.blocks.multiblock.TestMultiBlockBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Set;
import java.util.function.Supplier;

public class TestBlocks {

    public static final PolyRegistry<Block> BLOCKS = PolyRegistry.create(Registries.BLOCK, TestModCommon.MOD_ID);
    public static final PolyRegistry<BlockEntityType<?>> BLOCK_ENTITIES = PolyRegistry.create(Registries.BLOCK_ENTITY_TYPE, TestModCommon.MOD_ID);

    public static final Supplier<Block> TEST_BLOCK = BLOCKS.registerBlock("test_block", "Test Block", BlockInventoryTest::new);
    public static final Supplier<BlockEntityType<BlockEntityInventoryTest>> TEST_BLOCK_ENTITY = BLOCK_ENTITIES.register("test_block_entity",
            () -> new BlockEntityType<>(BlockEntityInventoryTest::new, Set.of(TEST_BLOCK.get())));


    public static final Supplier<Block> CREATIVE_POWER_BLOCK = BLOCKS.registerBlock("creative_power", "Creative Power Block", CreativePowerBlock::new);
    public static final Supplier<BlockEntityType<CreativePowerBlockEntity>> CREATIVE_ENERGY_BLOCK_TILE = BLOCK_ENTITIES.register("creative_power_tile",
            () -> new BlockEntityType<>(CreativePowerBlockEntity::new, Set.of(CREATIVE_POWER_BLOCK.get())));

    public static final Supplier<Block> FLUID_TANK_BLOCK = BLOCKS.registerBlock("fluid_tank", "Fluid Tank", FluidTankBlock::new);
    public static final Supplier<BlockEntityType<FluidTankBlockEntity>> FLUID_TANK_BLOCK_ENTITY = BLOCK_ENTITIES.register("fluid_tank_block_entity",
            () -> new BlockEntityType<>(FluidTankBlockEntity::new, Set.of(FLUID_TANK_BLOCK.get())));

    public static final Supplier<Block> MULTIBLOCK_BLOCK = BLOCKS.registerBlock("multiblock_test_block", "Multiblock Block", BlockTestMultiblockBlock::new);
    public static final Supplier<BlockEntityType<TestMultiBlockBlockEntity>> MULTIBLOCK_TEST_TILE = BLOCK_ENTITIES.register("multiblock_tile",
            () -> new BlockEntityType<>(TestMultiBlockBlockEntity::new, Set.of(MULTIBLOCK_BLOCK.get())));

    public static void init() {
        BLOCKS.init();
        BLOCK_ENTITIES.init();
    }
}
