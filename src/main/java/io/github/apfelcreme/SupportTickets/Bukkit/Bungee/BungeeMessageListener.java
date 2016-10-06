package io.github.apfelcreme.SupportTickets.Bukkit.Bungee;

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

    public void onPluginMessageReceived(String s, Player p, final byte[] bytes) {
        if (!s.equals("SupportTickets")) {
            return;
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
        String subChannel = in.readUTF();
        if (subChannel.equals("WARP")) {
            UUID uuid = UUID.fromString(in.readUTF());
            Location location = new Location(SupportTickets.getInstance().getServer().getWorld(in.readUTF()),
                    in.readDouble(), in.readDouble(), in.readDouble(),
                    (float) in.readDouble(), (float) in.readDouble());
            warp(uuid, location);
        } else if (subChannel.equals("POSITIONREQUEST")) {
            UUID uuid = UUID.fromString(in.readUTF());
            String message = in.readUTF();
            answerPositionRequest(uuid, message);
        }
    }

    /**
     * answers a position request
     *
     * @param uuid    a players uuid
     * @param message a ticket message (which is just carried around, so it does not have to be mapped in the
     *                bungee instance somewhere to wait for this request to return)
     */
    private void answerPositionRequest(UUID uuid, String message) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);

        try {
            Player player = SupportTickets.getInstance().getServer().getPlayer(uuid);
            if (player != null) {
                out.writeUTF("POSITIONANSWER");
                out.writeUTF(uuid.toString());
                out.writeUTF(SupportTickets.getInstance().getServer().getIp() + ":" + SupportTickets.getInstance().getServer().getPort());
                out.writeUTF(player.getWorld().getName());
                out.writeDouble(player.getLocation().getX());
                out.writeDouble(player.getLocation().getY());
                out.writeDouble(player.getLocation().getZ());
                out.writeDouble((double) player.getLocation().getYaw());
                out.writeDouble((double) player.getLocation().getPitch());
                out.writeUTF(message);
                player.sendPluginMessage(SupportTickets.getInstance(), "SupportTickets", b.toByteArray());
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
        SupportTickets.getInstance().getServer().getScheduler().runTaskLater(SupportTickets.getInstance(), new Runnable() {
            @Override
            public void run() {
                Player player = SupportTickets.getInstance().getServer().getPlayer(uuid);
                if (uuid != null) {
                    player.teleport(location);
                }
            }
        }, 20L);
    }

}
