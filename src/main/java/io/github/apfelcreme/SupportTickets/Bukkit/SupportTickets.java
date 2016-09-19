package io.github.apfelcreme.SupportTickets.Bukkit;

import io.github.apfelcreme.SupportTickets.Bukkit.Bungee.BungeeMessageListener;
import io.github.apfelcreme.SupportTickets.Bukkit.Database.Connector.MongoConnector;
import io.github.apfelcreme.SupportTickets.Bukkit.Database.Connector.MySQLConnector;
import io.github.apfelcreme.SupportTickets.Bukkit.Database.Controller.DatabaseController;
import io.github.apfelcreme.SupportTickets.Bukkit.Database.Controller.MongoController;
import io.github.apfelcreme.SupportTickets.Bukkit.Database.Controller.SQLController;
import io.github.apfelcreme.SupportTickets.Bukkit.Listener.PlayerLoginListener;
import io.github.apfelcreme.SupportTickets.Bukkit.Task.ReminderTask;
import net.zaiyers.UUIDDB.bukkit.UUIDDB;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;

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
public class SupportTickets extends JavaPlugin {

    /**
     * the database controller
     */
    private static DatabaseController databaseController = null;

    /**
     * a cache for name -> uuid
     */
    private Map<String, UUID> uuidCache = null;

    /**
     * a list with online players
     */
    private List<UUID> onlinePlayers = null;

    /**
     * returns the plugin instance
     *
     * @return the plugin instance
     */
    public static SupportTickets getInstance() {
        return (SupportTickets) Bukkit.getServer().getPluginManager().getPlugin("SupportTickets");
    }

    /**
     * returns the databaseController
     *
     * @return the databaseController
     */
    public static DatabaseController getDatabaseController() {
        return databaseController;
    }

