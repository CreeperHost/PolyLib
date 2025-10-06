package net.creeperhost.polylib.fabric.mixins;

import net.creeperhost.polylib.client.modulargui.sprite.PolyTextures;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Created by brandon3055 on 06/10/2025
 */
@Mixin (TextureAtlas.class)
public class TextureAtlasMixin {

    @Shadow @Nullable
    private TextureAtlasSprite missingSprite;

    @Unique
    private TextureAtlas getThis() {
        return (TextureAtlas) (Object) this;
    }

    @Inject (method = "upload(Lnet/minecraft/client/renderer/texture/SpriteLoader$Preparations;)V", at = @At ("TAIL"))
    public void upload(SpriteLoader.Preparations preparations, CallbackInfo ci) {
        if (missingSprite == null) return;
        if (getThis().location().equals(PolyTextures.TEXTURE_ID)) {
            PolyTextures.setAtlas(getThis());
        }
    }

}
