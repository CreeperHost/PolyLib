package net.creeperhost.polylib.mulitblock;

/**
 * Thrown when a multiblock controller determines that its connected parts do not
 * form a valid assembled machine.
 */
public class MultiblockValidationException extends Exception
{
    private static final long serialVersionUID = -4038176177468678877L;

    /**
     * Creates a validation exception with a human-readable failure reason.
     *
     * @param reason the validation failure reason
     */
    public MultiblockValidationException(String reason)
    {
        super(reason);
    }
}
