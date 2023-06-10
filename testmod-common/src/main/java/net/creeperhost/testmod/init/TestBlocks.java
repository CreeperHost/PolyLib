package net.creeperhost.testmod.init;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.creeperhost.testmod.TestMod;
import net.creeperhost.testmod.blocks.inventorytestblock.InventoryTestBlock;
import net.creeperhost.testmod.blocks.inventorytestblock.InventoryTestBlockEntity;
import net.creeperhost.testmod.blocks.machinetest.MachineTestBlock;
import net.creeperhost.testmod.blocks.machinetest.MachineTestBlockEntity;
import net.creeperhost.testmod.blocks.multiblock.BlockTestMultiblockBlock;
import net.creeperhost.testmod.blocks.multiblock.TestMultiBlockBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class TestBlocks
{
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(TestMod.MOD_ID, Registries.BLOCK);
    public static final DeferredRegister<BlockEntityType<?>> TILES_ENTITIES = DeferredRegister.create(TestMod.MOD_ID, Registries.BLOCK_ENTITY_TYPE);

    public static final RegistrySupplier<Block> INVENTORY_TEST_BLOCK = BLOCKS.register("inventory_test_block", InventoryTestBlock::new);
    public static final RegistrySupplier<Block> MULTIBLOCK_TEST_BLOCK = BLOCKS.register("multiblock_test_block", BlockTestMultiblockBlock::new);

    public static final RegistrySupplier<Block> MACHINE_TEST_BLOCK = BLOCKS.register("machine_test_block", MachineTestBlock::new);


    public static final RegistrySupplier<BlockEntityType<InventoryTestBlockEntity>> INVENTORY_TEST_TILE = TILES_ENTITIES.register("inventory_test_block",
            () -> BlockEntityType.Builder.of(InventoryTestBlockEntity::new, TestBlocks.INVENTORY_TEST_BLOCK.get()).build(null));

    public static final RegistrySupplier<BlockEntityType<TestMultiBlockBlockEntity>> MULTIBLOCK_TEST_TILE = TILES_ENTITIES.register("multiblock_test_block",
            () -> BlockEntityType.Builder.of(TestMultiBlockBlockEntity::new, TestBlocks.MULTIBLOCK_TEST_BLOCK.get()).build(null));

    public static final RegistrySupplier<BlockEntityType<MachineTestBlockEntity>> MACHINE_TEST_TILE = TILES_ENTITIES.register("machine_test_block",
            () -> BlockEntityType.Builder.of(MachineTestBlockEntity::new, TestBlocks.MACHINE_TEST_BLOCK.get()).build(null));
}
