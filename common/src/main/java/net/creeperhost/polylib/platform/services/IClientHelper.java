package net.creeperhost.polylib.platform.services;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public interface IClientHelper
{
    ToolTipResult postRenderTooltipPre(@NotNull ItemStack stack, GuiGraphicsExtractor graphics, int x, int y, int screenWidth, int screenHeight, @NotNull List<ClientTooltipComponent> components, @NotNull Font font, @NotNull ClientTooltipPositioner positioner);

    ToolTipColour postTooltipColour(@NotNull ItemStack stack, GuiGraphicsExtractor graphics, int x, int y, int backgroundStart, int backgroundEnd , int borderStart, int borderEnd, @NotNull Font font, @NotNull List<ClientTooltipComponent> components);

    List<ClientTooltipComponent> postGatherTooltipComponents(ItemStack stack, List<? extends FormattedText> textElements, Optional<TooltipComponent> itemComponent, int mouseX, int screenWidth, int screenHeight, Font fallbackFont);

    void onItemDecorate(GuiGraphicsExtractor guiGraphics, Font font, ItemStack stack, int xOffset, int yOffset);

    public interface ToolTipResult {
        int getX();
        int getY();
        @NotNull Font getFont();
        boolean canceled();
    }

    public interface ToolTipColour {
        int getBackgroundStart();
        int getBackgroundEnd();
        int getBorderStart();
        int getBorderEnd();
    }
}
