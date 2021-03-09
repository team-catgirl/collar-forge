package team.catgirl.collar.mod.commands;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import team.catgirl.collar.api.messaging.TextMessage;
import team.catgirl.collar.client.Collar;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class MessageCollarCommand extends CollarCommand {
    public MessageCollarCommand(Supplier<Collar> collarSupplier) {
        super(collarSupplier);
    }

    public List<String> getAliases() {
        return Arrays.asList("w", "msg");
    }

    @Override
    public String getName() {
        return "tell";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "commands.message.usage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 2) {
            throw new WrongUsageException("commands.message.usage");
        } else {
            EntityPlayer entityplayer = getPlayer(server, sender, args[0]);
            if (entityplayer == sender) {
                throw new PlayerNotFoundException("commands.message.sameTarget");
            } else {
                ITextComponent message = getChatComponentFromNthArg(sender, args, 1, !(sender instanceof EntityPlayer));
                TextComponentTranslation messageToSender = new TextComponentTranslation("commands.message.display.outgoing", entityplayer.getDisplayName(), message.createCopy());
                TextComponentTranslation messageToRecipient = new TextComponentTranslation("commands.message.display.incoming", sender.getDisplayName(), message.createCopy());
                collar().messaging().sendPrivateMessage(player(entityplayer), new TextMessage(messageToRecipient.getFormattedText(), messageToSender.getFormattedText()));
            }
        }
    }

    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
    }

    public boolean isUsernameIndex(String[] args, int index) {
        return index == 0;
    }
}
