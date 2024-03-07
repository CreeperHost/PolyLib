package net.creeperhost.polylib.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.creeperhost.polylib.PolyLib;
import net.creeperhost.polylib.client.modulargui.ModularGui;
import net.creeperhost.polylib.client.modulargui.ModularGuiContainer;
import net.creeperhost.polylib.client.modulargui.elements.GuiElement;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by brandon3055 on 24/12/2023
 */
@JeiPlugin
public class PolyJEIPlugin implements IModPlugin {
    private static final ResourceLocation ID = new ResourceLocation(PolyLib.MOD_ID, "jei_plugin");

    public PolyJEIPlugin() {
    }

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(ModularGuiContainer.class, new IGuiContainerHandler<>() {
            @Override
            public List<Rect2i> getGuiExtraAreas(ModularGuiContainer containerScreen) {
                return containerScreen.getModularGui().getJeiExclusions().stream().map(e -> e.getRectangle().toRect2i()).toList();
            }
        });
        registration.addGhostIngredientHandler(ModularGuiContainer.class, new IngredientDropHandler());
    }

    private static class IngredientDropHandler implements IGhostIngredientHandler<ModularGuiContainer> {
        private ModularGui gui;
        private boolean highlight = true;

        @Override
        public <I> List<Target<I>> getTargetsTyped(ModularGuiContainer screen, ITypedIngredient<I> ingredient, boolean doStart) {
            gui = screen.getModularGui();
            gui.setJeiHighlightTime(doStart ? 60 * 20 : 3 * 20);
            highlight = !doStart;
            if (!doStart) return Collections.emptyList();
            List<Target<I>> targets = new ArrayList<>();
            ingredient.getIngredient(VanillaTypes.ITEM_STACK).ifPresent(stack -> gui.getJeiDropTargets().forEach(e -> targets.add(new DropTarget<>(e))));
            return targets;
        }

        @Override
        public void onComplete() {
            highlight = true;
            gui.setJeiHighlightTime(0);
        }

        @Override
        public boolean shouldHighlightTargets() {
            return highlight;
        }
    }

    private record DropTarget<I>(GuiElement<?> element) implements IGhostIngredientHandler.Target<I> {
        @Override
        public Rect2i getArea() {
            return element.getRectangle().toRect2i();
        }

        @Override
        public void accept(I ingredient) {
            element.getJeiDropConsumer().accept((ItemStack) ingredient);
        }
    }
}
