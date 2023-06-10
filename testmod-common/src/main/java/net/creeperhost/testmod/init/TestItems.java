package net.creeperhost.testmod.init;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.creeperhost.testmod.TestMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;

public class TestItems
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(TestMod.MOD_ID, Registries.ITEM);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(TestMod.MOD_ID, Registries.CREATIVE_MODE_TAB);

    public static final RegistrySupplier<CreativeModeTab> CREATIVE_TAB = CREATIVE_MODE_TABS.register("test", () -> CreativeTabRegistry.create(Component.literal("testmod"), () -> new ItemStack(Items.DIAMOND)));

    public static final RegistrySupplier<Item> INVENTORY_TEST_ITEMBLOCK = ITEMS.register("inventory_test_block", () -> new BlockItem(TestBlocks.INVENTORY_TEST_BLOCK.get(), new Item.Properties()));
    public static final RegistrySupplier<Item> MULTIBLOCK_TEST_ITEMBLOCK = ITEMS.register("multiblock_test_block", () -> new BlockItem(TestBlocks.MULTIBLOCK_TEST_BLOCK.get(), new Item.Properties()));

    public static final RegistrySupplier<Item> MACHINE_TEST_ITEMBLOCK = ITEMS.register("machine_test_block", () -> new BlockItem(TestBlocks.MACHINE_TEST_BLOCK.get(), new Item.Properties()));


    static
    {
        CreativeTabRegistry.append(CREATIVE_TAB, INVENTORY_TEST_ITEMBLOCK, MULTIBLOCK_TEST_ITEMBLOCK, MACHINE_TEST_ITEMBLOCK);
    }
}
