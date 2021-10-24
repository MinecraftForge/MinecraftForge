package net.minecraftforge.event.entity.minecart;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.phys.Vec3;
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
        private Vec3 movement;

        public Steered(AbstractMinecart minecart, Player player, Vec3 movement) {
            super(minecart);
            this.player = player;
            this.movement = movement;
        }

        public Player getPlayer() {
            return player;
        }

        public Vec3 getMovement() {
            return this.movement;
        }

        public void setMovement(Vec3 movement) {
            this.movement = movement;
        }

    }

    public static class PushedByEntity extends MinecartEvent {

        private final Entity pusher;
        private double pushX;
        private double pushZ;

        public PushedByEntity(AbstractMinecart minecart, Entity pusher, double pushX, double pushZ) {
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
    public static class PushedByMinecart extends MinecartEvent {

        private final AbstractMinecart otherCart;
        private final double pushForceX;
        private final double pushForceZ;

        public PushedByMinecart(AbstractMinecart thisCart, AbstractMinecart otherCart, double pushForceX, double pushForceZ) {
            super(thisCart);
            this.otherCart = otherCart;
            this.pushForceX = pushForceX;
            this.pushForceZ = pushForceZ;
        }

        public AbstractMinecart getOtherCart() {
            return otherCart;
        }

        public double getPushForceX() {
            return pushForceX;
        }

        public double getPushForceZ() {
            return pushForceZ;
        }

    }

    public static class RollingResistance extends MinecartEvent {

        private double resistance;

        public RollingResistance(AbstractMinecart minecart, double resistance) {
            super(minecart);
            this.resistance = resistance;
        }

        public void setResistance(double resistance) {
            this.resistance = resistance;
        }

        public double getResistance() {
            return resistance;
        }

    }

    public static class SlopeSpeed extends MinecartEvent {

        private double slopeX;
        private double slopeZ;

        public SlopeSpeed(AbstractMinecart minecart, double slopeX, double slopeZ) {
            super(minecart);
            this.slopeX = slopeX;
            this.slopeZ = slopeZ;
        }

        public double getSlopeX() {
            return slopeX;
        }

        public double getSlopeZ() {
            return slopeZ;
        }

        public void setSlopeX(double slopeX) {
            this.slopeX = slopeX;
        }

        public void setSlopeZ(double slopeZ) {
            this.slopeZ = slopeZ;
        }

    }
}
