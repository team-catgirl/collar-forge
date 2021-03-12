package team.catgirl.collar.mod.plastic.forge.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import team.catgirl.collar.mod.plastic.ui.Display;

public class DisplayImpl implements Display {
    
    @Override public void displayStatus(String message) {
        Minecraft.getMinecraft().player.sendStatusMessage(new TextComponentString(message), true);
    }

    @Override public void sendMessage(String message) {
        Minecraft.getMinecraft().player.sendStatusMessage(new TextComponentString(message), false);
    }
}
