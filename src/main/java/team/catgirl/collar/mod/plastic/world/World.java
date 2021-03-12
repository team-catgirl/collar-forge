package team.catgirl.collar.mod.plastic.world;

import team.catgirl.collar.mod.plastic.player.Player;

import java.util.List;

public interface World {
    Player currentPlayer();
    List<Player> allPlayers();
}
