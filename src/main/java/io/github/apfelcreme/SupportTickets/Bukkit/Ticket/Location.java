package io.github.apfelcreme.SupportTickets.Bukkit.Ticket;

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

    private String server = null;
    private String worldName = null;
    private Double locationX = null;
    private Double locationY = null;
    private Double locationZ = null;
    private Double yaw = null;
    private Double pitch = null;

    public Location(String server, String worldName, Double locationX, Double locationY, Double locationZ, Double yaw, Double pitch) {
        this.server = server;
        this.worldName = worldName;
        this.locationX = locationX;
        this.locationY = locationY;
        this.locationZ = locationZ;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    /**
     * returns the server ip + port
     *
     * @return the server ip + port
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
    public Double getLocationX() {
        return locationX;
    }

    /**
     * returns the y location
     *
     * @return the y location
     */
    public Double getLocationY() {
        return locationY;
    }

    /**
     * returns the z location
     *
     * @return the z location
     */
    public Double getLocationZ() {
        return locationZ;
    }


    /**
     * returns the yaw
     *
     * @return yaw
     */
    public double getYaw() {
        return yaw;
    }

    /**
     * returns the pitch
     *
     * @return the pitch
     */
    public double getPitch() {
        return pitch;
    }
}
