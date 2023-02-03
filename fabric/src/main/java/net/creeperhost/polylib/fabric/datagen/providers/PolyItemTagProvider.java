package net.creeperhost.polylib.fabric.datagen.providers;

import com.google.common.collect.Maps;
import net.creeperhost.polylib.fabric.datagen.ModuleType;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.data.CachedOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Map;

public class PolyItemTagProvider extends FabricTagProvider.ItemTagProvider
{
    private final ModuleType moduleType;
    private final Path basePath;
    private final Map<Item, TagKey<Item>> values = Maps.newLinkedHashMap();

    public PolyItemTagProvider(FabricDataGenerator dataGenerator, @Nullable BlockTagProvider blockTagProvider, ModuleType moduleType)
    {
        super(dataGenerator, blockTagProvider);
        this.moduleType = moduleType;
        basePath = Path.of("").toAbsolutePath().getParent().getParent();
    }

    @Override
    protected void generateTags()
    {
        values.forEach((item, tagKey) -> tag(tagKey).add(item));
    }

    public void add(TagKey<Item> tagKey, Item block, ModuleType moduleType)
    {
        if(this.moduleType == moduleType)
        {
            values.put(block, tagKey);
        }
    }

    @Override
    public void run(CachedOutput cachedOutput)
    {
        //If values is empty don't generate an empty array json file
        if(values.isEmpty()) return;
        pathProvider.root = appendPath(moduleType);
        super.run(cachedOutput);
    }

    public Path appendPath(ModuleType moduleType)
    {
        return basePath.resolve(moduleType.name().toLowerCase() + "/src/generated/resources/data");
    }
}
