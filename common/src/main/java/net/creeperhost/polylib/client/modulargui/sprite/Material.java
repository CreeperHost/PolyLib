package net.creeperhost.polylib.client.modulargui.sprite;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * This is similar to Minecraft's {@link net.minecraft.client.resources.model.Material}
 * This contains the essential data required to render an atlas sprite.
 * <p>
 * The primary purpose of this class is to make porting between MC versions easier.
 * It also allows for loading sprites from a custom texture atlas. Minecraft's material class can only load from vanilla atlases.
 * <p>
 * Created by brandon3055 on 20/08/2023
 */
public class Material {
    private final Identifier atlasLocation;
    private final Identifier texture;
    private final Function<Identifier, TextureAtlasSprite> spriteFunction;

    @Nullable
    private RenderType renderType;
    @Nullable
    private net.minecraft.client.resources.model.Material vanillaMat;

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
        return spriteFunction.apply(texture());
    }

    /**
     * Returns the cached render type for this material.
     * The supplied function will be used to create the render type the first time this method is called.
     *
     * @param typeBuilder a function that will be used to create the render type if it does not already exist.
     * @return The render type for this material.
     */
    public RenderType renderType(Function<Identifier, RenderType> typeBuilder) {
        if (this.renderType == null) {
            this.renderType = typeBuilder.apply(atlasLocation());
        }
        return this.renderType;
    }

    /**
     * Convenience method to create a vertex consumer using this materials render type.
     *
     * @param buffers     bugger source.
     * @param typeBuilder a function that will be used to create the render type if it does not already exist.
     */
    public VertexConsumer buffer(MultiBufferSource buffers, Function<Identifier, RenderType> typeBuilder) {
        return buffers.getBuffer(renderType(typeBuilder));
    }

    public net.minecraft.client.resources.model.Material getVanillaMat() {
        if (vanillaMat == null) {
            vanillaMat = new net.minecraft.client.resources.model.Material(atlasLocation, texture);
        }
        return vanillaMat;
    }


    private static TextureAtlasSprite getAtlasSprite(Identifier atlas, Identifier sprite) {
        return Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(atlas).getSprite(sprite);
    }

    /**
     * Convenient method for getting a material from a vanilla texture atlas.
     *
     * @return an un-cached material from a vanilla atlas.
     */
    public static Material fromAtlas(Identifier atlasLocation, String texture) {
        return new Material(atlasLocation, Identifier.fromNamespaceAndPath(atlasLocation.getNamespace(), texture), e -> getAtlasSprite(atlasLocation, e));
    }

    /**
     * Create a material from an existing sprite.
     * Note: This will only work with sprites from a vanilla atlas.
     */
    @Deprecated //this may be broken now, try to avoid
    @Nullable
    public static Material fromSprite(@Nullable TextureAtlasSprite sprite) {
        if (sprite == null) return null;
        return new Material(sprite.atlasLocation(), sprite.contents().name(), e -> getAtlasSprite(sprite.atlasLocation(), e));
    }

    public static Material fromRawTexture(Identifier texture) {
        return new Material(texture, texture, FullSprite::new);
    }

    private static class FullSprite extends TextureAtlasSprite {
        private FullSprite(Identifier location) {
            super(location, new SpriteContents(location, new FrameSize(1, 1), new NativeImage(1, 1, false)), 1, 1, 0, 0, 0);
        }

        @Override
        public float getU(float u)
        {
            return u / 16;
        }

        @Override
        public float getV(float v)
        {
            return v / 16;
        }
    }
}
