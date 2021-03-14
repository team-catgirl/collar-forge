package team.catgirl.collar.mod.plastic;

import com.mojang.brigadier.CommandDispatcher;
import team.catgirl.collar.mod.plastic.ui.Display;
import team.catgirl.collar.mod.plastic.world.World;

import java.io.File;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Minecraft mod api abstraction
 */
public abstract class Plastic {
    public final Display display;
    public final World world;

    protected Plastic(Display display, World world) {
        this.display = display;
        this.world = world;
    }

    /**
     * Minecraft home directory
     * @return home
     */
    public abstract File home();

    /**
     * Server IP
     * @return serverIP or null if not connected
     */
    public abstract String serverIp();

    /**
     * Register a command
     * @param name name of command
     * @param source for the dispatcher
     * @param supplier to provided the dispatcher
     * @param <T> of the source
     */
    public abstract <T> void registerCommand(String name, T source, Supplier<CommandDispatcher<T>> supplier);
}
