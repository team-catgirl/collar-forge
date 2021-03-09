package team.catgirl.collar.mod.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.entity.player.EntityPlayer;
import org.jetbrains.annotations.NotNull;
import team.catgirl.collar.client.Collar;
import team.catgirl.collar.security.mojang.MinecraftPlayer;

import java.util.function.Supplier;

public abstract class CollarCommand extends CommandBase {
    private final Supplier<Collar> collarSupplier;

    public CollarCommand(Supplier<Collar> collarSupplier) {
        this.collarSupplier = collarSupplier;
    }

    protected Collar collar() {
        return collarSupplier.get();
    }

    protected MinecraftPlayer player(EntityPlayer player) {
        return new MinecraftPlayer(player.getGameProfile().getId(), collar().player().minecraftPlayer.server);
    }

    @Override
    public int compareTo(@NotNull ICommand o) {
        return 0;
    }
}
