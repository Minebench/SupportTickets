package io.github.apfelcreme.SupportTickets.Bungee.Ticket;

import java.util.ArrayList;
import java.util.Date;
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
public class Ticket {

    private int ticketId = -1;
    private final UUID sender;
    private UUID closed = null;
    private final List<Comment> comments;
    private final Date date;
    private String assigned = null;
    private Date assignedDate = null;
    private Date closedDate = null;
    private final String message;
    private final Location location;
    private final TicketStatus ticketStatus;

    public Ticket(UUID sender, String message, Date date, Location location, TicketStatus ticketStatus) {
        this.sender = sender;
        this.message = message;
        this.date = date;
        this.location = location;
        this.ticketStatus = ticketStatus;
        comments = new ArrayList<>();
    }

    public Ticket(int ticketId, UUID sender, UUID closed, List<Comment> comments, Date date, String assigned,
                  Date assignedDate, Date closedDate, String message, Location location, TicketStatus ticketStatus) {
        this.ticketId = ticketId;
        this.sender = sender;
        this.closed = closed;
        this.comments = comments;
        this.date = date;
        this.assigned = assigned;
        this.assignedDate = assignedDate;
        this.closedDate = closedDate;
        this.message = message;
        this.location = location;
        this.ticketStatus = ticketStatus;
    }

    /**
     * returns the ticket Id
     *
     * @return the ticket Id
     */
    public int getTicketId() {
        return ticketId;
    }

    /**
     * sets the ticket id
     *
     * @param ticketId the ticket id
     */
    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    /**
     * returns the senders uuid
     *
     * @return the senders uuid
     */
    public UUID getSender() {
        return sender;
    }

    /**
     * gets the assignment target
     *
     * @return the assignment target
     */
    public String getAssigned() {
        return assigned;
    }

    /**
     * sets the assignment target
     *
     * @param assigned the assignment target
     */
    public void setAssigned(String assigned) {
        this.assigned = assigned;
    }

    /**
     * returns the uuid of the player who closed the ticket
     *
     * @return the uuid of the player who closed the ticket
     */
    public UUID getClosed() {
        return closed;
    }

    /**
     * returns the uuid of the player who closed the ticket
     *
     * @param closed the uuid of the player who closed the ticket
     */
    public void setClosed(UUID closed) {
        this.closed = closed;
    }

    /**
     * returns the list of comments
     *
     * @return the list of comments
     */
    public List<Comment> getComments() {
        return comments;
    }

    /**
     * returns the creation date
     *
     * @return the creation date
     */
    public Date getDate() {
        return date;
    }

    /**
     * returns the date when the ticket was assigned to someone or something
     *
     * @return the date when the ticket was assigned to someone or something
     */
    public Date getAssignedDate() {
        return assignedDate;
    }

    /**
     * sets the date when the ticket was assigned to someone or something
     *
     * @param assignedDate the date when the ticket was assigned to someone or something
     */
    public void setAssignedDate(Date assignedDate) {
        this.assignedDate = assignedDate;
    }

    /**
     * returns the date when the ticket was closed
     *
     * @return the date when the ticket was closed
     */
    public Date getClosedDate() {
        return closedDate;
    }

    /**
     * sets the date when the ticket was closed
     *
     * @param closedDate the date when the ticket was closed
     */
    public void setClosedDate(Date closedDate) {
        this.closedDate = closedDate;
    }

    /**
     * returns the ticket message
     *
     * @return the ticket message
     */
    public String getMessage() {
        return message;
    }

    /**
     * returns the location where the ticket was opened
     *
     * @return the location where the ticket was opened
     */
    public Location getLocation() {
        return location;
    }

    /**
     * returns the ticket status
     *
     * @return the ticket status
     */
    public TicketStatus getTicketStatus() {
        return ticketStatus;
    }

    /**
     * the status of the ticket
     */
    public enum TicketStatus {
        OPEN, CLOSED, REOPENED, ASSIGNED;

        /**
         * returns the matching number
         *
         * @return a matching number
         */
        public Integer toInt() {
            switch (this) {
                case OPEN:
                    return 0;
                case CLOSED:
                    return 1;
                case REOPENED:
                    return 2;
                case ASSIGNED:
                    return 3;
            }
            return 0;
        }

        /**
         * returns the matching TicketStatus
         *
         * @param status a number
         * @return a matching ticketStatus
         */
        public static TicketStatus fromInt(Integer status) {
            switch (status) {
                case 0:
                    return TicketStatus.OPEN;
                case 1:
                    return TicketStatus.CLOSED;
                case 2:
                    return TicketStatus.REOPENED;
                case 3:
                    return TicketStatus.ASSIGNED;
                default:
                    return null;
            }
        }
    }


}
