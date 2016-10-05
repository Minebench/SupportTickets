package io.github.apfelcreme.SupportTickets.Bungee;

import io.github.apfelcreme.SupportTickets.Bungee.Command.*;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

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
public class TicketCommandExecutor extends Command {

    public TicketCommandExecutor() {
        super("ticket", null, "ti", "petition", "pe");
    }

    @Override
    public void execute(final CommandSender commandSender, final String[] strings) {

        if (commandSender instanceof ProxiedPlayer) {
            SubCommand subCommand = null;
            if (strings.length > 0) {
                Operation operation = Operation.getOperation(strings[0]);
                if (operation != null) {
                    switch (operation) {
                        case ASSIGN:
                            subCommand = new AssignCommand();
                            break;
                        case CLOSE:
                            subCommand = new CloseCommand();
                            break;
                        case CLOSED:
                            subCommand = new ClosedCommand();
                            break;
                        case COMMENT:
                        case LOG:
                            subCommand = new CommentCommand();
                            break;
                        case HELP:
                            subCommand = new HelpCommand();
                            break;
                        case INFO:
                            subCommand = new InfoCommand();
                            break;
                        case LIST:
                            subCommand = new ListCommand();
                            break;
                        case OPEN:
                        case NEW:
                            subCommand = new NewCommand();
                            break;
                        case OPENED:
                            subCommand = new OpenedCommand();
                            break;
                        case RELOAD:
                            subCommand = new ReloadCommand();
                            break;
                        case REOPEN:
                            subCommand = new ReopenCommand();
                            break;
                        case SHOW:
                            subCommand = new ShowCommand();
                            break;
                        case TOP:
                            subCommand = new TopCommand();
                            break;
                        case UNASSIGN:
                            subCommand = new UnassignCommand();
                            break;
                        case VIEW:
                            subCommand = new ViewCommand();
                            break;
                        case GOTO:
                        case WARP:
                            subCommand = new WarpCommand();
                            break;
                    }
                } else {
                    SupportTickets.sendMessage(commandSender, SupportTicketsConfig.getInstance().getText("error.unknownCommand")
                            .replace("{0}", strings[0]));
                }
            } else {
                subCommand = new HelpCommand();
            }
            if (subCommand != null) {
                final SubCommand finalSubCommand = subCommand;

                // execute the subcommand in a thread
                SupportTickets.getInstance().getProxy().getScheduler().runAsync(SupportTickets.getInstance(), new Runnable() {
                    public void run() {
                        finalSubCommand.execute(commandSender, strings);
                    }
                });
            }
        } else {
            SupportTickets.getInstance().getProxy().getLogger().info("This command can only be run by a player!");
        }
    }

    /**
     * a list of available subcommands
     */
    public enum Operation {
        ASSIGN, CLOSE, CLOSED, COMMENT, GOTO, HELP, INFO, LIST, LOG, NEW, OPEN, OPENED, RELOAD, REOPEN, SHOW, TOP, UNASSIGN, VIEW, WARP;

        /**
         * returns the matching operation
         *
         * @param key the key
         * @return the matching operation
         */
        public static Operation getOperation(String key) {
            for (Operation operation : Operation.values()) {
                if (operation.name().equalsIgnoreCase(key)) {
                    return operation;
                }
            }
            return null;
        }
    }
}
