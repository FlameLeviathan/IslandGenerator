package com.chivalrycraft.islandgenerator;




import org.bukkit.*;

import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.noise.NoiseGenerator;
import org.bukkit.util.noise.PerlinNoiseGenerator;
import org.bukkit.util.noise.SimplexOctaveGenerator;


import java.lang.reflect.Array;
import java.util.*;

import static java.lang.Math.sqrt;

public class CustomChunkGenerator extends ChunkGenerator {
    Core core;

    public CustomChunkGenerator(Core core) {
        this.core = core;
    }

    @Override
    public org.bukkit.generator.ChunkGenerator.ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, org.bukkit.generator.ChunkGenerator.BiomeGrid biome) {
        Random randomGen = new Random(world.getSeed());
        ChunkData chunk = createChunkData(world);

        //if((randomGen.nextInt(2-1) + 1) == 2) {
        // TODO Chunk generation code here.

        //Random randomGen = new Random(world.getSeed());
        int variance = (randomGen.nextInt(100) + 50);
        int heightVariance = (randomGen.nextInt(150) + 50);

        SimplexOctaveGenerator generatorTop = new SimplexOctaveGenerator(new Random(world.getSeed()), 8);
        SimplexOctaveGenerator generatorBottom = new SimplexOctaveGenerator(new Random(world.getSeed()), 8);
        //The larger the scale is, the steeper the terrain
        generatorTop.setScale(1 / 64D);
        generatorBottom.setScale(1 / 70D);
        //will run from (0, 0) to (15, 15) which indicates the current (X,Z) coordinate we are working with:


        for (int X = 0; X < 16; X++)
            for (int Z = 0; Z < 16; Z++) {
                // TODO Heights and blocks generation code here.

                int top = (int) ((generatorTop.noise(chunkX * 16 + X, chunkZ * 16 + Z, .5D, 0.7D, true) + 1) * 15D + 128D);
                // Freq: 1.000000000001
                //int bottomOne = (int) ((generatorBottom.noise(chunkX * 16 + X, chunkZ * 16 + Z, 0D, 0D, true) + .5));
                int bottom = (int) ((generatorBottom.noise(chunkX * 16 + X, chunkZ * 16 + Z, 1D, 200D, true) + 1) * variance /*The lower this number the larger the islands*/ + 60D);

                //int bottom = Math.max(bottomOne, bottomTwo);
                //elevation = (int) (Math.floor(distance) + elevation * (Math.ceil(distance) - Math.floor(distance)));

                //if(X < distance && Z <distance) {
                //The current (X,Z) coordinate of the world can be retrieved by multiplying the chunkX, chunkZprovided and adding each of them with the current chunks's X, Z:
                //15 - the multiplier is the amount of difference between the highest and lowest possible heights of the world, and 50 is the minimum height of the whole world. You can change these if you want.
                // REPLACED BY ELEVATION: int currentHeight = (int) ((generatorTop.noise(chunkX * 16 + nx, chunkZ * 16 + nz, 0.5D, 0.5D, true) + 1) * 15D + 128D);
                //Set the highest block of the "pillar" to grass block:
                if ((top - bottom) > 1) {
                    chunk.setBlock(X, top, Z, Material.GRASS);
                    //Set the lower block to dirt:
                    chunk.setBlock(X, top - 1, Z, Material.DIRT);
                    //From the third block to the almost bottom block of the "pillar", place blocks of stone:
                    for (int i = top - 2; i > bottom; i--)
                        chunk.setBlock(X, i, Z, Material.STONE/*getRandomBlockType(parseBottomBlocks("plains"))*/);
                }
            }

        //CAVES
            populateCaves(world, random, chunk, chunkX, chunkZ);
/*        //SimplexOctaveGenerator noiseGenerator = new SimplexOctaveGenerator(new Random(world.getSeed()), 2);
        PerlinNoiseGenerator noiseGenerator = new PerlinNoiseGenerator(new Random(world.getSeed()));
        //noiseGenerator.setScale(1/64D);

        for (int X = 0; X < 16; X++) {
            for (int Y = 0; Y < 120; Y++) {
                for (int Z = 0; Z < 16; Z++) {

                    //double xNoise = noiseGenerator.noise(chunkZ  + Z, chunkZ  + Z, .3D, 0.5D, true);
                    //double yNoise = noiseGenerator.noise(chunkX  + X, chunkZ  + Z, .5D, 0.5D, true) + 70D;
                    //double zNoise = noiseGenerator.noise(chunkX  + X, chunkX  + X, .3D, 0.5D, true);

                    //System.out.println("X, Y, Z: " + xNoise + ", " + yNoise + ", " + zNoise);

                    double noise = noiseGenerator.noise(chunkX * 16  + X, Y, chunkZ * 16  + Z*//*chunkX * 16  + X, (chunkX * 16) + (chunkZ * 16) + Y, chunkZ * 16 + Z,*//* );
                    if(noise > .5){
                        //if(chunk.getType((int) (xNoise) + X, (int) (yNoise) + Y, (int)(zNoise) + Z) != Material.AIR)
                            chunk.setBlock(X,Y,Z, Material.WOOL);
                    }
                }
            }
        }*/


        //}
        return chunk;
    }

    HashMap<Material, Double> parseBottomBlocks(String biome) {
        HashMap<Material, Double> blocks = new HashMap<>();
        switch (biome) {
            //case PLAIN:
            //break;
            default:
                blocks.put(Material.STONE, 85.0);
                blocks.put(Material.COBBLESTONE, 50.0);
                blocks.put(Material.IRON_ORE, 9.0);
                blocks.put(Material.COAL_ORE, 10.0);
                blocks.put(Material.GOLD_ORE, 3.0);
                blocks.put(Material.DIAMOND_ORE, 2.0);
                blocks.put(Material.EMERALD_ORE, 1.0);
                break;
        }
        return blocks;
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

    Random random = new Random();
    World world;

    int currentX;
    int currentY;
    int currentZ;
    boolean finished = false;
;



    Location startingLocation;

    public void populateCaves(World world, Random random, ChunkData chunk, int chunkX, int chunkZ) {
        this.world = world;
        startingLocation = new Location(world, chunkX * 16, 70, chunkZ * 16);
        int maximumLength = 3;//random.nextInt(50 - 10) + 10;


        currentX = startingLocation.getBlockX();
        currentY = startingLocation.getBlockY();
        currentZ = startingLocation.getBlockZ();
        finished = false;

        Cave cave= new Cave(startingLocation, maximumLength);
        System.out.println("Starting Location: " + startingLocation);

        if((random.nextInt(100-1) + 1) >= 10)
            cave.getAllLocations();

        cave.makeCave(chunk, chunkX, chunkZ);
    }

    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        ArrayList<BlockPopulator> retVal = new ArrayList<>();
        retVal.add(new populateTrees());
        retVal.add(new populateGrass());
        retVal.add(new populateLakes());
        retVal.add(new populateOres());
        //retVal.add(new populateCaves());
        return retVal;
    }


    private class populateTrees extends BlockPopulator {
        @Override
        public void populate(World world, Random random, Chunk chunk) {
            // TODO Tree populator's code here

            if (random.nextBoolean()) {
                int amount = random.nextInt(4) + 1;  // Amount of trees
                for (int i = 1; i < amount; i++) {
                    int X = random.nextInt(15);
                    int Z = random.nextInt(15);
                    int Y;
                    for (Y = world.getMaxHeight() - 1; chunk.getBlock(X, Y, Z).getType() == Material.AIR; Y--)
                        ; // Find the highest block of the (X,Z) coordinate chosen.
                    //core.loadSchematic(new Location(world, X, Y, Z), "Trees", null); // The tree type can be changed if you want.
                    world.generateTree(chunk.getBlock(X, Y, Z).getLocation(), TreeType.TREE);
                }
            }
        }
    }

    private class populateGrass extends BlockPopulator {
        @Override
        public void populate(World world, Random random, Chunk chunk) {
            // TODO Tree populator's code here

            if (random.nextBoolean()) {
                int amount = random.nextInt(4) + 1;  // Amount of trees
                for (int i = 1; i < amount; i++) {
                    int X = random.nextInt(15);
                    int Z = random.nextInt(15);
                    int Y;
                    for (Y = world.getMaxHeight() - 1; chunk.getBlock(X, Y, Z).getType() == Material.AIR; Y--)
                        ; // Find the highest block of the (X,Z) coordinate chosen.
                    chunk.getBlock(X, Y + 1, Z).setType(Material.RED_ROSE);
                }
            }
        }
    }

    private class populateLakes extends BlockPopulator {
        @Override
        public void populate(World world, Random random, Chunk chunk) {
            // TODO Tree populator's code here

            if (random.nextInt(100) > 0/* < 80*/) {  // The chance of spawning a lake
                Block block;
                int chunkX = chunk.getX();
                int chunkZ = chunk.getZ();
                int X = chunkX * 16 + random.nextInt(15) - 8;
                int Z = chunkZ * 16 + random.nextInt(15) - 8;
                int Y;
                for (Y = world.getMaxHeight() - 1; chunk.getBlock(X, Y, Z).getType() == Material.AIR; Y--) ;
                Y -= 7;
                block = world.getBlockAt(Z + 8, Y, Z + 8);
                if (random.nextInt(100) < 90) block.setType(Material.WATER);
                else block.setType(Material.LAVA);  // The chance of spawing a water or lava lake
                boolean[] aboolean = new boolean[2048];
                boolean flag;
                int i = random.nextInt(4) + 4;

                int j, j1, k1;

                for (j = 0; j < i; ++j) {
                    double d0 = random.nextDouble() * 6.0D + 3.0D;
                    double d1 = random.nextDouble() * 4.0D + 2.0D;
                    double d2 = random.nextDouble() * 6.0D + 3.0D;
                    double d3 = random.nextDouble() * (16.0D - d0 - 2.0D) + 1.0D + d0 / 2.0D;
                    double d4 = random.nextDouble() * (8.0D - d1 - 4.0D) + 2.0D + d1 / 2.0D;
                    double d5 = random.nextDouble() * (16.0D - d2 - 2.0D) + 1.0D + d2 / 2.0D;

                    for (int k = 1; k < 15; ++k) {
                        for (int l = 1; l < 15; ++l) {
                            for (int i1 = 1; i1 < 7; ++i1) {
                                double d6 = ((double) k - d3) / (d0 / 2.0D);
                                double d7 = ((double) i1 - d4) / (d1 / 2.0D);
                                double d8 = ((double) l - d5) / (d2 / 2.0D);
                                double d9 = d6 * d6 + d7 * d7 + d8 * d8;

                                if (d9 < 1.0D) {
                                    aboolean[(k * 16 + l) * 8 + i1] = true;
                                }
                            }
                        }
                    }
                }

                for (j = 0; j < 16; ++j) {
                    for (k1 = 0; k1 < 16; ++k1) {
                        for (j1 = 0; j1 < 8; ++j1) {
                            if (aboolean[(j * 16 + k1) * 8 + j1]) {
                                world.getBlockAt(X + j, Y + j1, Z + k1).setType(j1 > 4 ? Material.AIR : block.getType());
                            }
                        }
                    }
                }

                for (j = 0; j < 16; ++j) {
                    for (k1 = 0; k1 < 16; ++k1) {
                        for (j1 = 4; j1 < 8; ++j1) {
                            if (aboolean[(j * 16 + k1) * 8 + j1]) {
                                int X1 = X + j;
                                int Y1 = Y + j1 - 1;
                                int Z1 = Z + k1;
                                if (world.getBlockAt(X1, Y1, Z1).getType() == Material.DIRT) {
                                    world.getBlockAt(X1, Y1, Z1).setType(Material.GRASS);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private class populateOres extends BlockPopulator {
        @Override
        public void populate(World world, Random random, Chunk chunk) {
            int X, Y, Z;
            boolean isStone;
            for (int i = 1; i < 15; i++) {  // Number of tries
                if (random.nextInt(100) < 60) {  // The chance of spawning
                    X = random.nextInt(15);
                    Z = random.nextInt(15);
                    Y = random.nextInt(200 - 60) + 60;  // Get randomized coordinates
                    if (chunk.getBlock(X, Y, Z).getType() == Material.STONE) {
                        isStone = true;
                        while (isStone) {
                            chunk.getBlock(X, Y, Z).setType(Material.COAL_ORE);
                            if (random.nextInt(100) < 70) {   // The chance of continuing the vein
                                switch (random.nextInt(5)) {  // The direction chooser
                                    case 0:
                                        X++;
                                        break;
                                    case 1:
                                        Y++;
                                        break;
                                    case 2:
                                        Z++;
                                        break;
                                    case 3:
                                        X--;
                                        break;
                                    case 4:
                                        Y--;
                                        break;
                                    case 5:
                                        Z--;
                                        break;
                                }
                                isStone = (chunk.getBlock(X, Y, Z).getType() == Material.STONE) && (chunk.getBlock(X, Y, Z).getType() != Material.COAL_ORE);
                            } else isStone = false;
                        }
                    }
                }
            }
        }
    }

    /*private class populateCaves extends BlockPopulator {
    }*/
}

        /**
         * 3 for statements that go in the order of y, x, z
         * @param radius
         * @param center
         */
    /*public void createCircle(int radius, Location center){
        for (int y = -radius; y <= radius; y++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if ((x * x) + (z * z) <= ((radius - 1) * (radius - 1))) {
                        //if((y*y) <= ((radius - 1) * (radius - 1)) ) {

                        int dx = x + center.getBlockX();
                        int dy = y + center.getBlockY();
                        int dz = z + center.getBlockZ();
                        //if(Bukkit.getWorld("world").getBlockAt(dx, dy, dz).getType() != Material.AIR)
                        System.out.println("Making sphere at: " + dx + " " + dy + " " + dz);
                        center.getWorld().getBlockAt(new Location(center.getWorld(), dx, dy, dz)).setType(Material.WOOL);
                        //Bukkit.getWorld("world").getBlockAt(dx, dy, dz).setType(type);
                        //Island.placeBlock(new Location(center.getWorld(), dx, dy, dz), type);
                        //}
                    }
                }
            }
        }}
    }*/
