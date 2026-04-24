package net.creeperhost.testmod.items;

import net.creeperhost.polylib.inventory.power.IPolyEnergyStorage;
import net.creeperhost.polylib.inventory.power.IPolyEnergyStorageItem;
import net.creeperhost.polylib.inventory.power.PolyEnergyItem;
import net.creeperhost.polylib.inventory.power.PolyItemEnergyStorage;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public class ItemPowered extends Item implements PolyEnergyItem {

    public ItemPowered(Properties properties) {
        super(properties);
    }

    @Override
    public IPolyEnergyStorageItem getEnergyStorage(ItemStack stack) {
        return new PolyItemEnergyStorage(stack, 32000, 64);
    }

    @Override
    public boolean isBarVisible(ItemStack itemStack) {
        return true;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) return super.use(level, player, hand);
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() instanceof ItemPowered) {
            IPolyEnergyStorage energy = getEnergyStorage(stack);
            if (player.isShiftKeyDown()) {
                energy.receiveEnergy(1000, false);
                return  InteractionResult.SUCCESS;
            } else {
                energy.extractEnergy(1000, false);
                return InteractionResult.SUCCESS;
            }
        }
        return super.use(level, player, hand);
    }

    @Override
    public int getBarWidth(ItemStack itemStack) {
        IPolyEnergyStorage energy = getEnergyStorage(itemStack);
        float charge = energy.getEnergyStored() / (float) energy.getMaxEnergyStored();
        return Math.round(13.0F * charge);
    }

    @Override
    public int getBarColor(ItemStack itemStack) {
        IPolyEnergyStorage energy = getEnergyStorage(itemStack);
        float charge = energy.getEnergyStored() / (float) energy.getMaxEnergyStored();
        return Mth.hsvToRgb(charge / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
        IPolyEnergyStorage energy = getEnergyStorage(itemStack);
        consumer.accept(Component.literal("Energy: " + energy.getEnergyStored() + " / " + energy.getMaxEnergyStored()));
    }
}
