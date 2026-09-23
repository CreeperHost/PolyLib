package net.creeperhost.polylib.client.modulargui.elements;

import net.creeperhost.polylib.client.modulargui.lib.Assembly;
import net.creeperhost.polylib.client.modulargui.lib.BackgroundRender;
import net.creeperhost.polylib.client.modulargui.lib.Constraints;
import net.creeperhost.polylib.client.modulargui.lib.GuiRender;
import net.creeperhost.polylib.client.modulargui.lib.geometry.GuiParent;
import net.creeperhost.polylib.client.modulargui.sprite.Material;
import net.creeperhost.polylib.client.modulargui.sprite.PolyTextures;
import net.creeperhost.polylib.helpers.FormatHelper;
import net.creeperhost.polylib.inventory.fluid.FluidManager;
import net.creeperhost.polylib.inventory.fluid.PolyFluidStack;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static net.minecraft.ChatFormatting.DARK_AQUA;
import static net.minecraft.ChatFormatting.GOLD;
import static net.minecraft.ChatFormatting.GRAY;

/**
 * Modular GUI element that renders a single PolyLib fluid tank.
 * <p>
 * The tank accepts {@link PolyFluidStack} suppliers so container screens can
 * bind it directly to synced fluid data.
 */
public class GuiFluidTank extends GuiElement<GuiFluidTank> implements BackgroundRender {
    /**
     * Default overlay texture for the tank glass.
     */
    public static final Material DEFAULT_WINDOW = PolyTextures.getUncached("widgets/tank_window");

    private int gaugeColour = 0xFF101010;
    private boolean drawGauge = true;
    private Material window = DEFAULT_WINDOW;
    private Supplier<Long> capacity = () -> FluidManager.BUCKET;
    private Supplier<PolyFluidStack> fluidStack = () -> PolyFluidStack.EMPTY;
    private BiFunction<PolyFluidStack, Long, List<Component>> toolTipFormatter;

    /**
     * Creates a fluid tank element.
     *
     * @param parent parent GUI element
     */
    public GuiFluidTank(@NotNull GuiParent<?> parent) {
        super(parent);
        setTooltipDelay(0);
        setToolTipFormatter(defaultFormatter());
    }

    /**
     * Sets the capacity of this tank in droplets.
     *
     * @param capacity tank capacity in droplets
     * @return this tank element
     */
    public GuiFluidTank setCapacity(long capacity) {
        return setCapacity(() -> capacity);
    }

    /**
     * Supplies the capacity of this tank in droplets.
     *
     * @param capacity capacity supplier
     * @return this tank element
     */
    public GuiFluidTank setCapacity(Supplier<Long> capacity) {
        this.capacity = capacity;
        return this;
    }

    /**
     * Sets the current stored fluid stack.
     *
     * @param fluidStack current fluid stack
     * @return this tank element
     */
    public GuiFluidTank setFluidStack(PolyFluidStack fluidStack) {
        return setFluidStack(() -> fluidStack);
    }

    /**
     * Supplies the current stored fluid stack.
     *
     * @param fluidStack fluid stack supplier
     * @return this tank element
     */
    public GuiFluidTank setFluidStack(Supplier<PolyFluidStack> fluidStack) {
        this.fluidStack = fluidStack;
        return this;
    }

    /**
     * Installs a custom tooltip formatter.
     *
     * @param toolTipFormatter formatter receiving the current stack and capacity
     * @return this tank element
     */
    public GuiFluidTank setToolTipFormatter(BiFunction<PolyFluidStack, Long, List<Component>> toolTipFormatter) {
        this.toolTipFormatter = toolTipFormatter;
        setTooltip(() -> this.toolTipFormatter.apply(getFluidStack(), getCapacity()));
        return this;
    }

    /**
     * Sets the tank window texture.
     *
     * @param window window material, or {@code null} to render no overlay
     * @return this tank element
     */
    public GuiFluidTank setWindow(@Nullable Material window) {
        this.window = window;
        return this;
    }

    /**
     * Enables or disables built-in gauge marks.
     *
     * @param drawGauge true to draw gauge marks
     * @return this tank element
     */
    public GuiFluidTank setDrawGauge(boolean drawGauge) {
        this.drawGauge = drawGauge;
        return this;
    }

    /**
     * Sets the built-in gauge mark color.
     *
     * @param gaugeColour ARGB gauge color
     * @return this tank element
     */
    public GuiFluidTank setGaugeColour(int gaugeColour) {
        this.gaugeColour = gaugeColour;
        return this;
    }

    /**
     * @return the tank capacity in droplets
     */
    public long getCapacity() {
        return capacity.get();
    }

    /**
     * @return the current fluid stack
     */
    public PolyFluidStack getFluidStack() {
        PolyFluidStack stack = fluidStack.get();
        return stack == null ? PolyFluidStack.EMPTY : stack;
    }

