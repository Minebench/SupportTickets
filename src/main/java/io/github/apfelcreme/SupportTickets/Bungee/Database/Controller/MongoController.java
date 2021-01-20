package io.github.apfelcreme.SupportTickets.Bungee.Database.Controller;

import com.mongodb.*;
import io.github.apfelcreme.SupportTickets.Bungee.Database.Connector.MongoConnector;
import io.github.apfelcreme.SupportTickets.Bungee.SupportTickets;
import io.github.apfelcreme.SupportTickets.Bungee.Ticket.Comment;
import io.github.apfelcreme.SupportTickets.Bungee.Ticket.Location;
import io.github.apfelcreme.SupportTickets.Bungee.Ticket.Ticket;

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
public class MongoController implements DatabaseController {

    private final SupportTickets plugin;
    private final MongoConnector connector;

    public MongoController(SupportTickets plugin) {
        this.plugin = plugin;
        connector = new MongoConnector(plugin);
    }

    /**
     * loads a ticket
     *
     * @param ticketId the ticket id
     * @return a ticket
     */
    @Override
    public Ticket loadTicket(Integer ticketId) {
        DBCollection collection = connector.getCollection();
        BasicDBObject query = new BasicDBObject();
        query.put("ticket_id", ticketId);
        DBCursor dbCursor = collection.find(query);
        if (dbCursor.hasNext()) {
            return buildTicket(dbCursor.next());
        }
        return null;
    }

    /**
     * saves a ticket
     *
     * @param ticket a ticket
     * @return the ticket id
     */
    @Override
        public int saveTicket(Ticket ticket) {
            DBCollection collection = connector.getCollection();
            BasicDBObject ticketObject = new BasicDBObject();
            ticketObject.put("ticket_id", (int) collection.count());
            ticketObject.put("sender", ticket.getSender().toString());
            ticketObject.put("message", ticket.getMessage());
            ticketObject.put("status", Ticket.TicketStatus.OPEN.toInt());
            ticketObject.put("time_stamp", ticket.getDate().getTime());
            if (ticket.getLocation() != null) {
                ticketObject.put("server", ticket.getLocation().getServer());
                ticketObject.put("world", ticket.getLocation().getWorldName());
                ticketObject.put("loc_X", ticket.getLocation().getLocationX());
                ticketObject.put("loc_Y", ticket.getLocation().getLocationY());
                ticketObject.put("loc_Z", ticket.getLocation().getLocationZ());
                ticketObject.put("yaw", ticket.getLocation().getYaw());
                ticketObject.put("pitch", ticket.getLocation().getPitch());
            }

            collection.insert(ticketObject);

            return ticketObject.getInt("ticket_id");
    }

    /**
     * assign a ticket to someone or something
     *
     * @param ticket the ticket
     * @param to     the target
     */
    @Override
    public void assignTicket(Ticket ticket, String to) {
        DBCollection collection = connector.getCollection();
        BasicDBObject query = new BasicDBObject();
        query.put("ticket_id", ticket.getTicketId());
        DBCursor dbCursor = collection.find(query);
        if (dbCursor.hasNext()) {
            DBObject ticketObject = dbCursor.next();
            ticketObject.put("assigned", to);
            ticketObject.put("assigned_time_stamp", new Date().getTime());
            ticketObject.put("status", Ticket.TicketStatus.ASSIGNED.toInt());
            collection.update(query, ticketObject);
        }
    }

    /**
     * unassigns a ticket
     *
     * @param ticket the ticket
     */
    @Override
    public void unassignTicket(Ticket ticket) {
        DBCollection collection = connector.getCollection();
        BasicDBObject query = new BasicDBObject();
        query.put("ticket_id", ticket.getTicketId());
        DBCursor dbCursor = collection.find(query);
        if (dbCursor.hasNext()) {
            DBObject ticketObject = dbCursor.next();
            ticketObject.removeField("assigned");
            ticketObject.removeField("assigned_time_stamp");
            ticketObject.put("status", Ticket.TicketStatus.OPEN.toInt());
            collection.update(query, ticketObject);
        }

    }

    /**
     * closes a ticket
     *
     * @param ticket the ticket
     * @param closer the uuid of the player who closed the ticket
     * @param reason the reason why the ticket was closed
     */
    @Override
    public void closeTicket(Ticket ticket, UUID closer, String reason) {

        DBCollection collection = connector.getCollection();
        BasicDBObject query = new BasicDBObject();
        query.put("ticket_id", ticket.getTicketId());
        DBCursor dbCursor = collection.find(query);
        if (dbCursor.hasNext()) {
            DBObject ticketObject = dbCursor.next();
            ticketObject.put("closer", closer.toString());
            ticketObject.put("closed_time_stamp", new Date().getTime());
            ticketObject.put("status", Ticket.TicketStatus.CLOSED.toInt());
            collection.update(query, ticketObject);
        }
    }

