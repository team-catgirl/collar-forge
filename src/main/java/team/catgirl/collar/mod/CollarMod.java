package team.catgirl.collar.mod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.DimensionType;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Logger;
import team.catgirl.collar.api.CollarPlugin;
import team.catgirl.collar.api.entities.Entity;
import team.catgirl.collar.api.entities.EntityType;
import team.catgirl.collar.api.location.Dimension;
import team.catgirl.collar.api.location.Location;
import team.catgirl.collar.client.Collar;
import team.catgirl.collar.client.Collar.State;
import team.catgirl.collar.client.CollarConfiguration;
import team.catgirl.collar.client.CollarListener;
import team.catgirl.collar.client.minecraft.Ticks;
import team.catgirl.collar.client.security.ClientIdentityStore;
import team.catgirl.collar.security.mojang.MinecraftSession;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SideOnly(Side.CLIENT)
@Mod(modid = CollarMod.MODID, name = CollarMod.NAME, version = CollarMod.VERSION)
public class CollarMod implements CollarListener
{
    public static final String MODID = "team.catgirl.collar";
    public static final String NAME = "Collar";
    public static final String VERSION = "0.1";

    private static Logger logger;

    private static final Ticks TICKS = new Ticks();
    private static boolean isConnectedToServer = false;
    private Collar collar;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {}

    @SubscribeEvent
    public void onWorldLoaded(WorldEvent.Load load) throws IOException {
        collar = createCollar();
    }

    @SubscribeEvent
    public void onTick(ClientTickEvent event) {
        TICKS.onTick();
    }

    @SubscribeEvent
    public void connected(ClientConnectedToServerEvent connected) throws IOException {
        isConnectedToServer = true;
    }

    @SubscribeEvent
    public void disconnected(ClientDisconnectionFromServerEvent disconnection) {
        isConnectedToServer = false;
        if (collar != null) {
            collar.disconnect();
            collar = null;
        }
    }

    @Override
    public void onStateChanged(Collar collar, State state) {
        plugins().forEach(plugin -> {
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

    private Stream<CollarPlugin> plugins() {
        return Loader.instance().getActiveModList().stream()
                .filter(modContainer -> modContainer.getMod() instanceof CollarPlugin)
                .map(modContainer -> (CollarPlugin) modContainer.getMod());
    }

    private Collar createCollar() throws IOException {
        CollarConfiguration configuration = new CollarConfiguration.Builder()
                .withCollarDevelopmentServer()
                .withListener(this)
                .withTicks(TICKS)
                .withHomeDirectory(Minecraft.getMinecraft().mcDataDir)
                .withPlayerLocation(this::currentLocation)
                .withEntitiesSupplier(this::nearbyPlayerEntities)
                .withSession(this::getMinecraftSession).build();
        return Collar.create(configuration);
    }

    private MinecraftSession getMinecraftSession() {
        if (!isConnectedToServer || Minecraft.getMinecraft().getCurrentServerData() == null) {
            throw new IllegalStateException("not connected to server");
        }
        String serverIP = Minecraft.getMinecraft().getCurrentServerData().serverIP;
        UUID playerId = Minecraft.getMinecraft().getSession().getProfile().getId();
        String playerName = Minecraft.getMinecraft().getSession().getProfile().getName();
        String token = Minecraft.getMinecraft().getSession().getToken();
        return MinecraftSession.mojang(playerId, playerName, serverIP, token);
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
