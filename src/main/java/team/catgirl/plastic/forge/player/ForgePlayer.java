package team.catgirl.plastic.forge.player;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import team.catgirl.plastic.world.Dimension;
import team.catgirl.plastic.player.Player;
import team.catgirl.plastic.world.Position;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class ForgePlayer implements Player {

    private final static Cache<String, Optional<BufferedImage>> AVATAR_CACHE = CacheBuilder.newBuilder()
            .expireAfterAccess(60, TimeUnit.SECONDS)
            .initialCapacity(50)
            .build();

    public final UUID id;
    public final EntityPlayer player;

    public ForgePlayer(EntityPlayer player) {
        this.id = player.getUniqueID();
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

    @Override
    public Optional<BufferedImage> avatar() {
        try {
            return AVATAR_CACHE.get(name(), () -> {
                EntityPlayer playerEntityByName = Minecraft.getMinecraft().world.getPlayerEntityByName(player.getName());
                if (playerEntityByName == null) {
                    return Optional.empty();
                }
                EntityOtherPlayerMP playerMP = (EntityOtherPlayerMP) playerEntityByName;
                ResourceLocation locationSkin = playerMP.getLocationSkin();
                try {
                    IResource resource = Minecraft.getMinecraft().getResourceManager().getResource(locationSkin);
                    try (InputStream stream = resource.getInputStream()) {
                        BufferedImage bufferedImage = TextureUtil.readBufferedImage(stream);
                        return Optional.of(bufferedImage.getSubimage(8, 8, 15, 15));
                    }
                } catch (IOException e) {
                    return Optional.empty();
                }
            });
        } catch (ExecutionException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ForgePlayer that = (ForgePlayer) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
