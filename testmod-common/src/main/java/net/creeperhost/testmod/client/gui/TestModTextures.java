package net.creeperhost.testmod.client.gui;

import net.creeperhost.polylib.client.modulargui.sprite.Material;
import net.creeperhost.polylib.client.modulargui.sprite.SpriteUploader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static net.creeperhost.testmod.TestMod.MOD_ID;

/**
 * This class contains Polylib's gui texture atlas and methods for retrieving gui materials.
 * <p>
 * Mods wishing to use their own textures in guis can pretty much just copy-paste this class.
 * Just make sure MOD_ID points to your mod's id.
 * <p>
 * Don't forget to register the atlas holder as a resource reload listener.
 * polylib does this in PolyLibFabric (fabric) and ForgeClientEvents (forge)
 * <p>
 * Then the last thing you need is an atlas json identical to polylib's in "assets/[mod-id]/atlasses/gui.json"
 *
 * <p>
 * Created by brandon3055 on 21/10/2023
 */
public class TestModTextures {

    public static final ResourceLocation ATLAS_LOCATION = new ResourceLocation(MOD_ID, "textures/atlas/gui.png");

    private static final Map<String, Material> MATERIAL_CACHE = new HashMap<>();
    private static final SpriteUploader SPRITE_UPLOADER = new SpriteUploader(new ResourceLocation(MOD_ID, "textures/gui"), ATLAS_LOCATION, "gui");

    //Must be registered as a resource reload listener!
    public static SpriteUploader getUploader() {
        return SPRITE_UPLOADER;
    }

    public static void register(Map<ResourceLocation, Consumer<TextureAtlasSprite>> register, String location) {
        register.put(new ResourceLocation(MOD_ID, location), null);
    }

    public static Material get(String location) {
        return MATERIAL_CACHE.computeIfAbsent(MOD_ID + ":" + location, s -> getUncached(location));
    }

    public static Supplier<Material> getter(Supplier<String> texture) {
        return () -> get(texture.get());
    }

    public static Material getUncached(String texture) {
        return new Material(ATLAS_LOCATION, new ResourceLocation(MOD_ID, texture), SPRITE_UPLOADER::getSprite);
    }
}