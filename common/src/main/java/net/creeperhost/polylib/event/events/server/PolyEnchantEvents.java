package net.creeperhost.polylib.event.events.server;

import net.creeperhost.polylib.event.PolyEvent;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.List;

/**
 * Events related to enchanting mechanics.
 */
public final class PolyEnchantEvents
{
    /**
     * Fired to adjust the effective enchantment level of an item. Modify {@code enchantments}
     * (it is a {@code ItemEnchantments.Mutable}) to change results.
     * <p>
     * NeoForge: {@code GetEnchantmentLevelEvent} (GAME bus)<br>
     * Fabric: mixin on {@code EnchantmentHelper#getEnchantmentLevel}
     */
    public static final PolyEvent<GetEnchantLevel> GET_ENCHANT_LEVEL = PolyEvent.create(
            handlers -> (stack, targetKey, enchantments) ->
                    handlers.forEach(h -> h.onGetEnchantLevel(stack, targetKey, enchantments)));

    /**
     * Fired to adjust the level offered in an enchanting table slot. Modify {@code level[0]}.
     * <p>
     * NeoForge: {@code EnchantmentLevelSetEvent} (GAME bus)<br>
     * Fabric: mixin on {@code EnchantingTableBlockEntity#tick}
     */
    public static final PolyEvent<EnchantTableLevel> ENCHANT_TABLE_LEVEL = PolyEvent.create(
            handlers -> (stack, power, originalLevel, level) ->
                    handlers.forEach(h -> h.onEnchantTableLevel(stack, power, originalLevel, level)));

    private PolyEnchantEvents() {}

    @FunctionalInterface
    public interface GetEnchantLevel
    {
        /** {@code enchantments} is a mutable map — add/remove enchantments via it. */
        void onGetEnchantLevel(ItemStack stack, Holder<Enchantment> targetEnchant,
                               net.minecraft.world.item.enchantment.ItemEnchantments.Mutable enchantments);
    }

    @FunctionalInterface
    public interface EnchantTableLevel
    {
        /** {@code level[0]} — the offered enchantment level (mutable). */
        void onEnchantTableLevel(ItemStack stack, int power, int originalLevel, int[] level);
    }
}
