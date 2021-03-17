package team.catgirl.plastic.forge;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import team.catgirl.plastic.forge.ForgeTextBuilder;
import team.catgirl.plastic.ui.Display;
import team.catgirl.plastic.ui.TextBuilder;

public class ForgeDisplay implements Display {
    @Override
    public void displayStatusMessage(String message) {
        Minecraft.getMinecraft().player.sendStatusMessage(new TextComponentString(message), true);
    }

    @Override
    public void displayStatusMessage(TextBuilder message) {
        Minecraft.getMinecraft().player.sendStatusMessage(ITextComponent.Serializer.jsonToComponent(message.toJSON()), true);
    }

    @Override
    public void displayMessage(TextBuilder message) {
        Minecraft.getMinecraft().player.sendStatusMessage(ITextComponent.Serializer.jsonToComponent(message.toJSON()), false);
    }

    @Override
    public TextBuilder textBuilderFromJSON(String json) {
        return new ForgeTextBuilder(ITextComponent.Serializer.jsonToComponent(json));
    }

    @Override
    public TextBuilder textBuilderFromFormattedString(String text) {
        return new ForgeTextBuilder(new TextComponentString(text));
    }

    @Override
    public TextBuilder newTextBuilder() {
        return new ForgeTextBuilder();
    }
}
