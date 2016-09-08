package io.github.apfelcreme.SupportTickets.Bukkit.Database.Controller;

import io.github.apfelcreme.SupportTickets.Bukkit.Ticket.Comment;
import io.github.apfelcreme.SupportTickets.Bukkit.Ticket.Ticket;

import java.util.List;
import java.util.UUID;

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
public interface DatabaseController {

    /**
     * loads a ticket
     *
     * @param ticketId the ticket id
     * @return a ticket
     */
    Ticket loadTicket(Integer ticketId);

    /**
     * saves a ticket
     *
     * @param ticket a ticket
     * @return the ticket id
     */
    int saveTicket(Ticket ticket);

    /**
     * assign a ticket to someone or something
     *
     * @param ticket the ticket
     * @param to     the target
     */
    void assignTicket(Ticket ticket, String to);

    /**
     * unassigns a ticket
     *
     * @param ticket the ticket
     */
    void unassignTicket(Ticket ticket);

    /**
     * closes a ticket
     *
     * @param ticket the ticket
     * @param closer the uuid of the player who closed the ticket
     * @param reason the reason why the ticket was closed
     */
    void closeTicket(Ticket ticket, UUID closer, String reason);

    /**
     * reopens a ticket
     *
     * @param ticket the ticket
     */
    void reopenTicket(Ticket ticket);

    /**
     * returns a list of tickets with the given status
     *
     * @param ticketStatus a ticket status
     * @return a list of tickets
     */
    List<Ticket> getTickets(Ticket.TicketStatus... ticketStatus);

    /**
     * returns a list of tickets which were closed by a given player
     *
     * @param closer the players uuid
     * @return a list of tickets
     */
    List<Ticket> getTicketsClosedBy(UUID closer);

    /**
     * returns a list of tickets which were opened by a given player
     *
     * @param opener the players uuid
     * @return a list of tickets
     */
    List<Ticket> getTicketsOpenedBy(UUID opener);

    /**
     * returns a list of a players tickets
     *
     * @param uuid         the players uuid
     * @param ticketStatus one or more ticket status
     * @return a list of tickets
     */
    List<Ticket> getPlayerTickets(UUID uuid, Ticket.TicketStatus... ticketStatus);

    /**
     * saves a comment
     *
     * @param comment a comment
     */
    void saveComment(Comment comment);

    /**
     * marks a comment as read
     *
     * @param comment the comment
     */
    void setCommentRead(Comment comment);

}
