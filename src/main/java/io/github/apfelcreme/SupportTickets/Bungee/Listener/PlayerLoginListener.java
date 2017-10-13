package io.github.apfelcreme.SupportTickets.Bungee.Listener;

import io.github.apfelcreme.SupportTickets.Bungee.SupportTickets;
import io.github.apfelcreme.SupportTickets.Bungee.SupportTicketsConfig;
import io.github.apfelcreme.SupportTickets.Bungee.Ticket.Comment;
import io.github.apfelcreme.SupportTickets.Bungee.Ticket.Ticket;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import org.bukkit.event.EventHandler;
import java.util.*;
import java.util.concurrent.TimeUnit;

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

    private final SupportTickets plugin;

    public PlayerLoginListener(SupportTickets plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerLogin(final PostLoginEvent event) {
        ProxyServer.getInstance().getScheduler().schedule(SupportTickets.getInstance(), () -> {
            List<Ticket> tickets = plugin.getDatabaseController().getTicketsOpenedBy(event.getPlayer().getUniqueId());
            Set<Integer> ticketIds = new HashSet<>();
            for (Ticket ticket : tickets) {
                for (Comment comment : ticket.getComments()) {
                    if (!comment.getSenderHasNoticed()) {
                        ticketIds.add(ticket.getTicketId());
                    }
                }
            }

            if (ticketIds.size() > 0) {
                if (ticketIds.size() == 1) {
                    plugin.sendMessage(event.getPlayer(), "info.login.newCommentsSingular",
                            ticketIds.iterator().next().toString());
                } else {
                    plugin.sendMessage(event.getPlayer(), "info.login.newCommentsPlural"),
                            SupportTickets.join(ticketIds.toArray(), ", ", " & "));
                }
            }
        }, 2, TimeUnit.SECONDS);
    }
}
