package net.creeperhost.polylib.helpers;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.HashSet;
import java.util.Set;

public class VoxelShapeHelper
{
    public static VoxelShape rotateY(VoxelShape shape, int rotation) {
        Set<VoxelShape> rotatedShapes = new HashSet<>();

        shape.forAllBoxes((x1, y1, z1, x2, y2, z2) -> {
            x1 = (x1 * 16) - 8; x2 = (x2 * 16) - 8;
            z1 = (z1 * 16) - 8; z2 = (z2 * 16) - 8;

            switch (rotation) {
                case 90 -> rotatedShapes.add(boxSafe(8 - z1, y1 * 16, 8 + x1, 8 - z2, y2 * 16, 8 + x2));
                case 180 -> rotatedShapes.add(boxSafe(8 - x1, y1 * 16, 8 - z1, 8 - x2, y2 * 16, 8 - z2));
                case 270 -> rotatedShapes.add(boxSafe(8 + z1, y1 * 16, 8 - x1, 8 + z2, y2 * 16, 8 - x2));
                default -> throw new IllegalArgumentException("invalid rotation " + rotation + " (must be 90,180 or 270)");
            }
        });

        return rotatedShapes.stream().reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).orElse(shape);
    }

    public static VoxelShape rotateX(VoxelShape shape, int rotation) {
        Set<VoxelShape> rotatedShapes = new HashSet<>();

        shape.forAllBoxes((x1, y1, z1, x2, y2, z2) -> {
            y1 = (y1 * 16) - 8; y2 = (y2 * 16) - 8;
            z1 = (z1 * 16) - 8; z2 = (z2 * 16) - 8;

            switch (rotation) {
                case 90 -> rotatedShapes.add(boxSafe(x1 * 16, 8 - z1, 8 + y1, x2 * 16, 8 - z2, 8 + y2));
                case 180 -> rotatedShapes.add(boxSafe(x1 * 16, 8 - z1, 8 - y1, x2 * 16, 8 - z2, 8 - y2));
                case 270 -> rotatedShapes.add(boxSafe(x1 * 16, 8 + z1, 8 - y1, x2 * 16, 8 + z2, 8 - y2));
                default -> throw new IllegalArgumentException("invalid rotation " + rotation + " (must be 90,180 or 270)");
            }
        });

        return rotatedShapes.stream().reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).orElse(shape);
    }

    public static VoxelShape combine(BooleanOp func, VoxelShape... shapes) {
        VoxelShape result = Shapes.empty();
        for (VoxelShape shape : shapes) {
            result = Shapes.joinUnoptimized(result, shape, func);
        }
        return result.optimize();
    }

    private static VoxelShape boxSafe(double pMinX, double pMinY, double pMinZ, double pMaxX, double pMaxY, double pMaxZ) {
        // MC 1.17+ is picky about min/max order, unlike 1.16 and earlier...
        double x1 = Math.min(pMinX, pMaxX);
        double x2 = Math.max(pMinX, pMaxX);
        double y1 = Math.min(pMinY, pMaxY);
        double y2 = Math.max(pMinY, pMaxY);
        double z1 = Math.min(pMinZ, pMaxZ);
        double z2 = Math.max(pMinZ, pMaxZ);
        return Block.box(x1, y1, z1, x2, y2, z2);
    }
}
