package io.github.apfelcreme.SupportTickets.Bungee.Database.Controller;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import io.github.apfelcreme.SupportTickets.Bungee.Database.Connector.MongoConnector;
import io.github.apfelcreme.SupportTickets.Bungee.SupportTickets;
import io.github.apfelcreme.SupportTickets.Bungee.Ticket.Comment;
import io.github.apfelcreme.SupportTickets.Bungee.Ticket.Location;
import io.github.apfelcreme.SupportTickets.Bungee.Ticket.Ticket;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
        MongoCollection<Document> collection = connector.getCollection();
        MongoCursor<Document> dbCursor = collection.find(Filters.eq("ticket_id", ticketId)).cursor();
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
        MongoCollection<Document> collection = connector.getCollection();
        Document ticketObject = new Document();
        ticketObject.put("ticket_id", (int) collection.countDocuments());
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

        collection.insertOne(ticketObject);

        return ticketObject.getInteger("ticket_id");
    }

    /**
     * assign a ticket to someone or something
     *
     * @param ticket the ticket
     * @param to     the target
     */
    @Override
    public void assignTicket(Ticket ticket, String to) {
        MongoCollection<Document> collection = connector.getCollection();
        Bson query = Filters.eq("ticket_id", ticket.getTicketId());
        MongoCursor<Document> dbCursor = collection.find(query).cursor();
        if (dbCursor.hasNext()) {
            Document ticketObject = dbCursor.next();
            ticketObject.put("assigned", to);
            ticketObject.put("assigned_time_stamp", new Date().getTime());
            ticketObject.put("status", Ticket.TicketStatus.ASSIGNED.toInt());
            collection.replaceOne(query, ticketObject);
        }
    }

    /**
     * unassigns a ticket
     *
     * @param ticket the ticket
     */
    @Override
    public void unassignTicket(Ticket ticket) {
        MongoCollection<Document> collection = connector.getCollection();
        Bson query = Filters.eq("ticket_id", ticket.getTicketId());
        MongoCursor<Document> dbCursor = collection.find(query).cursor();
        if (dbCursor.hasNext()) {
            Document ticketObject = dbCursor.next();
            ticketObject.remove("assigned");
            ticketObject.remove("assigned_time_stamp");
            ticketObject.put("status", Ticket.TicketStatus.OPEN.toInt());
            collection.replaceOne(query, ticketObject);
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
        MongoCollection<Document> collection = connector.getCollection();
        Bson query = Filters.eq("ticket_id", ticket.getTicketId());
        MongoCursor<Document> dbCursor = collection.find(query).cursor();
        if (dbCursor.hasNext()) {
            Document ticketObject = dbCursor.next();
            ticketObject.put("closer", closer.toString());
            ticketObject.put("closed_time_stamp", new Date().getTime());
            ticketObject.put("status", Ticket.TicketStatus.CLOSED.toInt());
            collection.replaceOne(query, ticketObject);
        }
    }

    /**
     * reopens a ticket
     *
     * @param ticket the ticket
     */
    @Override
    public void reopenTicket(Ticket ticket) {
        MongoCollection<Document> collection = connector.getCollection();
        Bson query = Filters.eq("ticket_id", ticket.getTicketId());
        MongoCursor<Document> dbCursor = collection.find(query).cursor();
        if (dbCursor.hasNext()) {
            collection.updateOne(query, Updates.set("status", Ticket.TicketStatus.REOPENED.toInt()));
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
        MongoCollection<Document> collection = connector.getCollection();
        Bson query = Filters.or(Arrays.stream(ticketStatus)
                .map(s -> Filters.eq("status", s.toInt()))
                .collect(Collectors.toList()));
        MongoCursor<Document> dbCursor = collection.find(query).sort(Sorts.ascending("ticket_id")).cursor();
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
        MongoCollection<Document> collection = connector.getCollection();
        MongoCursor<Document> dbCursor = collection.find(Filters.eq("closer", closer.toString()))
                .sort(Sorts.ascending("ticket_id")).cursor();
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
        MongoCollection<Document> collection = connector.getCollection();
        MongoCursor<Document> dbCursor = collection.find(Filters.eq("sender", opener.toString()))
                .sort(Sorts.ascending("ticket_id")).cursor();
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
        MongoCollection<Document> collection = connector.getCollection();
        Bson query = Filters.and(
                Filters.eq("sender", uuid.toString()),
                Filters.or(Arrays.stream(ticketStatus)
                        .map(s -> Filters.eq("status", s.toInt()))
                        .collect(Collectors.toList()))
        );
        MongoCursor<Document> dbCursor = collection.find(query).sort(Sorts.ascending("ticket_id")).cursor();
        while (dbCursor.hasNext()) {
            tickets.add(buildTicket(dbCursor.next()));
        }
        return tickets;
    }

    @Override
    public List<Ticket> getTicketsInRadius(Location location, int radius) {
        List<Ticket> tickets = new ArrayList<>();
        MongoCollection<Document> collection = connector.getCollection();
        Bson query = Filters.and(
                Filters.eq("server", location.getServer()),
                Filters.eq("world", location.getWorldName()),
                Filters.gt("loc_X", location.getLocationX() - radius),
                Filters.lt("loc_X", location.getLocationX() + radius),
                Filters.gt("loc_Y", location.getLocationY() - radius),
                Filters.lt("loc_Y", location.getLocationY() + radius),
                Filters.gt("loc_Z", location.getLocationZ() - radius),
                Filters.lt("loc_Z", location.getLocationZ() + radius)
        );
        MongoCursor<Document> dbCursor = collection.find(query).sort(Sorts.ascending("ticket_id")).cursor();
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
        MongoCollection<Document> collection = connector.getCollection();
        Bson query = Filters.eq("ticket_id", comment.getTicketId());
        MongoCursor<Document> dbCursor = collection.find(query).cursor();
        if (dbCursor.hasNext()) {
            Document ticketObject = dbCursor.next();
            List<Object> comments = (List<Object>) ticketObject.get("comments");
            if (comments == null) {
                comments = new ArrayList<>();
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
            collection.updateOne(query, Updates.set("comments", comments));
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
        MongoCollection<Document> collection = connector.getCollection();
        Bson query = Filters.eq("ticket_id", comment.getCommentId());
        MongoCursor<Document> dbCursor = collection.find(query).cursor();
        if (dbCursor.hasNext()) {
            Document ticketObject = dbCursor.next();
            if (ticketObject.get("comments") instanceof List) {
                List<Document> comments = (List<Document>) ticketObject.get("comments");
                for (Document commentObject : comments) {
                    if (commentObject.getInteger("comment_id") == comment.getCommentId()) {
                        commentObject.put("sender_has_noticed", true);
                    }
                }
                collection.updateOne(query, Updates.set("comments", comments));
            }
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
    private Ticket buildTicket(Document dbObject) {

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
        if (dbObject.get("comments") instanceof List) {
            List<Document> comments = (List<Document>) dbObject.get("comments");
            for (Document commentObject : comments) {
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
                        commentObject.getInteger("comment_id"),
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

    private static boolean hasLocation(Document dbObject) {
        return dbObject.containsKey("server")
                && dbObject.containsKey("world")
                && dbObject.containsKey("loc_X")
                && dbObject.containsKey("loc_Y")
                && dbObject.containsKey("loc_Z")
                && dbObject.containsKey("yaw")
                && dbObject.containsKey("pitch");
    }
}
