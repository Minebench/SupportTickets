package io.github.apfelcreme.SupportTickets.Bungee.Command;

import io.github.apfelcreme.SupportTickets.Bungee.SupportTickets;
import io.github.apfelcreme.SupportTickets.Bungee.SupportTicketsConfig;
import io.github.apfelcreme.SupportTickets.Bungee.Ticket.Comment;
import io.github.apfelcreme.SupportTickets.Bungee.Ticket.Ticket;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

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
public class ReopenCommand implements SubCommand {

    /**
     * executes a subcommand
     *
     * @param sender the sender
     * @param args   the string arguments in an array
     */
    public void execute(CommandSender sender, String[] args) {
        final ProxiedPlayer player = (ProxiedPlayer) sender;
        if (player.hasPermission("SupportTickets.mod")) {
            if (args.length > 1) {
                if (SupportTickets.isNumeric(args[1])) {
                    Ticket ticket = SupportTickets.getDatabaseController().loadTicket(Integer.parseInt(args[1]));
                    if (ticket != null) {

                        SupportTickets.getDatabaseController().reopenTicket(ticket);
                        Comment comment = new Comment(
                                ticket.getTicketId(),
                                player.getUniqueId(),
                                SupportTicketsConfig.getInstance().getText("info.reopen.reopenComment")
                                        .replace("{0}", player.getName()),
                                new Date());

                        SupportTickets.getDatabaseController().saveComment(comment);

                        SupportTickets.sendTeamMessage(SupportTicketsConfig.getInstance().getText("info.reopen.reopened")
                                .replace("{0}", ticket.getTicketId().toString()));
                        SupportTickets.sendMessage(ticket.getSender(),
                                SupportTicketsConfig.getInstance().getText("info.reopen.yourTicketGotReopened")
                                        .replace("{0}", ticket.getTicketId().toString()));
                    } else {
                        SupportTickets.sendMessage(player, SupportTicketsConfig.getInstance().getText("error.unknownTicket"));
                    }
                } else {
                    SupportTickets.sendMessage(player, SupportTicketsConfig.getInstance().getText("error.wrongUsage")
                            .replace("{0}", "/pe reopen <#>"));
                }
            } else {
                SupportTickets.sendMessage(player, SupportTicketsConfig.getInstance().getText("error.wrongUsage")
                        .replace("{0}", "/pe reopen <#>"));
            }
        } else {
            SupportTickets.sendMessage(sender, SupportTicketsConfig.getInstance().getText("error.noPermission"));
        }
    }
}
