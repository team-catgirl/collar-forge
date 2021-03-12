package team.catgirl.collar.mod.plastic.forge;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraftforge.client.ClientCommandHandler;
import team.catgirl.collar.mod.plastic.Plastic;
import team.catgirl.collar.mod.plastic.forge.ui.DisplayImpl;
import team.catgirl.collar.mod.plastic.forge.world.WorldForge;

import java.io.File;
import java.util.Objects;
import java.util.function.Consumer;

public class PlasticForge extends Plastic {
    public PlasticForge() {
        super(new DisplayImpl(), new WorldForge());
    }

    @Override
    public File home() {
        return Minecraft.getMinecraft().mcDataDir;
    }

    @Override
    public String serverIp() {
        ServerData serverData = Minecraft.getMinecraft().getCurrentServerData();
        return serverData == null ? null : serverData.serverIP;
    }

    @Override
    public <T> void registerCommand(String name, T source, Consumer<CommandDispatcher<T>> consumer) {
        ClientCommandHandler.instance.registerCommand(new PlasticCommand(name, this, source));
    }
}
