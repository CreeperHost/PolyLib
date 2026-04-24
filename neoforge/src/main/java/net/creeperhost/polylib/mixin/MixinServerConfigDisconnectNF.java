package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.events.server.PolyServerLifecycleEvents;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * NeoForge mixin bridge for {@link PolyServerLifecycleEvents#CONFIGURATION_DISCONNECT}.
 * Fires when a client disconnects during the server configuration phase.
 */
@Mixin(ServerConfigurationPacketListenerImpl.class)
public abstract class MixinServerConfigDisconnectNF
{
    @Inject(method = "onDisconnect", at = @At("HEAD"))
    private void polylib$onConfigurationDisconnect(DisconnectionDetails details, CallbackInfo ci)
    {
        ServerConfigurationPacketListenerImpl self = (ServerConfigurationPacketListenerImpl) (Object) this;
        PolyServerLifecycleEvents.CONFIGURATION_DISCONNECT.invoker()
                .onConfigurationDisconnect(self, ServerLifecycleHooks.getCurrentServer());
    }
}
