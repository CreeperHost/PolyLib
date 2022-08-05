package net.creeperhost.polylib.generators;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;

public interface PolyDataGeneratorRegistryEvent
{
    Event<PolyDataGeneratorRegistryEvent.Register> REGISTER_EVENT = EventFactory.createEventResult();

    interface Register
    {
        void onRegister(PolyDataGenerator polyDataGenerator);
    }
}
