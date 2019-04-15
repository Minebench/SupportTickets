package io.github.apfelcreme.SupportTickets.Bungee.Message;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.github.apfelcreme.SupportTickets.Bungee.SupportTickets;
import io.github.apfelcreme.SupportTickets.Bungee.Ticket.Location;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

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
public class BukkitMessenger {

    private static Map<UUID, BukkitPositionAnswer> queuedPositionRequests = new ConcurrentHashMap<>();

    /**
     * calls bukkit to fetch a players positon. the message is sent with it,
     * so it can be read in the BukkitMessageListener later on
     * @param player  the player
     * @param answer what to do when the answer arrives
     */
    public static void fetchPosition(ProxiedPlayer player, BukkitPositionAnswer answer) {
        ServerInfo target = player.getServer().getInfo();
        if (target != null) {
            queuedPositionRequests.put(player.getUniqueId(), answer);
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF(player.getUniqueId().toString());
            target.sendData("tickets:requestpos", out.toByteArray());
        }
    }


    /**
     * warps a player to a location
     *
     * @param uuid     the players uuid
     * @param location the location
     */
    public static void warp(UUID uuid, Location location) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
        if (player != null) {
            try {
                ServerInfo serverInfo = SupportTickets.getServer(location.getServer());
                if (serverInfo != null) {
                    if (SupportTickets.getInstance().getServerClusters() != null) {
                        SupportTickets.getInstance().getServerClusters().getTeleportUtils().teleportToLocation(
                                player,
                                serverInfo,
                                location.getWorldName(),
                                location.getLocationX(),
                                location.getLocationY(),
                                location.getLocationZ(),
                                (float) location.getYaw(),
                                (float) location.getPitch()
                        );
                    } else {
                        if (!player.getServer().getInfo().equals(serverInfo) && serverInfo.getAddress().getAddress().isReachable(2000)) {
                            player.connect(serverInfo);
                        }

                        ByteArrayDataOutput out = ByteStreams.newDataOutput();
                        out.writeUTF(uuid.toString());
                        out.writeUTF(location.getWorldName());
                        out.writeDouble(location.getLocationX());
                        out.writeDouble(location.getLocationY());
                        out.writeDouble(location.getLocationZ());
                        out.writeDouble(location.getYaw());
                        out.writeDouble(location.getPitch());
                        serverInfo.sendData("tickets:warp", out.toByteArray());
                    }
                } else {
                    SupportTickets.getInstance().getLogger().log(Level.WARNING, "No server found for '" + location.getServer() + "'!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static BukkitPositionAnswer getQueuedPositionAnswer(UUID uuid) {
        return queuedPositionRequests.get(uuid);
    }
}
