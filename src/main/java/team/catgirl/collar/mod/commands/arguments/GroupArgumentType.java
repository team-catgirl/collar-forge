package team.catgirl.collar.mod.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import team.catgirl.collar.api.groups.Group;
import team.catgirl.collar.api.groups.GroupType;
import team.catgirl.collar.client.Collar;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class GroupArgumentType implements ArgumentType<Group> {

    private final Collar collar;
    private final GroupType type;

    public GroupArgumentType(Collar collar, GroupType type) {
        this.collar = collar;
        this.type = type;
    }

    @Override
    public Group parse(StringReader reader) throws CommandSyntaxException {
        if (collar == null) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException().create("Collar not connected");
        }
        return collar.groups().all().stream()
                .filter(group -> group.name.equals(reader.readUnquotedString()))
                .findFirst().orElseThrow(() -> CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException().create("group not found"));
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        if (collar == null) {
            return builder.buildFuture();
        }
        collar.groups().all().stream().filter(group -> group.type.equals(type))
                .filter(group -> group.name.toLowerCase().startsWith(builder.getRemaining().toLowerCase()))
                .forEach(group -> builder.suggest(group.name));
        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        if (collar == null) {
            return Collections.emptyList();
        }
        return collar.groups().all().stream()
                .filter(group -> group.type.equals(type))
                .limit(3)
                .map(group -> group.name).collect(Collectors.toList());
    }
}
