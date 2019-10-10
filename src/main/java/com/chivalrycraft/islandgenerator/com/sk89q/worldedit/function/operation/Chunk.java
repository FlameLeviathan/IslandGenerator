package com.chivalrycraft.islandgenerator.com.sk89q.worldedit.function.operation;

import net.minecraft.server.v1_12_R1.PacketPlayOutMapChunk;
import org.bukkit.craftbukkit.v1_12_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class Chunk {
    private final net.minecraft.server.v1_12_R1.Chunk chunk;

    public Chunk(final org.bukkit.Chunk chunk) {
        this.chunk = ((CraftChunk)chunk).getHandle();
    }

    public final void send(final Player player) {
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutMapChunk(chunk, 20));
    }

}
