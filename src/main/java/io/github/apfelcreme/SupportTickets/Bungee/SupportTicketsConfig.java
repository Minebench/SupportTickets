package io.github.apfelcreme.SupportTickets.Bungee;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

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

    /**
     * the configuration
     */
    private Configuration configuration;
    /**
     * the language configuration
     */
    private Configuration languageConfiguration;

    /**
     * the configuration provider
     */
    private final  ConfigurationProvider yamlProvider = ConfigurationProvider
            .getProvider(net.md_5.bungee.config.YamlConfiguration.class);

    /**
     * constructor
     */
    public SupportTicketsConfig() {

        File configurationFile = new File(SupportTickets.getInstance().getDataFolder().getAbsoluteFile() + "/config.yml");
        try {
            if (!SupportTickets.getInstance().getDataFolder().exists()) {
                SupportTickets.getInstance().getDataFolder().mkdir();
            }
            if (!configurationFile.exists()) {
                createConfigFile("config.yml", configurationFile);
            }
            configuration = yamlProvider.load(configurationFile);

            for (ServerInfo serverInfo : ProxyServer.getInstance().getServers().values()) {
                if (configuration.get("disabledWorlds." + serverInfo.getAddress().getHostName() + "."
                        + serverInfo.getAddress().getPort()) == null) {
                    configuration.set("disabledWorlds." + serverInfo.getAddress().getHostName() + "."
                            + serverInfo.getAddress().getPort(), "");
                }
            }
            yamlProvider.save(configuration, configurationFile);

        } catch (IOException e) {
            e.printStackTrace();
        }

        File languageConfigurationFile = new File(SupportTickets.getInstance().getDataFolder().getAbsoluteFile() + "/lang." + getLanguage() + ".yml");
        try {
            if (!languageConfigurationFile.exists()) {
                createConfigFile("lang.de.yml", languageConfigurationFile);
            }
            languageConfiguration = yamlProvider.load(languageConfigurationFile);
            yamlProvider.save(languageConfiguration, languageConfigurationFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * saves both configs
     */
    public void save() {
        File configurationFile = new File(SupportTickets.getInstance().getDataFolder().getAbsoluteFile() + "/config.yml");
        File languageConfigurationFile = new File(SupportTickets.getInstance().getDataFolder().getAbsoluteFile() + "/lang." + getLanguage() + ".yml");
        try {
            yamlProvider.save(configuration, configurationFile);
            yamlProvider.save(languageConfiguration, languageConfigurationFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * copies a resource to the data folder
     *
     * @param source the resource file name
     * @param dest   the destination file
     * @throws IOException
     */
    private void createConfigFile(String source, File dest) throws IOException {
        Configuration configuration = yamlProvider.load(new InputStreamReader(SupportTickets.getInstance().getResourceAsStream(source)));
        yamlProvider.save(configuration, dest);
    }


    public String getLanguage() {
        return configuration.getString("lang", "de");
    }

    /**
     * returns the type of database that is used
     *
     * @return the type of database that is used
     */
    public DB getDb() {
        if (configuration.getString("db").equalsIgnoreCase("MySQL")) {
            return DB.MySQL;
        } else if (configuration.getString("db").equalsIgnoreCase("MongoDb")) {
            return DB.MongoDB;
        }
        return DB.MySQL;
    }

    /**
     * returns the username of the sql user
     *
     * @return the username of the sql user
     */
    public String getSqlUser() {
        return configuration.getString("sql.user");
    }

    /**
     * returns the sql password
     *
     * @return the sql password
     */
    public String getSqlPassword() {
        return configuration.getString("sql.password");
    }

    /**
     * returns the sql database name
     *
     * @return the sql database name
     */
    public String getSqlDatabase() {
        return configuration.getString("sql.database");
    }

    /**
     * returns the sql database url
     *
     * @return the sql database url
     */
    public String getSqlUrl() {
        return configuration.getString("sql.url");
    }

    /**
     * returns the mongo host name
     *
     * @return the mongo host name
     */
    public String getMongoHost() {
        return configuration.getString("mongo.host");
    }

    /**
     * returns the mongo port
     *
     * @return the mongo port
     */
    public int getMongoPort() {
        return configuration.getInt("mongo.port");
    }

    /**
     * returns the mongo user name
     *
     * @return the mongo user name
     */
    public String getMongoUser() {
        return configuration.getString("mongo.user");
    }

    /**
     * returns the mongo pass
     *
     * @return the mongo pass
     */
    public String getMongoPass() {
        return configuration.getString("mongo.pass");
    }

    /**
     * returns the mongo auth database
     *
     * @return the mongo auth database
     */
    public String getMongoAuthDb() {
        return configuration.getString("mongo.authdb");
    }

    /**
     * returns the mongo database name
     *
     * @return the mongo database name
     */
    public String getMongoDatabase() {
        return configuration.getString("mongo.database");
    }

    /**
     * returns the mongo collection name
     *
     * @return the mongo collection name
     */
    public String getMongoCollection() {
        return configuration.getString("mongo.collection");
    }

    /**
     * returns the ticket table name
     *
     * @return the ticket table name
     */
    public String getTicketTable() {
        return configuration.getString("sql.tables.tickets");
    }

    /**
     * returns the comments table name
     *
     * @return the comments table name
     */
    public String getCommentTable() {
        return configuration.getString("sql.tables.comments");
    }

    /**
     * returns the player table name
     *
     * @return the player table name
     */
    public String getPlayerTable() {
        return configuration.getString("sql.tables.players");
    }

    /**
     * returns the number of items displayed on each site in /pe list
     *
     * @return the number of items displayed on each site in /pe list
     */
    public int getPageSize() {
        return configuration.getInt("pageSize");
    }

    /**
     * returns the size of the top list
     *
     * @return the size of the top list
     */
    public int getTopListSize() {
        return configuration.getInt("topListSize");
    }

    /**
     * returns the delay of the reminder task
     *
     * @return the delay of the reminder task
     */
    public int getReminderTaskDelay() {
        return configuration.getInt("reminderTaskDelay");
    }

    /**
     * returns the URL for API-Calls with the mojang API
     *
     * @return the URL for API-Calls with the mojang API
     */
    public String getAPINameUrl() {
        return configuration.getString("apiUrlName");
    }

    /**
     * returns the URL for API-Calls with the mojang API
     *
     * @return the URL for API-Calls with the mojang API
     */
    public String getAPIUUIDUrl() {
        return configuration.getString("apiUrlUUID");
    }

    /**
     * returns the config
     *
     * @return the config
     */
    public Configuration getLanguageConfiguration() {
        return languageConfiguration;
    }


    /**
     * returns a texty string
     *
     * @param key the config path
     * @return the text
     */
    public String getText(String key) {
        String ret = (String) languageConfiguration.get("texts." + key);
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
