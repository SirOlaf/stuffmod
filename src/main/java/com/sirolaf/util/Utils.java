package com.sirolaf.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class Utils {

    public static BlockPos getSpawnPoint(World world) {
        return new BlockPos(world.getLevelProperties().getSpawnX(), world.getLevelProperties().getSpawnY(), world.getLevelProperties().getSpawnZ());
    }

    public static double getSpawnPointDistance(World world, Vec3d pos) {
        var worldSpawn = getSpawnPoint(world);
        var newPos = new Vec3d(pos.x, worldSpawn.getY(), pos.z);
        return Math.sqrt(worldSpawn.getSquaredDistance(newPos));
    }

    public static double calcDifficultyMultiplier(World world, Vec3d pos, double min, double max) {
        // TODO: Config file to adjust scaling
        var spawnDist = getSpawnPointDistance(world, pos);
        var adjustedDist = spawnDist / 100.f + 2.0f; // TODO: Could scale it in rings of width 100 or something
        var unclamped = Math.log(adjustedDist) / 2.f; // TODO: Make a better curve
        return Math.max(min, Math.min(max, unclamped));
    }

    public static double calcDifficultyMultiplier(World world, Vec3d pos) {
        return calcDifficultyMultiplier(world, pos, 0.1, 10.0);
    }

}
