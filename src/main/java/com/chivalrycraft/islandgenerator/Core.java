package com.chivalrycraft.islandgenerator;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class Core extends JavaPlugin {

    public static ProtocolManager protocolManager;
    File cFile = new File(getDataFolder(), "config.yml");
    public FileConfiguration config = YamlConfiguration.loadConfiguration(cFile);

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        config = YamlConfiguration.loadConfiguration(cFile);
        config.options().copyDefaults(true);
        //System.out.println(config.toString());

        // Plugin startup logic
        //CommandExecutor cmd = new CommandHandler(this);
        this.getCommand("island").setExecutor(new CommandHandler(this));
        boolean schematicFolder = new File(Bukkit.getPluginManager().getPlugin("IslandGenerator").getDataFolder() + File.separator + "/schematics").mkdirs();
        if(!schematicFolder){
            System.out.println("Schematic folder Directory has failed!");
        }

        protocolManager = ProtocolLibrary.getProtocolManager();
        //cFile.getParentFile().mkdirs();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        config.options().copyDefaults(true);
    }

    public FileConfiguration getCustomConfig(){
        return config;
    }

    @Override
    public void reloadConfig(){
        config = YamlConfiguration.loadConfiguration(cFile);
        Bukkit.getLogger().info(ChatColor.GREEN + "Config file reloaded!");
    }

    public static void report(String s){
        Bukkit.getPlayer("FlameKnight15").sendMessage(s);
    }
}
