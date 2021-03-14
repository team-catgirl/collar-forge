package team.catgirl.collar.mod.features;

import team.catgirl.collar.api.groups.Group;
import team.catgirl.collar.api.location.Location;
import team.catgirl.collar.api.session.Player;
import team.catgirl.collar.api.waypoints.Waypoint;
import team.catgirl.collar.client.Collar;
import team.catgirl.collar.client.api.location.LocationApi;
import team.catgirl.collar.client.api.location.LocationListener;
import team.catgirl.collar.mod.plastic.Plastic;

import java.util.Set;

public class Locations implements LocationListener {

    private final Plastic plastic;

    public Locations(Plastic plastic) {
        this.plastic = plastic;
    }

    @Override
    public void onLocationUpdated(Collar collar, LocationApi locationApi, Player player, Location location) {}

    @Override
    public void onWaypointCreated(Collar collar, LocationApi locationApi, Group group, Waypoint waypoint) {
        String message;
        if (group == null) {
            message = String.format("Waypoint %s created", waypoint.name);
        } else {
            message = String.format("Waypoint %s created in %s %s", waypoint.name, group.type.name, group.name);
        }
        plastic.display.displayStatus(message);
        plastic.display.sendMessage(message);
    }

    @Override
    public void onWaypointRemoved(Collar collar, LocationApi locationApi, Group group, Waypoint waypoint) {
        String message;
        if (group == null) {
            message = String.format("Waypoint %s removed", waypoint.name);
        } else {
            message = String.format("Waypoint %s removed from %s %s", waypoint.name, group.type.name, group.name);
        }
        plastic.display.displayStatus(message);
        plastic.display.sendMessage(message);
    }

    @Override
    public void onPrivateWaypointsReceived(Collar collar, LocationApi locationApi, Set<Waypoint> privateWaypoints) {}
}
