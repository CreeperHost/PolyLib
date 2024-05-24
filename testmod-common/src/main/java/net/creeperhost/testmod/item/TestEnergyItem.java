package net.creeperhost.testmod.item;

import net.creeperhost.polylib.inventory.power.IPolyEnergyStorage;
import net.creeperhost.polylib.inventory.power.PolyEnergyItem;
import net.creeperhost.polylib.inventory.power.PolyItemEnergyStorage;
import net.creeperhost.testmod.init.TestItems;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by brandon3055 on 03/03/2024
 */
public class TestEnergyItem extends Item implements PolyEnergyItem {

    public TestEnergyItem(Properties properties) {
        super(properties);
    }

    //TODO Add a base "PolyEnergyItem" class that handles all this common energy stuff.

    @Override
    public void fillItemCategory(CreativeModeTab creativeModeTab, NonNullList<ItemStack> nonNullList) {
        if (this.allowedIn(creativeModeTab)) {
            nonNullList.add(new ItemStack(this));
            ItemStack chargedItem = new ItemStack(TestItems.TEST_ENERGY_ITEM.get());
            IPolyEnergyStorage energy = ((TestEnergyItem)chargedItem.getItem()).getEnergyStorage(chargedItem);
            energy.modifyEnergyStored(energy.getMaxEnergyStored());
            nonNullList.add(chargedItem);
        }
    }

    @Override
    public IPolyEnergyStorage getEnergyStorage(ItemStack stack) {
        return new PolyItemEnergyStorage(stack, 32000, 64);
    }

    @Override
    public boolean isBarVisible(ItemStack itemStack) {
        return true;
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
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        IPolyEnergyStorage energy = getEnergyStorage(itemStack);

        list.add(Component.literal("Energy"));
        list.add(Component.literal(energy.getEnergyStored() + " / " + energy.getMaxEnergyStored()));

    }
}
