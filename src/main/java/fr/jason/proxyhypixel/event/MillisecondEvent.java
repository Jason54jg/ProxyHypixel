package fr.jason.proxyhypixel.event;

import net.minecraftforge.fml.common.eventhandler.Event;

public class MillisecondEvent extends Event {
    public long timestamp;

    public MillisecondEvent() {
        timestamp = System.currentTimeMillis();
    }
}
