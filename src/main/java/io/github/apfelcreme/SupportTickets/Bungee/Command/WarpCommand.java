package io.github.apfelcreme.SupportTickets.Bungee.Command;

import io.github.apfelcreme.SupportTickets.Bungee.Message.BukkitMessenger;
import io.github.apfelcreme.SupportTickets.Bungee.SupportTickets;
import io.github.apfelcreme.SupportTickets.Bungee.Ticket.Comment;
import io.github.apfelcreme.SupportTickets.Bungee.Ticket.Location;
import io.github.apfelcreme.SupportTickets.Bungee.Ticket.Ticket;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

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
public class WarpCommand extends SubCommand {

    public WarpCommand(SupportTickets plugin, String name, String usage, String permission, String... aliases) {
        super(plugin, name, usage, permission, aliases);
    }

    /**
     * executes a subcommand
     *
     * @param sender the sender
     * @param args   the string arguments in an array
     */
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            plugin.sendMessage(sender, "error.playerCommand");
            return;
        }

        final ProxiedPlayer player = (ProxiedPlayer) sender;

        Ticket ticket = plugin.getDatabaseController().loadTicket(Integer.parseInt(args[1]));
        if (ticket == null) {
            plugin.sendMessage(player, "error.unknownTicket");
            return;
        }

        Location location = ticket.getLocation();
        if (args.length > 2) {
            int commentNumber = Integer.parseInt(args[2]);
            if (ticket.getComments().size() < commentNumber) {
                plugin.sendMessage(player, "error.unknownComment");
                return;
            }

            location = ticket.getComments().get(commentNumber - 1).getLocation();
        }

        if (location == null) {
            plugin.sendMessage(sender, "error.noLocation");
            return;
        }

//      BungeeMessenger.sendWarpMessage(player.getUniqueId(), ticket);
        BukkitMessenger.warp(player.getUniqueId(), ticket.getLocation());

        plugin.sendMessage(player, "info.warp.warped",
                "ticket", String.valueOf(ticket.getTicketId()),
                "date", SupportTickets.formatDate(ticket.getDate()),
                "new", "",
                "sender", plugin.getNameByUUID(ticket.getSender()),
                "message", ticket.getMessage(),
                "comments", String.valueOf(ticket.getComments().size()));

        plugin.sendMessage(sender, "info.view.comment",
                "ticket", String.valueOf(ticket.getTicketId()),
                "date", SupportTickets.formatDate(ticket.getDate()),
                "new", "",
                "sender", plugin.getNameByUUID(ticket.getSender()),
                "message", ticket.getMessage(),
                "number", "");

        int i = 1;
        for (Comment comment : ticket.getComments()) {
            plugin.sendMessage(sender, "info.view.comment",
                    "ticket", String.valueOf(ticket.getTicketId()),
                   "date", SupportTickets.formatDate(comment.getDate()),
                    "new", comment.getSenderHasNoticed() ? "" : plugin.getConfig().getText("info.view.new"),
                    "sender", plugin.getNameByUUID(comment.getSender()),
                    "message", comment.getComment(),
                    "number", String.valueOf(i));

            if (!comment.getSenderHasNoticed() && player.getUniqueId().equals(ticket.getSender())) {
                plugin.getDatabaseController().setCommentRead(comment);
            }
            i++;
        }
        if (sender.hasPermission("SupportTickets.mod")) {
            plugin.sendMessage(sender, "info.view.actions","ticket", String.valueOf(ticket.getTicketId()));
        }
        plugin.addShownTicket(sender, ticket.getTicketId());
    }
}
