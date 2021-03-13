package team.catgirl.collar.mod.commands;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import team.catgirl.collar.api.friends.Friend;
import team.catgirl.collar.api.groups.Group;
import team.catgirl.collar.api.groups.GroupType;
import team.catgirl.collar.api.groups.Member;
import team.catgirl.collar.api.waypoints.Waypoint;
import team.catgirl.collar.client.Collar;
import team.catgirl.collar.client.api.groups.GroupInvitation;
import team.catgirl.collar.mod.commands.arguments.GroupArgumentType;
import team.catgirl.collar.mod.commands.arguments.InvitationArgumentType;
import team.catgirl.collar.mod.commands.arguments.PlayerArgumentType;
import team.catgirl.collar.mod.plastic.Plastic;
import team.catgirl.collar.mod.plastic.player.Player;
import team.catgirl.collar.mod.service.CollarService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.mojang.brigadier.arguments.DoubleArgumentType.doubleArg;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static team.catgirl.collar.mod.commands.arguments.GroupArgumentType.getGroup;
import static team.catgirl.collar.mod.commands.arguments.InvitationArgumentType.getInvitation;
import static team.catgirl.collar.mod.commands.arguments.PlayerArgumentType.getPlayer;

public class Commands {

    private final CollarService collarService;
    private final Plastic plastic;

    public Commands(CollarService collarService, Plastic plastic) {
        this.collarService = collarService;
        this.plastic = plastic;
    }

    public void register(CommandDispatcher<CollarService> dispatcher) {
        registerServiceCommands(dispatcher);
        registerFriendCommands(dispatcher);
        registerLocationCommands(dispatcher);
        registerGroupCommands("party", "parties", GroupType.PARTY, dispatcher);
        registerGroupCommands("group", "groups", GroupType.GROUP, dispatcher);
    }

    private void registerServiceCommands(CommandDispatcher<CollarService> dispatcher) {
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
    }

    private void registerFriendCommands(CommandDispatcher<CollarService> dispatcher) {
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
    }

    private void registerGroupCommands(String name, String plural, GroupType type, CommandDispatcher<CollarService> dispatcher) {
        // collar party create [name]
        dispatcher.register(literal(name)
                .then(literal("create")
                        .then(argument("name", string())
                                .executes(context -> {
                                    collarService.with(collar -> {
                                        collar.groups().create(getString(context, "name"), GroupType.PARTY, ImmutableList.of());
                                    });
                                    return 1;
                                })
                        )
                )
        );

        // collar party delete [name]
        dispatcher.register(literal(name)
                .then(literal("delete")
                        .then(argument("name", group(type))
                                .executes(context -> {
                                    collarService.with(collar -> {
                                        Group group = getGroup(context, "name");
                                        collar.groups().delete(group);
                                    });
                                    return 1;
                                })
                        )
                )
        );

        // collar party leave [name]
        dispatcher.register(literal(name)
                .then(literal("leave")
                        .then(argument("name", string())
                                .executes(context -> {
                                    collarService.with(collar -> {
                                        Group group = getGroup(context, "name");
                                        collar.groups().leave(group);
                                    });
                                    return 1;
                                })
                        )
                )
        );

        // collar party accept [name]
        dispatcher.register(literal(name)
                .then(literal("accept"))
                .then(argument("groupName", invitation(type)))
                .executes(context -> {
                    collarService.with(collar -> {
                        GroupInvitation invitation = getInvitation(context, "groupName");
                        collar.groups().accept(invitation);
                    });
                    return 1;
                })
        );

        // collar party list
        dispatcher.register(literal(name)
                .then(literal("list")
                        .executes(context -> {
                            collarService.with(collar -> {
                                List<Group> parties = collar.groups().all().stream().filter(group -> group.type.equals(GroupType.PARTY)).collect(Collectors.toList());
                                if (parties.isEmpty()) {
                                    plastic.display.sendMessage("You are not a member of any " + plural);
                                } else {
                                    plastic.display.sendMessage("You belong to the following " + plural + ":");
                                    parties.forEach(group -> plastic.display.sendMessage(group.name));
                                }
                            });
                            return 1;
                        })
                )
        );

        // collar party [name] add [player]
        dispatcher.register(literal(name)
                .then(argument("groupName", group(type)))
                        .then(literal("add"))
                        .then(argument("playerName", player()))
                        .executes(context -> {
                            collarService.with(collar -> {
                                Group group = getGroup(context, "groupName");
                                Player player = getPlayer(context, "playerName");
                                collar.groups().invite(group, ImmutableList.of(player.id()));
                            });
                            return 1;
                        })
        );

        // collar party [name] remove [player]
        dispatcher.register(literal(name)
                .then(argument("groupName", group(type)))
                .then(literal("remove"))
                .then(argument("playerName", player()))
                .executes(context -> {
                    collarService.with(collar -> {

                    });
                    return 1;
                })
        );
    }

