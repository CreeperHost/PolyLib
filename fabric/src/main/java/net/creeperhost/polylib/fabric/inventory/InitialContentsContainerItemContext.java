package net.creeperhost.polylib.fabric.inventory;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.List;

public class InitialContentsContainerItemContext implements ContainerItemContext {
	private final SingleVariantStorage<ItemVariant> backingSlot = new SingleVariantStorage<>() {
		@Override
		protected ItemVariant getBlankVariant() {
			return ItemVariant.blank();
		}

		@Override
		protected long getCapacity(ItemVariant variant) {
			return Long.MAX_VALUE;
		}
	};

	public InitialContentsContainerItemContext(ItemVariant initialVariant, long initialAmount) {
		backingSlot.variant = initialVariant;
		backingSlot.amount = initialAmount;
	}

	public InitialContentsContainerItemContext(ItemStack stack) {
		this(ItemVariant.of(stack), stack.getCount());
	}

	@Override
	public SingleSlotStorage<ItemVariant> getMainSlot() {
		return backingSlot;
	}

	@Override
	public long insertOverflow(ItemVariant itemVariant, long maxAmount, TransactionContext transactionContext) {
		StoragePreconditions.notBlankNotNegative(itemVariant, maxAmount);
		// Always allow anything to be inserted.
		return maxAmount;
	}

	@Override
	public List<SingleSlotStorage<ItemVariant>> getAdditionalSlots() {
		return Collections.emptyList();
	}
}