package team.catgirl.collar.mod.features.events;

import team.catgirl.event.Event;
import team.catgirl.plastic.world.Dimension;
import team.catgirl.plastic.world.Position;

public final class WaypointCreatedEvent extends Event {
    public final String name;
    public final Position position;
    public final Dimension dimension;

    public WaypointCreatedEvent(String name, Position position, Dimension dimension) {
        this.name = name;
        this.position = position;
        this.dimension = dimension;
    }
}
