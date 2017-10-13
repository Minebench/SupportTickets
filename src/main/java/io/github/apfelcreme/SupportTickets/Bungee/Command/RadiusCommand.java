package io.github.apfelcreme.SupportTickets.Bungee.Command;

import io.github.apfelcreme.SupportTickets.Bungee.Message.BukkitMessenger;
import io.github.apfelcreme.SupportTickets.Bungee.SupportTickets;
import io.github.apfelcreme.SupportTickets.Bungee.SupportTicketsConfig;
import io.github.apfelcreme.SupportTickets.Bungee.Ticket.Ticket;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Date;
import java.util.List;

/*
 * Copyright (C) 2017 Max Lee (https://github.com/Phoenix616)
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
 * @author Max Lee aka Phoenix616
 */
public class RadiusCommand extends SubCommand {

    public RadiusCommand(SupportTickets plugin, String name, String usage, String permission, String... aliases) {
        super(plugin, name, usage, permission, aliases);
    }

    /**
     * executes a subcommand
     *
     * @param sender the sender
     * @param args   the string arguments in an array
     */
    public void execute(final CommandSender sender, final String[] args) {
        final ProxiedPlayer player = (ProxiedPlayer) sender;

        int radius = args.length > 1 ? Integer.parseInt(args[1]) : 16;
        BukkitMessenger.fetchPosition(player, (location) -> {
            List<Ticket> tickets = plugin.getDatabaseController().getTicketsInRadius(location, radius);

            plugin.sendMessage(sender, plugin.getConfig().getText("info.radius.header")
                    .replace("{0}", String.valueOf(radius))
                    .replace("{1}", String.valueOf(tickets.size())));

            for (Ticket ticket : tickets) {
                plugin.sendMessage(sender, plugin.getConfig().getText("info.radius.element")
                        .replace("{0}", String.valueOf(ticket.getTicketId()))
                        .replace("{1}", plugin.isPlayerOnline(ticket.getSender())
                                ? plugin.getConfig().getText("info.radius.online")
                                : plugin.getConfig().getText("info.radius.offline"))
                        .replace("{2}", plugin.getNameByUUID(ticket.getSender()))
                        .replace("{3}", ticket.getAssigned() != null ? ticket.getAssigned() : "*")
                        .replace("{4}", ticket.getMessage())
                        .replace("{5}", String.valueOf(ticket.getComments().size()))
                        .replace("{6}", String.valueOf(ticket.getTicketStatus())));
                plugin.addShownTicket(sender, ticket.getTicketId());
            }
        });
    }
}
