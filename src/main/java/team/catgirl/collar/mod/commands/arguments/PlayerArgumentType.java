/**
 * MIT License
 *
 * Copyright (c) 2020 Headpat Services
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package team.catgirl.collar.mod.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import org.jetbrains.annotations.Contract;
import services.headpat.forgeextensions.utils.PlayerUtils;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class PlayerArgumentType implements ArgumentType<EntityPlayer> {
	private PlayerArgumentType() {
	}

	/**
	 * Shortcut to create a new {@link PlayerArgumentType} instance.
	 *
	 * @return {@link PlayerArgumentType} instance.
	 */
	@Contract(value = " -> new", pure = true)
	public static PlayerArgumentType player() {
		return new PlayerArgumentType();
	}

	/**
	 * Quick shortcut of {@link CommandContext#getArgument(String, Class)} for a player argument.
	 *
	 * @param context Command context.
	 * @param name    Name of the argument.
	 * @return The player specified by the argument name in the command context.
	 */
	public static EntityPlayer getPlayer(CommandContext<?> context, String name) {
		return context.getArgument(name, EntityPlayerMP.class);
	}

	@Override
	public EntityPlayer parse(StringReader reader) throws CommandSyntaxException {
		return Minecraft.getMinecraft().world.playerEntities.stream()
				.filter(thePlayer -> thePlayer.getName().equals(reader.readUnquotedString()))
				.findFirst().orElseThrow(() -> CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException().create("player not found"));
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		Minecraft.getMinecraft().world.playerEntities.forEach(player -> {
			if (player.getName().toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
				builder.suggest(player.getName());
			}
		});
		return builder.buildFuture();
	}

	@Override
	public Collection<String> getExamples() {
		return PlayerUtils.getPlayerList().getPlayers().stream().map(EntityPlayer::getName).collect(Collectors.toList());
	}
}
