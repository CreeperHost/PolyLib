package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.events.client.PolyClientBlockEntityEvents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * NeoForge bridge for {@link PolyClientBlockEntityEvents#CLIENT_BLOCK_ENTITY_LOAD}.
 * Fires after {@code ClientLevel#onBlockEntityAdded(BlockEntity)}.
 */
@Mixin(ClientLevel.class)
public abstract class MixinClientBELoadNF
{
    @Inject(method = "onBlockEntityAdded", at = @At("RETURN"))
    private void polylib$onBELoad(BlockEntity be, CallbackInfo ci)
    {
        if (be != null)
        {
            PolyClientBlockEntityEvents.CLIENT_BLOCK_ENTITY_LOAD.invoker()
                    .onBlockEntityLoad(be, (ClientLevel) (Object) this);
        }
    }
}
