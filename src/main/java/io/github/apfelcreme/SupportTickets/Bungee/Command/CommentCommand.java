package io.github.apfelcreme.SupportTickets.Bungee.Command;

import io.github.apfelcreme.SupportTickets.Bungee.SupportTickets;
import io.github.apfelcreme.SupportTickets.Bungee.Ticket.Comment;
import io.github.apfelcreme.SupportTickets.Bungee.Ticket.Ticket;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Date;
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
public class CommentCommand extends SubCommand {

    public CommentCommand(SupportTickets plugin, String name, String usage, String permission, String... aliases) {
        super(plugin, name, usage, permission, aliases);
    }

    /**
     * executes a subcommand
     *
     * @param sender the sender
     * @param args   the string arguments in an array
     */
    public void execute(CommandSender sender, final String[] args) {

        Ticket ticket =  plugin.getDatabaseController().loadTicket(Integer.parseInt(args[1]));
        if (ticket == null) {
            plugin.sendMessage(sender, plugin.getConfig().getText("error.unknownTicket"));
            return;
        }

        if (ticket.getTicketStatus() == Ticket.TicketStatus.CLOSED) {
            plugin.sendMessage(sender, plugin.getConfig().getText("error.ticketAlreadyClosed"));
            return;
        }

        UUID senderId = sender instanceof ProxiedPlayer ? ((ProxiedPlayer) sender).getUniqueId() : new UUID(0, 0);

        if (!ticket.getSender().equals(senderId) && !sender.hasPermission("SupportTickets.mod")) {
            plugin.sendMessage(sender, plugin.getConfig().getText("error.notYourTicket"));
            return;
        }

        String message = "";
        for (int i = 2; i < args.length; i++) {
            message += args[i] + " ";
        }
        message = message.trim();
        Comment comment = new Comment(ticket.getTicketId(), senderId, message, new Date());

        plugin.getDatabaseController().saveComment(comment);

        plugin.sendTeamMessage(plugin.getConfig().getText("info.comment.commented")
                .replace("{0}", sender.getName())
                .replace("{1}", String.valueOf(ticket.getTicketId()))
                .replace("{2}", message));

        plugin.sendMessage(ticket.getSender(),
                plugin.getConfig().getText("info.comment.yourTicketGotCommented")
                        .replace("{0}", String.valueOf(ticket.getTicketId()))
                        .replace("{1}", sender.getName())
                        .replace("{2}", message));
        plugin.addShownTicket(sender, ticket.getTicketId());
    }
}
