package com.chivalrycraft.islandgenerator;

import com.boydti.fawe.bukkit.v1_12.packet.FaweChunkPacket;
import com.boydti.fawe.object.FaweChunk;
import com.boydti.fawe.regions.general.plot.FaweChunkManager;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.sk89q.worldedit.blocks.BlockData;
import net.minecraft.server.v1_12_R1.*;
import net.minecraft.server.v1_12_R1.World;
import org.bukkit.*;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_12_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;


public class Island {
    //TODO: Add boolean or check for adding dungeons or other
    IslandType type;
    Location location;
    HashMap<Material, Double> bottomBlocks = new HashMap<Material, Double>();

    File cFile = new File(Bukkit.getPluginManager().getPlugin("IslandGenerator").getDataFolder(), "config.yml");
    public FileConfiguration config = YamlConfiguration.loadConfiguration(cFile);


    Island(IslandType type, int centerPointX, int centerPointY, int centerPointZ) {
        //radius is island bottom radius
        //topBlocks are the blocks that will appear above ground
        //bottomBlocks are the blocks that will appear in the ground (i.e. stone, cobblestone, ores, etc)
    }

    Island(IslandType type, Location location) {
        this.type = type;
        this.location = location;
        this.bottomBlocks = type.bottomBlocks;
        //radius is island bottom radius
        //topBlocks are the blocks that will appear above ground
        //bottomBlocks are the blocks that will appear in the ground (i.e. stone, cobblestone, ores, etc)
    }

    public void makeIsland(int maxIslandCombo) {
        Random random = new Random();
        // max = radius; min = -radius;
        int originalRadius = type.getRadius();
        int islandCount = maxIslandCombo;//random.nextInt(maxIslandCombo);
        //for(int i = 0; i < islandCount; i++) {
        int radius;
            /*if(i % 2 != 0) {
                radius = random.nextInt(originalRadius - 1) + 1;
            } else {
                radius = random.nextInt((originalRadius/2) + 1 - 5) + 5;
            }*/
        //if (i == 0)
        radius = originalRadius;
        int xSign = random.nextBoolean() ? -1 : 1;
        int zSign = random.nextBoolean() ? -1 : 1;
        double x, y, z;
        type.setRadius(radius);
        Bukkit.getPlayer("FlameKnight15").sendMessage("Radius: " + radius);//+ " | xSign: " + xSign + " | zSign: " + zSign);
        do {
            x = (location.getX() + (/*xSign **/ random.nextInt(radius + 1 + radius) - radius / 2)); //+ radius / 4); //+ (radius)); // Was type.getRadius() + type.getRadius()
            z = (location.getZ() + (/*zSign **/ random.nextInt(radius + 1 + radius) - radius / 2)); //+ radius / 4); //+ (radius));
            y = (location.getY() + random.nextInt(1 + 1 + 1) - 1);
        } while (location.getWorld().getBlockAt(((int) x), ((int) y), ((int) z)).getType() != Material.AIR);

        location.setX(x);
        location.setY(y);
        location.setZ(z);
        createIsland();

        //}


    }

    public void createIsland() {
/*        Bukkit.getScheduler().runTaskAsynchronously(Bukkit.getPluginManager().getPlugin("IslandGenerator"), new Runnable() {
            @Override
            public void run() {*/
        createLowerPart(type.getRadius(), location.add(0, 0, 0));
        //createUpperPart(type.getRadius(), location.add(0, 1, 0));
/*            }
        });*/

    }

    public void createUpperPart(int radius, Location loc) {
        Landscape top = new Landscape();
        top.generate(radius, loc);
    }


    private void createLowerPart(int radius, Location loc) {

        //HashMap<Location, IBlockData> blocksToPlace = new HashMap<>();
        int islandBlockPlaceAmount = config.getInt("numberOfBlocksToPlacePerTick");
        ArrayList<Block> locationList = new ArrayList<>();
        ArrayList<Material> blocksToPlace = new ArrayList<>();
        int startX = loc.getBlockX(), startY = loc.getBlockY(), startZ = loc.getBlockZ();
        double decrease = Math.sqrt((radius * radius) + ((radius * 2) * (radius * 2)));

        for (int y = startY; y >= (startY - (decrease * 2)); y--) {
            //Could tamper with i to make it shorter per level
            for (int i = 0; i <= (y - (startY - (decrease * 2))) / 14 + 2; i++) {
                for (double x = -radius; x <= radius; x++) {
                    for (double z = -radius; z <= radius; z++) {

                        if ((x * x) + (z * z) <= (radius * radius)) {

                            double xNew = startX + x;//(decrease * Math.cos(angle));//- x;
                            int yNew = y;
                            double zNew = startZ + z;//(decrease * Math.sin(angle));//- z;
                            if (pointOnEdge(startX, xNew, startZ, zNew, radius)) {
                                Random random = new Random();
                                if (random.nextInt(100 - 1 + 1) + 1 < 25) {
                                    Block block = loc.getWorld().getBlockAt(new Location(loc.getWorld(), xNew, yNew, zNew));
                                    locationList.add(block);
                                    blocksToPlace.add(Material.AIR);
                                } else {
                                    Block block = loc.getWorld().getBlockAt(new Location(loc.getWorld(), xNew, yNew, zNew));
                                    locationList.add(block);
                                    blocksToPlace.add(getRandomBlockType(bottomBlocks));
                                }
                            } else {
                                Block block = loc.getWorld().getBlockAt(new Location(loc.getWorld(), xNew, yNew, zNew));
                                locationList.add(block);
                                blocksToPlace.add(getRandomBlockType(bottomBlocks));
                            }
                        }
                    }
                }
                y--;
            }
            y++;
            radius--;
        }
        procedurallyPlaceBlocks(locationList, blocksToPlace, islandBlockPlaceAmount);
    }

