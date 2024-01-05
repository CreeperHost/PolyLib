package net.creeperhost.polylib.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.creeperhost.polylib.PolyLib;
import net.creeperhost.polylib.client.modulargui.ModularGuiContainer;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;

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
                return containerScreen.getModularGui().getJeiExclusions().map(e -> e.getRectangle().toRect2i()).toList();
            }
        });
    }
}
