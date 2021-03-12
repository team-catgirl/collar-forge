package team.catgirl.collar.mod.plastic.player;

import team.catgirl.collar.mod.plastic.world.Position;
import team.catgirl.collar.mod.plastic.world.Dimension;

import java.util.UUID;

public interface Player {
    UUID id();
    int networkId();
    String name();
    Position position();
    Dimension dimension();
}
