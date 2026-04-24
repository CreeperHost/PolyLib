package net.creeperhost.polylib.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public final class PolyEvent<T>
{

    private final List<T> handlers = new ArrayList<>();
    private final Function<List<T>, T> combiner;
    private T invoker;

    private PolyEvent(Function<List<T>, T> combiner)
    {
        this.combiner = combiner;
        this.invoker = combiner.apply(Collections.emptyList());
    }

    public static <T> PolyEvent<T> create(Function<List<T>, T> combiner)
    {
        return new PolyEvent<>(combiner);
    }

    public void register(T handler)
    {
        handlers.add(handler);
        invoker = combiner.apply(Collections.unmodifiableList(handlers));
    }

    public T invoker()
    {
        return invoker;
    }
}
