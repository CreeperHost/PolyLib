package net.creeperhost.polylib.client.modulargui.sprite;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
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
    private final ResourceLocation atlasLocation;
    private final ResourceLocation texture;
    private final Function<ResourceLocation, TextureAtlasSprite> spriteFunction;

    @Nullable
    private RenderType renderType;
    @Nullable
    private net.minecraft.client.resources.model.Material vanillaMat;

    public Material(ResourceLocation atlasLocation, ResourceLocation texture, Function<ResourceLocation, TextureAtlasSprite> spriteFunction) {
        this.atlasLocation = atlasLocation;
        this.texture = texture;
        this.spriteFunction = spriteFunction;
    }

    public ResourceLocation atlasLocation() {
        return atlasLocation;
    }

    public ResourceLocation texture() {
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
    public RenderType renderType(Function<ResourceLocation, RenderType> typeBuilder) {
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
    public VertexConsumer buffer(MultiBufferSource buffers, Function<ResourceLocation, RenderType> typeBuilder) {
        return buffers.getBuffer(renderType(typeBuilder));
    }

    public net.minecraft.client.resources.model.Material getVanillaMat() {
        if (vanillaMat == null) {
            vanillaMat = new net.minecraft.client.resources.model.Material(atlasLocation, texture);
        }
        return vanillaMat;
    }
}
