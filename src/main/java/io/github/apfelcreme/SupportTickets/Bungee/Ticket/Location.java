package io.github.apfelcreme.SupportTickets.Bungee.Ticket;

/**
 * Copyright (C) 2016 Lord36 aka Apfelcreme
 * <p>
 * This program is free software;
 * you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * <p>
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, see <http://www.gnu.org/licenses/>.
 *
 * @author Lord36 aka Apfelcreme
 */
public class Location {

    private final String server;
    private final String worldName;
    private final double locationX;
    private final double locationY;
    private final double locationZ;
    private final float yaw;
    private final float pitch;

    public Location(String server, String worldName, double locationX, double locationY, double locationZ, float yaw, float pitch) {
        this.server = server;
        this.worldName = worldName;
        this.locationX = locationX;
        this.locationY = locationY;
        this.locationZ = locationZ;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    /**
     * returns the name of the server or it's address (ip:port)
     *
     * @return the name of the server/ip:port
     */
    public String getServer() {
        return server;
    }

    /**
     * returns the name of the world
     *
     * @return the name of the world
     */
    public String getWorldName() {
        return worldName;
    }

    /**
     * returns the x location
     *
     * @return the x location
     */
    public double getLocationX() {
        return locationX;
    }

    /**
     * returns the y location
     *
     * @return the y location
     */
    public double getLocationY() {
        return locationY;
    }

    /**
     * returns the z location
     *
     * @return the z location
     */
    public double getLocationZ() {
        return locationZ;
    }


    /**
     * returns the yaw
     *
     * @return yaw
     */
    public float getYaw() {
        return yaw;
    }

    /**
     * returns the pitch
     *
     * @return the pitch
     */
    public float getPitch() {
        return pitch;
    }
}
