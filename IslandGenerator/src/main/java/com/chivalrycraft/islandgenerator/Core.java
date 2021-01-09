package com.chivalrycraft.islandgenerator;

import com.boydti.fawe.FaweAPI;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public final class Core extends JavaPlugin {

    //public static ProtocolManager protocolManager;
    File cFile = new File(getDataFolder(), "config.yml");
    public FileConfiguration config = YamlConfiguration.loadConfiguration(cFile);
    HashMap<Location, ArrayList<BlockProperty>> chunkBlocks = new HashMap<>();

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        config = YamlConfiguration.loadConfiguration(cFile);
        config.options().copyDefaults(true);
        //System.out.println(config.toString());

        // Plugin startup logic
        this.getCommand("island").setExecutor(new CommandHandler(this));
        boolean schematicFolder = new File(Bukkit.getPluginManager().getPlugin("IslandGenerator").getDataFolder() + File.separator + "/schematics").mkdirs();
        if(!schematicFolder){
            System.out.println("Schematic folder Directory has failed!");
        }
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

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return new CustomChunkGenerator(this);
    }

    public Plugin getPlugin(){
        return this;
    }

    public static void report(String s){
        Bukkit.getPlayer("FlameKnight15").sendMessage(s);
    }

    protected void loadSchematic(Location loc, String fileDir, String fileName) {
        Random random = new Random();
        File schematic;

        File schematicDir = new File(Bukkit.getPluginManager().getPlugin("IslandGenerator").getDataFolder() + File.separator + "/schematics/" + fileDir);

        if(fileName == null) {
            int ranNum = random.nextInt(schematicDir.listFiles().length);
            schematic = new File( schematicDir.listFiles()[ranNum] + "");
        } else {
            schematic = new File(Bukkit.getPluginManager().getPlugin("IslandGenerator").getDataFolder() + File.separator + "/schematics/" + fileDir + "/" + fileName + ".schematic");
        }

        try {
            //Core.report(schematicDir.listFiles().length + ", " + schematic);

            Clipboard clipboard = ClipboardFormat.SCHEMATIC.load(schematic).getClipboard();
            Vector pos = new Vector(loc.getX() - clipboard.getDimensions().getX()/2, loc.getY(), loc.getZ() - clipboard.getDimensions().getZ()/2);
            EditSession editSession = ClipboardFormat.findByFile(schematic).load(schematic).paste(FaweAPI.getWorld(loc.getWorld().getName()), pos, true, false, null);
            editSession.setFastMode(true);
            editSession.setBlockChangeLimit(Integer.MAX_VALUE);
            editSession.flushQueue();
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
    }
}
