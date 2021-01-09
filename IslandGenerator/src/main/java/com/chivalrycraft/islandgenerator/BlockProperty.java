package com.chivalrycraft.islandgenerator;


import org.bukkit.Location;
import org.bukkit.Material;


public class BlockProperty {
    public Material blockType;
    public Location blockLocation;

    public BlockProperty(Location loc, Material block){
        blockLocation = loc;
        blockType = block;
    }
}
