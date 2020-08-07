package io.github.apfelcreme.SupportTickets.Bungee.Task;

import io.github.apfelcreme.SupportTickets.Bungee.SupportTickets;
import io.github.apfelcreme.SupportTickets.Bungee.SupportTicketsConfig;
import io.github.apfelcreme.SupportTickets.Bungee.Ticket.Ticket;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;

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
public class ReminderTask implements Runnable {

    private final SupportTickets plugin;

    public ReminderTask(SupportTickets plugin) {
        this.plugin = plugin;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    public void run() {
        List<Ticket> tickets = plugin.getDatabaseController()
                .getTickets(Ticket.TicketStatus.OPEN, Ticket.TicketStatus.ASSIGNED, Ticket.TicketStatus.REOPENED);
        if (tickets.size() == 1) {
            plugin.sendMessage("SupportTickets.mod.server." + tickets.get(0).getLocation().getServer(), "info.reminderTask.infoSingular");
        } else if (tickets.size() > 1) {
            for (ProxiedPlayer player : plugin.getProxy().getPlayers()) {
                if (player.hasPermission("SupportTickets.mod")) {
                    long filteredAnz = tickets.stream().filter(t -> player.hasPermission("SupportTickets.mod.server." + t.getLocation().getServer())).count();
                    if (filteredAnz == 1) {
                        plugin.sendMessage(player, "info.reminderTask.infoSingular");
                    } else if (filteredAnz > 1) {
                        plugin.sendMessage(player, "info.reminderTask.infoPlural", "tickets", String.valueOf(filteredAnz));
                    }
                }
            }
        }
    }

}

