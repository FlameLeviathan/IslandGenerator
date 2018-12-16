package com.chivalrycraft.islandgenerator;

import org.bukkit.Material;
import org.bukkit.block.Block;
import java.util.ArrayList;

public class IslandType {
    //Radius
    //Top Blocks (Blocks above ground)
    //Bottom Blocks (Blocks below ground)
    //Spawns (Mobs)
    //Type / Biome
    //Size (radius and base area)
    //Dungeons or loot areas (Separate Class or area to determine loot in chests (I.e. file with defined loot and random picker to choose loot)
    //Variables
    int radius;
    Biome biome;
    ArrayList topBlocks, bottomBlocks, mobSpawns;
    boolean dungeons, lootArea;

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public void setBiome(Biome biome) {
        this.biome = biome;
    }

    public void setTopBlocks(ArrayList topBlocks) {
        this.topBlocks = topBlocks;
    }

    public void setBottomBlocks(ArrayList bottomBlocks) {
        this.bottomBlocks = bottomBlocks;
    }

    public void setMobSpawns(ArrayList mobSpawns) {
        this.mobSpawns = mobSpawns;
    }

    public void setDungeons(boolean dungeons) {
        this.dungeons = dungeons;
    }

    public void setLootArea(boolean lootArea) {
        this.lootArea = lootArea;
    }

    public int getRadius() {
        return radius;
    }

    public Biome getBiome() {
        return biome;
    }

    public ArrayList getTopBlocks() {
        return topBlocks;
    }

    public ArrayList getBottomBlocks() {
        return bottomBlocks;
    }

    public ArrayList getMobSpawns() {
        return mobSpawns;
    }

    public boolean isDungeons() {
        return dungeons;
    }

    public boolean isLootArea() {
        return lootArea;
    }



    IslandType(int radius, Biome biome, ArrayList topBlocks, ArrayList bottomBlocks, ArrayList mobSpawns){
        this.radius = radius;
        this.biome = biome;
        this.topBlocks = topBlocks;
        this.bottomBlocks = bottomBlocks;
        this.mobSpawns = mobSpawns;
    }

    IslandType(int radius, Biome biome){
        this.radius = radius;
        this.biome = biome;
    }

    IslandType(int size, Biome biome, ArrayList topBlocks, ArrayList bottomBlocks, ArrayList mobSpawns, boolean dungeons, boolean lootArea){
        this.radius = parseSize(size);
        this.biome = biome;
        this.topBlocks = topBlocks;
        this.bottomBlocks = bottomBlocks;
        this.mobSpawns = mobSpawns;
        this.dungeons = dungeons;
        this.lootArea = lootArea;
    }



    enum Biome{
        JUNGLE, PLAIN, NETHER, MUSHROOM, ICE_SPIKE, TAIGA, MOUNTAIN, FOREST, DARK_FOREST, BEACH, DESERT;
    }

    int parseSize(int size){
        //TODO: Randomly generate radius based on size flag for larger islands (3) to combine islands
        switch(size){
            case 1:
                return 8;
            case 2:
                return 16;
            case 3:
                return 36;
            default:
                return 12;
        }

    }

    ArrayList parseBiome(Biome biome){
        ArrayList blocks = new ArrayList();
        switch(biome){
            case PLAIN:
                blocks.add(Material.GRASS);
                break;
            default:
                blocks.add(Material.GRASS);
                break;
        }
        return  blocks;
    }
}
