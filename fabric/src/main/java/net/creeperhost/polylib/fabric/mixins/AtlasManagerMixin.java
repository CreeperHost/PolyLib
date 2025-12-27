package net.creeperhost.polylib.fabric.mixins;

import net.creeperhost.polylib.client.modulargui.sprite.PolyTextures;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.AtlasManager;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

/**
 * Created by brandon3055 on 06/10/2025
 */
@Mixin(AtlasManager.class)
public class AtlasManagerMixin {

    @Shadow @Final private Map<Identifier, AtlasManager.AtlasEntry> atlasByTexture;

    @Shadow @Final private Map<Identifier, AtlasManager.AtlasEntry> atlasById;

    @Inject (method = "<init>(Lnet/minecraft/client/renderer/texture/TextureManager;I)V", at = @At ("TAIL"))
    private void atlasInit(TextureManager textureManager, int i, CallbackInfo ci) {
        AtlasManager.AtlasConfig atlasConfig = new AtlasManager.AtlasConfig(PolyTextures.TEXTURE_ID, PolyTextures.DEFINITION_LOCATION, false);
        TextureAtlas textureAtlas = new TextureAtlas(atlasConfig.textureId());
        textureManager.register(atlasConfig.textureId(), textureAtlas);
        AtlasManager.AtlasEntry atlasEntry = new AtlasManager.AtlasEntry(textureAtlas, atlasConfig);
        this.atlasByTexture.put(atlasConfig.textureId(), atlasEntry);
        this.atlasById.put(atlasConfig.definitionLocation(), atlasEntry);
    }
}
