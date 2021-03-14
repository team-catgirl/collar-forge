package team.catgirl.plastic.player;

import team.catgirl.plastic.world.Position;
import team.catgirl.plastic.world.Dimension;

import java.util.UUID;

public interface Player {
    /**
     * @return unique ID of player
     */
    UUID id();

    /**
     * @return network id of player
     */
    int networkId();

    /**
     * @return name of player
     */
    String name();

    /**
     * @return current position
     */
    Position position();

    /**
     * @return current dimension
     */
    Dimension dimension();
}
