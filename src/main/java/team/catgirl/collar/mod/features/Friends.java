package team.catgirl.collar.mod.features;

import team.catgirl.collar.api.friends.Friend;
import team.catgirl.collar.client.Collar;
import team.catgirl.collar.client.api.friends.FriendsApi;
import team.catgirl.collar.client.api.friends.FriendsListener;

public class Friends implements FriendsListener {
    @Override
    public void onFriendChanged(Collar collar, FriendsApi friendsApi, Friend friend) {

    }

    @Override
    public void onFriendAdded(Collar collar, FriendsApi friendsApi, Friend added) {

    }

    @Override
    public void onFriendRemoved(Collar collar, FriendsApi friendsApi, Friend removed) {

    }
}
