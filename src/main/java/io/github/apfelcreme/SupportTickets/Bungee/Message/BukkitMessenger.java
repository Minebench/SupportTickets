package io.github.apfelcreme.SupportTickets.Bungee.Message;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.github.apfelcreme.SupportTickets.Bungee.SupportTickets;
import io.github.apfelcreme.SupportTickets.Bungee.Ticket.Location;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.UUID;

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

    /**
     * calls bukkit to fetch a players positon. the message is sent with it,
     * so it can be read in the BukkitMessageListener later on
     *
     * @param player  the player
     * @param message the ticket mesage
     */
    public static void fetchPosition(ProxiedPlayer player, String message) {
        ServerInfo target = player.getServer().getInfo();
        if (target != null) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("POSITIONREQUEST");
            out.writeUTF(player.getUniqueId().toString());
            out.writeUTF(message);
            target.sendData("SupportTickets", out.toByteArray());
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
                ServerInfo serverInfo = getTargetServer(location.getServer());
                if (serverInfo != null) {
                    if (!player.getServer().getInfo().equals(serverInfo) && serverInfo.getAddress().getAddress().isReachable(2000)) {
                        player.connect(serverInfo);
                    }

                    ByteArrayDataOutput out = ByteStreams.newDataOutput();
                    out.writeUTF("WARP");
                    out.writeUTF(uuid.toString());
                    out.writeUTF(location.getWorldName());
                    out.writeDouble(location.getLocationX());
                    out.writeDouble(location.getLocationY());
                    out.writeDouble(location.getLocationZ());
                    out.writeFloat(location.getYaw());
                    out.writeFloat(location.getPitch());
                    serverInfo.sendData("SupportTickets", out.toByteArray());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * returns the server info with the given ip (xxx.xxx.xxx.xxx:PORT)
     *
     * @param serverIp the ip:port
     * @return the serverInfo
     */
    private static ServerInfo getTargetServer(String serverIp) {
        for (ServerInfo serverInfo : ProxyServer.getInstance().getServers().values()) {
            if (serverInfo.getAddress().equals(new InetSocketAddress(serverIp.split(":")[0],
                    Integer.parseInt(serverIp.split(":")[1])))) {
                return serverInfo;
            }
        }
        return null;
    }
}
