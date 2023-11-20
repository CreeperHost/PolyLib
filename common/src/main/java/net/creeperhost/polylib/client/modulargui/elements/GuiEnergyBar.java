package net.creeperhost.polylib.client.modulargui.elements;

import net.creeperhost.polylib.client.modulargui.lib.Assembly;
import net.creeperhost.polylib.client.modulargui.lib.BackgroundRender;
import net.creeperhost.polylib.client.modulargui.lib.Constraints;
import net.creeperhost.polylib.client.modulargui.lib.GuiRender;
import net.creeperhost.polylib.client.modulargui.lib.geometry.GuiParent;
import net.creeperhost.polylib.client.modulargui.sprite.Material;
import net.creeperhost.polylib.client.modulargui.sprite.PolyTextures;
import net.creeperhost.polylib.helpers.FormatHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static net.minecraft.ChatFormatting.*;

/**
 * Created by brandon3055 on 10/09/2023
 */
public class GuiEnergyBar extends GuiElement<GuiEnergyBar> implements BackgroundRender {
    public static final Material EMPTY = PolyTextures.getUncached("widgets/energy_empty");
    public static final Material FULL = PolyTextures.getUncached("widgets/energy_full");

    private Supplier<Long> energy = () -> 0L;
    private Supplier<Long> capacity = () -> 0L;
    private BiFunction<Long, Long, List<Component>> toolTipFormatter;

    public GuiEnergyBar(@NotNull GuiParent<?> parent) {
        super(parent);
        setTooltipDelay(0);
        setToolTipFormatter(defaultFormatter());
    }

    public GuiEnergyBar setCapacity(long capacity) {
        return setCapacity(() -> capacity);
    }

    public GuiEnergyBar setCapacity(Supplier<Long> capacity) {
        this.capacity = capacity;
        return this;
    }

    public GuiEnergyBar setEnergy(long energy) {
        return setEnergy(() -> energy);
    }

    public GuiEnergyBar setEnergy(Supplier<Long> energy) {
        this.energy = energy;
        return this;
    }

    public long getEnergy() {
        return energy.get();
    }

    public long getCapacity() {
        return capacity.get();
    }

    /**
     * Install a custom formatter to control how the energy tool tip renders.
     */
    public GuiEnergyBar setToolTipFormatter(BiFunction<Long, Long, List<Component>> toolTipFormatter) {
        this.toolTipFormatter = toolTipFormatter;
        setTooltip(() -> this.toolTipFormatter.apply(getEnergy(), getCapacity()));
        return this;
    }

    @Override
    public void renderBehind(GuiRender render, double mouseX, double mouseY, float partialTicks) {
        float p = 1/128F;
        float height = getCapacity() <= 0 ? 0 : (float) ySize() * (getEnergy() / (float) getCapacity());
        float texHeight = height * p;
        render.partialSprite(EMPTY.renderType(GuiRender::texColType), xMin(), yMin(), xMax(), yMax(), EMPTY.sprite(), 0F, 1F - (p * (float) ySize()), p * (float) xSize(), 1F, 0xFFFFFFFF);
        render.partialSprite(FULL.renderType(GuiRender::texColType), xMin(), yMin() + (ySize() - height), xMax(), yMax(), FULL.sprite(), 0F, 1F - texHeight, p * (float) xSize(), 1F, 0xFFFFFFFF);
    }

    /**
     * Creates a simple energy bar using a simple slot as a background to make it look nice.
     */
    public static Assembly<GuiRectangle, GuiEnergyBar> simpleBar(@NotNull GuiParent<?> parent) {
        GuiRectangle container = GuiRectangle.vanillaSlot(parent);
        GuiEnergyBar energyBar = new GuiEnergyBar(container);
        Constraints.bind(energyBar, container, 1);
        return new Assembly<>(container, energyBar);
    }


    public static BiFunction<Long, Long, List<Component>> defaultFormatter() {
        return (energy, capacity) -> {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.translatable("energy_bar.polylib.energy_storage").withStyle(DARK_AQUA));
            boolean shift = Screen.hasShiftDown();
            tooltip.add(Component.translatable("energy_bar.polylib.capacity")
                    .withStyle(GOLD)
                    .append(" ")
                    .append(Component.literal(shift ? FormatHelper.addCommas(capacity) : FormatHelper.formatNumber(capacity))
                            .withStyle(GRAY)
                            .append(" ")
                            .append(Component.translatable("energy_bar.polylib.rf")
                                    .withStyle(GRAY)
                            )
                    )
            );
            tooltip.add(Component.translatable("energy_bar.polylib.stored")
                    .withStyle(GOLD)
                    .append(" ")
                    .append(Component.literal(shift ? FormatHelper.addCommas(energy) : FormatHelper.formatNumber(energy))
                            .withStyle(GRAY)
                    )
                    .append(" ")
                    .append(Component.translatable("energy_bar.polylib.rf")
                            .withStyle(GRAY)
                    )
                    .append(Component.literal(String.format(" (%.2f%%)", ((double) energy / (double) capacity) * 100D))
                            .withStyle(GRAY)
                    )
            );
            return tooltip;
        };
    }

}
