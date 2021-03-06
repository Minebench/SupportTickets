package io.github.apfelcreme.SupportTickets.Bukkit.Listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import io.github.apfelcreme.SupportTickets.Bukkit.SupportTickets;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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
public class BungeeMessageListener implements PluginMessageListener {
    private final SupportTickets plugin;

    public BungeeMessageListener(SupportTickets plugin) {
        this.plugin = plugin;
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, "tickets:warp", this);
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, "tickets:requestpos", this);
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "tickets:position");
    }

    public void onPluginMessageReceived(String s, Player p, final byte[] bytes) {
        if (!s.startsWith("tickets:")) {
            return;
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
        if (s.equals("tickets:warp")) {
            UUID uuid = UUID.fromString(in.readUTF());
            Location location = new Location(plugin.getServer().getWorld(in.readUTF()),
                    in.readDouble(), in.readDouble(), in.readDouble(),
                    (float) in.readDouble(), (float) in.readDouble());
            warp(uuid, location);
        } else if (s.equals("tickets:requestpos")) {
            UUID uuid = UUID.fromString(in.readUTF());
            answerPositionRequest(uuid);
        }
    }

    /**
     * answers a position request
     *
     * @param uuid    a players uuid
     */
    private void answerPositionRequest(UUID uuid) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);

        try {
            Player player = plugin.getServer().getPlayer(uuid);
            if (player != null) {
                out.writeUTF(uuid.toString());
                out.writeUTF(plugin.getServer().getIp() + ":" + plugin.getServer().getPort());
                out.writeUTF(player.getWorld().getName());
                out.writeDouble(player.getLocation().getX());
                out.writeDouble(player.getLocation().getY());
                out.writeDouble(player.getLocation().getZ());
                out.writeDouble((double) player.getLocation().getYaw());
                out.writeDouble((double) player.getLocation().getPitch());
                player.sendPluginMessage(plugin, "tickets:position", b.toByteArray());
                out.close();
                b.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * teleports a player to a location
     *
     * @param uuid     the players uuid
     * @param location the location
     */
    private void warp(final UUID uuid, final Location location) {
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            Player player = plugin.getServer().getPlayer(uuid);
            if (player != null) {
                player.teleportAsync(location);
            } else {
                plugin.addToQueue(uuid, location);
            }
        }, 20L);
    }

}
