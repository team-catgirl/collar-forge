package team.catgirl.plastic.player;

import team.catgirl.plastic.world.Position;
import team.catgirl.plastic.world.Dimension;

import java.util.UUID;

public interface Player {
    UUID id();
    int networkId();
    String name();
    Position position();
    Dimension dimension();
}
