package io.github.apfelcreme.SupportTickets.Bungee.Command;

import io.github.apfelcreme.SupportTickets.Bungee.Message.BukkitMessenger;
import io.github.apfelcreme.SupportTickets.Bungee.SupportTickets;
import io.github.apfelcreme.SupportTickets.Bungee.Ticket.Comment;
import io.github.apfelcreme.SupportTickets.Bungee.Ticket.Ticket;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bukkit.ChatColor;

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

        if (ticket.getLocation() == null) {
            plugin.sendMessage(sender, "error.noLocation");
            return;
        }

//      BungeeMessenger.sendWarpMessage(player.getUniqueId(), ticket);
        BukkitMessenger.warp(player.getUniqueId(), ticket.getLocation());

        plugin.sendMessage(player, "info.warp.warped",
                String.valueOf(ticket.getTicketId()),
                new SimpleDateFormat("dd.MM.yy HH:mm").format(ticket.getDate()),
                "",
                plugin.getNameByUUID(ticket.getSender()),
                ticket.getMessage(),
                String.valueOf(ticket.getComments().size()));

        plugin.sendMessage(sender, "info.view.comment",
                new SimpleDateFormat("dd.MM.yy HH:mm").format(ticket.getDate()),
                "",
                plugin.getNameByUUID(ticket.getSender()),
                ticket.getMessage());

        for (Comment comment : ticket.getComments()) {
            plugin.sendMessage(sender, "info.view.comment",
                   new SimpleDateFormat("dd.MM.yy HH:mm").format(comment.getDate()),
                   comment.getSenderHasNoticed() ?
                            "" : plugin.getConfig().getText("info.view.new"),
                    plugin.getNameByUUID(comment.getSender()),
                    comment.getComment());

            if (!comment.getSenderHasNoticed() && player.getUniqueId().equals(ticket.getSender())) {
                plugin.getDatabaseController().setCommentRead(comment);
            }
        }
        plugin.addShownTicket(sender, ticket.getTicketId());
    }
}
