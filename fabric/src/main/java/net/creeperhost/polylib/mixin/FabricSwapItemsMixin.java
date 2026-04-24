package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.CancelContext;
import net.creeperhost.polylib.event.events.server.PolyLivingEvents;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric bridge for {@link PolyLivingEvents#SWAP_ITEMS}.
 * Fires just before a player's main/off-hand items are swapped (F key).
 * Injected before the first {@code setItemInHand} call in
 * {@code handlePlayerAction}'s SWAP_ITEM_WITH_OFFHAND branch.
 */
@Mixin(ServerGamePacketListenerImpl.class)
public abstract class FabricSwapItemsMixin
{
    @Shadow public ServerPlayer player;

    @Inject(
        method = "handlePlayerAction",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerPlayer;setItemInHand(Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/item/ItemStack;)V",
            ordinal = 0
        ),
        cancellable = true
    )
    private void polylib$onSwapItems(ServerboundPlayerActionPacket packet, CallbackInfo ci)
    {
        ItemStack mainHand = this.player.getItemInHand(InteractionHand.MAIN_HAND);
        ItemStack offHand  = this.player.getItemInHand(InteractionHand.OFF_HAND);
        CancelContext ctx  = new CancelContext();
        // offHand → toMainHand, mainHand → toOffHand (mirrors NeoForge LivingSwapItemsEvent.Hands)
        PolyLivingEvents.SWAP_ITEMS.invoker().onSwapItems(this.player, offHand, mainHand, ctx);
        if (ctx.isCancelled()) ci.cancel();
    }
}
