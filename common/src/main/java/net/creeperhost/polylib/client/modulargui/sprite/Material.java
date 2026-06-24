package net.creeperhost.polylib.client.modulargui.sprite;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * 26.1 port of the old compatibility wrapper.
 *
 * Vanilla net.minecraft.client.resources.model.Material is gone in the new
 * rendering/model setup, so this class now stands entirely on its own.
 */
public class Material {
    private final Identifier atlasLocation;
    private final Identifier texture;
    private final Function<Identifier, TextureAtlasSprite> spriteFunction;

    @Nullable
    private RenderType renderType;

    public Material(Identifier atlasLocation, Identifier texture, Function<Identifier, TextureAtlasSprite> spriteFunction) {
        this.atlasLocation = atlasLocation;
        this.texture = texture;
        this.spriteFunction = spriteFunction;
    }

    public Identifier atlasLocation() {
        return atlasLocation;
    }

    public Identifier texture() {
        return texture;
    }

    public TextureAtlasSprite sprite() {
        return spriteFunction.apply(texture);
    }

    /**
     * Returns the cached render type for this material.
     */
    public RenderType renderType(Function<Identifier, RenderType> typeBuilder) {
        if (this.renderType == null) {
            this.renderType = typeBuilder.apply(atlasLocation);
        }
        return this.renderType;
    }

    /**
     * Convenience method to create a vertex consumer using this material's render type.
     */
    public VertexConsumer buffer(Function<RenderType, VertexConsumer> bufferSource, Function<Identifier, RenderType> typeBuilder) {
        return bufferSource.apply(renderType(typeBuilder));
    }

    /**
     * Resolves a sprite from an atlas in the 26.1-style pipeline.
     */
    private static TextureAtlasSprite getAtlasSprite(Identifier atlas, Identifier sprite) {
        return Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(atlas).getSprite(sprite);
    }

    /**
     * Convenient method for getting a material from an atlas.
     */
    public static Material fromAtlas(Identifier atlasLocation, String texturePath) {
        Identifier texture = Identifier.fromNamespaceAndPath(atlasLocation.getNamespace(), texturePath);
        return new Material(atlasLocation, texture, id -> getAtlasSprite(atlasLocation, id));
    }

    /**
     * Create a material from an existing atlas sprite.
     */
    @Nullable
    public static Material fromSprite(@Nullable TextureAtlasSprite sprite) {
        if (sprite == null) return null;
        return new Material(
                sprite.atlasLocation(),
                sprite.contents().name(),
                id -> getAtlasSprite(sprite.atlasLocation(), id)
        );
    }

    /**
     * Wrap a non-atlased raw texture.
     *
     * This preserves your old "full sprite" fallback behavior for code paths
     * that expect atlas-like UV helpers.
     */
    public static Material fromRawTexture(Identifier texture) {
        return new Material(texture, texture, FullSprite::new);
    }

    private static class FullSprite extends TextureAtlasSprite {
        private FullSprite(Identifier location) {
            super(location, new SpriteContents(location, new FrameSize(1, 1), new NativeImage(1, 1, false)),
                    1,
                    1,
                    0,
                    0,
                    0
            );
        }

        @Override
        public float getU(float u) {
            return u / 16F;
        }

        @Override
        public float getV(float v) {
            return v / 16F;
        }
    }
}
