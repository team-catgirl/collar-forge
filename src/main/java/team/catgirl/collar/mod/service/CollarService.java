package team.catgirl.collar.mod.service;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.DimensionType;
import org.apache.logging.log4j.Logger;
import scala.tools.cmd.Opt;
import team.catgirl.collar.api.entities.Entity;
import team.catgirl.collar.api.entities.EntityType;
import team.catgirl.collar.api.location.Dimension;
import team.catgirl.collar.api.location.Location;
import team.catgirl.collar.client.Collar;
import team.catgirl.collar.client.CollarConfiguration;
import team.catgirl.collar.client.CollarException;
import team.catgirl.collar.client.CollarListener;
import team.catgirl.collar.client.minecraft.Ticks;
import team.catgirl.collar.client.security.ClientIdentityStore;
import team.catgirl.collar.mod.features.Friends;
import team.catgirl.collar.mod.features.Locations;
import team.catgirl.collar.mod.features.Messaging;
import team.catgirl.collar.mod.features.Textures;
import team.catgirl.collar.mod.plugins.Plugins;
import team.catgirl.collar.security.mojang.MinecraftSession;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class CollarService implements CollarListener {

    private Collar collar;
    private final Ticks ticks;
    private final Plugins plugins;
    private final Logger logger;

    public final Locations locations = new Locations();
    public final Friends friends = new Friends();
    public final Messaging messaging = new Messaging();
    public final Textures textures = new Textures();

    public CollarService(Ticks ticks, Plugins plugins, Logger logger) {
        this.ticks = ticks;
        this.plugins = plugins;
        this.logger = logger;
    }

    public Optional<Collar> getCollar() {
        return collar == null ? Optional.empty() : Optional.of(collar);
    }

    public void start() {
        try {
            collar = createCollar();
        } catch (CollarException|IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void stop() {
        if (collar != null) {
            collar.disconnect();
        }
    }

    @Override
    public void onStateChanged(Collar collar, Collar.State state) {
        switch (state) {
            case CONNECTED:
                collar.location().subscribe(locations);
                collar.friends().subscribe(friends);
                collar.messaging().subscribe(messaging);
                collar.textures().subscribe(textures);
            case DISCONNECTED:
                collar.location().unsubscribe(locations);
                collar.friends().unsubscribe(friends);
                collar.messaging().unsubscribe(messaging);
                collar.textures().unsubscribe(textures);
                break;
        }
        plugins.find().forEach(plugin -> {
            switch (state) {
                case CONNECTING:
                    plugin.onConnecting(collar);
                    displayStatus("Connecting to Collar...");
                    break;
                case CONNECTED:
                    plugin.onConnected(collar);
                    displayStatus("Connected to Collar");
                    break;
                case DISCONNECTED:
                    plugin.onDisconnected(collar);
                    displayStatus("Disconnected from Collar");
                    break;
            }
        });
    }

    @Override
    public void onConfirmDeviceRegistration(Collar collar, String token, String approvalUrl) {
        displayStatus("Collar registration required");
        sendMessage("New Collar installation detected. You can register this installation with your Collar account at " + approvalUrl);
    }

    @Override
    public void onClientUntrusted(Collar collar, ClientIdentityStore store) {
        try {
            store.reset();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void onMinecraftAccountVerificationFailed(Collar collar, MinecraftSession session) {
        displayStatus("There was a problem with using Collar");
        sendMessage("Collar failed to verify your Minecraft account");
    }

    @Override
    public void onPrivateIdentityMismatch(Collar collar, String url) {
        sendMessage("Your private identity did not match. We cannot decrypt your private data. To resolve please visit " + url);
    }

    private static void displayStatus(String message) {
        Minecraft.getMinecraft().player.sendStatusMessage(new TextComponentString(message), true);
    }

    private static void sendMessage(String message) {
        Minecraft.getMinecraft().player.sendStatusMessage(new TextComponentString(message), false);
    }

    private Collar createCollar() throws IOException {
        CollarConfiguration configuration = new CollarConfiguration.Builder()
                .withCollarDevelopmentServer()
                .withListener(this)
                .withTicks(ticks)
                .withHomeDirectory(Minecraft.getMinecraft().mcDataDir)
                .withPlayerLocation(this::currentLocation)
                .withEntitiesSupplier(this::nearbyPlayerEntities)
                .withSession(this::getMinecraftSession).build();
        return Collar.create(configuration);
    }

    private MinecraftSession getMinecraftSession() {
        String serverIP = Objects.requireNonNull(Minecraft.getMinecraft().getCurrentServerData()).serverIP;
        UUID playerId = Minecraft.getMinecraft().getSession().getProfile().getId();
        String playerName = Minecraft.getMinecraft().getSession().getProfile().getName();
        String token = Minecraft.getMinecraft().getSession().getToken();
//        return MinecraftSession.mojang(playerId, playerName, serverIP, token);
        return MinecraftSession.noJang(playerId, playerName, serverIP);
    }

    private Set<Entity> nearbyPlayerEntities() {
        return Minecraft.getMinecraft().world.playerEntities.stream()
                .map(entityPlayer -> new Entity(entityPlayer.getUniqueID(), entityPlayer.getPersistentID(), EntityType.PLAYER))
                .collect(Collectors.toSet());
    }

    private Location currentLocation() {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        Dimension result;
        switch (DimensionType.getById(player.dimension)) {
            case NETHER:
                result = Dimension.NETHER;
                break;
            case OVERWORLD:
                result = Dimension.OVERWORLD;
                break;
            case THE_END:
                result = Dimension.END;
                break;
            default:
                result = Dimension.UNKNOWN;
                break;
        }
        return new Location(player.posX, player.posY, player.posZ, result);
    }
}
