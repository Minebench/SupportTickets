package io.github.apfelcreme.SupportTickets.Bungee;

import io.github.apfelcreme.SupportTickets.Bungee.Command.*;
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
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.zaiyers.UUIDDB.core.UUIDDBPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
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
     * directly store reference to UUIDDB plugin instead of always getting the instance
     */
    private UUIDDBPlugin uuidDb = null;

    /**
     * The plugin's config
     */
    private SupportTicketsConfig config;

    /**
     * Last shown ticket cache
     */
    private Map<String, Set<Integer>> shownTicketsCache = new HashMap<>();

    /**
     * returns the plugin instance
     * TODO: Get rid of the need for this static method
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

        if (getProxy().getPluginManager().getPlugin("UUIDDB") != null) {
            uuidDb = (UUIDDBPlugin) getProxy().getPluginManager().getPlugin("UUIDDB");
        }

        // initialize the uuid cache
        uuidCache = new HashMap<>();

        // init the config
        config = SupportTicketsConfig.getInstance();

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

        CommandExecutor ticketCommand = new CommandExecutor(this,   "ticket",                           null,                   "ti", "petition", "pe");

//                                       Class extends SubCommand   name        arguments               permission              aliases...
        ticketCommand.registerSubCommand(new AssignCommand(this,    "assign",   "<#> [<player>]",       "SupportTickets.mod"));
        ticketCommand.registerSubCommand(new CloseCommand(this,     "close",    "<#> <reason>",         "SupportTickets.user"));
        ticketCommand.registerSubCommand(new ClosedCommand(this,    "closed",   "<player> [<#page>]",   "SupportTickets.mod"));
        ticketCommand.registerSubCommand(new CommentCommand(this,   "comment",  "<#> <comment>",        "SupportTickets.user",  "log"));
        ticketCommand.registerSubCommand(new HelpCommand(this,      "help"));
        ticketCommand.registerSubCommand(new InfoCommand(this,      "info",     "<#>",                  "SupportTickets.mod"));
        ticketCommand.registerSubCommand(new ListCommand(this,      "list",     "[<#page>]",            "SupportTickets.mod"));
        ticketCommand.registerSubCommand(new NewCommand(this,       "new",      "<text>",               "SupportTickets.user",  "open"));
        ticketCommand.registerSubCommand(new OpenedCommand(this,    "opened",   "<player> [<#page>]",   "SupportTickets.mod"));
        ticketCommand.registerSubCommand(new ReloadCommand(this,    "reload",   "",                     "SupportTickets.mod"));
        ticketCommand.registerSubCommand(new ReopenCommand(this,    "reopen",   "<#>",                  "SupportTickets.mod"));
        ticketCommand.registerSubCommand(new ShowCommand(this,      "show",     "[<page>]",             "SupportTickets.user"));
        ticketCommand.registerSubCommand(new TopCommand(this,       "top",      "",                     "SupportTickets.mod"));
        ticketCommand.registerSubCommand(new UnassignCommand(this,  "unassign", "<#>",                  "SupportTickets.mod"));
        ticketCommand.registerSubCommand(new ViewCommand(this,      "view",     "<#>",                  "SupportTickets.user"));
        ticketCommand.registerSubCommand(new WarpCommand(this,      "warp",     "<#>",                  "SupportTickets.mod",   "goto"));

        // register the command
        getProxy().getPluginManager().registerCommand(this, ticketCommand);

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
        if (uuid.equals(new UUID(0, 0))) {
            return "[Console]";
        }
        String name = null;
        if (uuidDb != null) {
            name = uuidDb.getStorage().getNameByUUID(uuid);
        } else if (uuidCache.containsValue(uuid)) {
            for (Map.Entry<String, UUID> entry : uuidCache.entrySet()) {
                if (entry.getValue().equals(uuid)) {
                    name = entry.getKey();
                    break;
                }
            }
        }

        if (name == null) {
            //this should only occur if the player has never joined
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
                name = (String) ((JSONObject) jsonArray.get(jsonArray.size() - 1)).get("name");
                if (uuidDb != null) {
                    uuidDb.getStorage().insert(uuid, name);
                } else {
                    uuidCache.put(name, uuid);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return name != null ? name : "Unknown Player";
    }

    /**
     * returns the uuid of the player with the given name
     *
     * @param name a players name
     * @return his uuid
     */
    public UUID getUUIDByName(String name) {
        if ("[Console]".equalsIgnoreCase(name)) {
            return new UUID(0, 0);
        }
        UUID uuid = null;
        if (uuidDb != null) {
            uuid = UUID.fromString(uuidDb.getStorage().getUUIDByName(name, false));
        } else if (uuidCache.containsKey(name)) {
            uuid = uuidCache.get(name);
        } else {
            // Search for name with different case
            for (Map.Entry<String, UUID> entry : uuidCache.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(name)) {
                    uuid = entry.getValue();
                    break;
                }
            }
        }

        if (uuid == null) {
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
                // Get correct case of the inputted name
                name = jsonObject.get("name").toString();
                String id = jsonObject.get("id").toString();
                uuid = UUID.fromString(id.replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
                if (uuidDb != null) {
                    uuidDb.getStorage().insert(uuid, name);
                } else {
                    uuidCache.put(name, uuid);
                }
                return uuid;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return uuid;
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

    /**
     * returns the server info with the given ip (xxx.xxx.xxx.xxx:PORT)
     *
     * @param server the server name or ip:port
     * @return the serverInfo
     */
    public static ServerInfo getServer(String server) {
        ServerInfo sInfo = ProxyServer.getInstance().getServerInfo(server);
        if (sInfo != null) {
            return sInfo;
        }
        try {
            InetSocketAddress address = new InetSocketAddress(server.split(":")[0], Integer.parseInt(server.split(":")[1]));
            for (ServerInfo serverInfo : ProxyServer.getInstance().getServers().values()) {
                if (serverInfo.getAddress().equals(address)) {
                    return serverInfo;
                }
            }
        } catch (NumberFormatException e) {
            getInstance().getLogger().log(Level.SEVERE, "Error while getting server '" + server + "'!", e);
        }
        return null;
    }

    public SupportTicketsConfig getConfig() {
        return config;
    }

    public void addShownTicket(CommandSender sender, int ticketId) {
        Set<Integer> senderTickets = shownTicketsCache.get(sender.getName().toLowerCase());
        if (senderTickets == null) {
            senderTickets = new HashSet<>();
        }
        if (senderTickets.size() > 100) {
            Iterator ticketIt = senderTickets.iterator();
            while (senderTickets.size() > 100 && ticketIt.hasNext()) {
                ticketIt.next();
                ticketIt.remove();
            }
        }
        senderTickets.add(ticketId);
        shownTicketsCache.put(sender.getName().toLowerCase(), senderTickets);
    }

    public Set<Integer> getLastShownTickets(CommandSender sender) {
        Set<Integer> senderTickets = shownTicketsCache.get(sender.getName().toLowerCase());

        return senderTickets != null ? senderTickets : new HashSet<Integer>();
    }
}
