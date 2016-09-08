package io.github.apfelcreme.SupportTickets.Bukkit.Database.Connector;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.apfelcreme.SupportTickets.Bukkit.SupportTicketsConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
public class MySQLConnector {

    private static MySQLConnector instance = null;

    /**
     * a hikariCp data source
     */
    private HikariDataSource dataSource;

    /**
     * returns the MySQLConnector instance
     * @return
     */
    public static MySQLConnector getInstance() {
        if (instance == null) {
            instance = new MySQLConnector();
        }
        return instance;
    }

    /**
     * initializes the database connection
     */
    public void initConnection() {
        if (SupportTicketsConfig.getSqlDatabase() != null && !SupportTicketsConfig.getSqlDatabase().isEmpty()) {
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setJdbcUrl("jdbc:mysql://" + SupportTicketsConfig.getSqlUrl()
                    + "/" + SupportTicketsConfig.getSqlDatabase());
            hikariConfig.setUsername(SupportTicketsConfig.getSqlUser());
            hikariConfig.setPassword(SupportTicketsConfig.getSqlPassword());

            dataSource = new HikariDataSource(hikariConfig);
            initTables();
        }
    }

    /**
     * returns a connection from a HikariCP connection pool
     *
     * @return a connection from a HikariCP connection pool
     */
    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * closes a connection
     *
     * @param connection a connection
     */
    public void closeConnection(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * creates the database and tables
     */
    protected void initTables() {
        Connection connection = getConnection();
        try {
            if (connection != null) {
                PreparedStatement statement;
                statement = connection.prepareStatement("CREATE DATABASE IF NOT EXISTS "
                        + SupportTicketsConfig.getSqlDatabase());
                statement.executeUpdate();

                statement = connection.prepareStatement(
                        "CREATE TABLE IF NOT EXISTS " + SupportTicketsConfig.getPlayerTable() +
                                "(" +
                                "player_id INTEGER AUTO_INCREMENT, " +
                                "uuid VARCHAR(36) UNIQUE NOT NULL, " +
                                "name VARCHAR(50) NOT NULL, " +
                                "PRIMARY KEY (player_id, uuid)" +
                                ")");
                statement.executeUpdate();

                statement = connection.prepareStatement(
                        "CREATE TABLE IF NOT EXISTS " + SupportTicketsConfig.getTicketTable() +
                                "(" +
                                "ticket_id INTEGER auto_increment, " +
                                "player_id INTEGER not null, " +
                                "message VARCHAR(255), " +
                                "time_stamp BIGINT, " +
                                "assigned VARCHAR(50), " +
                                "assigned_time_stamp BIGINT," +
                                "closed_player_id INTEGER, " +
                                "closed_reason VARCHAR(255), " +
                                "closed_time_stamp BIGINT, " +
                                "status TINYINT, " +
                                "server VARCHAR(30), " +
                                "world VARCHAR(30), " +
                                "loc_X DOUBLE, " +
                                "loc_Y DOUBLE, " +
                                "loc_Z DOUBLE, " +
                                "yaw FLOAT, " +
                                "pitch FLOAT, " +
                                "PRIMARY KEY (ticket_id), " +
                                "FOREIGN KEY (player_id) references " + SupportTicketsConfig.getPlayerTable() + "(player_id), " +
                                "FOREIGN KEY (closed_player_id) references " + SupportTicketsConfig.getPlayerTable() + "(player_id) " +
                                ")");
                statement.executeUpdate();

                statement = connection.prepareStatement(
                        "CREATE TABLE IF NOT EXISTS " + SupportTicketsConfig.getCommentTable() +
                                "(" +
                                "comment_id INTEGER AUTO_INCREMENT, " +
                                "ticket_id INTEGER, " +
                                "player_id INTEGER, " +
                                "time_stamp BIGINT, " +
                                "sender_has_noticed TINYINT(1) DEFAULT 0, " +
                                "comment VARCHAR(255) NOT NULL, " +
                                "FOREIGN KEY (ticket_id) references " + SupportTicketsConfig.getTicketTable() + " (ticket_id), " +
                                "FOREIGN KEY (player_id) references " + SupportTicketsConfig.getPlayerTable() + " (player_id), " +
                                "PRIMARY KEY (comment_id)" +
                                ")");
                statement.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection);
        }
    }
}
