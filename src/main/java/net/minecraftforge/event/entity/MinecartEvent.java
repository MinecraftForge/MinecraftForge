package net.minecraftforge.event.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

public class MinecartEvent extends Event {

    private final AbstractMinecart minecart;

    public MinecartEvent(AbstractMinecart minecart) {
        this.minecart = minecart;
    }

    public AbstractMinecart getMinecart() {
        return minecart;
    }

    public static class Steered extends MinecartEvent {

        private final Player player;
        private double x, z;

        public Steered(AbstractMinecart minecart, Player player, double x, double z) {
            super(minecart);
            this.player = player;
            this.x = x;
            this.z = z;
        }

        public Player getPlayer() {
            return player;
        }

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getZ() {
            return z;
        }

        public void setZ(double z) {
            this.z = z;
        }

    }

    public static class Pushed extends MinecartEvent {

        private final Entity pusher;
        private double pushX;
        private double pushZ;

        public Pushed(AbstractMinecart minecart, Entity pusher, double pushX, double pushZ) {
            super(minecart);
            this.pusher = pusher;
            this.pushX = pushX;
            this.pushZ = pushZ;
        }

        public double getPushX() {
            return pushX;
        }

        public void setPushX(double pushX) {
            this.pushX = pushX;
        }

        public double getPushZ() {
            return pushZ;
        }

        public void setPushZ(double pushZ) {
            this.pushZ = pushZ;
        }

        public Entity getPusher() {
            return pusher;
        }

    }

    @net.minecraftforge.eventbus.api.Cancelable
    public static class PreMove extends MinecartEvent {

        private final BlockPos railPos;
        private final BlockState railState;

        public PreMove(AbstractMinecart minecart, BlockPos railPos, BlockState railState) {
            super(minecart);
            this.railPos=railPos;
            this.railState=railState;
        }

        public BlockPos getRailPos() {
            return railPos;
        }

        public BlockState getRailState() {
            return railState;
        }

    }

    @net.minecraftforge.eventbus.api.Cancelable
    public static class PostMove extends MinecartEvent {

        private final BlockPos railPos;
        private final BlockState railState;

        public PostMove(AbstractMinecart minecart, BlockPos railPos, BlockState railState) {
            super(minecart);
            this.railPos=railPos;
            this.railState=railState;
        }

        public BlockPos getRailPos() {
            return railPos;
        }

        public BlockState getRailState() {
            return railState;
        }

    }
}
