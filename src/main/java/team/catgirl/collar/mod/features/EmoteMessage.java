package team.catgirl.collar.mod.features;

import team.catgirl.collar.api.messaging.Message;

public class EmoteMessage implements Message {
    public final byte[] emotePacket;

    public EmoteMessage(byte[] emotePacket) {
        this.emotePacket = emotePacket;
    }
}
