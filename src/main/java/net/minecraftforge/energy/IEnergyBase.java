package energy;

public interface IEnergyBase {

    /**
    * Sets the maximum amount of energy that can be stored
     */
    public void setMaxEnergy(int value);

    /**
    * Sets the amount of energy stored
     */
    public void setEnergy(int value);

    /**
    * Returns the maximum amount of energy that can be stored
     */
    public int getMaxEnergy();

    /**
    * Returns the stored energy
     */
    public int getEnergy();

}
