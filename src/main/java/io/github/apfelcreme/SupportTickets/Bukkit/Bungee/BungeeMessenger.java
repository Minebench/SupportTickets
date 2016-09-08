package io.github.apfelcreme.SupportTickets.Bukkit.Bungee;

import io.github.apfelcreme.SupportTickets.Bukkit.SupportTickets;
import io.github.apfelcreme.SupportTickets.Bukkit.Ticket.Ticket;
import org.bukkit.entity.Player;

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
public class BungeeMessenger {

    /**
     * sends a message to a player
     *
     * @param uuid    the player uuid
     * @param message the message
     */
    public static void sendMessage(UUID uuid, String message) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);

        try {
            if (SupportTickets.getInstance().getServer().getOnlinePlayers().size() > 0) {
                out.writeUTF("MESSAGE");
                out.writeUTF(uuid.toString());
                out.writeUTF(SupportTickets.getPrefix() + message);
                Player player = SupportTickets.getInstance().getServer().getOnlinePlayers().iterator().next();
                player.sendPluginMessage(SupportTickets.getInstance(), "SupportTickets", b.toByteArray());
                out.close();
                b.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * sends a message to a group of players with the permission 'SupportTickets.team' on Bungee
     *
     * @param message the message
     */
    public static void sendTeamMessage(String message) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);

        try {
            if (SupportTickets.getInstance().getServer().getOnlinePlayers().size() > 0) {
                out.writeUTF("TEAMMESSAGE");
                out.writeUTF(SupportTickets.getPrefix() + message);
                Player player = SupportTickets.getInstance().getServer().getOnlinePlayers().iterator().next();
                player.sendPluginMessage(SupportTickets.getInstance(), "SupportTickets", b.toByteArray());
                out.close();
                b.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * sends a player to a ticket location
     *
     * @param uuid   the player uuid
     * @param ticket the ticket
     */
    public static void sendWarpMessage(UUID uuid, Ticket ticket) {

        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);

        try {
            if (SupportTickets.getInstance().getServer().getOnlinePlayers().size() > 0) {
                out.writeUTF("WARP");
                out.writeUTF(uuid.toString());
                out.writeInt(ticket.getTicketId());
                out.writeUTF(ticket.getLocation().getServer());
                Player player = SupportTickets.getInstance().getServer().getOnlinePlayers().iterator().next();
                player.sendPluginMessage(SupportTickets.getInstance(), "SupportTickets", b.toByteArray());
                out.close();
                b.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