    /**
     * reopens a ticket
     *
     * @param ticket the ticket
     */
    @Override
    public void reopenTicket(Ticket ticket) {

        DBCollection collection = connector.getCollection();
        BasicDBObject query = new BasicDBObject();
        query.put("ticket_id", ticket.getTicketId());
        DBCursor dbCursor = collection.find(query);
        if (dbCursor.hasNext()) {
            DBObject ticketObject = dbCursor.next();
            ticketObject.put("status", Ticket.TicketStatus.REOPENED.toInt());
            collection.update(query, ticketObject);
        }
    }

    /**
     * returns a list of tickets with the given status
     *
     * @param ticketStatus a ticket status
     * @return a list of tickets
     */
    @Override
    public List<Ticket> getTickets(Ticket.TicketStatus... ticketStatus) {
        List<Ticket> tickets = new ArrayList<>();
        DBCollection collection = connector.getCollection();
        BasicDBObject query = new BasicDBObject();
        BasicDBList or = new BasicDBList();
        for (Ticket.TicketStatus status : ticketStatus) {
            or.add(new BasicDBObject("status", status.toInt()));
        }
        query.put("$or", or);
        DBCursor dbCursor = collection.find(query).sort(new BasicDBObject("ticket_id", 1));
        while (dbCursor.hasNext()) {
            tickets.add(buildTicket(dbCursor.next()));
        }
        return tickets;
    }

    /**
     * returns a list of tickets which were closed by a given player
     *
     * @param closer the players uuid
     * @return a list of tickets
     */
    @Override
    public List<Ticket> getTicketsClosedBy(UUID closer) {

        List<Ticket> tickets = new ArrayList<>();
        DBCollection collection = connector.getCollection();
        BasicDBObject query = new BasicDBObject();
        query.put("closer", closer.toString());
        DBCursor dbCursor = collection.find(query).sort(new BasicDBObject("ticket_id", 1));
        while (dbCursor.hasNext()) {
            tickets.add(buildTicket(dbCursor.next()));
        }
        return tickets;
    }

    /**
     * returns a list of tickets which were opened by a given player
     *
     * @param opener the players uuid
     * @return a list of tickets
     */
    @Override
    public List<Ticket> getTicketsOpenedBy(UUID opener) {

        List<Ticket> tickets = new ArrayList<>();
        DBCollection collection = connector.getCollection();
        BasicDBObject query = new BasicDBObject();
        query.put("sender", opener.toString());
        DBCursor dbCursor = collection.find(query).sort(new BasicDBObject("ticket_id", 1));
        while (dbCursor.hasNext()) {
            tickets.add(buildTicket(dbCursor.next()));
        }
        return tickets;
    }

    /**
     * returns a list of a players tickets
     *
     * @param uuid         the players uuid
     * @param ticketStatus one or more ticket status
     * @return a list of tickets
     */
    @Override
    public List<Ticket> getPlayerTickets(UUID uuid, Ticket.TicketStatus... ticketStatus) {
        List<Ticket> tickets = new ArrayList<>();
        DBCollection collection = connector.getCollection();
        BasicDBObject query = new BasicDBObject();
        query.put("sender", uuid.toString());
        BasicDBList or = new BasicDBList();
        for (Ticket.TicketStatus status : ticketStatus) {
            or.add(new BasicDBObject("status", status.toInt()));
        }
        query.put("$or", or);
        DBCursor dbCursor = collection.find(query).sort(new BasicDBObject("ticket_id", 1));
        while (dbCursor.hasNext()) {
            tickets.add(buildTicket(dbCursor.next()));
        }
        return tickets;
    }

    @Override
    public List<Ticket> getTicketsInRadius(Location location, int radius) {
        List<Ticket> tickets = new ArrayList<>();
        DBCollection collection = connector.getCollection();
        BasicDBObject query = new BasicDBObject();
        query.put("server", location.getServer());
        query.put("world", location.getWorldName());
        query.put("loc_X",
                new BasicDBObject("$gt", location.getLocationX() - radius)
                        .append("$lt", location.getLocationX() + radius));
        query.put("loc_Y",
                new BasicDBObject("$gt", location.getLocationY() - radius)
                        .append("$lt", location.getLocationY() + radius));
        query.put("loc_Z",
                new BasicDBObject("$gt", location.getLocationZ() - radius)
                        .append("$lt", location.getLocationZ() + radius));
        DBCursor dbCursor = collection.find(query).sort(new BasicDBObject("ticket_id", 1));
        while (dbCursor.hasNext()) {
            tickets.add(buildTicket(dbCursor.next()));
        }
        return tickets;
    }

