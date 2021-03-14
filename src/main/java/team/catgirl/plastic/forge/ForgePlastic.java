package team.catgirl.plastic.forge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import team.catgirl.plastic.Plastic;
import team.catgirl.plastic.forge.ui.ForgeCommands;
import team.catgirl.plastic.forge.ui.ForgeDisplay;
import team.catgirl.plastic.forge.world.ForgeWorld;

import java.io.File;

public class ForgePlastic extends Plastic {


    public ForgePlastic() {
        super(new ForgeDisplay(), new ForgeWorld(), new ForgeCommands());
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
}
