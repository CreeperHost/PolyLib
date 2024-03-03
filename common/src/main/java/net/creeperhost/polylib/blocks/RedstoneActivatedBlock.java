package net.creeperhost.polylib.blocks;

import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.function.Predicate;

/**
 * All functionality for this interface is already built into {@link PolyBlockEntity}
 * This means all you have to do is implement this on a {@link BlockEntity} that extends PolyBlockEntity.
 * you can then use {@link PolyBlockEntity#isTileEnabled()} to check if the tile is currently allowed to run.
 * <p>
 * Created by brandon3055 on 19/02/2024
 */
public interface RedstoneActivatedBlock extends ChangeListener {

    RSMode getRSMode();

    void setRSMode(RSMode mode);

    enum RSMode {
        ALWAYS_ACTIVE(0, signal -> true),
        ACTIVE_HIGH(1, signal -> signal),
        ACTIVE_LOW(2, signal -> !signal),
        NEVER_ACTIVE(3, signal -> false);

        public int index;
        private Predicate<Boolean> canRun;

        RSMode(int index, Predicate<Boolean> canRun) {
            this.index = index;
            this.canRun = canRun;
        }

        public RSMode next(boolean prev) {
            if (prev) {
                return values()[index - 1 < 0 ? values().length - 1 : index - 1];
            }
            return values()[index + 1 == values().length ? 0 : index + 1];
        }

        public boolean canRun(boolean signal) {
            return canRun.test(signal);
        }
    }
}
