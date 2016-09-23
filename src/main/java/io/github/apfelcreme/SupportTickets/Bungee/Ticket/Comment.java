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

    private Integer commentId = null;
    private Integer ticketId = null;
    private UUID sender = null;
    private String comment = null;
    private Boolean senderHasNoticed = null;
    private Date date = null;

    public Comment(Integer commentId, Integer ticketId, UUID sender, String comment, Boolean senderHasNoticed, Date date) {
        this.commentId = commentId;
        this.ticketId = ticketId;
        this.sender = sender;
        this.comment = comment;
        this.senderHasNoticed = senderHasNoticed;
        this.date = date;
    }

    public Comment(Integer ticketId, UUID sender, String comment, Date date) {
        this.ticketId = ticketId;
        this.sender = sender;
        this.comment = comment;
        this.senderHasNoticed = false;
        this.date = date;
    }

    /**
     * returns the comment id
     *
     * @return the comment id
     */
    public Integer getCommentId() {
        return commentId;
    }

    /**
     * returns the ticket id
     *
     * @return the ticket id
     */
    public Integer getTicketId() {
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
    public Boolean getSenderHasNoticed() {
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
