package io.github.apfelcreme.SupportTickets.Bungee.Command;

import io.github.apfelcreme.SupportTickets.Bungee.SupportTickets;
import io.github.apfelcreme.SupportTickets.Bungee.SupportTicketsConfig;
import io.github.apfelcreme.SupportTickets.Bungee.Ticket.Ticket;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

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
        Ticket ticket = SupportTickets.getDatabaseController().loadTicket(Integer.parseInt(args[1]));
        if (ticket == null) {
            SupportTickets.sendMessage(sender, plugin.getConfig().getText("error.unknownTicket"));
            return;
        }

        SupportTickets.sendMessage(sender, plugin.getConfig().getText("info.info.info")
                .replace("{0}", String.valueOf(ticket.getTicketId()))
                .replace("{1}", plugin.getNameByUUID(ticket.getSender())));
        SupportTickets.sendMessage(sender, plugin.getConfig().getText("info.info.date")
                .replace("{0}", new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(ticket.getDate())));
        SupportTickets.sendMessage(sender, plugin.getConfig().getText("info.info.comments")
                .replace("{0}", String.valueOf(ticket.getComments().size()))
                .replace("{1}", String.valueOf(ticket.getTicketId())));
        SupportTickets.sendMessage(sender, plugin.getConfig().getText("info.info.location")
                .replace("{0}", ticket.getLocation().getServer())
                .replace("{1}", new DecimalFormat("0").format(ticket.getLocation().getLocationX()))
                .replace("{2}", new DecimalFormat("0").format(ticket.getLocation().getLocationY()))
                .replace("{3}", new DecimalFormat("0").format(ticket.getLocation().getLocationZ()))
                .replace("{4}", ticket.getLocation().getWorldName()));

        plugin.addShownTicket(sender, ticket.getTicketId());
    }
}
