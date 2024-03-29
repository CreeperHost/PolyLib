package net.creeperhost.polylib.client.modulargui.elements;

import dev.architectury.fluid.FluidStack;
import dev.architectury.hooks.fluid.FluidStackHooks;
import dev.architectury.platform.Platform;
import net.creeperhost.polylib.client.modulargui.lib.Assembly;
import net.creeperhost.polylib.client.modulargui.lib.BackgroundRender;
import net.creeperhost.polylib.client.modulargui.lib.Constraints;
import net.creeperhost.polylib.client.modulargui.lib.GuiRender;
import net.creeperhost.polylib.client.modulargui.lib.geometry.GuiParent;
import net.creeperhost.polylib.client.modulargui.sprite.Material;
import net.creeperhost.polylib.client.modulargui.sprite.PolyTextures;
import net.creeperhost.polylib.helpers.FormatHelper;
import net.creeperhost.polylib.inventory.fluid.FluidManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static net.minecraft.ChatFormatting.*;

/**
 * When implementing this tank, you must specify the tank capacity in mb,
 * And then you have two options for specifying the tank contents.
 * You can set the fluid and the amount stored,
 * Or you can provide a {@link FluidStack}
 * <p>
 * Created by brandon3055 on 11/09/2023
 */
public class GuiFluidTank extends GuiElement<GuiFluidTank> implements BackgroundRender {
    //TODO make a better texture, This feels a little too.. cluttered.
    public static final Material DEFAULT_WINDOW = PolyTextures.getUncached("widgets/tank_window");

    private int gaugeColour = 0xFF909090;
    private boolean drawGauge = true;
    private Material window = null;
    private Supplier<Long> capacity = () -> 10000L;
    private Supplier<FluidStack> fluidStack = FluidStack::empty;

    private BiFunction<FluidStack, Long, List<Component>> toolTipFormatter;

    public GuiFluidTank(@NotNull GuiParent<?> parent) {
        super(parent);
        setTooltipDelay(0);
        setToolTipFormatter(defaultFormatter());
    }

    /**
     * Sets the capacity of this tank in milli-buckets.
     */
    public GuiFluidTank setCapacity(long capacity) {
        return setCapacity(() -> capacity);
    }

    /**
     * Supply the capacity of this tank in milli-buckets.
     */
    public GuiFluidTank setCapacity(Supplier<Long> capacity) {
        this.capacity = capacity;
        return this;
    }

    /**
     * Allows you to set the current stored fluid stack.
     */
    public GuiFluidTank setFluidStack(FluidStack fluidStack) {
        return setFluidStack(() -> fluidStack);
    }

    /**
     * Allows you to supply the current stored fluid stack.
     */
    public GuiFluidTank setFluidStack(Supplier<FluidStack> fluidStack) {
        this.fluidStack = fluidStack;
        return this;
    }

    /**
     * Install a custom formatter to control how the fluid tool tip renders.
     */
    public GuiFluidTank setToolTipFormatter(BiFunction<FluidStack, Long, List<Component>> toolTipFormatter) {
        this.toolTipFormatter = toolTipFormatter;
        setTooltip(() -> this.toolTipFormatter.apply(getFluidStack(), getCapacity()));
        return this;
    }

    /**
     * Sets the tank window texture, Will be tiled to fit the tank size.
     *
     * @param window New window texture or null for no window texture.
     */
    public GuiFluidTank setWindow(@Nullable Material window) {
        this.window = window;
        return this;
    }

    /**
     * Enable the built-in fluid gauge lines.
     */
    public GuiFluidTank setDrawGauge(boolean drawGauge) {
        this.drawGauge = drawGauge;
        return this;
    }

    /**
     * Sets the colour of the built-in fluid gauge lines
     */
    public GuiFluidTank setGaugeColour(int gaugeColour) {
        this.gaugeColour = gaugeColour;
        return this;
    }

    public Long getCapacity() {
        return capacity.get();
    }

    public FluidStack getFluidStack() {
        return fluidStack.get();
    }

