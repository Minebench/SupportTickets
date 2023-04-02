package io.github.apfelcreme.SupportTickets.Bungee.Database.Connector;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.github.apfelcreme.SupportTickets.Bungee.SupportTickets;
import org.bson.Document;

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
public class MongoConnector {

    private final SupportTickets plugin;

    private MongoClient mongoClient;

    public MongoConnector(SupportTickets plugin) {
        this.plugin = plugin;
    }

    /**
     * returns the mongo collection
     * @return a DBCollection
     */
    public MongoCollection<Document> getCollection() {
        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString("mongodb://" + plugin.getConfig().getMongoUser() + ":" + plugin.getConfig().getMongoPass() + "@" + plugin.getConfig().getMongoHost() + ":" + plugin.getConfig().getMongoPort() + "/"))
                .serverApi(serverApi)
                .build();

        try (MongoClient mongo = MongoClients.create(settings)) {
            this.mongoClient = mongo;
            // get collection
            MongoDatabase db = mongo.getDatabase(plugin.getConfig().getMongoDatabase());
            return db.getCollection(plugin.getConfig().getMongoCollection());
        } catch (MongoException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * closes the mongodb connection
     */
    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}
