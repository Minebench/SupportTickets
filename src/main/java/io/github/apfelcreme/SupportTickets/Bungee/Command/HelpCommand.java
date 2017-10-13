package io.github.apfelcreme.SupportTickets.Bungee.Command;

import io.github.apfelcreme.SupportTickets.Bungee.SupportTickets;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.config.Configuration;

import java.util.*;

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
public class HelpCommand extends SubCommand {

    public HelpCommand(SupportTickets plugin, String name) {
        super(plugin, name);
    }

    /**
     * executes a subcommand
     *
     * @param sender the sender
     * @param args   the string arguments in an array
     */
    public void execute(CommandSender sender, String[] args) {
        Configuration configurationSection =
                plugin.getConfig().getLanguageConfiguration().getSection("texts.info.help.commands");
        List<String> keys = new ArrayList<>();
        List<String> strings = new ArrayList<>();

        for (String key : configurationSection.getSection("user").getKeys()) {
            keys.add("info.help.commands.user."+key);
        }

        if (sender.hasPermission("SupportTickets.mod")) {
            for (String key : configurationSection.getSection("mod").getKeys()) {
                keys.add("info.help.commands.mod."+key);
            }
        }
        for (String key : keys) {
            System.out.println(key);
            strings.add(plugin.getConfig().getText(key));
        }
        Collections.sort(strings);
        plugin.sendMessage(sender, plugin.getConfig().getText("info.help.header"));
        for (Object s : strings) {
            plugin.sendMessage(sender, ChatColor.translateAlternateColorCodes('&', s.toString()));
        }
    }
}
