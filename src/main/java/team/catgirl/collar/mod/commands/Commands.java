package team.catgirl.collar.mod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import team.catgirl.collar.api.friends.Friend;
import team.catgirl.collar.api.groups.GroupType;
import team.catgirl.collar.client.Collar;
import team.catgirl.collar.mod.commands.arguments.GroupArgumentType;
import team.catgirl.collar.mod.commands.arguments.PlayerArgumentType;
import team.catgirl.collar.mod.plastic.Plastic;
import team.catgirl.collar.mod.plastic.player.Player;
import team.catgirl.collar.mod.service.CollarService;

import java.util.Set;

import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static team.catgirl.collar.mod.commands.arguments.PlayerArgumentType.getPlayer;

public class Commands {

    private final CollarService collarService;
    private final Plastic plastic;

    public Commands(CollarService collarService, Plastic plastic) {
        this.collarService = collarService;
        this.plastic = plastic;
    }

    public void register(CommandDispatcher<CollarService> dispatcher) {

        // collar connect
        dispatcher.register(literal("connect").executes(context -> {
            collarService.connect();
            return 1;
        }));

        // collar disconnect
        dispatcher.register(literal("disconnect").executes(context -> {
            collarService.disconnect();
            return 1;
        }));

        // collar status
        dispatcher.register(literal("status").executes(context -> {
            collarService.with(collar -> {
                plastic.display.sendMessage("Collar is " + collar.getState().name().toLowerCase());
            }, () -> plastic.display.sendMessage("Collar is disconnected"));
            return 1;
        }));

        // collar friend add [user]
        dispatcher.register(literal("friend")
            .then(literal("add"))
                .then(argument("name", player()))
                    .executes(context -> {
                        collarService.with(collar -> {
                            Player player = getPlayer(context, "name");
                            collar.friends().addFriend(player.id());
                        });
                        return 1;
                    })
        );

        // collar friend remove [user]
        dispatcher.register(literal("friend")
                .then(literal("remove"))
                .then(argument("name", player()))
                .executes(context -> {
                    collarService.with(collar -> {
                        Player player = getPlayer(context, "name");
                        collar.friends().removeFriend(player.id());
                    });
                    return 1;
                })
        );

        // collar friend list
        dispatcher.register(literal("friend")
                .then(literal("list"))
                .executes(context -> {
                    collarService.with(collar -> {
                        Set<Friend> friends = collar.friends().list();
                        if (friends.isEmpty()) {
                            plastic.display.sendMessage("You don't have any friends");
                        } else {
                            friends.forEach(friend -> plastic.display.sendMessage(friend.friend.name));
                        }
                    });
                    return 1;
                })
        );


        // collar party create [name]

        // collar party delete [name]

        // collar party leave [name]

        // collar party

        dispatcher.register(literal("group")
                .then(argument("name", string())
                        .then(literal("add")
                                .then(argument("user", string())
                                        .executes(context -> 1)
                                )
                        )
                )
        );
    }

    public static <T> RequiredArgumentBuilder<CollarService, T> argument(final String name, final ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }

    public static LiteralArgumentBuilder<CollarService> literal(final String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    private GroupArgumentType group() {
        Collar collar = collarService.getCollar().orElse(null);
        return new GroupArgumentType(collar, GroupType.GROUP);
    }

    private GroupArgumentType party() {
        Collar collar = collarService.getCollar().orElse(null);
        return new GroupArgumentType(collar, GroupType.GROUP);
    }

    private PlayerArgumentType player() {
        return new PlayerArgumentType(plastic);
    }
}