    int taskId;

    public void procedurallyPlaceBlocks(List<Block> locationList, List<Material> blockDataList, int time) {
        //final Location[] loc = {null};
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(Bukkit.getPluginManager().getPlugin("IslandGenerator"), new Runnable() {
            @Override
            public void run() {
                Bukkit.getPlayer("FlameKnight15").sendMessage("LOC SIZE: " + locationList.size());
                for (int i = 0; i < locationList.size(); i++) {
                    //Bukkit.getPlayer("FlameKnight15").sendMessage("i: " + i + " Time: " + time);
                    if (i <= time) {
                        locationList.get(i).setType(blockDataList.get(i));
                        //Bukkit.getPlayer("FlameKnight15").sendMessage("Material Placed 2");
                        //placeBlock(locationList.get(i), blockDataList.get(i));
                        locationList.remove(i);
                        blockDataList.remove(i);
                    } else {
                        //Bukkit.getPlayer("FlameKnight15").sendMessage("Material not Placed");
                        break;
                    }
                }

                /*for (int i = 0; i < locationList.size(); i++){
                    if(i <= time) {
                        //Bukkit.getPlayer("FlameKnight15").sendMessage("REMOVING ENTRY");

                        locationList.remove(i);
                        blockDataList.remove(i);
                    } else {
                        break;
                    }
                }*/

                if (locationList.size() == 0) {
                    Bukkit.getPlayer("FlameKnight15").sendMessage("ISLAND FINISHED");
                    stopScheduler();
                }
                return;

            }
        }, 0L, 1L);


    }

    public void stopScheduler() {
        Bukkit.getServer().getScheduler().cancelTask(taskId);
    }

    public void PacketMapChunk(org.bukkit.Chunk chunk) {
        chunk = (Chunk) ((CraftChunk) chunk).getHandle();
    }

    public final void send(final Player player, Chunk chunk) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutMapChunk());
    }

    public void updateChunks(Chunk chunk) {
        int diffx, diffz;
        int view = Bukkit.getServer().getViewDistance() << 4;
        //for (Chunk chunk : changedChunks) {
        World world = ((CraftChunk) chunk).getHandle().world;
        for (EntityHuman ep : world.players) {
            diffx = Math.abs((int) ep.locX - chunk.getX() << 4);
            diffz = Math.abs((int) ep.locZ - chunk.getZ() << 4);
            if (diffx <= view && diffz <= view) {
                //ep.chunkCoordIntPairQueue.add(new ChunkCoordIntPair(chunk.getX(), chunk.getZ()));
            }
        }
        //}
    }

    /**
     * Places a block at locaction loc with block blockData
     * This method uses NMS to place the block
     *
     * @param loc       location of block being placed
     * @param blockData block to place with data
     */
    public static void placeBlock(Location loc, IBlockData blockData) {
        net.minecraft.server.v1_12_R1.World w = ((CraftWorld) loc.getWorld()).getHandle();
        net.minecraft.server.v1_12_R1.Chunk chunk = w.getChunkAt(loc.getBlockX() >> 4, loc.getBlockZ() >> 4);
        BlockPosition pos = new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        IBlockData data = blockData;//net.minecraft.server.v1_12_R1.Material.getByName("DIRT").getBlockData();
        chunk.a(pos, data);
        return;
    }

    Material getRandomBlockType(HashMap<Material, Double> blocks) {
        Material block = Material.STONE;
        for (Material b : blocks.keySet()) {
            Random random = new Random();
            if (random.nextInt(100 - 1 + 1) + 1 <= blocks.get(b)) {
                block = b;
                break;
            }
        }
        return block;
    }


    boolean pointOnEdge(int startX, double xNew, int startZ, double zNew, int radius) {

        double dx = startX - xNew;
        double dz = startZ - zNew;

        return Math.abs(Math.sqrt((dx * dx) + (dz * dz)) - radius) <= .9;
    }
}
