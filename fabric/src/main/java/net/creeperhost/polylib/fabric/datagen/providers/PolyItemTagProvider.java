package net.creeperhost.polylib.fabric.datagen.providers;

import com.google.common.collect.Maps;
import net.creeperhost.polylib.fabric.datagen.ModuleType;
import net.creeperhost.polylib.fabric.datagen.PolyDataGen;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class PolyItemTagProvider extends FabricTagProvider.ItemTagProvider
{
    private final ModuleType moduleType;
    private final Map<Item, TagKey<Item>> values = Maps.newLinkedHashMap();

    public PolyItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture, @Nullable BlockTagProvider blockTagProvider, ModuleType moduleType)
    {
        super(output, completableFuture, blockTagProvider);
        this.moduleType = moduleType;
    }

    //TODO
//    @Override
//    protected void generateTags()
//    {
//        values.forEach((item, tagKey) ->
//        {
//            tag(tagKey).add(item);
//        });
//    }

    public void add(TagKey<Item> tagKey, Item block, ModuleType moduleType)
    {
        if (this.moduleType == moduleType)
        {
            values.put(block, tagKey);
        }
    }

    @Override
    public CompletableFuture<?> run(@NotNull CachedOutput cachedOutput)
    {
        //If values is empty don't generate an empty array json file
        if (values.isEmpty()) return null;
        pathProvider.root = appendPath(moduleType);
        return super.run(cachedOutput);
    }

    public Path appendPath(ModuleType moduleType)
    {
        return PolyDataGen.getPathFromModuleType(moduleType).resolve("data");
    }

    @Override
    protected void addTags(HolderLookup.Provider arg)
    {

    }
}