    private void registerLocationCommands(CommandDispatcher<CollarService> dispatcher) {
        // collar location share start [any group name]
        dispatcher.register(literal("location")
                .then(literal("share"))
                .then(literal("start"))
                .then(argument("name", groups()))
                .executes(context -> {
                    return 1;
                })
        );

        // collar location share stop [any group name]
        dispatcher.register(literal("location")
                .then(literal("share"))
                .then(literal("stop"))
                .then(argument("name", groups()))
                .executes(context -> {
                    return 1;
                })
        );

        // collar location waypoint list [any group name]
        dispatcher.register(literal("me")
                .then(literal("waypoint"))
                .then(literal("list"))
                .executes(context -> {
                    collarService.with(collar -> {
                        Set<Waypoint> waypoints = collar.location().privateWaypoints();
                        if (waypoints.isEmpty()) {
                            plastic.display.sendMessage("You have no private waypoints");
                        } else {
                            waypoints.forEach(waypoint -> plastic.display.sendMessage(waypoint.displayName()));
                        }
                    });
                    return 1;
                })
        );

        // collar location waypoint add [name] [x] [y] [z] with [group]
        dispatcher.register(literal("me")
                .then(literal("waypoint"))
                .then(literal("add"))
                .then(argument("name", string()))
                .then(argument("x", doubleArg()))
                .then(argument("y", doubleArg()))
                .then(argument("z", doubleArg()))
                .executes(context -> {
                    collarService.with(collar -> {

                    });
                    return 1;
                })
        );

        // collar location waypoint remove [name] from [group]
        dispatcher.register(literal("me")
                .then(literal("waypoint"))
                .then(literal("remove"))
                .then(argument("name", string()))
                .executes(context -> {
                    collarService.with(collar -> {

                    });
                    return 1;
                })
        );

        // collar location waypoint list
        dispatcher.register(literal("me")
                .then(literal("waypoint"))
                .then(literal("list"))
                .executes(context -> {
                    collarService.with(collar -> {
                        Set<Waypoint> waypoints = collar.location().privateWaypoints();
                        if (waypoints.isEmpty()) {
                            plastic.display.sendMessage("You have no private waypoints");
                        } else {
                            waypoints.forEach(waypoint -> plastic.display.sendMessage(waypoint.displayName()));
                        }
                    });
                    return 1;
                })
        );

        // collar waypoint add [name] [x] [y] [z]
        dispatcher.register(literal("me")
                .then(literal("waypoint"))
                .then(literal("add"))
                .then(argument("name", string()))
                .then(argument("x", doubleArg()))
                .then(argument("y", doubleArg()))
                .then(argument("z", doubleArg()))
                .executes(context -> {
                    collarService.with(collar -> {

                    });
                    return 1;
                })
        );

        // collar waypoint remove [name]
        dispatcher.register(literal("me")
                .then(literal("waypoint"))
                .then(literal("remove"))
                .then(argument("name", string()))
                .executes(context -> {
                    collarService.with(collar -> {

                    });
                    return 1;
                })
        );
    }
    public static <T> RequiredArgumentBuilder<CollarService, T> argument(final String name, final ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }

    public static LiteralArgumentBuilder<CollarService> literal(final String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    private GroupArgumentType group(GroupType type) {
        return new GroupArgumentType(collarService, type);
    }

    private GroupArgumentType groups() {
        return new GroupArgumentType(collarService, null);
    }

    private InvitationArgumentType invitation(GroupType type) {
        return new InvitationArgumentType(collarService, type);
    }

    private PlayerArgumentType player() {
        return new PlayerArgumentType(plastic);
    }
}
