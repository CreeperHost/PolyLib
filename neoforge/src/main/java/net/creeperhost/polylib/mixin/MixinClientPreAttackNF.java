package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.CancelContext;
import net.creeperhost.polylib.event.events.client.PolyClientInteractionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * NeoForge bridge for {@link PolyClientInteractionEvents#CLIENT_PRE_ATTACK}.
 * Fires before {@code MultiPlayerGameMode#attack(Player, Entity)}.
 */
@Mixin(MultiPlayerGameMode.class)
public abstract class MixinClientPreAttackNF
{
    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void polylib$onPreAttack(Player player, Entity target, CallbackInfo ci)
    {
        Minecraft mc = Minecraft.getInstance();
        if (!(player instanceof net.minecraft.client.player.LocalPlayer localPlayer)) return;
        CancelContext ctx = new CancelContext();
        PolyClientInteractionEvents.CLIENT_PRE_ATTACK.invoker()
                .onPreAttack(mc, localPlayer, 0, ctx);
        if (ctx.isCancelled()) ci.cancel();
    }
}
