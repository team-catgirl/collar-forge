package team.catgirl.plastic.world;

import team.catgirl.plastic.player.Player;

import java.util.List;

public interface World {
    Player currentPlayer();
    List<Player> allPlayers();
}
