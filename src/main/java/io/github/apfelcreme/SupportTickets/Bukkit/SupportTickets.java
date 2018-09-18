package io.github.apfelcreme.SupportTickets.Bukkit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.github.apfelcreme.SupportTickets.Bukkit.Listener.BungeeMessageListener;
import io.github.apfelcreme.SupportTickets.Bukkit.Listener.PlayerListener;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

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

    private Cache<UUID, Location> teleportQueue = CacheBuilder.newBuilder().expireAfterWrite(60, TimeUnit.SECONDS).build();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        new BungeeMessageListener(this); // registers itself
    }

    public void addToQueue(UUID playerId, Location location) {
        teleportQueue.put(playerId, location);
    }

    public Location getQueuedLocation(UUID playerId) {
        return teleportQueue.getIfPresent(playerId);
    }

    public void removeQueue(UUID playerId) {
        teleportQueue.invalidate(playerId);
    }
}
