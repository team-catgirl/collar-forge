package team.catgirl.collar.mod.features;

import team.catgirl.collar.api.friends.Friend;
import team.catgirl.collar.client.Collar;
import team.catgirl.collar.client.api.friends.FriendsApi;
import team.catgirl.collar.client.api.friends.FriendsListener;
import team.catgirl.plastic.Plastic;
import team.catgirl.plastic.ui.TextFormatting;

public class Friends implements FriendsListener {

    private final Plastic plastic;

    public Friends(Plastic plastic) {
        this.plastic = plastic;
    }

    @Override
    public void onFriendChanged(Collar collar, FriendsApi friendsApi, Friend friend) {
        plastic.display.displayStatus(plastic.display.newTextBuilder().add(String.format("%s is %s", friend.friend.name, friend.status.name().toLowerCase()), TextFormatting.GREEN).formatted());
    }

    @Override
    public void onFriendAdded(Collar collar, FriendsApi friendsApi, Friend added) {
        plastic.display.sendMessage(plastic.display.newTextBuilder().add(String.format("Added %s as a friend", added.friend.name), TextFormatting.GREEN).formatted());
    }

    @Override
    public void onFriendRemoved(Collar collar, FriendsApi friendsApi, Friend removed) {
        plastic.display.sendMessage(plastic.display.newTextBuilder().add(String.format("Removed %s as a friend", removed.friend.name), TextFormatting.GREEN).formatted());
    }
}
