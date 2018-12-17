package com.chivalrycraft.islandgenerator;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.Vector;

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
        createLowerPart(type.getRadius(), location.add(0, 0,0));
    }


    private void createLowerPart(int radius, Location loc){

        int startX = loc.getBlockX(), startY = loc.getBlockY(), startZ = loc.getBlockZ();
/*        for(int x = startX; x < startX + (radius * 2); x++){
            for (int y = startY; y < startY + (radius  *2); y++){
                for(int z = startZ; z < startZ + (radius * 2); z++){



                }
                //startZ--;
            }
            startY--;
            //radius--;
        }*/
        //const MAX_REMOVABLE_BLOCKS =
        Bukkit.getPlayer("FlameKnight15").sendMessage(startX  + "," + startY + "," + startZ + ", Radius: " + radius);
        int decrease = radius;// + .5;
        int amount = 145;
        double increment = ( 2 * Math.PI * radius) / amount;

        for (int y = startY; y >= (startY - radius); y--) {
            for (int x = -decrease; x <= /*startX +*/ decrease; x++) {
                for (int z = -decrease; z <= /*startZ +*/ decrease; z++) {
                for(int i = 0;i < amount; i++) {
                    double angle = i * increment;

                    double xNew = startX + (decrease * Math.cos(angle));//- x;
                    int yNew = y;
                    double zNew = startZ + (decrease * Math.sin(angle));//- z;

                    //Block block = location.add(xNew, yNew, zNew).getBlock();
                    Block block = loc.getWorld().getBlockAt(new Location(loc.getWorld(), xNew, yNew, zNew));
                    //Block block = loc.getWorld().getBlockAt(xNew, yNew, zNew);
                    //if(blockLoc.getLocation().add(0, y,0).distance(blockLoc.getLocation()) >= radius) {

                    block.setType(Material.STONE);

                    Bukkit.getPlayer("FlameKnight15").sendMessage(y + " | " + xNew + "," + yNew + "," + zNew + "");
                    //}
                    }
                }

            }
            decrease--;
        }
    }
}
