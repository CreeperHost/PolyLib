package net.creeperhost.polylib.client.modulargui.nodegraph;

import java.util.List;

/**
 * Generic helper class for managing block insertion and displacement in a vertical visual scripting environment.
 * Handles calculating the necessary recursive Y-displacement when a block is inserted 
 * between existing blocks in a stack.
 */
public class BlockInsertionHelper {

    /**
     * Interface representing a generic visual script block that has a height.
     */
    public interface IBlockNode {
        double getBlockHeight();
        void setOffsetY(double yOffset);
        double getOffsetY();
    }

    /**
     * Re-calculates the vertical offsets of all blocks in a stack sequentially.
     * Use this after inserting or removing a block to ensure all lower blocks 
     * are pushed down or pulled up correctly.
     * 
     * @param blocks The list of blocks in the stack, ordered from top to bottom.
     * @param startY The initial Y offset for the top block (typically 0 or the header height).
     * @param blockSpacing The gap to leave between each block (typically 0 for perfectly snapped blocks).
     * @return The total height of the stack after arrangement.
     */
    public static double arrangeStackVertical(List<? extends IBlockNode> blocks, double startY, double blockSpacing) {
        double currentY = startY;
        for (IBlockNode block : blocks) {
            block.setOffsetY(currentY);
            currentY += block.getBlockHeight() + blockSpacing;
        }
        return currentY - startY; // Return total computed height
    }

    /**
     * Calculates the recursive Y displacement required to push lower blocks down 
     * when a new block is inserted at the given index.
     *
     * @param blocks The list of blocks in the stack.
     * @param insertIndex The index where the new block will be/has been inserted.
     * @param displacement The height of the inserted block.
     */
    public static void applyDisplacement(List<? extends IBlockNode> blocks, int insertIndex, double displacement) {
        for (int i = insertIndex; i < blocks.size(); i++) {
            IBlockNode block = blocks.get(i);
            block.setOffsetY(block.getOffsetY() + displacement);
        }
    }
}
