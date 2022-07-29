package net.creeperhost.testmod.init;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.creeperhost.testmod.TestMod;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class TestItems
{
    public static final CreativeModeTab CREATIVE_MODE_TAB = CreativeTabRegistry.create(new ResourceLocation(TestMod.MOD_ID, "_creativetab"), () -> new ItemStack(Items.DIAMOND));
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(TestMod.MOD_ID, Registry.ITEM_REGISTRY);

    public static final RegistrySupplier<Item> CATCHER_ITEM = ITEMS.register("test", () -> new Item(new Item.Properties().tab(CREATIVE_MODE_TAB)));

}
