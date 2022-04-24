package net.creeperhost.polylib.events;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.world.Difficulty;

public interface DifficultyChangedEvent
{
    Event<DifficultyChangedEvent.DifficultyChanged> DIFFICULTY_CHANGED = EventFactory.createEventResult();

    interface DifficultyChanged
    {
        void onDifficultyChanged(Difficulty difficulty, Difficulty oldDifficulty);
    }
}
