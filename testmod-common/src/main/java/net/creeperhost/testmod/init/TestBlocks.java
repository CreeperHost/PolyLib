package net.creeperhost.testmod.init;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.creeperhost.testmod.TestMod;
import net.creeperhost.testmod.blocks.inventorytestblock.InventoryTestBlock;
import net.creeperhost.testmod.blocks.inventorytestblock.InventoryTestBlockEntity;
import net.creeperhost.testmod.blocks.mguitestblock.MGuiTestBlock;
import net.creeperhost.testmod.blocks.mguitestblock.MGuiTestBlockEntity;
import net.creeperhost.testmod.blocks.multiblock.BlockTestMultiblockBlock;
import net.creeperhost.testmod.blocks.multiblock.TestMultiBlockBlockEntity;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class TestBlocks
{
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(TestMod.MOD_ID, Registry.BLOCK_REGISTRY);
    public static final DeferredRegister<BlockEntityType<?>> TILES_ENTITIES = DeferredRegister.create(TestMod.MOD_ID, Registry.BLOCK_ENTITY_TYPE_REGISTRY);

    public static final RegistrySupplier<Block> INVENTORY_TEST_BLOCK = BLOCKS.register("inventory_test_block", InventoryTestBlock::new);
    public static final RegistrySupplier<Block> MULTIBLOCK_TEST_BLOCK = BLOCKS.register("multiblock_test_block", BlockTestMultiblockBlock::new);
    public static final RegistrySupplier<Block> MGUI_TEST_BLOCK = BLOCKS.register("mgui_test_block", MGuiTestBlock::new);

    public static final RegistrySupplier<BlockEntityType<InventoryTestBlockEntity>> INVENTORY_TEST_TILE = TILES_ENTITIES.register("inventory_test_block",
            () -> BlockEntityType.Builder.of(InventoryTestBlockEntity::new, TestBlocks.INVENTORY_TEST_BLOCK.get()).build(null));

    public static final RegistrySupplier<BlockEntityType<TestMultiBlockBlockEntity>> MULTIBLOCK_TEST_TILE = TILES_ENTITIES.register("multiblock_test_block",
            () -> BlockEntityType.Builder.of(TestMultiBlockBlockEntity::new, TestBlocks.MULTIBLOCK_TEST_BLOCK.get()).build(null));

    public static final RegistrySupplier<BlockEntityType<MGuiTestBlockEntity>> MGUI_TEST_BLOCK_ENTITY = TILES_ENTITIES.register("mgui_test_block",
            () -> BlockEntityType.Builder.of(MGuiTestBlockEntity::new, TestBlocks.MGUI_TEST_BLOCK.get()).build(null));
}
