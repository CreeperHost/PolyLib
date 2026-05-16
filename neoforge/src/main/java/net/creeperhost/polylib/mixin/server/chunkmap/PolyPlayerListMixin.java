package net.creeperhost.polylib.mixin.server.chunkmap;

import com.llamalad7.mixinextras.sugar.Local;
import net.creeperhost.polylib.chunkmap.server.PolyChunkMapServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * PlayerList mixin: notifies {@link PolyChunkMapServer} when a player's OP status
 * changes so the appropriate Hello/Bye payload is sent.
 */
@Mixin(PlayerList.class)
public class PolyPlayerListMixin
{
    @Inject(
            method = "op(Lnet/minecraft/server/players/NameAndId;Ljava/util/Optional;Ljava/util/Optional;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/players/PlayerList;sendPlayerPermissionLevel(Lnet/minecraft/server/level/ServerPlayer;)V"
            )
    )
    private void polylib$onOpPlayer(CallbackInfo ci,
                                     @Local(name = "player") ServerPlayer player)
    {
        if (!net.creeperhost.polylib.PolyFeatures.isChunkMapEnabled()) return;
        PolyChunkMapServer server = PolyChunkMapServer.getInstance();
        if (server != null) server.onOpPlayer(player);
    }

    @Inject(
            method = "deop",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/players/PlayerList;sendPlayerPermissionLevel(Lnet/minecraft/server/level/ServerPlayer;)V"
            )
    )
    private void polylib$onDeOpPlayer(CallbackInfo ci,
                                       @Local(name = "player") ServerPlayer player)
    {
        if (!net.creeperhost.polylib.PolyFeatures.isChunkMapEnabled()) return;
        PolyChunkMapServer server = PolyChunkMapServer.getInstance();
        if (server != null) server.onDeOpPlayer(player);
    }
}
