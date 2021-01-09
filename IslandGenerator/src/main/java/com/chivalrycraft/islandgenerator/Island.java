package com.chivalrycraft.islandgenerator;


import com.boydti.fawe.FaweAPI;
import com.mysql.fabric.xmlrpc.base.Array;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import net.minecraft.server.v1_15_R1.PacketPlayOutMapChunk;
import org.bukkit.*;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import org.bukkit.craftbukkit.v1_15_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.noise.SimplexOctaveGenerator;

import java.io.File;
import java.io.IOException;
import java.util.*;


public class Island {
    //TODO: Add boolean or check for adding dungeons or other
    IslandType type;
    Location location;
    HashMap<Material, Double> bottomBlocks = new HashMap<Material, Double>();
    ChunkGenerator.ChunkData chunk;



    File cFile = new File(Bukkit.getPluginManager().getPlugin("IslandGenerator").getDataFolder(), "config.yml");
    public FileConfiguration config = YamlConfiguration.loadConfiguration(cFile);
    int islandBlockPlaceAmount = config.getInt("numberOfBlocksToPlacePerTick");
    Core core;


    Island(IslandType type, int centerPointX, int centerPointY, int centerPointZ) {
        //radius is island bottom radius
        //topBlocks are the blocks that will appear above ground
        //bottomBlocks are the blocks that will appear in the ground (i.e. stone, cobblestone, ores, etc)
    }

    Island(IslandType type, Location location, ChunkGenerator.ChunkData chunk, Core c) {
        this.type = type;
        this.location = location;
        this.bottomBlocks = type.bottomBlocks;
        this.chunk = chunk;
        this.core = c;
        //radius is island bottom radius
        //topBlocks are the blocks that will appear above ground
        //bottomBlocks are the blocks that will appear in the ground (i.e. stone, cobblestone, ores, etc)
    }

    public void makeIsland(int maxIslandCombo) {
        Random random = new Random();
        // max = radius; min = -radius;
        int originalRadius = type.getRadius();
        int islandCount = maxIslandCombo;//random.nextInt(maxIslandCombo);
        for(int i = 0; i < islandCount; i++) {
        int radius;
            if(i % 2 != 0) {
                radius = random.nextInt(originalRadius - 1) + 1;
            } else {
                radius = random.nextInt((originalRadius/2) + 1 - 5) + 5;
            }
        if (i == 0)
            radius = originalRadius;
        int xSign = random.nextBoolean() ? -1 : 1;
        int zSign = random.nextBoolean() ? -1 : 1;
        double x, y, z;
        type.setRadius(radius);
        //Bukkit.getPlayer("FlameKnight15").sendMessage("Radius: " + radius);//+ " | xSign: " + xSign + " | zSign: " + zSign);
            do {
            x = (location.getX() + (/*xSign **/ random.nextInt(radius + 1 + radius) - radius / 2)); //+ radius / 4); //+ (radius)); // Was type.getRadius() + type.getRadius()
            z = (location.getZ() + (/*zSign **/ random.nextInt(radius + 1 + radius) - radius / 2)); //+ radius / 4); //+ (radius));
            y = (location.getY() + random.nextInt(1 + 1 + 1) - 1);
        } while (chunk.getType(((int) x), ((int) y), ((int) z)) != Material.AIR);
           //Bukkit.getLogger().info("Passes Unit 4");
        location.setX(x);
        location.setY(y);
        location.setZ(z);

        createIsland();
            //Bukkit.getLogger().info("Passes Unit 5");
        }
    }

    public void createIsland() {
/*        Bukkit.getScheduler().runTaskAsynchronously(Bukkit.getPluginManager().getPlugin("IslandGenerator"), new Runnable() {
            @Override
            public void run() {*/
        //createLowerPart(type.getRadius(), location.add(0, 0, 0));
        //Bukkit.getLogger().info("Passes Part 1");
        createUpperPart(type.getRadius(), location.add(0, 1, 0));

        //generateCaves(location, 5, 5, 5);
/*            }
        });*/

    }

    public void createUpperPart(int radius, Location loc) {
        //top.generate(radius, loc);
        generate(radius, loc);
    }


