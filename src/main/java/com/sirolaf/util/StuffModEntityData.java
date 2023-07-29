package com.sirolaf.util;

import net.minecraft.util.math.Vec3d;

public class StuffModEntityData {

    public static void setSpawnPos(IStuffModEntityData entity, Vec3d pos) {
        var nbt = entity.getPersistentData();
        nbt.putLongArray("spawnPos", new long[]{(long) pos.x,(long)pos.y, (long)pos.z});
    }

    public static Vec3d getSpawnPos(IStuffModEntityData entity) {
        var nbt = entity.getPersistentData();
        var data = nbt.getLongArray("spawnPos");
        return new Vec3d(data[0], data[1], data[2]);
    }

    public static boolean hasSpawnPos(IStuffModEntityData entity) {
        var nbt = entity.getPersistentData();
        return nbt.contains("spawnPos");
    }

    public static void setDifficulty(IStuffModEntityData entity, double difficulty) {
        var nbt = entity.getPersistentData();
        nbt.putDouble("difficulty", difficulty);
    }

    public static double getDifficulty(IStuffModEntityData entity) {
        var nbt = entity.getPersistentData();
        return nbt.getDouble("difficulty");
    }

}
