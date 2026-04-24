package net.creeperhost.testmod.init;

import net.creeperhost.polylib.registry.PolyRegistry;
import net.creeperhost.testmod.TestModCommon;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.function.Supplier;

public class TestCreativeTabs {

    public static final PolyRegistry<CreativeModeTab> TABS = PolyRegistry.create(Registries.CREATIVE_MODE_TAB, TestModCommon.MOD_ID);

    public static final Supplier<CreativeModeTab> TEST_TAB = TABS.registerCreativeTab(
            "test_tab",
            "Test Tab",
            () -> new ItemStack(Items.DIAMOND),
            (params, output) -> {
                output.accept(TestItems.TEST_ITEM.get());
                output.accept(TestItems.TEST_ITEM_2.get());
                output.accept(TestItems.TEST_BLOCK.get());
            }
    );

    public static void init() {
        TABS.init();
    }
}
