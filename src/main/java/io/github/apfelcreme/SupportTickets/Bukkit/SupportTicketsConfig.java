package io.github.apfelcreme.SupportTickets.Bukkit;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.net.URISyntaxException;

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
public class SupportTicketsConfig {

    private static YamlConfiguration languageConfig;

    private static SupportTickets plugin;

    /**
     * loads the config
     *
     * @throws IOException
     */
    public static void load() throws IOException, URISyntaxException {
        plugin = SupportTickets.getInstance();
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        plugin.saveDefaultConfig();
        plugin.saveResource("lang.de.yml", false);

        languageConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder() + "/lang.de.yml"));
    }

    /**
     * returns the type of database that is used
     *
     * @return the type of database that is used
     */
    public static DB getDb() {
        if (plugin.getConfig().getString("db").equalsIgnoreCase("MySQL")) {
            return DB.MySQL;
        } else if (plugin.getConfig().getString("db").equalsIgnoreCase("MongoDb")) {
            return DB.MongoDB;
        }
        return DB.MySQL;
    }

    /**
     * returns the username of the sql user
     *
     * @return the username of the sql user
     */
    public static String getSqlUser() {
        return plugin.getConfig().getString("sql.user");
    }

    /**
     * returns the sql password
     *
     * @return the sql password
     */
    public static String getSqlPassword() {
        return plugin.getConfig().getString("sql.password");
    }

    /**
     * returns the sql database name
     *
     * @return the sql database name
     */
    public static String getSqlDatabase() {
        return plugin.getConfig().getString("sql.database");
    }

    /**
     * returns the sql database url
     *
     * @return the sql database url
     */
    public static String getSqlUrl() {
        return plugin.getConfig().getString("sql.url");
    }

    /**
     * returns the mongo host name
     *
     * @return the mongo host name
     */
    public static String getMongoHost() {
        return plugin.getConfig().getString("mongo.host");
    }

    /**
     * returns the mongo port
     *
     * @return the mongo port
     */
    public static Integer getMongoPort() {
        return plugin.getConfig().getInt("mongo.port");
    }

    /**
     * returns the mongo database name
     *
     * @return the mongo database name
     */
    public static String getMongoDatabase() {
        return plugin.getConfig().getString("mongo.database");
    }

    /**
     * returns the mongo collection name
     *
     * @return the mongo collection name
     */
    public static String getMongoCollection() {
        return plugin.getConfig().getString("mongo.collection");
    }

    /**
     * returns the ticket table name
     *
     * @return the ticket table name
     */
    public static String getTicketTable() {
        return plugin.getConfig().getString("sql.tables.tickets");
    }

    /**
     * returns the comments table name
     *
     * @return the comments table name
     */
    public static String getCommentTable() {
        return plugin.getConfig().getString("sql.tables.comments");
    }

    /**
     * returns the player table name
     *
     * @return the player table name
     */
    public static String getPlayerTable() {
        return plugin.getConfig().getString("sql.tables.players");
    }

    /**
     * returns the number of items displayed on each site in /pe list
     *
     * @return the number of items displayed on each site in /pe list
     */
    public static Integer getPageSize() {
        return plugin.getConfig().getInt("pageSize");
    }

    /**
     * returns the delay of the reminder task
     *
     * @return the delay of the reminder task
     */
    public static Integer getReminderTaskDelay() {
        return plugin.getConfig().getInt("reminderTaskDelay");
    }

    /**
     * returns the URL for API-Calls with the mojang API
     *
     * @return the URL for API-Calls with the mojang API
     */
    public static String getAPINameUrl() {
        return plugin.getConfig().getString("apiUrlName");
    }

    /**
     * returns the URL for API-Calls with the mojang API
     *
     * @return the URL for API-Calls with the mojang API
     */
    public static String getAPIUUIDUrl() {
        return plugin.getConfig().getString("apiUrlUUID");
    }

    /**
     * returns the delay before a player is teleported to a ticket
     *
     * @return a long
     */
    public static long getTeleportDelay() {
        return plugin.getConfig().getLong("teleportDelay");
    }

    /**
     * returns the config
     *
     * @return the config
     */
    public static YamlConfiguration getLanguageConfig() {
        return languageConfig;
    }


    /**
     * returns a texty string
     *
     * @param key the config path
     * @return the text
     */
    public static String getText(String key) {
        String ret = (String) languageConfig.get("texts." + key);
        if (ret != null && !ret.isEmpty()) {
            ret = ChatColor.translateAlternateColorCodes('&', ret);
            return ChatColor.translateAlternateColorCodes('§', ret);
        } else {
            return "Missing text node: " + key;
        }
    }

    public enum DB {
        MySQL, MongoDB;
    }
}
