package com.chivalrycraft.islandgenerator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class Island {

    //TODO: Add boolean or check for adding dungeons or other
    IslandType type;
    Location location;

    Island(IslandType type, int centerPointX, int centerPointY, int centerPointZ){
        //radius is island bottom radius
        //topBlocks are the blocks that will appear above ground
        //bottomBlocks are the blocks that will appear in the ground (i.e. stone, cobblestone, ores, etc)
    }

    Island(IslandType type, Location location){
        this.type = type;
        this.location = location;
        //radius is island bottom radius
        //topBlocks are the blocks that will appear above ground
        //bottomBlocks are the blocks that will appear in the ground (i.e. stone, cobblestone, ores, etc)
    }

    public void createIsland(){
        createLowerPart(type.getRadius(), location.add(15, 0,0));
    }


    private void createLowerPart(int radius, Location loc){

        int startX = loc.getBlockX(), startY = loc.getBlockY(), startZ = loc.getBlockZ();
        for(int x = startX; x < startX + (radius * 2); x++){
            for (int y = startY; y < startY + (radius * 2); y++){
                for(int z = startZ; z < startZ + (radius * 2); z++){
                    Block blockLoc = loc.getWorld().getBlockAt(x, y, z);
                    blockLoc.setType(Material.STONE);
                }
            }
        }
        //const MAX_REMOVABLE_BLOCKS =
    }
}
