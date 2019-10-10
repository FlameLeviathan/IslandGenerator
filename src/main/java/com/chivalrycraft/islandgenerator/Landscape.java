package com.chivalrycraft.islandgenerator;

import com.boydti.fawe.FaweAPI;
import com.chivalrycraft.islandgenerator.com.sk89q.worldedit.function.operation.Chunk;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sun.xml.internal.bind.v2.model.core.EnumConstant;
import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.Blocks;
import net.minecraft.server.v1_12_R1.IBlockData;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.noise.SimplexOctaveGenerator;

import java.io.File;
import java.io.IOException;
import java.util.Random;


public class Landscape extends ChunkGenerator {
    //private int radius;
    //private Location loc;
    //public static World world = loc.getWorld();
    //int currentHeight;


    void generate(int radius, Location loc) {
        Core.report("Generating island...");
        //Bukkit.getPlayer("FlameKnight15").sendMessage("Test 1");
        //Location loc = Bukkit.getPlayer("FlameKnight15").getLocation();
        World world = loc.getWorld();
        double chunkX = loc.getX();
        double chunkZ = loc.getZ();
        int currentHeight;
        //this.loc = loc;
        //this.radius = radius;

        radius++;
        SimplexOctaveGenerator generator = new SimplexOctaveGenerator(new Random(world.getSeed()), 8);
        //The larger the scale is, the steeper the terrain.
        generator.setScale(0.005D);

        //ChunkGenerator.ChunkData chunk = createChunkData(loc.getWorld());

        for (int x = -radius; x <= radius; x++)
            for (int z = -radius; z <= radius; z++) {
                if ((x * x) + (z * z) <= (radius * radius)) {
                    double xNew = chunkX + x;
                    double zNew = chunkZ + z;
                    //int highestPoint = (int) loc.getY() + radius + x;
                    //Bukkit.getPlayer("FlameKnight15").sendMessage(xNew + ", " + zNew + " | Your Chunk: " + chunkX + ", " + chunkZ);//+ world.getBlockAt(Bukkit.getPlayer("FlameKnight15").getLocation()));

                    // TODO Heights and blocks generation code here.
                    currentHeight = (int) ((generator.noise(chunkX * radius + xNew, chunkZ * radius + zNew, 1.5D, 5D, true) + 1) * 15D + loc.getY());
                    currentHeight = (int) ((currentHeight) / (15D + loc.getY())) + (int) loc.getY();

                    Block block = loc.getWorld().getBlockAt(new Location(loc.getWorld(), xNew, currentHeight, zNew) );
                    Island.placeBlock(block.getLocation(), Blocks.GRASS.getBlockData());
                    Block blockUnder = loc.getWorld().getBlockAt(block.getLocation().add(0, -1, 0));
                    if (blockUnder.getType() == Material.AIR && blockUnder.getLocation().getY() >= (int) loc.getY()) {
                        block = loc.getWorld().getBlockAt(new Location(loc.getWorld(), xNew, currentHeight - 1, zNew));
                        //block.setType(Material.DIRT);
                        Island.placeBlock(block.getLocation(), Blocks.DIRT.getBlockData());

                    }
                    Bukkit.getWorld("world").loadChunk(loc.getChunk());
                }
            }
        //generateTrees(radius, loc);
        generateCaves(loc, 5, 5, 5);
        generateLake(radius/2, loc, 100, true);
/*        Chunk chunk = new Chunk(loc.getChunk());
        //for(final Chunk chunk : chunks) {
            for(final Player player : Bukkit.getOnlinePlayers()) {
                chunk.send(player);
            }
            world.loadChunk(loc.getChunk().getX(), loc.getChunk().getZ());*/
        //}
    }

/*    public void placeBlock(Location loc, IBlockData blockData){
        net.minecraft.server.v1_12_R1.World w = ((CraftWorld)loc.getWorld()).getHandle();
        net.minecraft.server.v1_12_R1.Chunk chunk = w.getChunkAt(loc.getBlockX() >> 4, loc.getBlockZ() >> 4);
        BlockPosition pos = new BlockPosition(loc.getBlockX() ,loc.getBlockY(), loc.getBlockZ());
        IBlockData data = blockData;//net.minecraft.server.v1_12_R1.Block.getByName("DIRT").getBlockData();
        chunk.a(pos, data);
    }*/

    /*void generateCaves(int radius, Location centerOfIsland){
        //Create a bunch of circles - Some small some large off of other circles but make sure that they are within
        //the island
        //Probably only call this for islands that are large
    }*/

    private void generateTrees(int radius, Location loc) {
        World world = loc.getWorld();
        //generateLake(radius, loc, 100, true);
        Random random = new Random(world.getSeed());
        if (random.nextBoolean()) {
            int amount = random.nextInt((radius*2) - 1) + 1;  // Amount of trees
            for (int i = 1; i < amount; i++) {
                int x = (int) loc.getX() + random.nextInt(radius+radius);
                int z = (int) loc.getZ() + random.nextInt(radius+radius);
                while(!(Math.abs(loc.getX() - x) <= (radius))){
                    x = (int) loc.getX() - random.nextInt(radius+radius);
                }
                while(!(Math.abs(loc.getZ() - z) <= (radius))){
                   z = (int) loc.getZ() - random.nextInt(radius+radius);
                }
                int y = (int) loc.getY() - 1;
                //for (int j = world.getMaxHeight() - 1; loc.getWorld().getBlockAt(x, j, z).getType() == Material.AIR; j--);// Find the highest block of the (x,z) coordinate chosen.
                //while (loc.getWorld().getBlockAt(x, y, z).getType() != Material.AIR) {
                //    y++;
                //}
                loadSchematic(new Location(loc.getWorld(), x, y, z), "Trees", null);

                //for(int temp = 0; temp < 10000; temp ++);
                //world.generateTree(chunk.getBlock(x, y, z).getLocation(), TreeType.TREE); // The tree type can be changed if you want.
            }
        }
    }

