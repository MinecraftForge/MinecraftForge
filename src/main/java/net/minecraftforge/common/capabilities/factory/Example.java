package net.minecraftforge.common.capabilities.factory;

import net.minecraft.core.Direction;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.HashMap;
import java.util.Map;

public class Example {
    public static final Cap<IEnergyStorage> STORAGE_CAP = Cap.create();
    public static final Cap<IEnergyStorage> STORAGE_CAP_DOWN = STORAGE_CAP.createSub("down", Direction.DOWN);

    public static class Cap<I> {
        private final Map<Object, Cap<I>> map = new HashMap<>();

        public static <I> Cap<I> create() {
            return new Cap<I>();
        }

        public <A> Cap<I> createSub(String name, A object) {
            if (!canCreateSub()) throw new IllegalStateException("Cannot create a sub capability of a sub capability.");

            var exists = map.get(object);
            if (exists == null) {
                Cap<I> cap = new SubCap<>();
                map.put(object, cap);
                return cap;
            } else {
                return exists;
            }
        }

        protected boolean canCreateSub() {
            return true;
        }
    }

    private static class SubCap<I> extends Cap<I> {
        @Override
        protected boolean canCreateSub() {
            return false;
        }
    }

    public static void main(String[] args) {
        System.out.println("LOL");
    }
}
