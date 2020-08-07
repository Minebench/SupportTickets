package io.github.apfelcreme.SupportTickets.Bungee.Command;

import io.github.apfelcreme.SupportTickets.Bungee.SupportTickets;
import io.github.apfelcreme.SupportTickets.Bungee.Ticket.Ticket;
import net.md_5.bungee.api.CommandSender;

import java.text.DecimalFormat;

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
public class InfoCommand extends SubCommand {

    public InfoCommand(SupportTickets plugin, String name, String usage, String permission, String... aliases) {
        super(plugin, name, usage, permission, aliases);
    }

    /**
     * executes a subcommand
     *
     * @param sender the sender
     * @param args   the string arguments in an array
     */
    @Override
    public void execute(CommandSender sender, String[] args) {
        Ticket ticket = plugin.getDatabaseController().loadTicket(Integer.parseInt(args[1]));
        if (ticket == null) {
            plugin.sendMessage(sender, "error.unknownTicket");
            return;
        }

        if (!sender.hasPermission("SupportTickets.mod.server." + ticket.getLocation().getServer())) {
            plugin.sendMessage(sender, "error.noPermissionOnServer", "server", ticket.getLocation().getServer());
            return;
        }

        plugin.sendMessage(sender, "info.info.text",
                "ticket", String.valueOf(ticket.getTicketId()),
                "sender", plugin.getNameByUUID(ticket.getSender()),
                "date", SupportTickets.formatDate(ticket.getDate()),
                "comments", String.valueOf(ticket.getComments().size()),
                "server", ticket.getLocation().getServer(),
                "x", new DecimalFormat("0").format(ticket.getLocation().getLocationX()),
                "y", new DecimalFormat("0").format(ticket.getLocation().getLocationY()),
                "z", new DecimalFormat("0").format(ticket.getLocation().getLocationZ()),
                "world", ticket.getLocation().getWorldName());
        if (sender.hasPermission("SupportTickets.mod")) {
            plugin.sendMessage(sender, "info.info.actions","ticket", String.valueOf(ticket.getTicketId()));
        }
        plugin.addShownTicket(sender, ticket.getTicketId());
    }
}
