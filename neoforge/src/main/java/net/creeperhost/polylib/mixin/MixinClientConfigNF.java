package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.events.client.PolyClientConnectionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientConfigurationPacketListenerImpl;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.network.Connection;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.protocol.configuration.ClientboundFinishConfigurationPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * NeoForge bridge for:
 * <ul>
 *   <li>{@link PolyClientConnectionEvents#CLIENT_CONFIGURATION_INIT} — constructor TAIL</li>
 *   <li>{@link PolyClientConnectionEvents#CLIENT_CONFIGURATION_COMPLETE} — {@code handleConfigurationFinished} HEAD</li>
 *   <li>{@link PolyClientConnectionEvents#CLIENT_CONFIGURATION_DISCONNECT} — {@code onDisconnect} HEAD</li>
 * </ul>
 */
@Mixin(ClientConfigurationPacketListenerImpl.class)
public abstract class MixinClientConfigNF
{
    @Inject(method = "<init>", at = @At("TAIL"))
    private void polylib$onConfigInit(Minecraft client, Connection connection,
                                      CommonListenerCookie cookie, CallbackInfo ci)
    {
        PolyClientConnectionEvents.CLIENT_CONFIGURATION_INIT.invoker()
                .onConfigurationInit((ClientConfigurationPacketListenerImpl) (Object) this, client);
    }

    @Inject(method = "handleConfigurationFinished", at = @At("HEAD"))
    private void polylib$onConfigComplete(ClientboundFinishConfigurationPacket packet, CallbackInfo ci)
    {
        Minecraft mc = Minecraft.getInstance();
        PolyClientConnectionEvents.CLIENT_CONFIGURATION_COMPLETE.invoker()
                .onConfigurationComplete((ClientConfigurationPacketListenerImpl) (Object) this, mc);
    }

    @Inject(method = "onDisconnect", at = @At("HEAD"))
    private void polylib$onConfigDisconnect(DisconnectionDetails details, CallbackInfo ci)
    {
        Minecraft mc = Minecraft.getInstance();
        PolyClientConnectionEvents.CLIENT_CONFIGURATION_DISCONNECT.invoker()
                .onConfigurationDisconnect((ClientConfigurationPacketListenerImpl) (Object) this, mc);
    }
}
