package io.github.apfelcreme.SupportTickets.Bungee;

import io.github.apfelcreme.SupportTickets.Bungee.Database.Connector.MongoConnector;
import io.github.apfelcreme.SupportTickets.Bungee.Database.Connector.MySQLConnector;
import io.github.apfelcreme.SupportTickets.Bungee.Database.Controller.DatabaseController;
import io.github.apfelcreme.SupportTickets.Bungee.Database.Controller.MongoController;
import io.github.apfelcreme.SupportTickets.Bungee.Database.Controller.SQLController;
import io.github.apfelcreme.SupportTickets.Bungee.Listener.PlayerLoginListener;
import io.github.apfelcreme.SupportTickets.Bungee.Message.BukkitMessageListener;
import io.github.apfelcreme.SupportTickets.Bungee.Task.ReminderTask;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.zaiyers.UUIDDB.bungee.UUIDDB;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;
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
public class SupportTickets extends Plugin {

    /**
     * the database controller
     */
    private static DatabaseController databaseController = null;

    /**
     * a cache for name -> uuid
     */
    private Map<String, UUID> uuidCache = null;

    /**
     * returns the plugin instance
     *
     * @return the plugin instance
     */
    public static SupportTickets getInstance() {
        return (SupportTickets) ProxyServer.getInstance().getPluginManager().getPlugin("SupportTickets");
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

        // initialize the uuid cache
        uuidCache = new HashMap<>();

        // init the config
        SupportTicketsConfig.getInstance();

        // init the database connection
        switch (SupportTicketsConfig.getInstance().getDb()) {
            case MySQL:
                MySQLConnector.getInstance().initConnection();
                databaseController = new SQLController();
                break;
            case MongoDB:
                databaseController = new MongoController();
                break;
        }

        // register the command
        getProxy().getPluginManager().registerCommand(this, new TicketCommandExecutor("ticket"));
        getProxy().getPluginManager().registerCommand(this, new TicketCommandExecutor("pe"));

        // register the Plugin channels for the bukkit <-> bungee communication
        getProxy().registerChannel("SupportTickets");
        getProxy().getPluginManager().registerListener(this, new BukkitMessageListener());

        // register the listeners
        getProxy().getPluginManager().registerListener(this, new PlayerLoginListener());

        // start the reminder task
        getProxy().getScheduler().schedule(this,
                new ReminderTask(), SupportTicketsConfig.getInstance().getReminderTaskDelay(), TimeUnit.MINUTES);
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
        if (receiver != null && text != null) {
            receiver.sendMessage(TextComponent.fromLegacyText(getPrefix() + text));
        }
    }

    /**
     * sends a message to a player
     *
     * @param uuid the players uuid
     * @param text the message
     */
    public static void sendMessage(UUID uuid, String text) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
        if (player != null) {
            sendMessage(player, text);
        }
    }

    /**
     * sends a message to all players with the permission "SupportTickets.receiveBukkitTeamMessage"
     *
     * @param message a text
     */
    public static void sendTeamMessage(String message) {
        for (ProxiedPlayer receiver : ProxyServer.getInstance().getPlayers()) {
            if (receiver.hasPermission("SupportTickets.mod")) {
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
        return SupportTicketsConfig.getInstance().getText("prefix");
    }

    /**
     * checks if a player is online
     *
     * @param uuid a players uuid
     * @return true or false
     */
    public boolean isPlayerOnline(UUID uuid) {
        for (ProxiedPlayer player : getProxy().getPlayers()) {
            if (player.getUniqueId().equals(uuid)) {
                return true;
            }
        }
        return false;
    }


    /**
     * returns the username of a player with the given uuid
     *
     * @param uuid a players uuid
     * @return his name
     */
    public String getNameByUUID(UUID uuid) {
        if (uuidCache.containsValue(uuid)) {
            for (Map.Entry<String, UUID> entry : uuidCache.entrySet()) {
                if (entry.getValue().equals(uuid)) {
                    return entry.getKey();
                }
            }
        } else if (getProxy().getPluginManager().getPlugin("UUIDDB") != null) {
            String name = UUIDDB.getInstance().getStorage().getNameByUUID(uuid);
            uuidCache.put(name, uuid);
            return name;
        } else {
            //this should only occur if the player has never joined this particular server.
            try {
                URL url = new URL(SupportTicketsConfig.getInstance().getAPINameUrl().replace("{0}", uuid.toString().replace("-", "")));
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                StringBuilder json = new StringBuilder();
                int read;
                while ((read = in.read()) != -1) {
                    json.append((char) read);
                }
                Object obj = new JSONParser().parse(json.toString());
                JSONArray jsonArray = (JSONArray) obj;
                String name = (String) ((JSONObject) jsonArray.get(jsonArray.size() - 1)).get("name");
                uuidCache.put(name, uuid);
                return name;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "Unknown Player";
    }

    /**
     * returns the uuid of the player with the given name
     *
     * @param name a players name
     * @return his uuid
     */
    public UUID getUUIDByName(String name) {
        if (uuidCache.containsKey(name)) {
            return uuidCache.get(name);
        } else if (getProxy().getPluginManager().getPlugin("UUIDDB") != null) {
            UUID uuid = UUID.fromString(UUIDDB.getInstance().getStorage().getUUIDByName(name, false));
            uuidCache.put(name, uuid);
            return uuid;
        } else {
            try {
                URL url = new URL(SupportTicketsConfig.getInstance().getAPIUUIDUrl().replace("{0}", name));
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
                UUID uuid = UUID.fromString(id.replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})",
                        "$1-$2-$3-$4-$5"));
                uuidCache.put(name, uuid);
                return uuid;
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
