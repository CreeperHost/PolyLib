package net.creeperhost.polylib;

import dev.architectury.platform.Platform;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.creeperhost.polylib.fluids.PolyFluid;
import net.minecraft.core.Registry;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

public class PolyLib
{
    public static final String MOD_ID = "polylib";

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(MOD_ID, Registry.BLOCK_REGISTRY);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(MOD_ID, Registry.FLUID_REGISTRY);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(MOD_ID, Registry.ITEM_REGISTRY);

    public static final RegistrySupplier<Fluid> TEST_FLUID = FLUIDS.register("test", PolyFluid::new);

    public static final RegistrySupplier<FlowingFluid> TEST_FLUID_FLOWING = FLUIDS.register("test_flowing", PolyFluid.Flowing::new);
    public static final Material material = (new Material.Builder(MaterialColor.DIRT)).noCollider().nonSolid().replaceable().liquid().build();
    public static final RegistrySupplier<Block> FLUID_BLOCK = BLOCKS.register("test", () -> new LiquidBlock(TEST_FLUID_FLOWING.get(), BlockBehaviour.Properties.of(material)));

    public static final RegistrySupplier<Item> FLUID_BLOCK_ITEM = ITEMS.register("test", () -> new ItemNameBlockItem(FLUID_BLOCK.get(), new Item.Properties().tab(CreativeModeTab.TAB_MISC)));

    public static void init()
    {
        if(Platform.isDevelopmentEnvironment())
        {
            FLUIDS.register();
            BLOCKS.register();
            ITEMS.register();
        }
    }
}
