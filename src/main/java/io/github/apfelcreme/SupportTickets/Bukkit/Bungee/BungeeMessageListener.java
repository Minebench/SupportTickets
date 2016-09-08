package io.github.apfelcreme.SupportTickets.Bukkit.Bungee;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import io.github.apfelcreme.SupportTickets.Bukkit.SupportTickets;
import io.github.apfelcreme.SupportTickets.Bukkit.SupportTicketsConfig;
import io.github.apfelcreme.SupportTickets.Bukkit.Ticket.Ticket;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

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

        SupportTickets.getInstance().getServer().getScheduler().runTaskAsynchronously(SupportTickets.getInstance(), new Runnable() {
            public void run() {
                ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
                String subChannel = in.readUTF();
                if (subChannel.equals("WARP")) {
                    UUID uuid = UUID.fromString(in.readUTF());
                    Ticket ticket = SupportTickets.getDatabaseController().loadTicket(in.readInt());
                    if ((ticket != null)) {
                        teleportPlayer(uuid, ticket);
                    }
                } else if (subChannel.equals("STATUSCHANGE")) {
                    UUID uuid = UUID.fromString(in.readUTF());
                    if (in.readBoolean()) {
                        SupportTickets.getInstance().setPlayerOnline(uuid);
                    } else {
                        SupportTickets.getInstance().setPlayerOffline(uuid);
                    }
                }
            }
        });
    }

    /**
     * teleports a player to a tickets location
     *
     * @param uuid   the player uuid
     * @param ticket the ticket
     */
    private void teleportPlayer(final UUID uuid, final Ticket ticket) {
        final World world = SupportTickets.getInstance().getServer().getWorld(ticket.getLocation().getWorldName());
        if (world != null) {
            SupportTickets.getInstance().getServer().getScheduler().runTaskLater(SupportTickets.getInstance(), new Runnable() {
                public void run() {
                    Player player = SupportTickets.getInstance().getServer().getPlayer(uuid);
                    if (player != null) {
                        Location location = new Location(world,
                                ticket.getLocation().getLocationX(),
                                ticket.getLocation().getLocationY(),
                                ticket.getLocation().getLocationZ(),
                                (float) ticket.getLocation().getYaw(),
                                (float) ticket.getLocation().getPitch());
                        player.teleport(location);

                        SupportTickets.sendMessage(player, SupportTicketsConfig.getText("info.warp.youGotWarped")
                                .replace("{0}", ticket.getTicketId().toString()));

                        SupportTickets.sendMessage(player, SupportTicketsConfig.getText("info.list.element")
                                .replace("{0}", ticket.getTicketId().toString())
                                .replace("{1}", SupportTickets.getNameByUUID(ticket.getSender()))
                                .replace("{2}", ticket.getAssigned() != null ? ticket.getAssigned() + ": " : "")
                                .replace("{3}", ticket.getMessage())
                                .replace("{4}", Integer.toString(ticket.getComments().size())));
                    }
                }
            }, SupportTicketsConfig.getTeleportDelay());
        }
    }


}
