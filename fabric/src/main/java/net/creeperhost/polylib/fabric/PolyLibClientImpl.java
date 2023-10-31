package net.creeperhost.polylib.fabric;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientTooltipEvent;
import net.creeperhost.polylib.PolyLibClient;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
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

/**
 * Created by brandon3055 on 26/08/2023
 */
public class PolyLibClientImpl {

    public static PolyLibClient.ToolTipResult postRenderTooltipPre(@NotNull ItemStack stack, PoseStack poseStack, int x, int y, int screenWidth, int screenHeight, @NotNull List<ClientTooltipComponent> components, @NotNull Font font) {
        EventResult result = ClientTooltipEvent.RENDER_PRE.invoker().renderTooltip(poseStack, components, x, y);
        return new TTR(x, y, font, result.isFalse());
    }

    public static PolyLibClient.ToolTipColour postTooltipColour(@NotNull ItemStack stack, PoseStack poseStack, int x, int y, int backgroundStart, int backgroundEnd , int borderStart, int borderEnd, @NotNull Font font, @NotNull List<ClientTooltipComponent> components) {
        return new TTC(backgroundStart, backgroundEnd, borderStart, borderEnd);
    }

    //This is mostly copied from GorgeHooksClient#gatherTooltipComponents
    public static List<ClientTooltipComponent> postGatherTooltipComponents(ItemStack stack, List<? extends FormattedText> textElements, Optional<TooltipComponent> itemComponent, int mouseX, int screenWidth, int screenHeight, Font font) {
        List<Either<FormattedText, TooltipComponent>> elements = (List) textElements.stream().map(Either::left).collect(Collectors.toCollection(ArrayList::new));
        itemComponent.ifPresent((c) -> elements.add(1, Either.right(c)));

        int tooltipTextWidth = elements.stream().mapToInt((either) -> {
            Objects.requireNonNull(font);
            return either.map(font::width, (component) -> 0);
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
                    .flatMap((either) -> (Stream) either.map((text) -> font.split(text, finalTooltipTextWidth).stream()
                    .map(ClientTooltipComponent::create), (component) -> Stream.of(ClientTooltipComponent.create(component))))
                    .toList();
        }

        return elements.stream()
                .map((either) -> either.map((text) -> ClientTooltipComponent.create(text instanceof Component ? ((Component) text).getVisualOrderText() : Language.getInstance().getVisualOrder(text)), ClientTooltipComponent::create))
                .toList();
    }

    public static void onItemDecorate(Font font, ItemStack stack, int xOffset, int yOffset) {

    }

        //@formatter:off
    private record TTR(int x, int y, Font font, boolean canceled) implements PolyLibClient.ToolTipResult {
        @Override public @NotNull Font getFont() { return font; }
        @Override public boolean canceled() { return canceled; }
        @Override public int getX() { return x; }
        @Override public int getY() { return y; }
    }

    private record TTC(int backgroundStart, int backgroundEnd, int borderStart, int borderEnd) implements PolyLibClient.ToolTipColour {
        @Override public int getBackgroundStart() { return backgroundStart; }
        @Override public int getBackgroundEnd() { return backgroundEnd; }
        @Override public int getBorderStart() { return borderStart; }
        @Override public int getBorderEnd() { return borderEnd; }
    }
    //@formatter:on

}
