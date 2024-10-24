package net.creeperhost.polylib.client.modulargui.sprite;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.profiling.Zone;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Created by brandon3055 on 20/08/2023
 */
public class ModAtlasHolder implements PreparableReloadListener, AutoCloseable {
    private final TextureAtlas textureAtlas;
    private final ResourceLocation atlasLocation;
    private final ResourceLocation atlasInfoLocation;
    private final String modid;

    /**
     * Defines a mod texture atlas.
     * Must be registered as a resource reload listener via RegisterClientReloadListenersEvent
     * This is all that is needed to create a custom texture atlas.
     *
     * @param modid             The mod id of the mod registering this atlas.
     * @param atlasLocation     The texture atlas location. e.g. "textures/atlas/gui.png" (Will have the modid: prefix added automatically)
     * @param atlasInfoLocation The path to the atlas json file relative to modid:atlases/
     *                          e.g. "gui" will point to modid:atlases/gui.json
     */
    public ModAtlasHolder(String modid, String atlasLocation, String atlasInfoLocation) {
        this.atlasInfoLocation = ResourceLocation.fromNamespaceAndPath(modid, atlasInfoLocation);
        this.atlasLocation = ResourceLocation.fromNamespaceAndPath(modid, atlasLocation);
        this.textureAtlas = new TextureAtlas(this.atlasLocation);
        this.modid = modid;
        Minecraft.getInstance().getTextureManager().register(this.textureAtlas.location(), this.textureAtlas);
    }

    public ResourceLocation atlasLocation() {
        return atlasLocation;
    }

    public TextureAtlasSprite getSprite(ResourceLocation resourceLocation) {
        return this.textureAtlas.getSprite(resourceLocation);
    }

    @Override
    public final CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier preparationBarrier, ResourceManager resourceManager, Executor executor, Executor executor2) {
        CompletableFuture<SpriteLoader.Preparations> future = ModSpriteLoader.create(this.textureAtlas, modid)
                .loadAndStitch(resourceManager, this.atlasInfoLocation, 0, executor, SpriteLoader.DEFAULT_METADATA_SECTIONS)
                .thenCompose(SpriteLoader.Preparations::waitForUpload);

        Objects.requireNonNull(preparationBarrier);
        return future.thenCompose(preparationBarrier::wait)
                .thenAcceptAsync(this::apply, executor2);
    }

    private void apply(SpriteLoader.Preparations preparations) {
        Zone zone = Profiler.get().zone("upload");

        try {
            this.textureAtlas.upload(preparations);
        } catch (Throwable var6) {
            if (zone != null) {
                try {
                    zone.close();
                } catch (Throwable var5) {
                    var6.addSuppressed(var5);
                }
            }

            throw var6;
        }

        if (zone != null) {
            zone.close();
        }

    }

    @Override
    public void close() {
        this.textureAtlas.clearTextureData();
    }
}
