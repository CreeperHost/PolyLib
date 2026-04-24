package net.creeperhost.polylib.event.data;

/**
 * Passed to cancellable PolyEvent handlers. Handlers call {@link #cancel()} to stop further propagation.
 * The combiner for cancellable events must check {@link #isCancelled()} after each handler and short-circuit.
 */
public final class CancelContext
{
    private boolean cancelled = false;

    public void cancel()
    {
        cancelled = true;
    }

    public boolean isCancelled()
    {
        return cancelled;
    }
}
