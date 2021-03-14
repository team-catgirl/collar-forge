package team.catgirl.collar.mod;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
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
import team.catgirl.collar.client.CollarListener;
import team.catgirl.collar.client.minecraft.Ticks;
import team.catgirl.collar.mod.commands.Commands;
import team.catgirl.collar.mod.plastic.Plastic;
import team.catgirl.collar.mod.plastic.forge.PlasticForge;
import team.catgirl.collar.mod.plugins.ForgePlugins;
import team.catgirl.collar.mod.plugins.Plugins;
import team.catgirl.collar.mod.service.CollarService;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;

@SideOnly(Side.CLIENT)
@Mod(modid = CollarMod.MODID, name = CollarMod.NAME, version = CollarMod.VERSION)
public class CollarMod implements CollarListener
{
    public static final String MODID = "team.catgirl.collar";
    public static final String NAME = "Collar";
    public static final String VERSION = "0.1";

    private static Logger logger;
    private static final Ticks TICKS = new Ticks();
    private static boolean isWorldLoaded = true;
    private static boolean isConnectedToServer = false;
    private static final Plugins PLUGINS = new ForgePlugins();
    private static final Plastic PLASTIC = new PlasticForge();

    private CollarService collarService;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        collarService = new CollarService(PLASTIC, TICKS, PLUGINS, logger);
        PLASTIC.registerCommand("collar", collarService, () -> {
            CommandDispatcher<CollarService> dispatcher = new CommandDispatcher<>();
            new Commands(collarService, PLASTIC).register(dispatcher);
            return dispatcher;
        });
    }

    @SubscribeEvent
    public void onTick(ClientTickEvent event) {
        TICKS.onTick();
    }

    @SubscribeEvent
    public void onWorldLoaded(WorldEvent.Load load) {
        // Only start collar when the world is loaded and the server is connected
        if (!isWorldLoaded && isConnectedToServer) {
            collarService.connect();
        }
        isWorldLoaded = true;
    }

    @SubscribeEvent
    public void connected(ClientConnectedToServerEvent connected) {
        isConnectedToServer = true;
    }

    @SubscribeEvent
    public void disconnected(ClientDisconnectionFromServerEvent disconnection) {
        isConnectedToServer = false;
        isWorldLoaded = false;
        // Stop collar and reset state
        if (collarService != null) {
            collarService.disconnect();
        }
    }
}
