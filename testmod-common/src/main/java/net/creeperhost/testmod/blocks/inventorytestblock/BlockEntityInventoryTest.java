package net.creeperhost.testmod.blocks.inventorytestblock;

import net.creeperhost.polylib.blocks.PolyBlockEntity;
import net.creeperhost.polylib.data.serializable.StringData;
import net.creeperhost.polylib.data.serializable.UUIDData;
import net.creeperhost.testmod.init.TestBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

public class BlockEntityInventoryTest extends PolyBlockEntity implements MenuProvider
{
    /**
     * A user-editable label stored in NBT and synced to clients.
     * Tests StringData persistence + CLIENT_CONTROL round-trip.
     */
    public final StringData testLabel;

    /**
     * UUID of the last player who opened this container.
     * Set server-side in createMenu; tests UUIDData NBT persistence + sync.
     */
    public final UUIDData lastVisitorUUID;

    public BlockEntityInventoryTest(BlockPos pos, BlockState state)
    {
        super(TestBlocks.TEST_BLOCK_ENTITY.get(), pos, state);
        testLabel = register("test_label", new StringData(""), SAVE_BOTH, SYNC, CLIENT_CONTROL);
        lastVisitorUUID = register("last_visitor_uuid", new UUIDData(), SAVE_BOTH, SYNC);
    }

    @Override
    public @NotNull Component getDisplayName()
    {
        return Component.literal("Inventory Test");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player)
    {
        lastVisitorUUID.set(player.getUUID());
        return new ContainerInventoryTest(i, inventory, this);
    }
}
