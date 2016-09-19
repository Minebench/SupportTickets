package io.github.apfelcreme.SupportTickets.Bukkit.Command;

import io.github.apfelcreme.SupportTickets.Bukkit.*;
import io.github.apfelcreme.SupportTickets.Bukkit.Ticket.Comment;
import io.github.apfelcreme.SupportTickets.Bukkit.Ticket.Ticket;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
public class ViewCommand implements SubCommand {

    /**
     * executes a subcommand
     *
     * @param sender the sender
     * @param args   the string arguments in an array
     */
    public void execute(CommandSender sender, final String[] args) {
        final Player player = (Player) sender;
        if (player.hasPermission("SupportTickets.view")) {
            if (args.length > 1) {
                if (SupportTickets.isNumeric(args[1])) {
                    Ticket ticket = SupportTickets.getDatabaseController().loadTicket(Integer.parseInt(args[1]));
                    if (ticket != null) {
                        if (ticket.getSender().equals(player.getUniqueId()) || player.hasPermission("SupportTickets.viewOthers")) {
                            SupportTickets.sendMessage(player, SupportTicketsConfig.getText("info.view.ticket")
                                    .replace("{0}", ticket.getTicketId().toString())
                                    .replace("{1}", new SimpleDateFormat("dd.MM.yy HH:mm").format(ticket.getDate()))
                                    .replace("{2}", SupportTickets.getInstance().getNameByUUID(ticket.getSender())));
                            SupportTickets.sendMessage(player, SupportTicketsConfig.getText("info.view.comment")
                                    .replace("{0}", new SimpleDateFormat("dd.MM.yy HH:mm").format(ticket.getDate()))
                                    .replace("{1}", "")
                                    .replace("{2}", SupportTickets.getInstance().getNameByUUID(ticket.getSender()))
                                    .replace("{3}", ticket.getMessage()));
                            for (Comment comment : ticket.getComments()) {
                                SupportTickets.sendMessage(player, SupportTicketsConfig.getText("info.view.comment")
                                        .replace("{0}", new SimpleDateFormat("dd.MM.yy HH:mm").format(comment.getDate()))
                                        .replace("{1}", comment.getSenderHasNoticed() ?
                                                "" : SupportTicketsConfig.getText("info.view.new"))
                                        .replace("{2}", SupportTickets.getInstance().getNameByUUID(comment.getSender()))
                                        .replace("{3}", comment.getComment()));
                                if (!comment.getSenderHasNoticed() && player.getUniqueId().equals(ticket.getSender())) {
                                    SupportTickets.getDatabaseController().setCommentRead(comment);
                                }
                            }
                        } else {
                            SupportTickets.sendMessage(player, SupportTicketsConfig.getText("error.notYourTicket"));
                        }
                    } else {
                        SupportTickets.sendMessage(player, SupportTicketsConfig.getText("error.unknownTicket"));
                    }
                } else {
                    SupportTickets.sendMessage(player, SupportTicketsConfig.getText("error.wrongUsage")
                            .replace("{0}", "/pe view <#>"));
                }
            } else {
                SupportTickets.sendMessage(player, SupportTicketsConfig.getText("error.wrongUsage")
                        .replace("{0}", "/pe view <#>"));
            }
        } else {
            SupportTickets.sendMessage(sender, SupportTicketsConfig.getText("error.noPermission"));
        }
    }
}
