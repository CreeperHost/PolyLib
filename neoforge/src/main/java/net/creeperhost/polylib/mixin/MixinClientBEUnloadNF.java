package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.events.client.PolyClientBlockEntityEvents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * NeoForge bridge for {@link PolyClientBlockEntityEvents#CLIENT_BLOCK_ENTITY_UNLOAD}.
 * Targets {@code Level#removeBlockEntity(BlockPos)} — fires before removal with a
 * {@code ClientLevel} instance guard so it only fires on the client side.
 */
@Mixin(Level.class)
public abstract class MixinClientBEUnloadNF
{
    @Inject(method = "removeBlockEntity", at = @At("HEAD"))
    private void polylib$onBEUnload(BlockPos pos, CallbackInfo ci)
    {
        Level self = (Level) (Object) this;
        if (!(self instanceof ClientLevel clientLevel)) return;
        BlockEntity be = clientLevel.getBlockEntity(pos);
        if (be != null)
        {
            PolyClientBlockEntityEvents.CLIENT_BLOCK_ENTITY_UNLOAD.invoker()
                    .onBlockEntityUnload(be, clientLevel);
        }
    }
}
