package team.catgirl.plastic.forge;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraftforge.client.ClientCommandHandler;
import team.catgirl.plastic.Plastic;
import team.catgirl.plastic.forge.ui.DisplayImpl;
import team.catgirl.plastic.forge.world.WorldForge;

import java.io.File;
import java.util.function.Supplier;

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
    public <T> void registerCommand(String name, T source, Supplier<CommandDispatcher<T>> supplier) {
        CommandDispatcher<T> dispatcher = supplier.get();
        ClientCommandHandler.instance.registerCommand(new PlasticCommand<T>(name, source, supplier.get()));
    }
}
