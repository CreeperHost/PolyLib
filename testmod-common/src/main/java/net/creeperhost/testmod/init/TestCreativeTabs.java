package net.creeperhost.testmod.init;

import net.creeperhost.polylib.platform.Services;
import net.creeperhost.polylib.register.creativetab.LazyCreativeTab;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class TestCreativeTabs {

    public static LazyCreativeTab TEST_TAB;

    public static void init() {
        TEST_TAB = Services.REGISTER_HELPER.registerCreativeTab(new LazyCreativeTab(
                Identifier.fromNamespaceAndPath("testmod", "test_tab"),
                Component.translatable("itemGroup.testmod.test_tab"),
                () -> new ItemStack(Items.DIAMOND),
                output -> {
                    output.accept(TestItems.TEST_ITEM.get());
                    output.accept(TestItems.TEST_ITEM_2.get());
                    output.accept(TestItems.TEST_BLOCK.get());
                }
        ));
    }
}
