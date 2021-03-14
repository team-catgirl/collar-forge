package team.catgirl.plastic.forge.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import team.catgirl.plastic.world.Dimension;
import team.catgirl.plastic.player.Player;
import team.catgirl.plastic.world.Position;

import java.util.UUID;

public class ForgePlayer implements Player {
    public final EntityPlayer player;

    public ForgePlayer(EntityPlayer player) {
        this.player = player;
    }

    @Override
    public UUID id() {
        return player.getGameProfile().getId();
    }

    @Override
    public int networkId() {
        return player.getEntityId();
    }

    @Override
    public String name() {
        return player.getName();
    }

    @Override
    public Position position() {
        BlockPos pos = player.getPosition();
        return new Position(pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public Dimension dimension() {
        switch (DimensionType.getById(player.dimension)) {
            case NETHER:
                return Dimension.NETHER;
            case OVERWORLD:
                return Dimension.OVERWORLD;
            case THE_END:
                return Dimension.END;
            default:
                return Dimension.UNKNOWN;
        }
    }
}
