package team.catgirl.collar.mod.plastic;

import team.catgirl.collar.client.Collar;
import team.catgirl.collar.mod.service.events.CollarConnectedEvent;
import team.catgirl.event.Subscribe;
import team.catgirl.plastic.player.Player;
import team.catgirl.plastic.ui.TextureProvider;
import team.catgirl.plastic.ui.TextureType;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class CollarTextureProvider implements TextureProvider {

    private Collar collar;

    @Override
    public Optional<BufferedImage> getTexture(Player player, TextureType type) {
        BufferedImage image;
        try {
            image = ImageIO.read(new File("src/main/resources/example-cape.png"));
        } catch (IOException e) {
            return Optional.empty();
        }
        return Optional.of(image);
    }

    @Subscribe
    public void onConnected(CollarConnectedEvent event) {
        collar = event.collar;
    }
}
