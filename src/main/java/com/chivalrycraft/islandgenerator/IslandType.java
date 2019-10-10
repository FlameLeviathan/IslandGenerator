package com.chivalrycraft.islandgenerator;


import org.bukkit.Material;
import org.bukkit.entity.Entity;

import java.util.HashMap;

public class IslandType {
    //Radius
    //Top Material (Material above ground)
    //Bottom Material (Material below ground)
    //Spawns (Mobs)
    //Type / Biome
    //Size (radius and base area)
    //Dungeons or loot areas (Separate Class or area to determine loot in chests (I.e. file with defined loot and random picker to choose loot)
    //Variables
    int radius;
    Biome biome;
    HashMap<Material, Double> bottomBlocks = new HashMap<Material, Double>();
    HashMap<Material, Double> topBlocks = new HashMap<Material, Double>();
    HashMap<Entity, Double> mobSpawns = new HashMap<Entity, Double>();
    boolean dungeons, lootArea;

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public void setBiome(Biome biome) {
        this.biome = biome;
    }

    public void setTopBlocks(HashMap<Material, Double> topBlocks) {
        this.topBlocks = topBlocks;
    }

    public void setBottomBlocks(HashMap<Material, Double> bottomBlocks) {
        this.bottomBlocks = bottomBlocks;
    }

    public void setMobSpawns(HashMap<Entity, Double> mobSpawns) {
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

    public HashMap<Material, Double> getTopBlocks() {
        return topBlocks;
    }

    public HashMap<Material, Double> getBottomBlocks() {
        return bottomBlocks;
    }

    public HashMap<Entity, Double> getMobSpawns() {
        return mobSpawns;
    }

    public boolean isDungeons() {
        return dungeons;
    }

    public boolean isLootArea() {
        return lootArea;
    }



    IslandType(int radius, Biome biome, HashMap<Material, Double> topBlocks, HashMap<Material, Double> bottomBlocks, HashMap<Entity, Double> mobSpawns){
        this.radius = radius;
        this.biome = biome;
        this.topBlocks = topBlocks;
        this.bottomBlocks = bottomBlocks;
        this.mobSpawns = mobSpawns;
    }

    //Test method
    IslandType(int radius, Biome biome){
        this.radius = radius;
        this.biome = biome;
        bottomBlocks = parseBottomBlocks();
    }

    IslandType(int size, Biome biome, HashMap<Material, Double> topBlocks, HashMap<Material, Double> bottomBlocks, HashMap<Entity, Double> mobSpawns, boolean dungeons, boolean lootArea){
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

    Biome parseBiome(Biome biome){
        Biome blocks = Biome.PLAIN;
        switch(biome){
            case PLAIN:
                //Change this to grass
                break;
            default:
                break;
        }
        return blocks;
    }

    HashMap<Material, Double> parseBottomBlocks(){
        HashMap<Material, Double> blocks = new HashMap<>();
        switch(biome){
            case PLAIN:
                //Change this to grass
                blocks.put(Material.STONE, 85.0);
                blocks.put(Material.COBBLESTONE, 50.0);
                blocks.put(Material.IRON_ORE, 9.0);
                blocks.put(Material.COAL_ORE, 10.0);
                blocks.put(Material.GOLD_ORE, 3.0);
                blocks.put(Material.DIAMOND_ORE, 1.0);
                break;
            default:
                blocks.put(Material.STONE, 75.0);
                break;
        }
        return  blocks;
    }
}
