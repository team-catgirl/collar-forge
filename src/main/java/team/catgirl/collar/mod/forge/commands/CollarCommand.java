package team.catgirl.collar.mod.forge.commands;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.entity.player.EntityPlayer;
import team.catgirl.collar.api.friends.Friend;
import team.catgirl.collar.api.groups.Group;
import team.catgirl.collar.api.groups.GroupType;
import team.catgirl.collar.client.Collar;
import team.catgirl.collar.mod.service.CollarService;

import java.util.Locale;
import java.util.Set;

import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;
import static team.catgirl.collar.mod.forge.commands.PlayerArgumentType.player;
import static team.catgirl.collar.mod.forge.commands.VoidArgumentType.none;

public class CollarCommand extends CollarCommandBase {

    private final CollarService collarService;

    public CollarCommand(CollarService collarService) {
        super("collar", collarService);
        this.collarService = collarService;
    }

    @Override
    protected void registerAll(CommandDispatcher<CollarService> dispatcher) {
        dispatcher.register(literal("connect").executes(context -> {
            collarService.connect();
            return 1;
        }));
        dispatcher.register(literal("disconnect").executes(context -> {
            collarService.disconnect();
            return 1;
        }));
        dispatcher.register(literal("status").executes(context -> {
            collarService.with(collar -> {
                collarService.sendMessage("Collar is " + collar.getState().name().toLowerCase(Locale.ROOT));
            }, () -> {
                collarService.sendMessage("Collar is disconnected");
            });
            return 1;
        }));

        dispatcher.register(literal("friends")
                .then(argument("add", player()))
                .executes(context -> {
                    collarService.with(collar -> {
                        EntityPlayer player = PlayerArgumentType.getPlayer(context, "add");
                        collar.friends().addFriend(player.getGameProfile().getId());
                    });
                    return 1;
                }));

        dispatcher.register(literal("friends")
                .then(argument("remove", player()))
                .executes(context -> {
                    collarService.with(collar -> {
                        EntityPlayer player = PlayerArgumentType.getPlayer(context, "remove");
                        collar.friends().removeFriend(player.getGameProfile().getId());
                    });
                    return 1;
                }));

        dispatcher.register(literal("friends")
                .then(argument("list", none()))
                .executes(context -> {
                    collarService.with(collar -> {
                        Set<Friend> friends = collar.friends().list();
                        if (friends.isEmpty()) {
                            collarService.sendMessage("You don't have any friends.");
                        } else {
                            friends.forEach(friend -> collarService.sendMessage(friend.friend.toString()));
                        }
                    });
                    return 1;
                }));

        dispatcher.register(literal("party")
                .then(argument("create", string()))
                .executes(context -> {
                    collarService.with(collar -> {
                        collar.groups().create(StringArgumentType.getString(context, "create"), GroupType.PARTY, ImmutableList.of());
                    });
                    return 1;
                }));

        dispatcher.register(literal("party")
                .then(argument("leave", party()))
                .executes(context -> {
                    collarService.with(collar -> {
                        Group group = context.getArgument("leave", Group.class);
                        collar.groups().leave(group);
                    });
                    return 1;
                }));

        dispatcher.register(literal("party")
                .then(argument("delete", party()))
                .executes(context -> {
                    collarService.with(collar -> {
                        Group group = context.getArgument("leave", Group.class);
                        collar.groups().delete(group);
                    });
                    return 1;
                }));

        dispatcher.register(literal("party")
                .then(argument("add", group()))
                .then(argument("player", player()))
                .executes(context -> {
                    collarService.with(collar -> {

                    });
                    return 1;
                }));

        dispatcher.register(literal("party")
                .then(argument("remove", player()))
                .executes(context -> {
                    collarService.with(collar -> {

                    });
                    return 1;
                }));
    }

    private GroupArgumentType group() {
        Collar collar = collarService.getCollar().orElse(null);
        return new GroupArgumentType(collar, GroupType.GROUP);
    }

    private GroupArgumentType party() {
        Collar collar = collarService.getCollar().orElse(null);
        return new GroupArgumentType(collar, GroupType.GROUP);
    }
}
