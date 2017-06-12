package me.ichun.mods.sync.common.block;

import me.ichun.mods.sync.common.Sync;
import net.minecraft.item.Item;
import net.minecraft.util.IStringSerializable;

/**
 * Types for blockstates of {@link BlockDualVertical}
 */
public enum EnumType implements IStringSerializable {
    CONSTRUCTOR {
        @Override
        public String getName() {
            return "constructor";
        }
    }, STORAGE {
        @Override
        public String getName() {
            return "storage";
        }
    }, TREADMILL {
        @Override
        public String getName() {
            return "treadmill";
        }
    };

    public static EnumType getByID(int itemDamage) {
        switch (itemDamage) {
            case 0:
                return CONSTRUCTOR;
            case 1:
                return STORAGE;
            case 2:
                return TREADMILL;
            default:
                throw new IndexOutOfBoundsException("Only values from 0-2 are permitted!");
        }
    }

    public static Item getItemForType(EnumType type) {
        switch (type) {
            case CONSTRUCTOR:
                return Sync.itemShellConstructor;
            case STORAGE:
                return Sync.itemShellStorage;
            case TREADMILL:
                return Sync.itemTreadmill;
            default:
                throw new RuntimeException("Invalid enum " + type);
        }
    }
}
