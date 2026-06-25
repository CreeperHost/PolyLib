package net.creeperhost.polylib.inventory.fluid;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.util.Objects;

/**
 * Loader-neutral fluid stack used by PolyLib's common fluid API.
 * <p>
 * Amounts are stored as droplets, matching Fabric Transfer's fluid unit. Use
 * {@link FluidManager#BUCKET} and {@link FluidManager#MILLIBUCKET} when converting
 * to bucket or millibucket-oriented APIs.
 */
public class PolyFluidStack {
    /**
     * Shared empty stack instance.
     */
    public static final PolyFluidStack EMPTY = new PolyFluidStack(Fluids.EMPTY, 0);

    /**
     * Persistent codec for storing fluid, amount, and data component patch.
     */
    public static final Codec<PolyFluidStack> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.FLUID.holderByNameCodec().fieldOf("fluid").forGetter(stack -> stack.getFluid().builtInRegistryHolder()),
            Codec.LONG.fieldOf("amount").forGetter(PolyFluidStack::getAmount),
            DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter(PolyFluidStack::getComponents)
    ).apply(instance, (Holder<Fluid> fluid, Long amount, DataComponentPatch components) -> new PolyFluidStack(fluid.value(), amount, components)));

    /**
     * Network codec for synchronizing fluid stack contents.
     */
    public static final StreamCodec<RegistryFriendlyByteBuf, PolyFluidStack> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public PolyFluidStack decode(RegistryFriendlyByteBuf buf) {
            long amount = buf.readVarLong();
            if (amount <= 0) {
                return EMPTY;
            }
            Fluid fluid = ByteBufCodecs.holderRegistry(net.minecraft.core.registries.Registries.FLUID).decode(buf).value();
            DataComponentPatch components = DataComponentPatch.STREAM_CODEC.decode(buf);
            return new PolyFluidStack(fluid, amount, components);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, PolyFluidStack stack) {
            if (stack.isEmpty()) {
                buf.writeVarLong(0);
                return;
            }
            buf.writeVarLong(stack.getAmount());
            ByteBufCodecs.holderRegistry(net.minecraft.core.registries.Registries.FLUID).encode(buf, stack.getFluid().builtInRegistryHolder());
            DataComponentPatch.STREAM_CODEC.encode(buf, stack.getComponents());
        }
    };

    private final Fluid fluid;
    private final long amount;
    private final DataComponentPatch components;

    /**
     * Creates a fluid stack with no data components.
     *
     * @param fluid the fluid type
     * @param amount the amount in droplets
     */
    public PolyFluidStack(Fluid fluid, long amount) {
        this(fluid, amount, DataComponentPatch.EMPTY);
    }

    /**
     * Creates a fluid stack with optional component data.
     *
     * @param fluid the fluid type
     * @param amount the amount in droplets
     * @param components component patch carried by the fluid
     */
    public PolyFluidStack(Fluid fluid, long amount, DataComponentPatch components) {
        this.fluid = amount <= 0 ? Fluids.EMPTY : fluid;
        this.amount = Math.max(0, amount);
        this.components = components;
    }

    /**
     * @return the stored fluid type, or {@link Fluids#EMPTY}
     */
    public Fluid getFluid() {
        return fluid;
    }

    /**
     * @return the amount stored in droplets
     */
    public long getAmount() {
        return amount;
    }

    /**
     * @return the component patch attached to this fluid stack
     */
    public DataComponentPatch getComponents() {
        return components;
    }

    /**
     * @return true when this stack has no meaningful fluid content
     */
    public boolean isEmpty() {
        return amount <= 0 || fluid == Fluids.EMPTY;
    }

    /**
     * @return a copy of this stack, or {@link #EMPTY} if empty
     */
    public PolyFluidStack copy() {
        return isEmpty() ? EMPTY : new PolyFluidStack(fluid, amount, components);
    }

    /**
     * Creates a copy with the same fluid and components but a new amount.
     *
     * @param amount the new amount in droplets
     * @return the resized stack, or {@link #EMPTY} if the amount is zero or this stack is empty
     */
    public PolyFluidStack copyWithAmount(long amount) {
        return amount <= 0 || isEmpty() ? EMPTY : new PolyFluidStack(fluid, amount, components);
    }

    /**
     * Compares only fluid identity and component data, ignoring amount.
     *
     * @param other stack to compare against
     * @return true if both stacks contain the same fluid with the same component patch
     */
    public boolean isSameFluid(PolyFluidStack other) {
        return other != null && fluid == other.fluid && Objects.equals(components, other.components);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof PolyFluidStack other)) return false;
        return amount == other.amount && isSameFluid(other);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fluid, amount, components);
    }
}
