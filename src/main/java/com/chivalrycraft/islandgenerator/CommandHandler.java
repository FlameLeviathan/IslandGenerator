package com.chivalrycraft.islandgenerator;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandHandler implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(label.equalsIgnoreCase("island")){
            IslandType type = new IslandType(7, IslandType.Biome.PLAIN);
            Island island = new Island(type, sender.getServer().getPlayer(sender.getName()).getLocation());
            island.createIsland();
            sender.sendMessage("Created!");
            return true;
        }
        return false;
    }
}
