package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.events.server.PolyServerLifecycleEvents;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * NeoForge mixin bridge for {@link PolyServerLifecycleEvents#LOGIN_QUERY_START}.
 * Fires when the server begins the login query phase.
 */
@Mixin(ServerLoginPacketListenerImpl.class)
public abstract class MixinServerLoginQueryStartNF
{
    @Inject(method = "handleHello", at = @At("HEAD"))
    private void polylib$onLoginQueryStart(
            net.minecraft.network.protocol.login.ServerboundHelloPacket packet, CallbackInfo ci)
    {
        ServerLoginPacketListenerImpl self = (ServerLoginPacketListenerImpl) (Object) this;
        PolyServerLifecycleEvents.LOGIN_QUERY_START.invoker()
                .onLoginQueryStart(self, ServerLifecycleHooks.getCurrentServer(), null, null);
    }
}
