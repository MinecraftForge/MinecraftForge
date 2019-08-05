package net.minecraftforge.test.capabilities;

import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.accessor.FlowCapabilityAccessor;
import net.minecraftforge.energy.EnergyStorage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Created by Dark(DarkGuardsman, Robert) on 8/10/2019.
 */
public class EnergyCapTest
{
    @Test
    public void basicEnergyAdd() {
        final EnergyStorage storage = new EnergyStorage(1000, 10, 10, 0);

        //Create a basic accessor
        final FlowCapabilityAccessor accessor = new FlowCapabilityAccessor(Direction.NORTH, () -> this);

        //Test the limits are still enforced
        final int received = storage.receiveEnergy(100, accessor);

        //Test we received only 10
        Assertions.assertEquals(10, received);
        Assertions.assertEquals(10, storage.getEnergyStored());
    }

    @Test
    public void basicEnergyRemove() {
        final EnergyStorage storage = new EnergyStorage(1000, 10, 10, 1000);

        //Create a basic accessor
        final FlowCapabilityAccessor accessor = new FlowCapabilityAccessor(Direction.NORTH, () -> this);

        //Test the limits are still enforced
        final int extract = storage.extractEnergy(100, accessor);

        //Test we removed only 10
        Assertions.assertEquals(10, extract);
        Assertions.assertEquals(990, storage.getEnergyStored());
    }

    @Test
    public void basicEnergyAddSimulate() {
        final EnergyStorage storage = new EnergyStorage(1000, 10, 10, 0);

        //Create a basic accessor
        final FlowCapabilityAccessor accessor = new FlowCapabilityAccessor(Direction.NORTH, () -> this).setSimulate(true);

        //Test the limits are still enforced
        final int received = storage.receiveEnergy(100, accessor);

        //Test we received only 10
        Assertions.assertEquals(10, received);
        Assertions.assertEquals(0, storage.getEnergyStored());
    }

    @Test
    public void basicEnergyRemoveSimulate() {
        final EnergyStorage storage = new EnergyStorage(1000, 10, 10, 1000);

        //Create a basic accessor
        final FlowCapabilityAccessor accessor = new FlowCapabilityAccessor(Direction.NORTH, () -> this).setSimulate(true);

        //Test the limits are still enforced
        final int extract = storage.extractEnergy(100, accessor);

        //Test we removed only 10
        Assertions.assertEquals(10, extract);
        Assertions.assertEquals(1000, storage.getEnergyStored());
    }

    @Test
    public void energyAddBypass() {
        final EnergyStorage storage = new EnergyStorage(1000, 10, 10, 0);

        //Create a basic accessor
        final FlowCapabilityAccessor accessor = new FlowCapabilityAccessor(Direction.NORTH, () -> this).setBypassLimits(true);

        //Test the limits are still enforced
        final int received = storage.receiveEnergy(100, accessor);

        //Test we received only 10
        Assertions.assertEquals(100, received);
        Assertions.assertEquals(100, storage.getEnergyStored());
    }

    @Test
    public void energyRemoveBypass() {
        final EnergyStorage storage = new EnergyStorage(1000, 10, 10, 1000);

        //Create a basic accessor
        final FlowCapabilityAccessor accessor = new FlowCapabilityAccessor(Direction.NORTH, () -> this).setBypassLimits(true);

        //Test the limits are still enforced
        final int extract = storage.extractEnergy(100, accessor);

        //Test we removed only 10
        Assertions.assertEquals(100, extract);
        Assertions.assertEquals(900, storage.getEnergyStored());
    }

    @Test //Tests normal reaction
    public void energyAddTakeAll() {
        final EnergyStorage storage = new EnergyStorage(1000, 10, 10, 0);

        //Create a basic accessor
        final FlowCapabilityAccessor accessor = new FlowCapabilityAccessor(Direction.NORTH, () -> this).setRequireFull(true);

        //Test the limits are still enforced
        final int received = storage.receiveEnergy(10, accessor);

        //Test we received only 10
        Assertions.assertEquals(10, received);
        Assertions.assertEquals(10, storage.getEnergyStored());
    }

    @Test //Tests normal reaction
    public void energyRemoveTakeAll() {
        final EnergyStorage storage = new EnergyStorage(1000, 10, 10, 100);

        //Create a basic accessor
        final FlowCapabilityAccessor accessor = new FlowCapabilityAccessor(Direction.NORTH, () -> this).setRequireFull(true);

        //Test the limits are still enforced
        final int extract = storage.extractEnergy(10, accessor);

        //Test we removed only 10
        Assertions.assertEquals(10, extract);
        Assertions.assertEquals(90, storage.getEnergyStored());
    }

    @Test //Tests the limit fail check
    public void energyAddTakeAllFailLimit() {
        final EnergyStorage storage = new EnergyStorage(1000, 10, 10, 0);

        //Create a basic accessor
        final FlowCapabilityAccessor accessor = new FlowCapabilityAccessor(Direction.NORTH, () -> this).setRequireFull(true);

        //Test the limits are still enforced
        final int received = storage.receiveEnergy(100, accessor);

        //Test we received only 10
        Assertions.assertEquals(0, received);
        Assertions.assertEquals(0, storage.getEnergyStored());
    }

    @Test //Tests the limit fail check
    public void energyRemoveTakeAllFailLimit() {
        final EnergyStorage storage = new EnergyStorage(1000, 10, 10, 100);

        //Create a basic accessor
        final FlowCapabilityAccessor accessor = new FlowCapabilityAccessor(Direction.NORTH, () -> this).setRequireFull(true);

        //Test the limits are still enforced
        final int extract = storage.extractEnergy(100, accessor);

        //Test we removed only 10
        Assertions.assertEquals(0, extract);
        Assertions.assertEquals(100, storage.getEnergyStored());
    }

    @Test //Tests the fail condition for no room left
    public void energyAddTakeAllFailNoRoom() {
        final EnergyStorage storage = new EnergyStorage(1000, 10, 10, 999);

        //Create a basic accessor
        final FlowCapabilityAccessor accessor = new FlowCapabilityAccessor(Direction.NORTH, () -> this).setRequireFull(true);

        //Test the limits are still enforced
        final int received = storage.receiveEnergy(10, accessor);

        //Test we received only 10
        Assertions.assertEquals(0, received);
        Assertions.assertEquals(999, storage.getEnergyStored());
    }

    @Test //Tests the fail condition for not enough energy left
    public void energyRemoveTakeAllFailNoEnergy() {
        final EnergyStorage storage = new EnergyStorage(1000, 10, 10, 3);

        //Create a basic accessor
        final FlowCapabilityAccessor accessor = new FlowCapabilityAccessor(Direction.NORTH, () -> this).setRequireFull(true);

        //Test the limits are still enforced
        final int extract = storage.extractEnergy(10, accessor);

        //Test we removed only 10
        Assertions.assertEquals(0, extract);
        Assertions.assertEquals(3, storage.getEnergyStored());
    }
}
