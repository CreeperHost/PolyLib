package net.creeperhost.polylib;

import net.creeperhost.polylib.client.modulargui.sprite.PolyTextures;
import net.minecraft.client.resources.model.sprite.AtlasManager;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterTextureAtlasesEvent;
import net.neoforged.neoforge.client.event.TextureAtlasStitchedEvent;

public class PolyLibClientNeoForge
{
    public static void init(IEventBus eventBus)
    {
        eventBus.addListener(PolyLibClientNeoForge::atlasStitched);
        eventBus.addListener(PolyLibClientNeoForge::registerTextureAtlas);
    }

    private static void registerTextureAtlas(RegisterTextureAtlasesEvent event) {
        AtlasManager.AtlasConfig config = new AtlasManager.AtlasConfig(PolyTextures.TEXTURE_ID, PolyTextures.DEFINITION_LOCATION, false);
        event.register(config);
    }

    private static void atlasStitched(TextureAtlasStitchedEvent event) {
        if (event.getAtlas().location().equals(PolyTextures.TEXTURE_ID)) {
            PolyTextures.setAtlas(event.getAtlas());
        }
    }
}
