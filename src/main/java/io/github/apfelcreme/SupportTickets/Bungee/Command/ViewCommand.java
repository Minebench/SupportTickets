package io.github.apfelcreme.SupportTickets.Bungee.Command;

import io.github.apfelcreme.SupportTickets.Bungee.SupportTickets;
import io.github.apfelcreme.SupportTickets.Bungee.SupportTicketsConfig;
import io.github.apfelcreme.SupportTickets.Bungee.Ticket.Comment;
import io.github.apfelcreme.SupportTickets.Bungee.Ticket.Ticket;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.text.SimpleDateFormat;
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
public class ViewCommand extends SubCommand {

    public ViewCommand(SupportTickets plugin, String name, String usage, String permission, String... aliases) {
        super(plugin, name, usage, permission, aliases);
    }

    /**
     * executes a subcommand
     *
     * @param sender the sender
     * @param args   the string arguments in an array
     */
    public void execute(CommandSender sender, final String[] args) {
        Ticket ticket = SupportTickets.getDatabaseController().loadTicket(Integer.parseInt(args[1]));
        if (ticket == null) {
            SupportTickets.sendMessage(sender, plugin.getConfig().getText("error.unknownTicket"));
            return;
        }

        UUID senderId = sender instanceof ProxiedPlayer ? ((ProxiedPlayer) sender).getUniqueId() : new UUID(0, 0);

        if (!ticket.getSender().equals(senderId) && !sender.hasPermission("SupportTickets.mod")) {
            SupportTickets.sendMessage(sender, plugin.getConfig().getText("error.notYourTicket"));
            return;
        }

        SupportTickets.sendMessage(sender, plugin.getConfig().getText("info.view.ticket")
                .replace("{0}", String.valueOf(ticket.getTicketId()))
                .replace("{1}", new SimpleDateFormat("dd.MM.yy HH:mm").format(ticket.getDate()))
                .replace("{2}", plugin.getNameByUUID(ticket.getSender())));

        SupportTickets.sendMessage(sender, plugin.getConfig().getText("info.view.comment")
                .replace("{0}", new SimpleDateFormat("dd.MM.yy HH:mm").format(ticket.getDate()))
                .replace("{1}", "")
                .replace("{2}", plugin.getNameByUUID(ticket.getSender()))
                .replace("{3}", ticket.getMessage()));

        for (Comment comment : ticket.getComments()) {
            SupportTickets.sendMessage(sender, plugin.getConfig().getText("info.view.comment")
                    .replace("{0}", new SimpleDateFormat("dd.MM.yy HH:mm").format(comment.getDate()))
                    .replace("{1}", comment.getSenderHasNoticed() ?
                            "" : plugin.getConfig().getText("info.view.new"))
                    .replace("{2}", plugin.getNameByUUID(comment.getSender()))
                    .replace("{3}", comment.getComment()));

            if (!comment.getSenderHasNoticed() && senderId.equals(ticket.getSender())) {
                SupportTickets.getDatabaseController().setCommentRead(comment);
            }
        }
    }
}
