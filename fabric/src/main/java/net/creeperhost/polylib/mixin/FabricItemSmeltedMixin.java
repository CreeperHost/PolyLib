package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.events.server.PolyPlayerEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric bridge for {@link PolyPlayerEvents#ITEM_SMELTED}.
 * Fires when a player takes a smelted item from a furnace.
 */
@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class FabricItemSmeltedMixin
{
    @Inject(method = "awardUsedRecipesAndPopExperience", at = @At("HEAD"))
    private void polylib$onAwardUsedRecipes(ServerPlayer player, CallbackInfo ci)
    {
        AbstractFurnaceBlockEntity self = (AbstractFurnaceBlockEntity)(Object) this;
        // Get the result slot item
        ItemStack result = self.getItem(2); // slot 2 = output
        if (!result.isEmpty())
        {
            PolyPlayerEvents.ITEM_SMELTED.invoker().onItemSmelted(player, result.copy());
        }
    }
}
