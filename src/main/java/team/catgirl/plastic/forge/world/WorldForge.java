package team.catgirl.plastic.forge.world;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import team.catgirl.plastic.player.Player;
import team.catgirl.plastic.world.World;
import team.catgirl.plastic.forge.player.PlayerForge;

import java.util.List;
import java.util.stream.Collectors;

public class WorldForge implements World {

    private net.minecraft.world.World world;

    public WorldForge() {
        this.world = Minecraft.getMinecraft().world;
    }

    @Override
    public Player currentPlayer() {
        EntityPlayer entityPlayer = Minecraft.getMinecraft().world.playerEntities.stream()
                .filter(player -> player.getEntityId() == Minecraft.getMinecraft().player.getEntityId())
                .findFirst().orElseThrow(() -> new IllegalStateException("could not find current player"));
        return new PlayerForge(entityPlayer);
    }

    @Override
    public List<Player> allPlayers() {
        return Minecraft.getMinecraft().world.playerEntities.stream()
                .map(PlayerForge::new)
                .collect(Collectors.toList());
    }
}
