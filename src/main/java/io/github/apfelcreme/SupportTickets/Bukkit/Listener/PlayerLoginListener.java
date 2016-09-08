package io.github.apfelcreme.SupportTickets.Bukkit.Listener;

import io.github.apfelcreme.SupportTickets.Bukkit.SupportTickets;
import io.github.apfelcreme.SupportTickets.Bukkit.SupportTicketsConfig;
import io.github.apfelcreme.SupportTickets.Bukkit.Ticket.Comment;
import io.github.apfelcreme.SupportTickets.Bukkit.Ticket.Ticket;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.*;

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
public class PlayerLoginListener implements Listener {

    @EventHandler
    public void onPlayerLogin(final PlayerLoginEvent event) {
        SupportTickets.getInstance().getServer().getScheduler().runTaskLaterAsynchronously(SupportTickets.getInstance(), new Runnable() {
            public void run() {
                List<Ticket> tickets = SupportTickets.getDatabaseController().getTicketsOpenedBy(event.getPlayer().getUniqueId());
                Set<Integer> ticketIds = new HashSet<Integer>();
                for (Ticket ticket : tickets) {
                    for (Comment comment : ticket.getComments()) {
                        if (!comment.getSenderHasNoticed()) {
                            ticketIds.add(ticket.getTicketId());
                        }
                    }
                }

                if (ticketIds.size() > 0) {
                    if (ticketIds.size() == 1) {
                        SupportTickets.sendMessage(event.getPlayer(),
                                SupportTicketsConfig.getText("info.login.newCommentsSingular")
                                        .replace("{0}", ticketIds.iterator().next().toString()));
                    } else {
                        SupportTickets.sendMessage(event.getPlayer(),
                                SupportTicketsConfig.getText("info.login.newCommentsPlural")
                                        .replace("{0}", SupportTickets.join(ticketIds.toArray(), ", ", " & ")));
                    }
                }
            }
        }, 40L);
    }
}