    /**
     * onEnable
     */
    @Override
    public void onEnable() {
        try {
            // initialize the uuid cache
            uuidCache = new HashMap<String, UUID>();

            // init a list
            onlinePlayers = new ArrayList<UUID>();

            // init the config
            SupportTicketsConfig.load();

            // init the database connection
            switch (SupportTicketsConfig.getDb()) {
                case MySQL:
                    MySQLConnector.getInstance().initConnection();
                    databaseController = new SQLController();
                    break;
                case MongoDB:
                    databaseController = new MongoController();
                    break;
            }

            // register the command
            getServer().getPluginCommand("ticket").setExecutor(new TicketCommandExecutor());

            // register the Plugin channels for the bungee communication
            getServer().getMessenger().registerOutgoingPluginChannel(this, "SupportTickets");
            getServer().getMessenger().registerIncomingPluginChannel(this, "SupportTickets",
                    new BungeeMessageListener());

            // register the listeners
            getServer().getPluginManager().registerEvents(new PlayerLoginListener(), this);

            // start the reminder task
            getServer().getScheduler().runTaskTimerAsynchronously(this,
                    new ReminderTask(), 100L, SupportTicketsConfig.getReminderTaskDelay());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * onDisable
     */
    @Override
    public void onDisable() {
        if (databaseController instanceof MongoController) {
            MongoConnector.getInstance().close();
        }
    }

    /**
     * sends a message to a player
     *
     * @param receiver the player
     * @param text     the message
     */
    public static void sendMessage(CommandSender receiver, String text) {
        receiver.sendMessage(getPrefix() + text);
    }

    /**
     * sends a message to all players with the permission "SupportTickets.receiveBukkitTeamMessage"
     *
     * @param message a text
     */
    public static void sendTeamMessage(String message) {
        for (Player receiver : Bukkit.getServer().getOnlinePlayers()) {
            if (receiver.hasPermission("SupportTickets.receiveBukkitTeamMessage")) {
                sendMessage(receiver, message);
            }
        }
    }

    /**
     * returns the prefix for messages
     *
     * @return the prefix for messages
     */
    public static String getPrefix() {
        return SupportTicketsConfig.getText("prefix");
    }

    /**
     * adds a player to the online list
     *
     * @param uuid a players uuid
     */
    public void setPlayerOnline(UUID uuid) {
        if (onlinePlayers.contains(uuid)) {
            onlinePlayers.remove(uuid);
        }
        onlinePlayers.add(uuid);
    }

    /**
     * removes a player from the online list
     *
     * @param uuid a players uuid
     */
    public void setPlayerOffline(UUID uuid) {
        onlinePlayers.remove(uuid);
    }

    /**
     * returns whether a player is online or not
     *
     * @param uuid a players uuid
     * @return true or false
     */
    public boolean isPlayerOnline(UUID uuid) {
        return onlinePlayers.contains(uuid);
    }

    /**
     * returns the username of a player with the given uuid
     *
     * @param uuid a players uuid
     * @return his name
     */
    public String getNameByUUID(UUID uuid) {
        OfflinePlayer offlinePlayer = getInstance().getServer().getOfflinePlayer(uuid);
        if (offlinePlayer.getName() != null) {
            return offlinePlayer.getName();
        } else if (uuidCache.containsValue(uuid)) {
            for (Map.Entry<String, UUID> entry : uuidCache.entrySet()) {
                if (entry.getValue().equals(uuid)) {
                    return entry.getKey();
                }
            }
        } else if (getServer().getPluginManager().getPlugin("UUIDDB") != null) {
            String name = net.zaiyers.UUIDDB.bungee.UUIDDB.getInstance().getStorage().getNameByUUID(uuid);
            uuidCache.put(name.toUpperCase(), uuid);
            return name;
        } else {
            //this should only occur if the player has never joined this particular server.
            try {
                URL url = new URL(SupportTicketsConfig.getAPINameUrl().replace("{0}", uuid.toString().replace("-", "")));
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                StringBuilder json = new StringBuilder();
                int read;
                while ((read = in.read()) != -1) {
                    json.append((char) read);
                }
                Object obj = new JSONParser().parse(json.toString());
                JSONArray jsonArray = (JSONArray) obj;
                return (String) ((JSONObject) jsonArray.get(jsonArray.size() - 1)).get("name");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "Unknown Player";
    }

    /**
     * returns the uuid of the player with the given uuid
     *
     * @param name a players name
     * @return his uuid
     */
    public UUID getUUIDByName(String name) {
        name = name.toUpperCase();
        if (uuidCache.containsKey(name)) {
            return uuidCache.get(name);
        } else if (getServer().getPluginManager().getPlugin("UUIDDB") != null) {
            UUID uuid = UUID.fromString(UUIDDB.getInstance().getStorage().getUUIDByName(name, false));
            uuidCache.put(name, uuid);
            return uuid;
        } else {
            try {
                URL url = new URL(SupportTicketsConfig.getAPIUUIDUrl().replace("{0}", name));
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                StringBuilder json = new StringBuilder();
                int read;
                while ((read = in.read()) != -1) {
                    json.append((char) read);
                }
                if (json.length() == 0) {
                    return null;
                }
                JSONObject jsonObject = (JSONObject) (new JSONParser().parse(json.toString()));
                String id = jsonObject.get("id").toString();
                return UUID.fromString(id.replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})",
                        "$1-$2-$3-$4-$5"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * joins a number of strings and places a seperator between them
     * -> taken from StringUtils to reduce the number of dependencies
     *
     * @param array     the array of strings
     * @param separator the seperator
     * @return a joined string
     */
    public static String join(Object[] array, String separator, String lastSeparator) {
        if (array == null) {
            return null;
        } else {
            if (separator == null) {
                separator = "";
            }
            if (lastSeparator == null) {
                lastSeparator = separator;
            }

            int noOfItems = array.length;
            if (noOfItems <= 0) {
                return "";
            } else {
                StringBuilder buf = new StringBuilder(noOfItems * 16);

                for (int i = 0; i < array.length; ++i) {
                    if (i > 0) {
                        if (i < array.length - 1) {
                            buf.append(separator);
                        } else {
                            buf.append(lastSeparator);
                        }
                    }

                    if (array[i] != null) {
                        buf.append(array[i]);
                    }
                }
                return buf.toString();
            }
        }
    }

    /**
     * checks if a String contains only numbers
     *
     * @param string a string
     * @return true or false
     */
    public static boolean isNumeric(String string) {
        return Pattern.matches("([0-9])*", string);
    }

}
