package io.github.apfelcreme.SupportTickets.Bungee.Ticket;

import java.util.Date;
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
public class Comment {

    private final int commentId;
    private final int ticketId;
    private final UUID sender;
    private final String comment;
    private final boolean senderHasNoticed;
    private final Date date;
    private final Location location;

    public Comment(int commentId, int ticketId, UUID sender, String comment, boolean senderHasNoticed, Date date, Location location) {
        this.commentId = commentId;
        this.ticketId = ticketId;
        this.sender = sender;
        this.comment = comment;
        this.senderHasNoticed = senderHasNoticed;
        this.date = date;
        this.location = location;
    }

    public Comment(int ticketId, UUID sender, String comment, Date date, Location location) {
        this(-1, ticketId, sender, comment, false, date, location);
    }

    /**
     * returns the comment id
     *
     * @return the comment id
     */
    public int getCommentId() {
        return commentId;
    }

    /**
     * returns the ticket id
     *
     * @return the ticket id
     */
    public int getTicketId() {
        return ticketId;
    }


    /**
     * returns the uuid of the sender
     *
     * @return the uuid of the sender
     */
    public UUID getSender() {
        return sender;
    }

    /**
     * returns the actual comment string
     *
     * @return the actual comment string
     */
    public String getComment() {
        return comment;
    }

    /**
     * has the sender read the comment already=
     * @return true or false
     */
    public boolean getSenderHasNoticed() {
        return senderHasNoticed;
    }

    /**
     * returns the date the comment was saved
     *
     * @return the date the comment was saved
     */
    public Date getDate() {
        return date;
    }

    /**
     * returns the location that the comment was saved at
     *
     * @return the location that the comment was saved at; might be null for console comments
     */
    public Location getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "commentId=" + commentId +
                ", ticketId=" + ticketId +
                ", sender=" + sender +
                ", comment='" + comment + '\'' +
                ", senderHasNoticed=" + senderHasNoticed +
                ", date=" + date +
                '}';
    }
}
