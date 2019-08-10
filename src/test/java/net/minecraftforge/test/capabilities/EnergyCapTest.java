package net.minecraftforge.test.capabilities;

import net.minecraftforge.energy.EnergyStorage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Created by Dark(DarkGuardsman, Robert) on 8/10/2019.
 */
public class EnergyCapTest
{
    @Test //Test that old saves will not load beyond the limit of the buffer
    public void basicEnergyInitBeyondLimit() {
        final EnergyStorage storage = new EnergyStorage(1000, 10, 10, 5000);

        //Test we received only 1000
        Assertions.assertEquals(1000, storage.getEnergyStored());
    }

    @Test
    public void basicEnergyAdd() {
        final EnergyStorage storage = new EnergyStorage(1000, 10, 10, 0);

        //Test the limits are still enforced
        final int received = storage.receiveEnergy(100, false, false, false);

        //Test we received only 10
        Assertions.assertEquals(10, received);
        Assertions.assertEquals(10, storage.getEnergyStored());
    }

    @Test
    public void basicEnergyAddBeyondLimit() {
        final EnergyStorage storage = new EnergyStorage(1000, 1000, 1000, 0);

        //Test the limits are still enforced
        final int received = storage.receiveEnergy(5000, false, false, false);

        //Test we received only 10
        Assertions.assertEquals(1000, received);
        Assertions.assertEquals(1000, storage.getEnergyStored());
    }

    @Test
    public void basicEnergyRemove() {
        final EnergyStorage storage = new EnergyStorage(1000, 10, 10, 1000);

        //Test the limits are still enforced
        final int extract = storage.extractEnergy(100, false, false, false);

        //Test we removed only 10
        Assertions.assertEquals(10, extract);
        Assertions.assertEquals(990, storage.getEnergyStored());
    }

    @Test
    public void basicEnergyRemoveBeyondLimit() {
        final EnergyStorage storage = new EnergyStorage(1000, 1000, 1000, 1000);

        //Test the limits are still enforced
        final int extract = storage.extractEnergy(5000, false, false, false);

        //Test we removed only 10
        Assertions.assertEquals(1000, extract);
        Assertions.assertEquals(0, storage.getEnergyStored());
    }

    @Test
    public void basicEnergyAddSimulate() {
        final EnergyStorage storage = new EnergyStorage(1000, 10, 10, 0);

        //Test the limits are still enforced
        final int received = storage.receiveEnergy(100, true, false, false);

        //Test we received only 10
        Assertions.assertEquals(10, received);
        Assertions.assertEquals(0, storage.getEnergyStored());
    }

    @Test
    public void basicEnergyRemoveSimulate() {
        final EnergyStorage storage = new EnergyStorage(1000, 10, 10, 1000);

        //Test the limits are still enforced
        final int extract = storage.extractEnergy(100, true, false, false);

        //Test we removed only 10
        Assertions.assertEquals(10, extract);
        Assertions.assertEquals(1000, storage.getEnergyStored());
    }

    @Test
    public void energyAddBypass() {
        final EnergyStorage storage = new EnergyStorage(1000, 10, 10, 0);

        //Test the limits are still enforced
        final int received = storage.receiveEnergy(100, false, true, false);

        //Test we received only 10
        Assertions.assertEquals(100, received);
        Assertions.assertEquals(100, storage.getEnergyStored());
    }

    @Test
    public void energyRemoveBypass() {
        final EnergyStorage storage = new EnergyStorage(1000, 10, 10, 1000);

        //Test the limits are still enforced
        final int extract = storage.extractEnergy(100, false, true, false);

        //Test we removed only 10
        Assertions.assertEquals(100, extract);
        Assertions.assertEquals(900, storage.getEnergyStored());
    }

    @Test //Tests normal reaction
    public void energyAddTakeAll() {
        final EnergyStorage storage = new EnergyStorage(1000, 10, 10, 0);

        //Test the limits are still enforced
        final int received = storage.receiveEnergy(10, false, false, true);

        //Test we received only 10
        Assertions.assertEquals(10, received);
        Assertions.assertEquals(10, storage.getEnergyStored());
    }

    @Test //Tests normal reaction
    public void energyRemoveTakeAll() {
        final EnergyStorage storage = new EnergyStorage(1000, 10, 10, 100);

        //Test the limits are still enforced
        final int extract = storage.extractEnergy(10, false, false, true);

        //Test we removed only 10
        Assertions.assertEquals(10, extract);
        Assertions.assertEquals(90, storage.getEnergyStored());
    }

    @Test //Tests the limit fail check
    public void energyAddTakeAllFailLimit() {
        final EnergyStorage storage = new EnergyStorage(1000, 10, 10, 0);

        //Test the limits are still enforced
        final int received = storage.receiveEnergy(100, false, false, true);

        //Test we received only 10
        Assertions.assertEquals(0, received);
        Assertions.assertEquals(0, storage.getEnergyStored());
    }

    @Test //Tests the limit fail check
    public void energyRemoveTakeAllFailLimit() {
        final EnergyStorage storage = new EnergyStorage(1000, 10, 10, 100);

        //Test the limits are still enforced
        final int extract = storage.extractEnergy(100, false, false, true);

        //Test we removed only 10
        Assertions.assertEquals(0, extract);
        Assertions.assertEquals(100, storage.getEnergyStored());
    }

    @Test //Tests the fail condition for no room left
    public void energyAddTakeAllFailNoRoom() {
        final EnergyStorage storage = new EnergyStorage(1000, 10, 10, 999);

        //Test the limits are still enforced
        final int received = storage.receiveEnergy(10,false, false, true);

        //Test we received only 10
        Assertions.assertEquals(0, received);
        Assertions.assertEquals(999, storage.getEnergyStored());
    }

    @Test //Tests the fail condition for not enough energy left
    public void energyRemoveTakeAllFailNoEnergy() {
        final EnergyStorage storage = new EnergyStorage(1000, 10, 10, 3);

        //Test the limits are still enforced
        final int extract = storage.extractEnergy(10,false, false, true);

        //Test we removed only 10
        Assertions.assertEquals(0, extract);
        Assertions.assertEquals(3, storage.getEnergyStored());
    }
}
