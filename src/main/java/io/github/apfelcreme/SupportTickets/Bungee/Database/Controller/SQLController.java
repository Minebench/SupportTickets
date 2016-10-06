package io.github.apfelcreme.SupportTickets.Bungee.Database.Controller;

import io.github.apfelcreme.SupportTickets.Bungee.Database.Connector.MySQLConnector;
import io.github.apfelcreme.SupportTickets.Bungee.SupportTickets;
import io.github.apfelcreme.SupportTickets.Bungee.SupportTicketsConfig;
import io.github.apfelcreme.SupportTickets.Bungee.Ticket.Comment;
import io.github.apfelcreme.SupportTickets.Bungee.Ticket.Location;
import io.github.apfelcreme.SupportTickets.Bungee.Ticket.Ticket;

import java.sql.*;
import java.util.*;
import java.util.Date;

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
public class SQLController implements DatabaseController {

    /**
     * loads a ticket
     *
     * @param ticketId the ticket id
     * @return a ticket
     */
    @Override
    public Ticket loadTicket(Integer ticketId) {
        Connection connection = MySQLConnector.getInstance().getConnection();
        if (connection != null) {
            try {
                PreparedStatement statement = connection.prepareStatement(
                        "Select *, (Select uuid from " + SupportTicketsConfig.getInstance().getPlayerTable() + " where player_id = t.closed_player_id) as uuidPlayerClosed "
                                + " from " + SupportTicketsConfig.getInstance().getTicketTable() + " t"
                                + " left join " + SupportTicketsConfig.getInstance().getPlayerTable() + " p on p.player_id = t.player_id"
                                + " where t.ticket_id = ?");
                statement.setInt(1, ticketId);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.first()) {
                    return buildTicket(resultSet);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                MySQLConnector.getInstance().closeConnection(connection);
            }
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

        Connection connection = MySQLConnector.getInstance().getConnection();
        try {
            if (connection != null) {

                // insert the closer if he doesnt exist yet
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "INSERT INTO " + SupportTicketsConfig.getInstance().getPlayerTable() + " (uuid, name) VALUES (?, ?) ON DUPLICATE KEY UPDATE name = ?");
                preparedStatement.setString(1, ticket.getSender().toString());
                preparedStatement.setString(2, SupportTickets.getInstance().getNameByUUID(ticket.getSender()));
                preparedStatement.setString(3, SupportTickets.getInstance().getNameByUUID(ticket.getSender()));
                preparedStatement.executeUpdate();
                preparedStatement.close();

                // insert the ticket itself
                preparedStatement = connection.prepareStatement("INSERT INTO " + SupportTicketsConfig.getInstance().getTicketTable() +
                        " (player_id, message, time_stamp, status, server, world, loc_X, loc_Y, loc_Z, yaw, pitch) VALUES " +
                        "((Select player_id from " + SupportTicketsConfig.getInstance().getPlayerTable() + " where uuid = ?), " +
                        "?, ?, ?, ?, ?, ?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, ticket.getSender().toString());
                preparedStatement.setString(2, ticket.getMessage());
                preparedStatement.setLong(3, ticket.getDate().getTime());
                preparedStatement.setInt(4, ticket.getTicketStatus().toInt());
                preparedStatement.setString(5, ticket.getLocation().getServer());
                preparedStatement.setString(6, ticket.getLocation().getWorldName());
                preparedStatement.setDouble(7, ticket.getLocation().getLocationX());
                preparedStatement.setDouble(8, ticket.getLocation().getLocationY());
                preparedStatement.setDouble(9, ticket.getLocation().getLocationZ());
                preparedStatement.setDouble(10, ticket.getLocation().getYaw());
                preparedStatement.setDouble(11, ticket.getLocation().getPitch());
                preparedStatement.executeUpdate();
                ResultSet resultSet = preparedStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            MySQLConnector.getInstance().closeConnection(connection);
        }
        return -1;
    }

    /**
     * assign a ticket to someone or something
     *
     * @param ticket the ticket
     * @param to     the target
     */
    @Override
    public void assignTicket(final Ticket ticket, final String to) {
        SupportTickets.getInstance().getProxy().getScheduler().runAsync(SupportTickets.getInstance(), new Runnable() {
            public void run() {
                Connection connection = MySQLConnector.getInstance().getConnection();
                try {
                    if (connection != null) {
                        // update the target, the time_stamp and the status
                        PreparedStatement preparedStatement = connection.prepareStatement(
                                "UPDATE " + SupportTicketsConfig.getInstance().getTicketTable() + " SET assigned = ?, assigned_time_stamp = ?, status = ? where ticket_id = ?");
                        preparedStatement.setString(1, to);
                        preparedStatement.setLong(2, new Date().getTime());
                        preparedStatement.setInt(3, Ticket.TicketStatus.ASSIGNED.toInt());
                        preparedStatement.setInt(4, ticket.getTicketId());
                        preparedStatement.executeUpdate();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    MySQLConnector.getInstance().closeConnection(connection);
                }
            }
        });
    }

    /**
     * unassigns a ticket
     *
     * @param ticket the ticket
     */
    @Override
    public void unassignTicket(final Ticket ticket) {
        SupportTickets.getInstance().getProxy().getScheduler().runAsync(SupportTickets.getInstance(), new Runnable() {
            public void run() {
                Connection connection = MySQLConnector.getInstance().getConnection();
                try {
                    if (connection != null) {
                        // update the target, the time_stamp and the status
                        PreparedStatement preparedStatement = connection.prepareStatement(
                                "UPDATE " + SupportTicketsConfig.getInstance().getTicketTable() + " SET assigned = null, assigned_time_stamp = null, status = ? where ticket_id = ?");
                        preparedStatement.setInt(1, Ticket.TicketStatus.OPEN.toInt());
                        preparedStatement.setInt(2, ticket.getTicketId());
                        preparedStatement.executeUpdate();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    MySQLConnector.getInstance().closeConnection(connection);
                }
            }
        });
    }

    /**
     * closes a ticket
     *
     * @param ticket the ticket
     * @param closer the uuid of the player who closed the ticket
     * @param reason the reason why the ticket was closed
     */
    @Override
    public void closeTicket(final Ticket ticket, final UUID closer, final String reason) {
        SupportTickets.getInstance().getProxy().getScheduler().runAsync(SupportTickets.getInstance(), new Runnable() {
            public void run() {
                Connection connection = MySQLConnector.getInstance().getConnection();
                try {
                    if (connection != null) {

                        // insert the closer if he doesnt exist yet
                        PreparedStatement preparedStatement = connection.prepareStatement(
                                "INSERT INTO " + SupportTicketsConfig.getInstance().getPlayerTable() + " (uuid, name) VALUES (?, ?) ON DUPLICATE KEY UPDATE name = ?");
                        preparedStatement.setString(1, ticket.getSender().toString());
                        preparedStatement.setString(2, SupportTickets.getInstance().getNameByUUID(ticket.getSender()));
                        preparedStatement.setString(3, SupportTickets.getInstance().getNameByUUID(ticket.getSender()));
                        preparedStatement.executeUpdate();
                        preparedStatement.close();

                        // set the status to 'closed' and enter the closer, the time_stamp and the reason
                        preparedStatement = connection.prepareStatement(
                                "UPDATE " + SupportTicketsConfig.getInstance().getTicketTable() + " SET " +
                                        "closed_player_id = (Select player_id from " + SupportTicketsConfig.getInstance().getPlayerTable() + " where uuid = ?), " +
                                        "closed_time_stamp = ?, " +
                                        "closed_reason = ?, " +
                                        "status = ? where ticket_id = ?");
                        preparedStatement.setString(1, closer.toString());
                        preparedStatement.setLong(2, new Date().getTime());
                        preparedStatement.setString(3, reason);
                        preparedStatement.setInt(4, Ticket.TicketStatus.CLOSED.toInt());
                        preparedStatement.setInt(5, ticket.getTicketId());
                        preparedStatement.executeUpdate();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    MySQLConnector.getInstance().closeConnection(connection);
                }
            }
        });
    }

    /**
     * reopens a ticket
     *
     * @param ticket the ticket
     */
    @Override
    public void reopenTicket(final Ticket ticket) {
        SupportTickets.getInstance().getProxy().getScheduler().runAsync(SupportTickets.getInstance(), new Runnable() {
            public void run() {
                Connection connection = MySQLConnector.getInstance().getConnection();
                try {
                    if (connection != null) {
                        PreparedStatement preparedStatement = connection.prepareStatement(
                                "UPDATE " + SupportTicketsConfig.getInstance().getTicketTable() + " SET status = ?, " +
                                        "closed_player_id = null, closed_reason = null, closed_time_stamp = null " +
                                        "WHERE ticket_id = ?");
                        preparedStatement.setInt(1, Ticket.TicketStatus.REOPENED.toInt());
                        preparedStatement.setInt(2, ticket.getTicketId());
                        preparedStatement.executeUpdate();
                        preparedStatement.close();
                        connection.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    MySQLConnector.getInstance().closeConnection(connection);
                }
            }
        });
    }

    /**
     * returns a list of tickets with the given status
     *
     * @param ticketStatus the status (e.g. OPEN, CLOSED or ASSIGNED)
     * @return a list of tickets
     */
    @Override
    public List<Ticket> getTickets(Ticket.TicketStatus... ticketStatus) {
        final List<Ticket> tickets = new ArrayList<>();
        Connection connection = MySQLConnector.getInstance().getConnection();
        if (connection != null) {
            try {

                String sqlReplace = "";
                for (int i = 0; i < ticketStatus.length; i++) {
                    sqlReplace += " t.status = " + ticketStatus[i].toInt();
                    if (i < ticketStatus.length - 1) {
                        sqlReplace += " or ";
                    }
                }
                PreparedStatement statement = connection.prepareStatement(
                        "Select *, (Select uuid from " + SupportTicketsConfig.getInstance().getPlayerTable() + " where player_id = t.closed_player_id) as uuidPlayerClosed "
                                + " from " + SupportTicketsConfig.getInstance().getTicketTable() + " t"
                                + " left join " + SupportTicketsConfig.getInstance().getPlayerTable() + " p on p.player_id = t.player_id"
                                + " where " + sqlReplace);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    tickets.add(buildTicket(resultSet));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                MySQLConnector.getInstance().closeConnection(connection);
            }
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
        final List<Ticket> tickets = new ArrayList<>();
        Connection connection = MySQLConnector.getInstance().getConnection();
        if (connection != null) {
            try {
                PreparedStatement statement = connection.prepareStatement(
                        "Select *, (Select uuid from " + SupportTicketsConfig.getInstance().getPlayerTable() + " where player_id = t.closed_player_id) as uuidPlayerClosed from "
                                + SupportTicketsConfig.getInstance().getTicketTable() + " t"
                                + " left join " + SupportTicketsConfig.getInstance().getPlayerTable() + " p on p.player_id = t.closed_player_id"
                                + " where p.uuid = ?");
                statement.setString(1, closer.toString());
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    tickets.add(buildTicket(resultSet));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                MySQLConnector.getInstance().closeConnection(connection);
            }
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
        final List<Ticket> tickets = new ArrayList<>();
        Connection connection = MySQLConnector.getInstance().getConnection();
        if (connection != null) {
            try {
                PreparedStatement statement = connection.prepareStatement(
                        "Select *, (Select uuid from " + SupportTicketsConfig.getInstance().getPlayerTable() + " where player_id = t.closed_player_id) as uuidPlayerClosed "
                                + " from " + SupportTicketsConfig.getInstance().getTicketTable() + " t"
                                + " left join " + SupportTicketsConfig.getInstance().getPlayerTable() + " p on p.player_id = t.player_id"
                                + " where p.uuid = ?");
                statement.setString(1, opener.toString());
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    tickets.add(buildTicket(resultSet));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                MySQLConnector.getInstance().closeConnection(connection);
            }
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
    public List<Ticket> getPlayerTickets(final UUID uuid, Ticket.TicketStatus... ticketStatus) {
        final List<Ticket> tickets = new ArrayList<>();
        Connection connection = MySQLConnector.getInstance().getConnection();
        if (connection != null) {
            try {

                String sqlReplace = "";
                for (int i = 0; i < ticketStatus.length; i++) {
                    sqlReplace += " t.status = " + ticketStatus[i].toInt();
                    if (i < ticketStatus.length - 1) {
                        sqlReplace += " or ";
                    }
                }

                PreparedStatement statement = connection.prepareStatement(
                        "Select *, (Select uuid from " + SupportTicketsConfig.getInstance().getPlayerTable() + " where player_id = t.closed_player_id) as uuidPlayerClosed"
                                + " from " + SupportTicketsConfig.getInstance().getTicketTable() + " t"
                                + " left join " + SupportTicketsConfig.getInstance().getPlayerTable() + " p on p.player_id = t.player_id"
                                + " where p.uuid = ? and " + sqlReplace);
                statement.setString(1, uuid.toString());
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    tickets.add(buildTicket(resultSet));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                MySQLConnector.getInstance().closeConnection(connection);
            }
        }
        return tickets;
    }

    /**
     * saves a comment
     *
     * @param comment a comment
     */
    @Override
    public void saveComment(final Comment comment) {
        SupportTickets.getInstance().getProxy().getScheduler().runAsync(SupportTickets.getInstance(), new Runnable() {
            public void run() {
                Connection connection = MySQLConnector.getInstance().getConnection();

                try {
                    if (connection != null) {
                        PreparedStatement preparedStatement = connection.prepareStatement(
                                "INSERT INTO " + SupportTicketsConfig.getInstance().getPlayerTable() + " (uuid, name) " +
                                        "VALUES (?, ?) ON DUPLICATE KEY UPDATE name = ?");
                        preparedStatement.setString(1, comment.getSender().toString());
                        preparedStatement.setString(2, SupportTickets.getInstance().getNameByUUID(comment.getSender()));
                        preparedStatement.setString(3, SupportTickets.getInstance().getNameByUUID(comment.getSender()));
                        preparedStatement.executeUpdate();
                        preparedStatement.close();

                        preparedStatement = connection.prepareStatement(
                                "INSERT INTO " + SupportTicketsConfig.getInstance().getCommentTable() +
                                        " (ticket_id, player_id, time_stamp, comment, sender_has_noticed) VALUES" +
                                        "(?, (Select player_id from " + SupportTicketsConfig.getInstance().getPlayerTable() +
                                        " where uuid = ?), ?, ?, ?)");
                        preparedStatement.setInt(1, comment.getTicketId());
                        preparedStatement.setString(2, comment.getSender().toString());
                        preparedStatement.setLong(3, comment.getDate().getTime());
                        preparedStatement.setString(4, comment.getComment());
                        preparedStatement.setBoolean(5, comment.getSenderHasNoticed());
                        preparedStatement.executeUpdate();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    MySQLConnector.getInstance().closeConnection(connection);
                }
            }
        });
    }

    /**
     * marks a comment as read
     *
     * @param comment the comment
     */
    @Override
    public void setCommentRead(final Comment comment) {
        if (comment.getCommentId() == -1) {
            return;
        }
        SupportTickets.getInstance().getProxy().getScheduler().runAsync(SupportTickets.getInstance(), new Runnable() {
            public void run() {
                Connection connection = MySQLConnector.getInstance().getConnection();
                try {
                    if (connection != null) {
                        PreparedStatement statement = connection.prepareStatement(
                                "UPDATE " + SupportTicketsConfig.getInstance().getCommentTable() +
                                        " SET sender_has_noticed = 1 WHERE comment_id = ?");
                        statement.setInt(1, comment.getCommentId());
                        statement.executeUpdate();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    MySQLConnector.getInstance().closeConnection(connection);
                }
            }
        });
    }

    /**
     * builds a ticket from a database resultset
     *
     * @param resultSet a resultset
     * @return a ticket
     */
    private Ticket buildTicket(ResultSet resultSet) throws SQLException {


        // create a ticket object
        Ticket ticket = new Ticket(
                UUID.fromString(resultSet.getString("uuid")),
                resultSet.getString("message"),
                new Date(resultSet.getLong("time_stamp")),
                new Location(
                        resultSet.getString("server"),
                        resultSet.getString("world"),
                        resultSet.getDouble("loc_X"),
                        resultSet.getDouble("loc_Y"),
                        resultSet.getDouble("loc_Z"),
                        resultSet.getFloat("yaw"),
                        resultSet.getFloat("pitch")

                ),
                Ticket.TicketStatus.fromInt(resultSet.getInt("status")));
        ticket.setTicketId(resultSet.getInt("ticket_id"));

        // load the assign text
        if (resultSet.getString("assigned") != null) {
            ticket.setAssigned(resultSet.getString("assigned"));
            ticket.setAssignedDate(resultSet.getDate("assigned_time_stamp"));
        }

        // load the player who might have closed the ticket
        if (resultSet.getString("uuidPlayerClosed") != null) {
            ticket.setClosed(UUID.fromString(resultSet.getString("uuidPlayerClosed")));
            ticket.setClosedDate(resultSet.getDate("closed_time_stamp"));
        }

        // load all comments
        PreparedStatement statement = resultSet.getStatement().getConnection().prepareStatement(
                "Select * from " + SupportTicketsConfig.getInstance().getCommentTable() + " c " +
                        "left join " + SupportTicketsConfig.getInstance().getPlayerTable() + " p on p.player_id = c.player_id " +
                        "where ticket_id = ? " +
                        "order by time_stamp asc");
        statement.setInt(1, resultSet.getInt("ticket_id"));
        resultSet = statement.executeQuery();
        while (resultSet.next()) {
            Comment comment = new Comment(
                    resultSet.getInt("comment_id"),
                    ticket.getTicketId(),
                    UUID.fromString(resultSet.getString("uuid")),
                    resultSet.getString("comment"),
                    resultSet.getBoolean("sender_has_noticed"),
                    new Date(resultSet.getLong("time_stamp")));
            ticket.getComments().add(comment);
        }
        return ticket;
    }
}
