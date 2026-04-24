package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.events.client.PolyClientConnectionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.network.Connection;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * NeoForge bridge for:
 * <ul>
 *   <li>{@link PolyClientConnectionEvents#CLIENT_LOGIN_INIT} — constructor TAIL</li>
 *   <li>{@link PolyClientConnectionEvents#CLIENT_LOGIN_QUERY_START} — {@code handleCustomQuery} HEAD</li>
 *   <li>{@link PolyClientConnectionEvents#CLIENT_LOGIN_QUERY_RESPONSE} — {@code handleCustomQuery} RETURN</li>
 *   <li>{@link PolyClientConnectionEvents#CLIENT_LOGIN_DISCONNECT} — {@code onDisconnect} HEAD</li>
 * </ul>
 */
@Mixin(ClientHandshakePacketListenerImpl.class)
public abstract class MixinClientLoginNF
{
    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void polylib$onLoginInit(Connection connection, Minecraft client, Object serverData,
                                     Object screen, boolean transfer, Object duration,
                                     Object statusListener, Object levelTracker,
                                     Object transferState, CallbackInfo ci)
    {
        PolyClientConnectionEvents.CLIENT_LOGIN_INIT.invoker()
                .onLoginInit((ClientHandshakePacketListenerImpl) (Object) this, client);
    }

    @Inject(method = "handleCustomQuery", at = @At("HEAD"))
    private void polylib$onQueryStart(ClientboundCustomQueryPacket packet, CallbackInfo ci)
    {
        if (packet.payload() != null)
        {
            PolyClientConnectionEvents.CLIENT_LOGIN_QUERY_START.invoker()
                    .onQueryStart((ClientHandshakePacketListenerImpl) (Object) this, minecraft, null, packet.payload());
        }
    }

    @Inject(method = "handleCustomQuery", at = @At("RETURN"))
    private void polylib$onQueryResponse(ClientboundCustomQueryPacket packet, CallbackInfo ci)
    {
        if (packet.payload() != null)
        {
            PolyClientConnectionEvents.CLIENT_LOGIN_QUERY_RESPONSE.invoker()
                    .onQueryResponse((ClientHandshakePacketListenerImpl) (Object) this, minecraft, packet.payload());
        }
    }

    @Inject(method = "onDisconnect", at = @At("HEAD"))
    private void polylib$onLoginDisconnect(DisconnectionDetails details, CallbackInfo ci)
    {
        PolyClientConnectionEvents.CLIENT_LOGIN_DISCONNECT.invoker()
                .onLoginDisconnect((ClientHandshakePacketListenerImpl) (Object) this, minecraft);
    }
}
