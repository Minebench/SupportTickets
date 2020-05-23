package io.github.apfelcreme.SupportTickets.Bungee.Command;

import com.google.common.collect.ImmutableMap;
import de.themoep.minedown.MineDown;
import io.github.apfelcreme.SupportTickets.Bungee.SupportTickets;
import io.github.apfelcreme.SupportTickets.Bungee.Ticket.Comment;
import io.github.apfelcreme.SupportTickets.Bungee.Ticket.Ticket;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
public class MarkReadCommand extends SubCommand {

    public MarkReadCommand(SupportTickets plugin, String name, String usage, String permission, String... aliases) {
        super(plugin, name, usage, permission, aliases);
    }

    /**
     * executes a subcommand
     *  @param sender the sender
     * @param args   the string arguments in an array
     */
    public void execute(CommandSender sender, final String[] args) {
        UUID target = sender instanceof ProxiedPlayer ? ((ProxiedPlayer) sender).getUniqueId() : new UUID(0, 0);
        List<Ticket> tickets = plugin.getDatabaseController().getTicketsOpenedBy(target);
        Set<Integer> ticketEntries = new LinkedHashSet<>();
        int i = 0;
        for (Ticket ticket : tickets) {
            for (Comment comment : ticket.getComments()) {
                if (!comment.getSenderHasNoticed()) {
                    plugin.getDatabaseController().setCommentRead(comment);
                    i++;
                    ticketEntries.add(ticket.getTicketId());
                }
            }
        }

        if (i > 0) {
            String entry = plugin.getConfig().getText("info.markread.ticketEntry");
            plugin.sendMessage(sender, "info.markread.marked",
                    ImmutableMap.of(
                            "comments", TextComponent.fromLegacyText(String.valueOf(i)),
                            "tickets", MineDown.parse(ticketEntries.stream().map(id -> SupportTickets.replace(entry, "ticket", String.valueOf(id))).collect(Collectors.joining(ChatColor.WHITE + ", ")))
                    ));
        } else {
            plugin.sendMessage(sender, "info.markread.none");
        }
    }
}