    /**
     * saves a comment
     *
     * @param comment a comment
     */
    @Override
    public void saveComment(Comment comment) {
        DBCollection collection = connector.getCollection();
        BasicDBObject query = new BasicDBObject();
        query.put("ticket_id", comment.getTicketId());
        DBCursor dbCursor = collection.find(query);
        if (dbCursor.hasNext()) {
            DBObject ticketObject = dbCursor.next();
            BasicDBList comments = (BasicDBList) ticketObject.get("comments");
            if (comments == null) {
                comments = new BasicDBList();
            }
            BasicDBObject commentObject = new BasicDBObject();
            commentObject.put("comment_id", comments.size());
            commentObject.put("sender", comment.getSender().toString());
            commentObject.put("comment", comment.getComment());
            commentObject.put("date", comment.getDate().getTime());
            if (comment.getLocation() != null) {
                commentObject.put("server", comment.getLocation().getServer());
                commentObject.put("world", comment.getLocation().getWorldName());
                commentObject.put("loc_X", comment.getLocation().getLocationX());
                commentObject.put("loc_Y", comment.getLocation().getLocationY());
                commentObject.put("loc_Z", comment.getLocation().getLocationZ());
                commentObject.put("yaw", comment.getLocation().getYaw());
                commentObject.put("pitch", comment.getLocation().getPitch());
            }
            commentObject.put("sender_has_noticed", comment.getSenderHasNoticed());
            comments.add(commentObject);
            ticketObject.put("comments", comments);
            collection.update(query, ticketObject);
        }
    }

    /**
     * marks a comment as read
     *
     * @param comment the comment
     */
    @Override
    public void setCommentRead(Comment comment) {
        if (comment.getCommentId() == -1) {
            return;
        }
        DBCollection collection = connector.getCollection();
        BasicDBObject query = new BasicDBObject();
        query.put("ticket_id", comment.getTicketId());
        DBCursor dbCursor = collection.find(query);
        if (dbCursor.hasNext()) {
            DBObject ticketObject = dbCursor.next();
            BasicDBList comments = (BasicDBList) ticketObject.get("comments");
            for (Object comment1 : comments) {
                BasicDBObject commentObject = (BasicDBObject) comment1;
                if (commentObject.getInt("comment_id") == comment.getCommentId()) {
                    commentObject.put("sender_has_noticed", true);
                }
            }
            ticketObject.put("comments", comments);
            collection.update(query, ticketObject);
        }
    }

    @Override
    public void disable() {
        connector.close();
    }

    /**
     * builds a ticket from a mongo dbObject
     *
     * @param dbObject a dbObject
     * @return a ticket
     */
    private Ticket buildTicket(DBObject dbObject) {

        // create a ticket object
        Ticket ticket = new Ticket(
                UUID.fromString((String) dbObject.get("sender")),
                (String) dbObject.get("message"),
                new Date((Long) dbObject.get("time_stamp")),
                hasLocation(dbObject) ? new Location(
                        (String) dbObject.get("server"),
                        (String) dbObject.get("world"),
                        (double) dbObject.get("loc_X"),
                        (double) dbObject.get("loc_Y"),
                        (double) dbObject.get("loc_Z"),
                        (double) dbObject.get("yaw"),
                        (double) dbObject.get("pitch")
                ) : null,
                Ticket.TicketStatus.fromInt((Integer) dbObject.get("status")));
        ticket.setTicketId((Integer) dbObject.get("ticket_id"));


        // load the assign text
        if (dbObject.get("assigned") != null) {
            ticket.setAssigned((String) dbObject.get("assigned"));
            ticket.setAssignedDate(new Date((Long) dbObject.get("assigned_time_stamp")));
        }

        // load the player who might have closed the ticket
        if (dbObject.get("closer") != null) {
            ticket.setClosed(UUID.fromString((String) dbObject.get("closer")));
            ticket.setClosedDate(new Date((Long) dbObject.get("closed_time_stamp")));
        }

        // load all comments
        BasicDBList comments = (BasicDBList) dbObject.get("comments");
        if (comments != null) {
            for (Object o : comments) {
                BasicDBObject commentObject = (BasicDBObject) o;
                Location location = null;
                if (hasLocation(dbObject)) {
                    location = new Location(
                            (String) dbObject.get("server"),
                            (String) dbObject.get("world"),
                            (double) dbObject.get("loc_X"),
                            (double) dbObject.get("loc_Y"),
                            (double) dbObject.get("loc_Z"),
                            (double) dbObject.get("yaw"),
                            (double) dbObject.get("pitch")
                    );
                }
                Comment comment = new Comment(
                        commentObject.getInt("comment_id"),
                        ticket.getTicketId(),
                        UUID.fromString(commentObject.getString("sender")),
                        commentObject.getString("comment"),
                        commentObject.getBoolean("sender_has_noticed"),
                        new Date(commentObject.getLong("date")),
                        location);
                ticket.getComments().add(comment);
            }
        }
        return ticket;
    }

    private static boolean hasLocation(DBObject dbObject) {
        return dbObject.containsField("server")
                && dbObject.containsField("world")
                && dbObject.containsField("loc_X")
                && dbObject.containsField("loc_Y")
                && dbObject.containsField("loc_Z")
                && dbObject.containsField("yaw")
                && dbObject.containsField("pitch");
    }
}
