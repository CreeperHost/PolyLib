package net.creeperhost.testmod.datagen;

import net.creeperhost.polylib.datagen.FabricPolyLibLangProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class TestModFabricLangProvider extends FabricPolyLibLangProvider
{
    public TestModFabricLangProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries)
    {
        super(output, registries);
    }

    @Override
    protected void addModTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder builder)
    {
        // All items, blocks, and creative tabs are contributed automatically.
        // Add any manual keys here (e.g. tooltips, chat messages).
    }
}
