package net.creeperhost.testmod.fabric.mixins;

import net.creeperhost.testmod.client.gui.TestModTextures;
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

@Mixin (TextureAtlas.class)
public class TextureAtlasMixin {

    @Shadow
    @Nullable
    private TextureAtlasSprite missingSprite;

    @Unique
    private TextureAtlas getThis() {
        return (TextureAtlas) (Object) this;
    }

    @Inject (method = "upload(Lnet/minecraft/client/renderer/texture/SpriteLoader$Preparations;)V", at = @At ("TAIL"))
    public void upload(SpriteLoader.Preparations preparations, CallbackInfo ci) {
        if (missingSprite == null) return;
        if (getThis().location().equals(TestModTextures.TEXTURE_ID)) {
            TestModTextures.setAtlas(getThis());
        }
    }
}