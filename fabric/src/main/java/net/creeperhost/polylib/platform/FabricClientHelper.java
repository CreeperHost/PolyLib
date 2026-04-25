package net.creeperhost.polylib.platform;

import com.mojang.datafixers.util.Either;
import net.creeperhost.polylib.platform.services.IClientHelper;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FabricClientHelper implements IClientHelper
{
    @Override
    public ToolTipResult postRenderTooltipPre(@NotNull ItemStack stack, GuiGraphicsExtractor graphics, int x, int y, int screenWidth, int screenHeight, @NotNull List<ClientTooltipComponent> components, @NotNull Font font, @NotNull ClientTooltipPositioner positioner)
    {
        //TODO find the fabric event and fire it
        return new TTR(x, y, font, false);
    }

    @Override
    public ToolTipColour postTooltipColour(@NotNull ItemStack stack, GuiGraphicsExtractor graphics, int x, int y, int backgroundStart, int backgroundEnd, int borderStart, int borderEnd, @NotNull Font font, @NotNull List<ClientTooltipComponent> components)
    {
        return new TTC(backgroundStart, backgroundEnd, borderStart, borderEnd);
    }

    @Override
    public List<ClientTooltipComponent> postGatherTooltipComponents(ItemStack stack, List<? extends FormattedText> textElements, Optional<TooltipComponent> itemComponent, int mouseX, int screenWidth, int screenHeight, Font fallbackFont)
    {
        List<Either<FormattedText, TooltipComponent>> elements = (List) textElements.stream().map(Either::left).collect(Collectors.toCollection(ArrayList::new));
        itemComponent.ifPresent((c) -> elements.add(1, Either.right(c)));

        int tooltipTextWidth = elements.stream().mapToInt((either) -> {
            Objects.requireNonNull(fallbackFont);
            return either.map(fallbackFont::width, (component) -> 0);
        }).max().orElse(0);

        boolean needsWrap = false;
        int tooltipX = mouseX + 12;
        if (tooltipX + tooltipTextWidth + 4 > screenWidth) {
            tooltipX = mouseX - 16 - tooltipTextWidth;
            if (tooltipX < 4) {
                if (mouseX > screenWidth / 2) {
                    tooltipTextWidth = mouseX - 12 - 8;
                } else {
                    tooltipTextWidth = screenWidth - 16 - mouseX;
                }

                needsWrap = true;
            }
        }

        if (needsWrap) {
            int finalTooltipTextWidth = tooltipTextWidth;
            return elements.stream()
                    .flatMap((either) -> (Stream) either.map((text) -> fallbackFont.split(text, finalTooltipTextWidth).stream()
                            .map(ClientTooltipComponent::create), (component) -> Stream.of(ClientTooltipComponent.create(component))))
                    .toList();
        }

        return elements.stream()
                .map((either) -> either.map((text) -> ClientTooltipComponent.create(text instanceof Component ? ((Component) text).getVisualOrderText() : Language.getInstance().getVisualOrder(text)), ClientTooltipComponent::create))
                .toList();
    }

    @Override
    public void onItemDecorate(GuiGraphicsExtractor guiGraphics, Font font, ItemStack stack, int xOffset, int yOffset) {}

    //@formatter:off
    private record TTR(int x, int y, Font font, boolean canceled) implements IClientHelper.ToolTipResult {
        @Override public @NotNull Font getFont() { return font; }
        @Override public boolean canceled() { return canceled; }
        @Override public int getX() { return x; }
        @Override public int getY() { return y; }
    }

    private record TTC(int backgroundStart, int backgroundEnd, int borderStart, int borderEnd) implements IClientHelper.ToolTipColour {
        @Override public int getBackgroundStart() { return backgroundStart; }
        @Override public int getBackgroundEnd() { return backgroundEnd; }
        @Override public int getBorderStart() { return borderStart; }
        @Override public int getBorderEnd() { return borderEnd; }
    }
    //@formatter:on
}
