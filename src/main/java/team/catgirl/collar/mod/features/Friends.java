package team.catgirl.collar.mod.features;

import team.catgirl.collar.api.friends.Friend;
import team.catgirl.collar.client.Collar;
import team.catgirl.collar.client.api.friends.FriendsApi;
import team.catgirl.collar.client.api.friends.FriendsListener;
import team.catgirl.plastic.Plastic;

public class Friends implements FriendsListener {

    private final Plastic plastic;

    public Friends(Plastic plastic) {
        this.plastic = plastic;
    }

    @Override
    public void onFriendChanged(Collar collar, FriendsApi friendsApi, Friend friend) {
        plastic.display.displayStatus(String.format("%s is %s", friend.friend.name, friend.status.name().toLowerCase()));
    }

    @Override
    public void onFriendAdded(Collar collar, FriendsApi friendsApi, Friend added) {
        plastic.display.sendMessage(String.format("Added %s as a friend", added.friend.name));
    }

    @Override
    public void onFriendRemoved(Collar collar, FriendsApi friendsApi, Friend removed) {
        plastic.display.sendMessage(String.format("Removed %s as a friend", removed.friend.name));
    }
}
