package team.catgirl.collar.mod.features;

import team.catgirl.collar.api.groups.Group;
import team.catgirl.collar.api.session.Player;
import team.catgirl.collar.client.Collar;
import team.catgirl.collar.client.api.groups.GroupInvitation;
import team.catgirl.collar.client.api.groups.GroupsApi;
import team.catgirl.collar.client.api.groups.GroupsListener;

public class Groups implements GroupsListener {
    @Override
    public void onGroupCreated(Collar collar, GroupsApi groupsApi, Group group) {

    }

    @Override
    public void onGroupJoined(Collar collar, GroupsApi groupsApi, Group group, Player player) {

    }

    @Override
    public void onGroupLeft(Collar collar, GroupsApi groupsApi, Group group, Player player) {

    }

    @Override
    public void onGroupInvited(Collar collar, GroupsApi groupsApi, GroupInvitation invitation) {

    }

    @Override
    public void onGroupMemberUpdated(Collar collar, GroupsApi groupsApi, Group group, Player player) {

    }
}
