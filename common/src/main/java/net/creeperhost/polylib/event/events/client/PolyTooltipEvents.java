package net.creeperhost.polylib.event.events.client;

import net.creeperhost.polylib.event.PolyEvent;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.List;

/**
 * Client-side tooltip events.
 */
public final class PolyTooltipEvents
{
    /**
     * Fired when an item tooltip is being built. The {@code lines} list is mutable.
     * <p>
     * NeoForge: {@code ItemTooltipEvent} (GAME bus, client only)<br>
     * Fabric: mixin on {@code Item#appendHoverText} or tooltip gathering
     */
    public static final PolyEvent<ItemTooltip> ITEM_TOOLTIP = PolyEvent.create(
            handlers -> (stack, player, lines, flags) ->
                    handlers.forEach(h -> h.onItemTooltip(stack, player, lines, flags)));

    /**
     * Fired to modify the attribute modifiers shown in an item's tooltip.
     * The {@code modifiers} list is mutable via the provided context.
     * <p>
     * NeoForge: {@code ItemAttributeModifierEvent} (GAME bus, client only)<br>
     * Fabric: mixin on {@code ItemAttributeModifiers#addToTooltip}
     */
    public static final PolyEvent<ItemAttributeModifiers> ITEM_ATTRIBUTE_MODIFIERS = PolyEvent.create(
            handlers -> (stack, modifiers) ->
                    handlers.forEach(h -> h.onItemAttributeModifiers(stack, modifiers)));

    private PolyTooltipEvents() {}

    @FunctionalInterface
    public interface ItemTooltip
    {
        /** {@code lines} is mutable — add, remove, or reorder tooltip lines. */
        void onItemTooltip(ItemStack stack, Player player,
                           List<Component> lines, TooltipFlag flags);
    }

    @FunctionalInterface
    public interface ItemAttributeModifiers
    {
        /**
         * {@code modifiers} is a mutable list of {@link ItemAttributeModifiers.Entry}.
         * Add or remove entries to change what is shown in the tooltip.
         */
        void onItemAttributeModifiers(ItemStack stack,
                                      List<net.minecraft.world.item.component.ItemAttributeModifiers.Entry> modifiers);
    }
}
