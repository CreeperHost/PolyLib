package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.events.client.PolyClientConnectionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.network.Connection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * NeoForge bridge for {@link PolyClientConnectionEvents#CLIENT_PLAY_INIT}.
 * Fires when the {@code ClientPacketListener} (play connection) is fully constructed.
 */
@Mixin(ClientPacketListener.class)
public abstract class MixinClientPlayInitNF
{
    @Inject(method = "<init>", at = @At("TAIL"))
    private void polylib$onPlayInit(Minecraft client, Connection connection,
                                    CommonListenerCookie cookie, CallbackInfo ci)
    {
        PolyClientConnectionEvents.CLIENT_PLAY_INIT.invoker()
                .onPlayInit((ClientPacketListener) (Object) this, null, client);
    }
}
