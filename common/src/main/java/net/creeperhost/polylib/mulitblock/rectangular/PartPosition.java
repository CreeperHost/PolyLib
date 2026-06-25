package net.creeperhost.polylib.mulitblock.rectangular;

/**
 * Position classification for a part inside a rectangular multiblock's bounding box.
 */
public enum PartPosition
{
    Unknown, Interior, FrameCorner, Frame, TopFace, BottomFace, NorthFace, SouthFace, EastFace, WestFace;

    /**
     * @param position the position to test
     * @return true when {@code position} represents one of the six outer faces
     */
    public boolean isFace(PartPosition position)
    {
        switch (position)
        {
            case TopFace:
            case BottomFace:
            case NorthFace:
            case SouthFace:
            case EastFace:
            case WestFace:
                return true;
            default:
                return false;
        }
    }
}
