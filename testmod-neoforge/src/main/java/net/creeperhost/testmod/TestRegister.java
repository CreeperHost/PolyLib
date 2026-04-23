package net.creeperhost.testmod;

import net.creeperhost.polylib.platform.Services;
import net.creeperhost.testmod.init.TestItems;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class TestRegister
{
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("testmod");
//
//    public static final DeferredItem<Item> TEST_ITEM = ITEMS.registerItem(Services.REGISTER_HELPER.registerItem(TestItems.TEST_ITEM));

    //TODO move this into a service maybe
//    private static DeferredItem<Item> register(LazyItem<Item> lazyItem)
//    {
//        return ITEMS.registerItem(lazyItem.getRegistryName().getPath(), lazyItem.getFunc(), lazyItem.getProperties());
//    }

    public static void init(){}
}