    private void loadSchematic(Location loc, String fileDir, String fileName) {
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

    public void generateLake(int radius, Location loc, int chanceOfSpawn, boolean isWater){
        Random random = new Random();

        if(radius > 0)
        if (random.nextInt(100-1) + 1 < chanceOfSpawn) { //Check chance of lake spawning on island
            loc.add(random.nextInt(radius + radius) - radius, 0, random.nextInt(radius + radius) - radius);
            int yDiff = random.nextInt(radius / 2) + 1;
            for (int y = loc.getBlockY(); y >= loc.getBlockY() - yDiff; y--) {
                //for(int i = 0; i < (y-(loc.getBlockY() - (1)))/14 + 2; i++)
                for (int x = -radius; x <= radius; x++) {
                    for (int z = -radius; z <= radius; z++) {
                        if ((x * x) + (z * z) <= ((radius - 1) * (radius - 1))) {
                            if(isWater)
                                //loc.getWorld().getBlockAt(loc.getBlockX() + x, y, loc.getBlockZ() + z).setType(Material.WATER);
                                Island.placeBlock(new Location(loc.getWorld(), loc.getBlockX() + x, y, loc.getBlockZ() + z), Blocks.FLOWING_WATER.getBlockData());
                            else
                                //loc.getWorld().getBlockAt(loc.getBlockX() + x, y, loc.getBlockZ() + z).setType(Material.LAVA);
                                Island.placeBlock(new Location(loc.getWorld(), loc.getBlockX() + x, y, loc.getBlockZ() + z), Blocks.FLOWING_LAVA.getBlockData());

                        }
                    }
                }
                radius--;
            }
        }
    }

    public void generateCaves(Location loc, double width, double height, double depth){
        Random random = new Random();
        Location cavePos = loc.clone();//new Location(Bukkit.getWorld("world"),random.nextInt((int)(width*width)), random.nextInt((int)(height*height)), random.nextInt((int)(depth*depth)));//a random point (x,y,z) within the (width,height,depth) space;
        double caveLength = random.nextFloat() * random.nextFloat() * 200;

        //cave direction is given by two angles and corresponding rate of change in those angles,
        //spherical coordinates perhaps?
        double theta = random.nextFloat() * Math.PI * 2;
        double deltaTheta = 0;
        double phi = random.nextFloat() * Math.PI * 2;
        double deltaPhi = 0;

        double caveRadius = random.nextFloat() * random.nextFloat();

        for (double len = 0; len < caveLength; len++) {
            cavePos.add(Math.sin(theta) * Math.cos(phi), Math.cos(theta) * Math.cos(phi), Math.sin(phi));

            theta += deltaTheta * 0.2;
            deltaTheta = (deltaTheta * 0.9) + random.nextFloat() - random.nextFloat();
            phi = phi/2 + deltaPhi/4;
            deltaPhi = (deltaPhi * 0.75) + random.nextFloat() - random.nextFloat();

            if (/*random.nextFloat() >= 0.25*/ true) {
                Location centerPos = new Location(Bukkit.getWorld("world"),
                        cavePos.getBlockX() + (random.nextInt(4) - 2) * 0.2,
                        cavePos.getBlockY() + (random.nextInt(1) - 1) * 0.2,
                        cavePos.getBlockZ() + (random.nextInt(4) - 2) * 0.2);
                //Bukkit.getLogger().info(ChatColor.DARK_GREEN + centerPos.toString());

                // eg. centerPos.x = cavePos.x + (randomInteger(4) - 2) * 0.2

                /*double radius = (height - centerPos.getBlockY()) / height;
                radius = 1.2 + (radius * 3.5 + 1) * caveRadius;
                radius = radius * Math.sin(len * Math.PI / caveLength);*/
                int radius = 5;

                fillOblateSpheroid( centerPos, (int)(radius), Blocks.AIR.getBlockData());
            }
        }

    }

    private void fillOblateSpheroid(Location center, int radius, IBlockData type) {

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = -radius; z <= radius; z++) {
                    if ((x * x) + (z * z) <= ((radius - 1) * (radius - 1))) {
                        int dx = x + center.getBlockX();
                        int dy = y + center.getBlockY();
                        int dz = z + center.getBlockZ();
                        if(Bukkit.getWorld("world").getBlockAt(dx, dy, dz).getType() != Material.AIR)
                            //Bukkit.getWorld("world").getBlockAt(dx, dy, dz).setType(type);
                            Island.placeBlock(new Location(center.getWorld(), dx, dy, dz), type);
                    }
                }
            }
        }

    }


}
