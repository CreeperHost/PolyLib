package net.creeperhost.polylib.client.render.fluid;

import dev.architectury.fluid.FluidStack;
import dev.architectury.hooks.fluid.FluidStackHooks;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.creeperhost.polylib.client.model.Model3D;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

public class FluidRenderHelper
{
    public static final FluidRenderMap<Int2ObjectMap<Model3D>> CACHED_FLUIDS = new FluidRenderMap<>();
    public static final int STAGES = 1400;

    public static TextureAtlasSprite getSprite(ResourceLocation spriteLocation)
    {
        return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(spriteLocation);
    }

    public static Model3D getFluidModel(FluidStack fluid, int stage)
    {
        if (CACHED_FLUIDS.containsKey(fluid) && CACHED_FLUIDS.get(fluid).containsKey(stage))
        {
            return CACHED_FLUIDS.get(fluid).get(stage);
        }
        Model3D model = new Model3D();
        model.setTexture(FluidRenderMap.getFluidTexture(fluid, FluidRenderMap.FluidType.STILL));

        if (FluidStackHooks.getStillTexture(fluid.getFluid()) != null)
        {
            model.minX = 0.135F;
            model.minY = 0.0725F;
            model.minZ = 0.135F;

            model.maxX = 0.865F;
            model.maxY = 0.0525F + 0.875F * (stage / (float) 1_400);
            model.maxZ = 0.865F;
        }
        CACHED_FLUIDS.computeIfAbsent(fluid, f -> new Int2ObjectOpenHashMap<>()).put(stage, model);
        return model;
    }

    //TODO once we add some sort of fluid tank to PolyLib
//    public static float getScale(FluidTank tank)
//    {
//        return getScale(tank.getFluidAmount(), tank.getCapacity(), tank.isEmpty());
//    }

    public static float getScale(int stored, int capacity, boolean empty)
    {
        return (float) stored / capacity;
    }

//    public static IFluidHandler getTank(Level world, BlockPos pos, Direction side)
//    {
//        BlockEntity tile = world.getBlockEntity(pos);
//        if (tile == null)
//        {
//            return null;
//        }
//        return tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).orElse(null);
//    }
}
