package net.creeperhost.polylib.platform;

import net.creeperhost.polylib.platform.services.IClientHelper;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class NeoForgeClientHelper implements IClientHelper
{
    @Override
    public ToolTipResult postRenderTooltipPre(@NotNull ItemStack stack, GuiGraphicsExtractor graphics, int x, int y, int screenWidth, int screenHeight, @NotNull List<ClientTooltipComponent> components, @NotNull Font font, @NotNull ClientTooltipPositioner positioner)
    {
        RenderTooltipEvent.Pre preEvent = ClientHooks.onRenderTooltipPre(stack, graphics, x, y, screenWidth, screenHeight, components, font, positioner);
        return new TTR(preEvent);
    }

    @Override
    public ToolTipColour postTooltipColour(@NotNull ItemStack stack, GuiGraphicsExtractor graphics, int x, int y, int backgroundStart, int backgroundEnd, int borderStart, int borderEnd, @NotNull Font font, @NotNull List<ClientTooltipComponent> components)
    {
        return new TTC(backgroundStart, backgroundEnd, borderStart, borderEnd);
    }

    @Override
    public List<ClientTooltipComponent> postGatherTooltipComponents(ItemStack stack, List<? extends FormattedText> textElements, Optional<TooltipComponent> itemComponent, int mouseX, int screenWidth, int screenHeight, Font fallbackFont)
    {
        return net.neoforged.neoforge.client.ClientHooks.gatherTooltipComponents(stack, textElements, itemComponent, mouseX, screenWidth, screenHeight, fallbackFont);
    }

    @Override
    public void onItemDecorate(GuiGraphicsExtractor guiGraphics, Font font, ItemStack stack, int xOffset, int yOffset)
    {
        net.neoforged.neoforge.client.ItemDecoratorHandler.of(stack).render(guiGraphics, font, stack, xOffset, yOffset);
    }

    //@formatter:off
    private static class TTR implements IClientHelper.ToolTipResult {
        private final RenderTooltipEvent.Pre event;
        public TTR(RenderTooltipEvent.Pre event) {
            this.event = event;
        }
        @Override public @NotNull Font getFont() { return event.getFont(); }
        @Override public boolean canceled() { return event.isCanceled(); }
        @Override public int getX() { return event.getX(); }
        @Override public int getY() { return event.getY(); }
    }

    private static class TTC implements IClientHelper.ToolTipColour {
        private final int backgroundStart;
        private final int backgroundEnd;
        private final int borderStart;
        private final int borderEnd;

        public TTC(int backgroundStart, int backgroundEnd, int borderStart, int borderEnd) {
            this.backgroundStart = backgroundStart;
            this.backgroundEnd = backgroundEnd;
            this.borderStart = borderStart;
            this.borderEnd = borderEnd;
        }

        @Override public int getBackgroundStart() { return backgroundStart; }
        @Override public int getBackgroundEnd() { return backgroundEnd; }
        @Override public int getBorderStart() { return borderStart; }
        @Override public int getBorderEnd() { return borderEnd; }
    }
    //@formatter:on
}
