package io.github.apfelcreme.SupportTickets.Bungee.Event;

/*
 * SupportTickets
 * Copyright (c) 2020 Max Lee aka Phoenix616 (max@themoep.de)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import io.github.apfelcreme.SupportTickets.Bungee.Ticket.Ticket;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Event;

public class TicketCloseEvent extends Event {
    private final Ticket ticket;
    private final CommandSender sender;
    private final String reason;

    public TicketCloseEvent(Ticket ticket, CommandSender sender, String reason) {
        this.ticket = ticket;
        this.sender = sender;
        this.reason = reason;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public CommandSender getSender() {
        return sender;
    }

    public String getReason() {
        return reason;
    }
}
