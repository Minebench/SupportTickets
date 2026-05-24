package io.github.apfelcreme.SupportTickets.Bungee.Message;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.themoep.connectorplugin.LocationInfo;
import de.themoep.connectorplugin.bungee.BungeeConnectorPlugin;
import io.github.apfelcreme.SupportTickets.Bungee.SupportTickets;
import io.github.apfelcreme.SupportTickets.Bungee.Ticket.Location;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
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

    private static final BungeeConnectorPlugin connector;

    static {
        connector = (BungeeConnectorPlugin) SupportTickets.getInstance().getProxy().getPluginManager().getPlugin("ConnectorPlugin");
    }

    /**
     * calls bukkit to fetch a sender's positon. the message is sent with it,
     * so it can be read in the BukkitMessageListener later on
     * @param sender the sender
     * @param answer what to do when the answer arrives
     */
    public static void fetchPosition(CommandSender sender, Consumer<Location> answer) {
        if (sender instanceof ProxiedPlayer player) {
            if (player.getServer() != null) {
                connector.getBridge().getLocation(player).thenAccept(location -> {
                    answer.accept(new Location(
                            location.getServer(),
                            location.getWorld(),
                            location.getX(),
                            location.getY(),
                            location.getZ(),
                            location.getYaw(),
                            location.getPitch()
                    ));
                });
            } else {
                answer.accept(null);
            }
        } else {
            answer.accept(null);
        }
    }


    /**
     * warps a player to a location
     *
     * @param player   the player
     * @param location the location
     */
    public static void warp(ProxiedPlayer player, Location location) {
        connector.getBridge().teleport(player, new LocationInfo(
                location.getServer(),
                location.getWorldName(),
                location.getLocationX(),
                location.getLocationY(),
                location.getLocationZ(),
                location.getYaw(),
                location.getPitch()
        ), player::sendMessage);
    }
}