    private void createLowerPart(int radius, Location loc) {
        //HashMap<Location, IBlockData> blocksToPlace = new HashMap<>();
        //int islandBlockPlaceAmount = config.getInt("numberOfBlocksToPlacePerTick");
        int startX = loc.getBlockX(), startY = loc.getBlockY(), startZ = loc.getBlockZ();
        double decrease = Math.sqrt((radius * radius) + ((radius * 2) * (radius * 2)));
        int numberOfLevels = (int)(startY - (decrease / .5/* * 2*/));


        //Bukkit.getPlayer("FlameKnight15").sendMessage(ChatColor.RED + "Decrease: " + ChatColor.GREEN + decrease + ChatColor.RESET + ";" + ChatColor.YELLOW + "\nLevel number:" + ChatColor.GREEN + numberOfLevels);


        for (int y = startY; y >= decrease; y--) {
            //Bukkit.getPlayer("FlameKnight15").sendMessage("Y = " + y);
            //Could tamper with i to make it shorter per level
            /*
            Fun Equations for islands:
                ((y - numberOfLevels) / (double)(radius  * radius))
                ((y - numberOfLevels) / (double)(radius  * 2))
             */
            if(y-numberOfLevels != 0 && radius != 0) {
                int numOfLayersPerLevel = ((y - numberOfLevels)) / (radius);
                //Bukkit.getPlayer("FlameKnight15").sendMessage("Layers on level " + y + ": " + numOfLayersPerLevel);
                for (int i = 0; i <= numOfLayersPerLevel; i++) {
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
                                        //locationList.add(block);
                                        //blocksToPlace.add(Material.AIR);

                                        //Bukkit.getLogger().info("Passes Test-1 1");
                                        loc = new Location(loc.getWorld(), (int)xNew, yNew, (int)zNew);
                                        //Bukkit.getLogger().info("Passes Test-1 2");
                                        ArrayList<BlockProperty> tempProp = new ArrayList<>();
                                        if(!core.chunkBlocks.isEmpty() && core.chunkBlocks.containsKey(loc))
                                            tempProp = core.chunkBlocks.get(loc);
                                        //Bukkit.getLogger().info("Passes Test-1 3");
                                        tempProp.add(new BlockProperty(loc, Material.AIR));
                                        //Bukkit.getLogger().info("Passes Test-1 4");
                                        //Bukkit.getLogger().info("Passes Test-1 4-02");

                                        if(core.chunkBlocks.containsKey(loc)) {
                                            //Bukkit.getLogger().info("Passes Test-1 4.1");

                                            core.chunkBlocks.remove(loc);
                                            //Bukkit.getLogger().info("Passes Test-1 4.2");

                                        }
                                        //Bukkit.getLogger().info("Passes Test-1 4.3");
                                        core.chunkBlocks.put(loc, tempProp);
                                        //Bukkit.getLogger().info("Passes Test-1 5");

                                        //chunk.setBlock((int)xNew, yNew, (int)zNew, Material.AIR);
                                    } else {
                                        Block block = loc.getWorld().getBlockAt(new Location(loc.getWorld(), xNew, yNew, zNew));
                                        /*locationList.add(block);
                                        blocksToPlace.add(getRandomBlockType(bottomBlocks));*/

                                        //Bukkit.getLogger().info("Passes Test 1");
                                        loc = new Location(loc.getWorld(), (int)xNew, yNew, (int)zNew);
                                        //Bukkit.getLogger().info("Passes Test 2");
                                        ArrayList<BlockProperty> tempProp = new ArrayList<>();
                                        if(!(core.chunkBlocks.isEmpty()))
                                            if(core.chunkBlocks.containsKey(loc))
                                                 tempProp = core.chunkBlocks.get(loc);
                                        //Bukkit.getLogger().info("Passes Test 3: " + loc.toString());
                                        tempProp.add(
                                                new BlockProperty(loc,
                                                        getRandomBlockType(bottomBlocks)));
                                        //Bukkit.getLogger().info("Passes Test 4");
                                        //Bukkit.getLogger().info("ChunkBlocks: " + core.chunkBlocks + " Val");
                                        //Bukkit.getLogger().info("Passes Test 4-02");

                                        if(core.chunkBlocks.containsKey(loc)) {
                                            //Bukkit.getLogger().info("Passes Test 4.1");

                                            core.chunkBlocks.remove(loc);
                                            //Bukkit.getLogger().info("Passes Test 4.2");

                                        }
                                        //Bukkit.getLogger().info("Passes Test 4.3");
                                        core.chunkBlocks.put(loc, tempProp);
                                        //Bukkit.getLogger().info("Passes Test 5");

                                        //chunk.setBlock((int)xNew, yNew, (int)zNew, getRandomBlockType(bottomBlocks));
                                    }
                                } else {
                                    Block block = loc.getWorld().getBlockAt(new Location(loc.getWorld(), xNew, yNew, zNew));
                                    /*locationList.add(block);
                                    blocksToPlace.add(getRandomBlockType(bottomBlocks))*/;
                                    //chunk.setBlock((int)xNew, yNew, (int)zNew, getRandomBlockType(bottomBlocks));

                                    //Bukkit.getLogger().info("Passes Test-0 1");
                                    loc = new Location(loc.getWorld(), (int)xNew, yNew, (int)zNew);
                                    //Bukkit.getLogger().info("Passes Test-0 2");
                                    ArrayList<BlockProperty> tempProp = new ArrayList<>();
                                    if(!core.chunkBlocks.isEmpty() && core.chunkBlocks.containsKey(loc))
                                        tempProp = core.chunkBlocks.get(loc);
                                    //Bukkit.getLogger().info("Passes Test-0 3");
                                    tempProp.add(new BlockProperty(loc, getRandomBlockType(bottomBlocks)));
                                    //Bukkit.getLogger().info("Passes Test-0 4");
                                    if(core.chunkBlocks.containsKey(loc))
                                        core.chunkBlocks.remove(loc);
                                    core.chunkBlocks.put(loc, tempProp);
                                    //Bukkit.getLogger().info("Passes Test-0 5");

                                }
                            }
                        }
                    }
                    y--;
                }
            }
            y++;
            radius--;
        }
        //TODO: Fix cave generation. This line below creates weird islands.
        //procedurallyPlaceBlocks(locationList, blocksToPlace, islandBlockPlaceAmount);
        //generateCaves(location, 5, 5, 5, locationList, blocksToPlace);
    }

    void generate(int radius, Location loc) {
        //Core.report("Generating island...");
        World world = loc.getWorld();
        double chunkX = loc.getX();
        double chunkZ = loc.getZ();
        int currentHeight;

        chunk.setBlock(loc.getBlockX(), 65, loc.getBlockZ(), Material.WOOL);
        radius++;
        SimplexOctaveGenerator generator = new SimplexOctaveGenerator(new Random(world.getSeed()), 8);
        //The larger the scale is, the steeper the terrain.
        generator.setScale(0.005D);


        for (int x = -radius; x <= radius; x++)
            for (int z = -radius; z <= radius; z++) {
                if ((x * x) + (z * z) <= (radius * radius)) {
                    double xNew = chunkX + x;
                    double zNew = chunkZ + z;
                    //int highestPoint = (int) loc.getY() + radius + x;

                    // TODO Heights and blocks generation code here.
                    currentHeight = (int) ((generator.noise(chunkX * radius + xNew, chunkZ * radius + zNew, 1.5D, 5D, true) + 1) * 15D + 63);//loc.getY());
                    currentHeight = (int) ((currentHeight) / (15D + loc.getY())) + (int) loc.getY();

                    Block block = loc.getWorld().getBlockAt(new Location(loc.getWorld(), xNew, currentHeight, zNew) );



                        block.setType(Material.GRASS);
                        block.getLocation().add(0, -1, 0).getBlock().setType(Material.DIRT);

                        //chunk.setBlock((int)xNew, currentHeight - 1, (int)zNew, Material.DIRT);
                    //}
                }
            }
        //generateTrees(radius, loc);
        ////generateCaves(loc, 5, 5, 5);
        //generateLake(radius/2, loc, 100, true);
    }

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
/*                for (int j = world.getMaxHeight() - 1; loc.getWorld().getBlockAt(x, j, z).getType() == Material.AIR; j--);// Find the highest block of the (x,z) coordinate chosen.
                while (loc.getWorld().getBlockAt(x, y, z).getType() != Material.AIR) {
                    y++;
                }*/
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
            com.sk89q.worldedit.Vector pos = new com.sk89q.worldedit.Vector(loc.getX() - clipboard.getDimensions().getX()/2, loc.getY(), loc.getZ() - clipboard.getDimensions().getZ()/2);
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

                //System.out.println("Radius: " + (radius/2));
                if (radius <= 2)
                    radius = 2;

                int yDiff = random.nextInt((radius / 2)) + 1;

                for (int y = loc.getBlockY(); y >= loc.getBlockY() - yDiff; y--) {
                    //for(int i = 0; i < (y-(loc.getBlockY() - (1)))/14 + 2; i++)
                    for (int x = -radius; x <= radius; x++) {
                        for (int z = -radius; z <= radius; z++) {
                            if ((x * x) + (z * z) <= ((radius - 1) * (radius - 1))) {
                                if(isWater){
                                    Location loca = new Location(loc.getWorld(), x, y, z);
                                    ArrayList<BlockProperty> tempProp = new ArrayList<>();
                                    if(!core.chunkBlocks.isEmpty() && !core.chunkBlocks.get(loc).isEmpty())
                                        tempProp = core.chunkBlocks.get(loc);
                                    tempProp.add(new BlockProperty(loca, Material.WATER));
                                    core.chunkBlocks.put(loca, tempProp);
                                }
                                    //chunk.setBlock(x, y, loc.getBlockZ(), Material.WATER);
                                else {
                                    Location loca = new Location(loc.getWorld(), x, y, z);
                                    ArrayList<BlockProperty> tempProp = new ArrayList<>();
                                    if(!core.chunkBlocks.isEmpty() && !core.chunkBlocks.get(loc).isEmpty())
                                        tempProp = core.chunkBlocks.get(loc);
                                    tempProp.add(new BlockProperty(loca, Material.LAVA));
                                    core.chunkBlocks.put(loca, tempProp);
                                }
                                    //chunk.setBlock(x, y, loc.getBlockZ(), Material.LAVA);
                            }
                        }
                    }
                    radius--;
                }
            }
    }

    public void generateCaves(Location loc, double width, double height, double depth){
        Random random = new Random();
        Location cavePos = loc.clone();//a random point (x,y,z) within the (width,height,depth) space;
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

                int radius = 5;

                fillOblateSpheroid(centerPos, (radius), Material.AIR);
            }
        }

    }

    private void fillOblateSpheroid(Location center, int radius, Material type) {
        //ArrayList<Block> locationList = new ArrayList<>();
        ArrayList<BlockProperty> blocksToPlace = new ArrayList<>();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = -radius; z <= radius; z++) {
                    if ((x * x) + (z * z) <= ((radius - 1) * (radius - 1))) {
                        int dx = x + center.getBlockX();
                        int dy = y + center.getBlockY();
                        int dz = z + center.getBlockZ();


                        //chunk.setBlock(x,y,z, Material.AIR);
                        Location loc = new Location(center.getWorld(), dx, dy, dz);
                        ArrayList<BlockProperty> tempProp = new ArrayList<>();
                        if(!core.chunkBlocks.isEmpty() && !core.chunkBlocks.get(loc).isEmpty())
                            tempProp = core.chunkBlocks.get(loc);
                        tempProp.add(new BlockProperty(loc, Material.AIR));
                        core.chunkBlocks.put(loc, tempProp);



                    }
                }
            }
        }


    }

    boolean pointOnEdge(int startX, double xNew, int startZ, double zNew, int radius) {

        double dx = startX - xNew;
        double dz = startZ - zNew;

        return Math.abs(Math.sqrt((dx * dx) + (dz * dz)) - radius) <= .9;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////To be implemented?////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    int taskId;
    ArrayList<Integer> scheds = new ArrayList<>();
    public void createLargeIslandLowerPart(int radius, Location loc){

        ArrayList<Block> locationList = new ArrayList<>();
        ArrayList<Material> blocksToPlace = new ArrayList<>();
        int startX = loc.getBlockX(), startY = loc.getBlockY(), startZ = loc.getBlockZ();
        Random random = new Random();
        int numOfIslandBases = random.nextInt(7-3)+3;

        ArrayList<Integer> islandRadii = new ArrayList();

        for(int n = 0; n < numOfIslandBases + 2 /*extra 2 so that we can remove the 1's on the ends*/; n++){
            int nCk = 1;
            for(int k = 0; k<= n; k++){
                nCk = nCk * (n-k)/(k+1);
                islandRadii.add(nCk);
            }
        }


        for(int islandsCreated = 0; islandsCreated < numOfIslandBases; islandsCreated++){
            //Remove the 1 at the front and back of array
            islandRadii.remove(islandRadii.get(0));
            islandRadii.remove(islandRadii.get(islandRadii.size() - 1));

            //Create the islands with space between them

        }
    }

    private void stopAllScheduler(ArrayList<Integer> list) {
        for (int sched : list){
            Bukkit.getServer().getScheduler().cancelTask(sched);
        }
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
        World world = chunk.getWorld();
        for (Player ep : world.getPlayers()) {
            diffx = Math.abs((int) ep.getLocation().getBlockX() - chunk.getX() << 4);
            diffz = Math.abs((int) ep.getLocation().getBlockZ() - chunk.getZ() << 4);
            if (diffx <= view && diffz <= view) {
                //ep.chunkCoordIntPairQueue.add(new ChunkCoordIntPair(chunk.getX(), chunk.getZ()));
            }
        }
        //}
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

    /*public void procedurallyPlaceBlocks(List<Block> locationList, List<Material> blockDataList, int time) {
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(Bukkit.getPluginManager().getPlugin("IslandGenerator"), new Runnable() {
            @Override
            public void run() {
                //Bukkit.getPlayer("FlameKnight15").sendMessage("LOC SIZE: " + locationList.size());
                if (locationList.size() == 0) {
                    //Bukkit.getPlayer("FlameKnight15").sendMessage("ISLAND FINISHED");
                    stopAllScheduler(scheds);
                }
                while (!locationList.isEmpty()) {
                    for (int i = 0; i < locationList.size(); i++) {
                        //Bukkit.getPlayer("FlameKnight15").sendMessage("i: " + i + " Time: " + time);
                        if (i <= time) {
                            //locationList.get(i).setType(blockDataList.get(i));
                            //TODO: chunk.setBlock(locationList.get(i).X, Y, Z, blockDataList.get(i));
                            //ChunkGenerator.ChunkData chunkData = createChunkData(locationList.get(i).getWorld());
                            chunk.setBlock(locationList.get(i).getX(), locationList.get(i).getY(), locationList.get(i).getZ(), blockDataList.get(i));

                            //Bukkit.getPlayer("FlameKnight15").sendMessage("Material Placed 2");
                            //placeBlock(locationList.get(i), blockDataList.get(i));
                            locationList.remove(i);
                            blockDataList.remove(i);
                        } else {
                            //Bukkit.getPlayer("FlameKnight15").sendMessage("Material not Placed");
                            break;
                        }

                    }
                }
            }

        }, 0L, 1L);
        scheds.add(taskId);
    }*/


/*    public void placeBlock(Location loc, IBlockData blockData){
        net.minecraft.server.v1_12_R1.World w = ((CraftWorld)loc.getWorld()).getHandle();
        net.minecraft.server.v1_12_R1.Chunk chunk = w.getChunkAt(loc.getBlockX() >> 4, loc.getBlockZ() >> 4);
        BlockPosition pos = new BlockPosition(loc.getBlockX() ,loc.getBlockY(), loc.getBlockZ());
        IBlockData data = blockData;//net.minecraft.server.v1_12_R1.Block.getByName("DIRT").getBlockData();
        chunk.a(pos, data);
    }*/
}
