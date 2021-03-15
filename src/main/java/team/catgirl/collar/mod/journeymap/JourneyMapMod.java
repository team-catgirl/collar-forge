package team.catgirl.collar.mod.journeymap;

import journeymap.client.api.IClientAPI;
import journeymap.client.api.IClientPlugin;
import journeymap.client.api.event.ClientEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import team.catgirl.collar.api.location.Location;
import team.catgirl.collar.api.session.Player;
import team.catgirl.collar.api.waypoints.Waypoint;
import team.catgirl.collar.client.Collar;
import team.catgirl.collar.client.api.location.LocationApi;
import team.catgirl.collar.mod.CollarMod;
import team.catgirl.collar.mod.features.events.PlayerLocationUpdatedEvent;
import team.catgirl.collar.mod.features.events.WaypointCreatedEvent;
import team.catgirl.collar.mod.features.events.WaypointDeletedEvent;
import team.catgirl.collar.mod.service.events.CollarConnectedEvent;
import team.catgirl.collar.mod.service.events.CollarDisconnectedEvent;
import team.catgirl.event.Subscribe;
import team.catgirl.plastic.Plastic;
import team.catgirl.plastic.world.Dimension;
import team.catgirl.plastic.world.Position;

import static team.catgirl.plastic.world.Dimension.*;

@journeymap.client.api.ClientPlugin
public class JourneyMapMod implements IClientPlugin {

    private final Plastic plastic = Plastic.getPlastic();
    private IClientAPI journeyMap;

    @Override
    public void initialize(IClientAPI journeyMap) {
        this.journeyMap = journeyMap;
        CollarMod.EVENT_BUS.subscribe(this);
    }

    @Override
    public String getModId() {
        return CollarMod.MODID;
    }

    @Override
    public void onEvent(ClientEvent event) {}

    @Subscribe
    public void onPlayerLocationUpdated(PlayerLocationUpdatedEvent event) {
        if (!event.player.id().equals(plastic.world.currentPlayer().id())) {
            journeymap.client.api.display.Waypoint playerWaypoint = waypointFrom(event.player.name(), event.position, event.dimension);
            if (event.position.equals(Position.UNKNOWN)) {
                journeyMap.remove(playerWaypoint);
            } else {
                show(playerWaypoint);
            }
        }
    }

    @Subscribe
    public void onConnected(CollarConnectedEvent event) {
        journeyMap.removeAll(CollarMod.MODID);
    }

    @Subscribe
    public void onDisconnected(CollarDisconnectedEvent event) {
        journeyMap.removeAll(CollarMod.MODID);
    }

    @Subscribe
    public void onWaypointCreated(WaypointCreatedEvent event) {
        show(waypointFrom(event.name, event.position, event.dimension));
    }

    @Subscribe
    public void onWaypointDeleted(WaypointDeletedEvent event) {
        journeyMap.remove(waypointFrom(event.name, event.position, event.dimension));
    }

    private void show(journeymap.client.api.display.Waypoint playerWaypoint) {
        try {
            journeyMap.show(playerWaypoint);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private journeymap.client.api.display.Waypoint waypointFrom(String name, Position position, Dimension dimension) {
        int dimensionId;
        switch (dimension) {
            case OVERWORLD:
                dimensionId = DimensionType.OVERWORLD.getId();
                break;
            case END:
                dimensionId = DimensionType.THE_END.getId();
                break;
            case NETHER:
                dimensionId = DimensionType.NETHER.getId();
                break;
            default:
                throw new IllegalStateException("could not get dimension id of " + dimension);
        }
        return new journeymap.client.api.display.Waypoint(
                CollarMod.MODID,
                name,
                dimensionId,
                new BlockPos(position.x, position.y, position.z)
        );
    }
}
