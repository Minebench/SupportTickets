package io.github.apfelcreme.SupportTickets.Bukkit.Command;

import io.github.apfelcreme.SupportTickets.Bukkit.*;
import io.github.apfelcreme.SupportTickets.Bukkit.Bungee.BungeeMessenger;
import io.github.apfelcreme.SupportTickets.Bukkit.Ticket.Location;
import io.github.apfelcreme.SupportTickets.Bukkit.Ticket.Ticket;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Date;

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
public class NewCommand implements SubCommand {

    /**
     * executes a subcommand
     *
     * @param sender the sender
     * @param args   the string arguments in an array
     */
    public void execute(final CommandSender sender, final String[] args) {
        final Player player = (Player) sender;

        if (sender.hasPermission("SupportTickets.new")) {
            if (args.length > 1) {
                String message = "";
                for (int i = 1; i < args.length; i++) {
                    message += args[i] + " ";
                }
                message = message.trim();
                Location location = new Location(
                        player.getServer().getIp() + ":" + player.getServer().getPort(),
                        player.getWorld().getName(),
                        player.getLocation().getX(),
                        player.getLocation().getY(),
                        player.getLocation().getZ(),
                        (double) player.getEyeLocation().getYaw(),
                        (double) player.getEyeLocation().getPitch());
                Ticket ticket = new Ticket(player.getUniqueId(), message, new Date(), location, Ticket.TicketStatus.OPEN);
                Integer ticketId = SupportTickets.getDatabaseController().saveTicket(ticket);
                SupportTickets.sendMessage(sender, SupportTicketsConfig.getText("info.new.created"));
                BungeeMessenger.sendTeamMessage(SupportTicketsConfig.getText("info.new.newTicket")
                        .replace("{0}", ticketId.toString())
                        .replace("{1}", player.getName())
                        .replace("{2}", message));
            } else {
                SupportTickets.sendMessage(sender, SupportTicketsConfig.getText("error.wrongUsage")
                        .replace("{0}", "/pe new <Text>"));
            }
        } else {
            SupportTickets.sendMessage(sender, SupportTicketsConfig.getText("error.noPermission"));
        }
    }
}
