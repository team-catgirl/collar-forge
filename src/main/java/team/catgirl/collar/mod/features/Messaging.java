package team.catgirl.collar.mod.features;

import team.catgirl.collar.api.groups.Group;
import team.catgirl.collar.api.messaging.Message;
import team.catgirl.collar.api.session.Player;
import team.catgirl.collar.client.Collar;
import team.catgirl.collar.client.api.messaging.MessagingApi;
import team.catgirl.collar.client.api.messaging.MessagingListener;
import team.catgirl.collar.security.mojang.MinecraftPlayer;

public class Messaging implements MessagingListener {

    @Override
    public void onPrivateMessageSent(Collar collar, MessagingApi messagingApi, Player player, Message message) {

    }

    @Override
    public void onPrivateMessageRecipientIsUntrusted(Collar collar, MessagingApi messagingApi, MinecraftPlayer player, Message message) {

    }

    @Override
    public void onPrivateMessageReceived(Collar collar, MessagingApi messagingApi, Player sender, Message message) {

    }

    @Override
    public void onGroupMessageSent(Collar collar, MessagingApi messagingApi, Group group, Message message) {

    }

    @Override
    public void onGroupMessageReceived(Collar collar, MessagingApi messagingApi, Group group, Player sender, Message message) {

    }
}
