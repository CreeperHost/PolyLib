package net.creeperhost.polylib.client.modulargui.sprite;

import net.minecraft.ResourceLocationException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.TextureAtlasHolder;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.stream.Stream;

/**
 * Created by brandon3055 on 21/10/2023
 */
public class SpriteUploader extends TextureAtlasHolder {
    private static final Logger LOGGER = LogManager.getLogger();
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
        try {
            return mc.getResourceManager().listResources(textureSource.getPath(), s -> s.endsWith(".png") && ResourceLocation.isValidResourceLocation(s))
                    .stream()
                    .filter(e -> e.getNamespace().equals(textureSource.getNamespace()))
                    .map(e -> new ResourceLocation(e.getNamespace(), e.getPath().replace(".png", "").replace("textures/" + prefix + "/", "")));
        } catch (ResourceLocationException e) {
            LOGGER.error("An error occurred while attempting to find mod resources.");
            LOGGER.error("This may be caused by a mod including an invalid file in their resources.", e);
            return Stream.empty();
        }
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
