package net.creeperhost.polylib.client.modulargui.sprite;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.TextureAtlasHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

import java.util.Map;
import java.util.stream.Stream;

/**
 * Created by brandon3055 on 21/10/2023
 */
public class SpriteUploader extends TextureAtlasHolder {
    private final String prefix;
    private final ResourceLocation textureSource;

    public SpriteUploader(ResourceLocation textureSource, ResourceLocation atlasLocation, String prefix) {
        super(Minecraft.getInstance().getTextureManager(), atlasLocation, prefix);
        this.textureSource = textureSource;
        this.prefix = prefix;
    }

    public SpriteUploader(ResourceLocation textureSource, ResourceLocation atlasLocation) {
        this(textureSource, atlasLocation, null);
    }

    @Override
    protected Stream<ResourceLocation> getResourcesToLoad() {
        Minecraft mc = Minecraft.getInstance();
        Map<ResourceLocation, Resource> resources = mc.getResourceManager().listResources(textureSource.getPath(), texture -> texture.getNamespace().equals(textureSource.getNamespace()) && texture.getPath().endsWith(".png"));
        return resources.keySet().stream().map(e -> new ResourceLocation(e.getNamespace(), e.getPath().replace(".png", "").replace("textures/" + prefix + "/", "")));
    }

    @Override
    public ResourceLocation resolveLocation(ResourceLocation location) {
        if (prefix != null) {
            return new ResourceLocation(location.getNamespace(), this.prefix + "/" + location.getPath());
        }
        return location;
    }

    @Override
    public TextureAtlasSprite getSprite(ResourceLocation resourceLocation) {
        return super.getSprite(resourceLocation);
    }
}
