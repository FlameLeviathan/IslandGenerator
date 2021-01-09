package com.chivalrycraft.islandgenerator;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.noise.SimplexOctaveGenerator;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Random;

public class Cave {

    private Location head;
    private Random random = new Random();
    private boolean finished;
    private int currentX, currentY, currentZ;
    private int currentLength;
    private int maximumLength;
    private List<Location> currentPath = new ArrayList<>();

    public Cave(Location head, int maximumLength){
        this.head = head;
        this.maximumLength = maximumLength;

        random.setSeed(head.getWorld().getSeed());
        currentX = head.getBlockX();
        currentY = head.getBlockY();
        currentZ = head.getBlockZ();
        System.out.println("Head Loc: " + head.toString());
    }

    private boolean hasNext() {
        return !finished;
    }

    private Location next() {
        SimplexOctaveGenerator genericNoiseGenerator = new SimplexOctaveGenerator(new Random(head.getWorld().getSeed()), 8);
        int xMovementOctaves = random.nextInt(8 - 2) + 2;
        int yMovementOctaves = random.nextInt(8 - 2) + 2;
        int zMovementOctaves = random.nextInt(8 - 2) + 2;

        // double randomValue = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
        double xSpreadFrequency = genericNoiseGenerator.noise(currentX, currentY, currentZ);
        double ySpreadFrequency = genericNoiseGenerator.noise(currentX, currentY, currentZ);
        double zSpreadFrequency = genericNoiseGenerator.noise(currentX, currentY, currentZ);

        double xSpreadAmplitude = genericNoiseGenerator.noise(currentX, currentY, currentZ);
        double ySpreadAmplitude = genericNoiseGenerator.noise(currentX, currentY, currentZ);
        double zSpreadAmplitude = genericNoiseGenerator.noise(currentX, currentY, currentZ);

        double xSpreadThreshHold = -1;//.2 + (.4 - .2) * random.nextDouble();
        double ySpreadThreshHold = -1;//.2 + (.4 - .2) * random.nextDouble();
        double zSpreadThreshHold = -1;//.2 + (.4 - .2) * random.nextDouble();

        SimplexOctaveGenerator xNoiseGenerator = new SimplexOctaveGenerator(new Random(head.getWorld().getSeed()), xMovementOctaves);
        SimplexOctaveGenerator yNoiseGenerator = new SimplexOctaveGenerator(new Random(head.getWorld().getSeed()), yMovementOctaves);
        SimplexOctaveGenerator zNoiseGenerator = new SimplexOctaveGenerator(new Random(head.getWorld().getSeed()), zMovementOctaves);

        if (finished) {
            return null;
        }
        Location result = new Location(head.getWorld(), currentX, currentY, currentZ);

        // precalculate next result
        // move in x direction
        double x = xNoiseGenerator.noise(currentX, currentY, currentZ,
                xMovementOctaves, xSpreadFrequency, xSpreadAmplitude, true);
        if (x >= xSpreadThreshHold) {
            currentX++;
        } else
        if (x <= (-1 * xSpreadThreshHold)) {
            currentX--;
        }

/*        // move in y direction
        double y = yNoiseGenerator.noise(currentX, currentY, currentZ,
                yMovementOctaves, ySpreadFrequency, ySpreadAmplitude, true);
        if (y >= ySpreadThreshHold) {
            currentY++;
        } else
        if (y <= (-1 * ySpreadThreshHold)) {
            currentY--;
        }*/

        // move in z direction
        double z = zNoiseGenerator.noise(currentX, currentY, currentZ,
                zMovementOctaves, zSpreadFrequency, zSpreadAmplitude, true);
        if (z >= zSpreadThreshHold) {
            currentZ++;
        } else
        if (z <= (-1 * zSpreadThreshHold)) {
            currentZ--;
        }
        // increment length counter
        currentLength++;
        //if maximum length was reached, stop
        if (currentLength >= maximumLength) {
            finished = true;
        }
        // if this point was already reached, we are inside a cycle and can stop
        // earlier
        Location nextOne = new Location(head.getWorld(), currentX,
                currentY, currentZ);
        if (currentPath.size() > 0 && currentPath.contains(nextOne)) {
            finished = true;
        } else {
            currentPath.add(nextOne);
        }
        return result;
    }

    private void setFinished(boolean finished) {
        this.finished = finished;
    }

    public void getAllLocations() throws ConcurrentModificationException {
        setFinished(false);
        //System.out.println("Get all Locations");
/*        if (currentX != startingLocation.getBlockX()
                || currentY != startingLocation.getBlockY()
                || currentZ != startingLocation.getBlockZ()) {
            throw new ConcurrentModificationException();
        }*/
        while (hasNext()) {
            Location next = next();
            currentPath.add(next);

        }
    }

    public void makeCave(ChunkGenerator.ChunkData chunkData, int chunkX, int chunkZ){
        if(currentPath.isEmpty())
            return;
        for (Location loc : currentPath){
            //System.out.println("Sphere made at: " + loc);
            fillCaveSpace(loc, 3, Material.WOOL, chunkData, chunkX, chunkZ);
        }
    }

    private void fillCaveSpace(Location center, int radius, Material type, ChunkGenerator.ChunkData chunk, int chunkX, int chunkZ) {

        center = center.add(chunkX  * 16, 0 , chunkZ*16);

        for (int y = -radius; y <= radius; y++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if ((x * x) + (z * z) <= ((radius - 1) * (radius - 1))) {
                        //XZ axis
                        int dx = x + center.getBlockX() / 16;
                        int dy = y + center.getBlockY() / 16;
                        int dz = z + center.getBlockZ() / 16;
                        //center.getWorld().getBlockAt(new Location(center.getWorld(), dx, dy, dz)).setType(Material.WOOL);
                        chunk.setBlock(dx, dy, dz, type);

                        System.out.println("Making sphere at: " + dx + " " + dy + " " + dz);

                        //YZ axis
                        dx = y + center.getBlockY() / 16;
                        dy = x + center.getBlockX() / 16;
                        dz = z + center.getBlockZ() / 16;
                        //center.getWorld().getBlockAt(new Location(center.getWorld(), dx, dy, dz)).setType(Material.WOOL);
                        chunk.setBlock(dx, dy, dz, type);


                        //YX axis
                        dx = x + center.getBlockX() / 16;
                        dy = z + center.getBlockZ() / 16;
                        dz = y + center.getBlockY() / 16 ;
                        //center.getWorld().getBlockAt(new Location(center.getWorld(), dx, dy, dz)).setType(Material.WOOL);
                        chunk.setBlock(dx, dy, dz, type);

                    }
                }
            }
        }
    }
}
