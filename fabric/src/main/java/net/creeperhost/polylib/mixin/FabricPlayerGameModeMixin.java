package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.data.CancelContext;
import net.creeperhost.polylib.event.events.server.PolyPlayerEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Bridges {@link PolyPlayerEvents#CHANGE_GAME_MODE} on Fabric.
 */
@Mixin(ServerPlayerGameMode.class)
public abstract class FabricPlayerGameModeMixin
{
    @Shadow
    protected ServerPlayer player;

    @Inject(method = "changeGameModeForPlayer", at = @At("HEAD"), cancellable = true)
    private void polylib$onChangeGameMode(GameType gameType, CallbackInfo ci)
    {
        GameType current = ((ServerPlayerGameMode) (Object) this).getGameModeForPlayer();
        CancelContext ctx = new CancelContext();
        PolyPlayerEvents.CHANGE_GAME_MODE.invoker().onChangeGameMode(player, current, gameType, ctx);
        if (ctx.isCancelled()) ci.cancel();
    }
}
