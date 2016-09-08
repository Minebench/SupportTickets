package io.github.apfelcreme.SupportTickets.Bukkit.Task;

import io.github.apfelcreme.SupportTickets.Bukkit.Bungee.BungeeMessenger;
import io.github.apfelcreme.SupportTickets.Bukkit.SupportTickets;
import io.github.apfelcreme.SupportTickets.Bukkit.SupportTicketsConfig;
import io.github.apfelcreme.SupportTickets.Bukkit.Ticket.Ticket;

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
        List<Ticket> tickets = SupportTickets.getDatabaseController()
                .getTickets(Ticket.TicketStatus.OPEN, Ticket.TicketStatus.ASSIGNED, Ticket.TicketStatus.REOPENED);
        Integer anz = tickets.size();
        if (anz > 0) {
            if (anz == 1) {
                BungeeMessenger.sendTeamMessage(SupportTicketsConfig
                        .getText("info.reminderTask.infoSingular"));
            } else {
                BungeeMessenger.sendTeamMessage(SupportTicketsConfig.getText("info.reminderTask.infoPlural")
                        .replace("{0}", anz.toString()));
            }
        }
    }

}

