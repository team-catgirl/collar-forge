package team.catgirl.collar.mod.features;

import team.catgirl.collar.api.groups.Group;
import team.catgirl.collar.api.session.Player;
import team.catgirl.collar.client.Collar;
import team.catgirl.collar.client.api.groups.GroupInvitation;
import team.catgirl.collar.client.api.groups.GroupsApi;
import team.catgirl.collar.client.api.groups.GroupsListener;
import team.catgirl.plastic.Plastic;
import team.catgirl.plastic.ui.TextFormatting;

public class Groups implements GroupsListener {

    private final Plastic plastic;

    public Groups(Plastic plastic) {
        this.plastic = plastic;
    }

    @Override
    public void onGroupCreated(Collar collar, GroupsApi groupsApi, Group group) {
        String msg = plastic.display.newTextBuilder().add(String.format("Created %s %s", group.type.name, group.name), TextFormatting.GREEN).formatted();
        this.plastic.display.sendMessage(msg);
    }

    @Override
    public void onGroupJoined(Collar collar, GroupsApi groupsApi, Group group, Player player) {
        String msg = plastic.display.newTextBuilder().add(String.format("Joined %s %s", group.type.name, group.name), TextFormatting.GREEN).formatted();
        this.plastic.display.sendMessage(msg);
    }

    @Override
    public void onGroupLeft(Collar collar, GroupsApi groupsApi, Group group, Player player) {
        String msg = plastic.display.newTextBuilder().add(String.format("Left %s %s", group.type.name, group.name), TextFormatting.GREEN).formatted();
        this.plastic.display.sendMessage(msg);
    }

    @Override
    public void onGroupInvited(Collar collar, GroupsApi groupsApi, GroupInvitation invitation) {
        team.catgirl.plastic.player.Player player = plastic.world.allPlayers()
                .stream().filter(player1 -> player1.id().equals(invitation.sender.minecraftPlayer.id))
                .findFirst().orElseThrow(() -> new IllegalStateException("cannot find player " + invitation.sender.minecraftPlayer.id));
        String message = String.format("You are invited to %s %s by %s", invitation.type.name, invitation.name, player.name());
        this.plastic.display.displayStatus(message);
        this.plastic.display.sendMessage(plastic.display.newTextBuilder().add(message, TextFormatting.GREEN).formatted());
    }
}
