package org.bukkit.entity;

/**
 * Represents an arrow.
 */
public interface Arrow extends Projectile
{

    public class Spigot extends Entity.Spigot
    {

        public double getDamage()
        {
            throw new UnsupportedOperationException( "Not supported yet." );
        }

        public void setDamage(double damage)
        {
            throw new UnsupportedOperationException( "Not supported yet." );
        }
    }

    Spigot spigot();
}
