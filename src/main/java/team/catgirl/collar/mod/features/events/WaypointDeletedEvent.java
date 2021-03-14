package team.catgirl.collar.mod.features.events;

import team.catgirl.event.Event;
import team.catgirl.plastic.world.Dimension;
import team.catgirl.plastic.world.Position;

public final class WaypointDeletedEvent extends Event {
    public final String name;
    public final Position position;
    public final Dimension dimension;

    public WaypointDeletedEvent(String name, Position position, Dimension dimension) {
        this.name = name;
        this.position = position;
        this.dimension = dimension;
    }
}
