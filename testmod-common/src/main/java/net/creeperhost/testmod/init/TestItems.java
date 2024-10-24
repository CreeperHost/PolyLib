package net.creeperhost.testmod.init;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.creeperhost.polylib.inventory.power.IPolyEnergyStorage;
import net.creeperhost.testmod.TestMod;
import net.creeperhost.testmod.item.TestEnergyItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;

public class TestItems
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(TestMod.MOD_ID, Registries.ITEM);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(TestMod.MOD_ID, Registries.CREATIVE_MODE_TAB);

    public static final RegistrySupplier<CreativeModeTab> CREATIVE_TAB = CREATIVE_MODE_TABS.register("test", () -> CreativeTabRegistry.create(Component.literal("testmod"), () -> new ItemStack(TestBlocks.CREATIVE_ENERGY_BLOCK.get())));


    static ResourceKey<Item> modKey (String name)
    {
        return ResourceKey.create(Registries.ITEM, modLoc(name));
    }

    static ResourceLocation modLoc (String name)
    {
        return ResourceLocation.fromNamespaceAndPath(TestMod.MOD_ID, name);
    }

    public static final RegistrySupplier<Item> INVENTORY_TEST_ITEMBLOCK = ITEMS.register("inventory_test_block", () -> new BlockItem(TestBlocks.INVENTORY_TEST_BLOCK.get(), new Item.Properties().setId(modKey("inventory_test_block"))));
    public static final RegistrySupplier<Item> MULTIBLOCK_TEST_ITEMBLOCK = ITEMS.register("multiblock_test_block", () -> new BlockItem(TestBlocks.MULTIBLOCK_TEST_BLOCK.get(), new Item.Properties().setId(modKey("multiblock_test_block"))));
    public static final RegistrySupplier<Item> MACHINE_TEST_ITEMBLOCK = ITEMS.register("machine_test_block", () -> new BlockItem(TestBlocks.MACHINE_TEST_BLOCK.get(), new Item.Properties().setId(modKey("machine_test_block"))));
    public static final RegistrySupplier<Item> MGUI_TEST_ITEMBLOCK = ITEMS.register("mgui_test_block", () -> new BlockItem(TestBlocks.MGUI_TEST_BLOCK.get(), new Item.Properties().setId(modKey("mgui_test_block"))));


    public static final RegistrySupplier<Item> CREATIVE_ENERGY_BLOCK = ITEMS.register("creative_energy_block", () -> new BlockItem(TestBlocks.CREATIVE_ENERGY_BLOCK.get(), new Item.Properties().setId(modKey("creative_energy_block"))));

    public static final RegistrySupplier<Item> TEST_ENERGY_ITEM = ITEMS.register("test_energy_item", () -> new TestEnergyItem(new Item.Properties().setId(modKey("test_energy_item"))));


    static
    {
        CreativeTabRegistry.append(CREATIVE_TAB, INVENTORY_TEST_ITEMBLOCK, MULTIBLOCK_TEST_ITEMBLOCK, MACHINE_TEST_ITEMBLOCK, MGUI_TEST_ITEMBLOCK, CREATIVE_ENERGY_BLOCK, TEST_ENERGY_ITEM);
    }

    public static void addCustomStacksToTab() {
        CreativeTabRegistry.appendBuiltinStack(CREATIVE_TAB.get(), () -> {
            ItemStack chargedItem = new ItemStack(TEST_ENERGY_ITEM.get());
            IPolyEnergyStorage energy = ((TestEnergyItem)chargedItem.getItem()).getEnergyStorage(chargedItem);
            energy.modifyEnergyStored(energy.getMaxEnergyStored());
            return chargedItem;
        });
    }
}
