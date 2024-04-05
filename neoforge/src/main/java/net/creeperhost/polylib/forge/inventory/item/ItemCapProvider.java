package net.creeperhost.polylib.forge.inventory.item;

import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.neoforged.neoforge.common.capabilities.Capabilities;
import net.neoforged.neoforge.common.capabilities.Capability;
import net.neoforged.neoforge.common.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.common.util.LazyOptional;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * Created by brandon3055 on 17/02/2024
 */
public class ItemCapProvider implements ICapabilityProvider {
    private final Function<Direction, Container> inventoryBlock;

    public ItemCapProvider(Function<Direction, Container> inventoryBlock) {
        this.inventoryBlock = inventoryBlock;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg) {
        if (capability == Capabilities.ITEM_HANDLER) {
            if (inventoryBlock.apply(arg) == null) return LazyOptional.empty();
            return LazyOptional.of(() -> {
                Container container = inventoryBlock.apply(arg);
                return container instanceof WorldlyContainer sided ? new SidedInvWrapper(sided, arg) : new InvWrapper(container);
            }).cast();
        }
        return LazyOptional.empty();
    }
}
