package team.catgirl.collar.mod.plastic;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import scala.tools.cmd.Opt;
import team.catgirl.collar.client.Collar;
import team.catgirl.collar.client.api.textures.Texture;
import team.catgirl.collar.mod.service.events.CollarConnectedEvent;
import team.catgirl.collar.mod.service.events.CollarDisconnectedEvent;
import team.catgirl.event.Subscribe;
import team.catgirl.plastic.player.Player;
import team.catgirl.plastic.ui.TextureProvider;
import team.catgirl.plastic.ui.TextureType;

import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CollarTextureProvider implements TextureProvider {

    private static final Logger LOGGER = Logger.getLogger(CollarTextureProvider.class.getName());

    private static final Cache<TextureKey, CompletableFuture<Optional<BufferedImage>>> TEXTURE_CACHE = CacheBuilder.newBuilder()
            .expireAfterAccess(5, TimeUnit.SECONDS)
            .initialCapacity(100)
            .build();

    private Collar collar;

    @Override
    public CompletableFuture<Optional<BufferedImage>> getTexture(Player player, TextureType type) {
        if (collar == null || !collar.getState().equals(Collar.State.CONNECTED)) {
            return CompletableFuture.completedFuture(Optional.empty());
        }
        try {
            return TEXTURE_CACHE.get(new TextureKey(player.id(), type), () -> loadTexture(player, type));
        } catch (ExecutionException e) {
            LOGGER.log(Level.SEVERE, "Could not load collar texture", e);
            return CompletableFuture.completedFuture(Optional.empty());
        }
    }

    private CompletableFuture<Optional<BufferedImage>> loadTexture(Player player, TextureType type) {
        System.out.println(player.id());
        team.catgirl.collar.api.textures.TextureType textureType;
        switch (type) {
            case CAPE:
                textureType = team.catgirl.collar.api.textures.TextureType.CAPE;
                break;
            case AVATAR:
                textureType = team.catgirl.collar.api.textures.TextureType.AVATAR;
                break;
            default:
                throw new IllegalStateException("unknown type " + type);
        }
        return collar.identities().resolvePlayer(player.id())
                .thenComposeAsync(thePlayer -> {
                    if (thePlayer.isPresent()) {
                        return collar.textures().playerTextureFuture(thePlayer.get(), textureType);
                    }
                    return CompletableFuture.completedFuture(Optional.empty());
                })
                .thenComposeAsync(texture -> {
                    if (!texture.isPresent()) {
                        LOGGER.log(Level.SEVERE, "Could not find collar texture for player " + player.name() + " type " + textureType);
                        return CompletableFuture.completedFuture(Optional.empty());
                    }
                    CompletableFuture<Optional<BufferedImage>> imageFuture = new CompletableFuture<>();
                    texture.ifPresent(c -> c.loadImage(imageFuture::complete));
                    return imageFuture;
                });
    }

    @Subscribe
    public void onConnected(CollarConnectedEvent event) {
        collar = event.collar;
        TEXTURE_CACHE.invalidateAll();
    }

    @Subscribe
    public void onDisconnect(CollarDisconnectedEvent event) {
        collar = null;
        TEXTURE_CACHE.invalidateAll();
    }

    private static final class TextureKey {
        public final UUID id;
        public final TextureType type;

        public TextureKey(UUID id, TextureType type) {
            this.id = id;
            this.type = type;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TextureKey that = (TextureKey) o;
            return id.equals(that.id) && type == that.type;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, type);
        }
    }
}