    @Override
    public void renderBehind(GuiRender render, double mouseX, double mouseY, float partialTicks) {
        PolyFluidStack stack = getFluidStack();
        long capacity = getCapacity();

        render.borderFill(xMin(), yMin(), xMax(), yMax(), 1, 0xFF2A2A2A, 0xFF111111);
        if (!stack.isEmpty() && capacity > 0) {
            double height = ySize() * Math.min(1D, stack.getAmount() / (double) capacity);
            render.fill(xMin() + 1, yMax() - height, xMax() - 1, yMax() - 1, fluidColour(stack));
        }

        if (window != null) {
            render.tileSprite(GuiRender.guiTexPipe(), xMin(), yMin(), xMax(), yMax(), window.sprite(), 0x99FFFFFF);
        }

        if (drawGauge) {
            double spacing = computeGaugeSpacing();
            double pos = spacing;
            while (spacing > 0 && pos + 1 < ySize()) {
                double width = xSize() / 4;
                double yPos = yMax() - 1 - pos;
                render.fill(xMax() - width, yPos, xMax(), yPos + 1, gaugeColour);
                pos += spacing;
            }
        }
    }

    private int fluidColour(PolyFluidStack stack) {
        if (stack.getFluid() == Fluids.WATER) return 0xCC3F76E4;
        if (stack.getFluid() == Fluids.LAVA) return 0xCCFF6A00;

        int hash = BuiltInRegistries.FLUID.getKey(stack.getFluid()).hashCode();
        int red = 96 + ((hash >> 16) & 0x5F);
        int green = 96 + ((hash >> 8) & 0x5F);
        int blue = 96 + (hash & 0x5F);
        return 0xCC000000 | red << 16 | green << 8 | blue;
    }

    private double computeGaugeSpacing() {
        double ySize = ySize();
        double capacityMb = getCapacity() / (double) FluidManager.MILLIBUCKET;
        if (capacityMb <= 0) return 0;
        if (ySize / (capacityMb / 100D) > 3) return ySize / (capacityMb / 100D);
        if (ySize / (capacityMb / 500D) > 3) return ySize / (capacityMb / 500D);
        if (ySize / (capacityMb / 1000D) > 3) return ySize / (capacityMb / 1000D);
        if (ySize / (capacityMb / 5000D) > 3) return ySize / (capacityMb / 5000D);
        if (ySize / (capacityMb / 10000D) > 3) return ySize / (capacityMb / 10000D);
        if (ySize / (capacityMb / 50000D) > 3) return ySize / (capacityMb / 50000D);
        return 0;
    }

    /**
     * Creates a tank with a vanilla-slot style backing rectangle.
     *
     * @param parent parent GUI element
     * @return assembly containing the backing rectangle and tank element
     */
    public static Assembly<GuiRectangle, GuiFluidTank> simpleTank(@NotNull GuiParent<?> parent) {
        GuiRectangle container = GuiRectangle.vanillaSlot(parent);
        GuiFluidTank tank = new GuiFluidTank(container);
        Constraints.bind(tank, container, 1);
        return new Assembly<>(container, tank);
    }

    /**
     * Creates the default fluid tank tooltip formatter.
     *
     * @return tooltip formatter
     */
    public static BiFunction<PolyFluidStack, Long, List<Component>> defaultFormatter() {
        return (fluidStack, capacity) -> {
            List<Component> tooltip = new ArrayList<>();
            boolean shift = Minecraft.getInstance().hasShiftDown();
            long storedMb = fluidStack.getAmount() / FluidManager.MILLIBUCKET;
            long capacityMb = capacity / FluidManager.MILLIBUCKET;
            tooltip.add(Component.translatable("fluid_tank.polylib.fluid_storage").withStyle(DARK_AQUA));
            if (!fluidStack.isEmpty()) {
                tooltip.add(Component.translatable("fluid_tank.polylib.contains")
                        .withStyle(GOLD)
                        .append(" ")
                        .append(Component.literal(BuiltInRegistries.FLUID.getKey(fluidStack.getFluid()).toString()).withStyle(GRAY)));
            }
            tooltip.add(Component.translatable("fluid_tank.polylib.capacity")
                    .withStyle(GOLD)
                    .append(" ")
                    .append(Component.literal(shift ? FormatHelper.addCommas(capacityMb) : FormatHelper.formatNumber(capacityMb)).withStyle(GRAY))
                    .append(" ")
                    .append(Component.translatable("fluid_tank.polylib.mb").withStyle(GRAY)));
            tooltip.add(Component.translatable("fluid_tank.polylib.stored")
                    .withStyle(GOLD)
                    .append(" ")
                    .append(Component.literal(shift ? FormatHelper.addCommas(storedMb) : FormatHelper.formatNumber(storedMb)).withStyle(GRAY))
                    .append(" ")
                    .append(Component.translatable("fluid_tank.polylib.mb").withStyle(GRAY))
                    .append(Component.literal(String.format(" (%.2f%%)", capacity <= 0 ? 0D : (fluidStack.getAmount() / (double) capacity) * 100D)).withStyle(GRAY)));
            return tooltip;
        };
    }
}
