package team.catgirl.collar.mod.service;

import org.apache.logging.log4j.Logger;
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
import team.catgirl.collar.mod.features.*;
import team.catgirl.collar.mod.plastic.Plastic;
import team.catgirl.collar.mod.plastic.player.Player;
import team.catgirl.collar.mod.plastic.world.Position;
import team.catgirl.collar.mod.plugins.Plugins;
import team.catgirl.collar.security.mojang.MinecraftSession;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CollarService implements CollarListener {

    private final ExecutorService backgroundJobs;
    private Collar collar;
    private final Plastic plastic;
    private final Ticks ticks;
    private final Plugins plugins;
    private final Logger logger;

    public final Locations locations;
    public final Friends friends;
    public final Messaging messaging;
    public final Textures textures;
    public final Groups groups;

    public CollarService(Plastic plastic, Ticks ticks, Plugins plugins, Logger logger) {
        this.plastic = plastic;
        this.ticks = ticks;
        this.plugins = plugins;
        this.locations = new Locations(plastic);
        this.friends = new Friends(plastic);
        this.messaging = new Messaging(plastic);
        this.textures = new Textures(plastic);
        this.groups = new Groups(plastic);
        this.logger = logger;
        this.backgroundJobs = Executors.newFixedThreadPool(5, r -> {
            Thread thread = new Thread(r);
            thread.setName("Collar Worker");
            return thread;
        });
    }

    public Optional<Collar> getCollar() {
        return collar == null ? Optional.empty() : Optional.of(collar);
    }

    public void with(Consumer<Collar> action, Runnable emptyAction) {
        if (collar == null || !collar.getState().equals(Collar.State.CONNECTED)) {
            emptyAction.run();
        } else {
            action.accept(collar);
        }
    }

    public void with(Consumer<Collar> action) {
        with(action, () -> plastic.display.sendMessage("Collar not connected"));
    }

    public void connect() {
        backgroundJobs.submit(() -> {
            try {
                collar = createCollar();
                collar.connect();
            } catch (CollarException e) {
                plastic.display.displayStatus(e.getMessage());
                logger.error(e.getMessage(), e);
            } catch (IOException e) {
                plastic.display.displayStatus("Failed to connect to Collar");
                logger.error(e.getMessage(), e);
            }
        });
    }

    public void disconnect() {
        backgroundJobs.submit(() -> {
            if (collar != null) {
                collar.disconnect();
            }
        });
    }

    @Override
    public void onStateChanged(Collar collar, Collar.State state) {
        backgroundJobs.submit(() -> {
            switch (state) {
                case CONNECTING:
                    plastic.display.displayStatus("Collar connecting...");
                case CONNECTED:
                    plastic.display.displayStatus("Collar connected");
                    collar.location().subscribe(locations);
                    collar.groups().subscribe(groups);
                    collar.friends().subscribe(friends);
                    collar.messaging().subscribe(messaging);
                    collar.textures().subscribe(textures);
                case DISCONNECTED:
                    plastic.display.displayStatus("Collar disconnected");
                    break;
            }
            plugins.find().forEach(plugin -> {
                switch (state) {
                    case CONNECTING:
                        plugin.onConnecting(collar);
                        break;
                    case CONNECTED:
                        plugin.onConnected(collar);
                        break;
                    case DISCONNECTED:
                        plugin.onDisconnected(collar);
                        break;
                }
            });
        });
    }

    @Override
    public void onConfirmDeviceRegistration(Collar collar, String token, String approvalUrl) {
        plastic.display.displayStatus("Collar registration required");
        plastic.display.sendMessage("New Collar installation detected. You can register this installation with your Collar account at " + approvalUrl);
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
        plastic.display.displayStatus("Please verify Collar");
        plastic.display.sendMessage("Collar failed to verify your Minecraft account");
    }

    @Override
    public void onPrivateIdentityMismatch(Collar collar, String url) {
        plastic.display.displayStatus("Collar encountered a problem");
        plastic.display.sendMessage("Your private identity did not match. We cannot decrypt your private data. To resolve please visit " + url);
    }

    private Collar createCollar() throws IOException {
        CollarConfiguration configuration = new CollarConfiguration.Builder()
                .withCollarDevelopmentServer()
                .withListener(this)
                .withTicks(ticks)
                .withHomeDirectory(plastic.home())
                .withPlayerLocation(this::currentLocation)
                .withEntitiesSupplier(this::nearbyPlayerEntities)
                .withSession(this::getMinecraftSession).build();
        return Collar.create(configuration);
    }

    private MinecraftSession getMinecraftSession() {
        String serverIP = plastic.serverIp();
        UUID playerId = plastic.world.currentPlayer().id();
        String playerName = plastic.world.currentPlayer().name();
        return MinecraftSession.noJang(playerId, playerName, serverIP);
    }

    private Set<Entity> nearbyPlayerEntities() {
        return plastic.world.allPlayers().stream()
                .map(entityPlayer -> new Entity(entityPlayer.networkId(), entityPlayer.id(), EntityType.PLAYER))
                .collect(Collectors.toSet());
    }

    private Location currentLocation() {
        Player player = plastic.world.currentPlayer();
        Dimension result;
        switch (player.dimension()) {
            case NETHER:
                result = Dimension.NETHER;
                break;
            case OVERWORLD:
                result = Dimension.OVERWORLD;
                break;
            case END:
                result = Dimension.END;
                break;
            default:
                result = Dimension.UNKNOWN;
                break;
        }
        Position position = player.position();
        return new Location(position.x, position.y, position.z, result);
    }
}
