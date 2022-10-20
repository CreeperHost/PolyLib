package net.creeperhost.testmod.init;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.creeperhost.polylib.holders.LevelHolder;
import net.creeperhost.polylib.holders.PlayerHolder;
import net.creeperhost.polylib.helpers.ComponentHelper;
import net.creeperhost.polylib.items.PolyItem;
import net.creeperhost.polylib.registry.CreativeTabRegistry;
import net.creeperhost.testmod.TestMod;
import net.minecraft.core.Registry;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.*;

public class TestItems
{
    public static final CreativeModeTab CREATIVE_MODE_TAB = CreativeTabRegistry.of(TestMod.MOD_ID, "_creativetab", () -> new ItemStack(Items.DIAMOND));
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(TestMod.MOD_ID, Registry.ITEM_REGISTRY);

    public static final RegistrySupplier<PolyItem> DEBUG_STICK_REGISTRY = ITEMS.register("debug_stick", () -> new PolyItem(new PolyItem.PolyItemProperties().tab(CREATIVE_MODE_TAB))
    {
        @Override
        public InteractionResultHolder<ItemStack> useItem(LevelHolder levelHolder, PlayerHolder playerHolder, InteractionHand interactionHand)
        {
            if(levelHolder.getLevel().isClientSide)
                playerHolder.getPlayer().displayClientMessage(ComponentHelper.literal("TEST"), false);
            return super.useItem(levelHolder, playerHolder, interactionHand);
        }
    });

    public static final RegistrySupplier<Item> INVENTORY_TEST_ITEMBLOCK = ITEMS.register("inventory_test_block", () -> new BlockItem(TestBlocks.INVENTORY_TEST_BLOCK.get(), new PolyItem.PolyItemProperties().tab(CREATIVE_MODE_TAB)));
    public static final RegistrySupplier<Item> MULTIBLOCK_TEST_ITEMBLOCK = ITEMS.register("multiblock_test_block", () -> new BlockItem(TestBlocks.MULTIBLOCK_TEST_BLOCK.get(), new PolyItem.PolyItemProperties().tab(CREATIVE_MODE_TAB)));

}