    @Override
    public void renderBehind(GuiRender render, double mouseX, double mouseY, float partialTicks) {
        FluidStack stack = getFluidStack();
        Material fluidMat = Material.fromSprite(FluidStackHooks.getStillTexture(stack));

        if (!stack.isEmpty() && fluidMat != null) {
            int fluidColor = FluidStackHooks.getColor(stack);
            float height = getCapacity() <= 0 ? 0 : (float) ySize() * (stack.getAmount() / (float) getCapacity());
            render.tileSprite(fluidMat.renderType(GuiRender::texColType), xMin(), yMax() - height, xMax(), yMax(), fluidMat.sprite(), fluidColor);
        }

        if (window != null) {
            render.tileSprite(window.renderType(GuiRender::texColType), xMin(), yMin(), xMax(), yMax(), window.sprite(), 0xFFFFFFFF);
        }

        gaugeColour = 0xFF000000;
        if (drawGauge) {
            double spacing = computeGaugeSpacing();
            if (spacing == 0) return;

            double pos = spacing;
            while (pos + 1 < ySize()) {
                double width = xSize() / 4;
                double yPos = yMax() - 1 - pos;
                render.fill(xMax() - width, yPos, xMax(), yPos + 1, gaugeColour);
                pos += spacing;
            }
        }
    }

    private double computeGaugeSpacing() {
        double ySize = ySize();
        double capacity = getCapacity();
        if (ySize / (capacity / 100D) > 3) return ySize / (capacity / 100D);
        else if (ySize / (capacity / 500D) > 3) return ySize / (capacity / 500D);
        else if (ySize / (capacity / 1000D) > 3) return ySize / (capacity / 1000D);
        else if (ySize / (capacity / 5000D) > 3) return ySize / (capacity / 5000D);
        else if (ySize / (capacity / 10000D) > 3) return ySize / (capacity / 10000D);
        else if (ySize / (capacity / 50000D) > 3) return ySize / (capacity / 50000D);
        else if (ySize / (capacity / 100000D) > 3) return ySize / (capacity / 100000D);
        return 0;
    }

    /**
     * Creates a simple tank using a simple slot as a background to make it look nice.
     */
    public static Assembly<GuiRectangle, GuiFluidTank> simpleTank(@NotNull GuiParent<?> parent) {
        GuiRectangle container = GuiRectangle.vanillaSlot(parent);
        GuiFluidTank energyBar = new GuiFluidTank(container);
        Constraints.bind(energyBar, container, 1);
        return new Assembly<>(container, energyBar);
    }

    public static BiFunction<FluidStack, Long, List<Component>> defaultFormatter() {
        return (fluidStack, capacity) -> {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.translatable("fluid_tank.polylib.fluid_storage").withStyle(DARK_AQUA));
            if (!fluidStack.isEmpty()) {
                tooltip.add(Component.translatable("fluid_tank.polylib.contains")
                        .withStyle(GOLD)
                        .append(" ")
                        .append(fluidStack.getName().copy()
                                .setStyle(Style.EMPTY
                                        .withColor(FluidStackHooks.getColor(fluidStack))
                                )
                        )
                );
            }

            tooltip.add(Component.translatable("fluid_tank.polylib.capacity")
                    .withStyle(GOLD)
                    .append(" ")
                    .append(Component.literal(FormatHelper.addCommas(capacity / FluidManager.MILLIBUCKET))
                            .withStyle(GRAY)
                            .append(" ")
                            .append(Component.translatable("fluid_tank.polylib.mb")
                                    .withStyle(GRAY)
                            )
                    )
            );
            tooltip.add(Component.translatable("fluid_tank.polylib.stored")
                    .withStyle(GOLD)
                    .append(" ")
                    .append(Component.literal(FormatHelper.addCommas(fluidStack.getAmount() / FluidManager.MILLIBUCKET))
                            .withStyle(GRAY)
                    )
                    .append(" ")
                    .append(Component.translatable("fluid_tank.polylib.mb")
                            .withStyle(GRAY)
                    )
                    .append(Component.literal(String.format(" (%.2f%%)", ((double) fluidStack.getAmount() / (double) capacity) * 100D))
                            .withStyle(GRAY)
                    )
            );
            return tooltip;
        };
    }

}
