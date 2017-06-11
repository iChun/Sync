package me.ichun.mods.sync.common.block;

import net.minecraft.util.IStringSerializable;

/**
 * Created by Tobias on 11.06.2017.
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
}
