package team.catgirl.plastic;

import team.catgirl.plastic.ui.Commands;
import team.catgirl.plastic.ui.Display;
import team.catgirl.plastic.world.World;

import java.io.File;

/**
 * Minecraft mod api abstraction
 */
public abstract class Plastic {
    /**
     * UI display
     */
    public final Display display;

    /**
     * The World
     */
    public final World world;

    /**
     * Command registration
     */
    public final Commands commands;

    protected Plastic(Display display, World world, Commands commands) {
        this.display = display;
        this.world = world;
        this.commands = commands;
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
}
