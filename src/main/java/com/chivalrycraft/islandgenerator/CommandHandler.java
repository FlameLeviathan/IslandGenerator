package com.chivalrycraft.islandgenerator;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandHandler implements CommandExecutor {

    Core core;
    CommandHandler(Core c){
        core = c;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("island")) {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("create")) {
                    IslandType type;
                    if (args.length >= 3) {
                        sender.sendMessage(Integer.parseInt(args[1]) + " : " + Integer.parseInt(args[2]));
                        type = new IslandType(Integer.parseInt(args[1]), IslandType.Biome.PLAIN);
                        Island island = new Island(type, sender.getServer().getPlayer(sender.getName()).getLocation());
                        island.makeIsland(Integer.parseInt(args[2]));
                        sender.sendMessage("Created!");
                    } else {
                        type = new IslandType(5, IslandType.Biome.PLAIN);
                        Island island = new Island(type, sender.getServer().getPlayer(sender.getName()).getLocation());
                        island.makeIsland(1);
                        sender.sendMessage("Created!");
                    }

                    return true;
                }
                else if (label.equalsIgnoreCase("reload")) {
                    core.reloadConfig();
                }
            } else {
                //Print all command options
            }
        }
        return false;
    }
}
