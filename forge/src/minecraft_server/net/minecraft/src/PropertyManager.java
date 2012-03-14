package net.minecraft.src;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PropertyManager
{
    /** Reference to the logger. */
    public static Logger logger = Logger.getLogger("Minecraft");

    /** The server properties object. */
    private Properties serverProperties = new Properties();

    /** The server properties file. */
    private File serverPropertiesFile;

    public PropertyManager(File par1File)
    {
        this.serverPropertiesFile = par1File;

        if (par1File.exists())
        {
            try
            {
                this.serverProperties.load(new FileInputStream(par1File));
            }
            catch (Exception var3)
            {
                logger.log(Level.WARNING, "Failed to load " + par1File, var3);
                this.generateNewProperties();
            }
        }
        else
        {
            logger.log(Level.WARNING, par1File + " does not exist");
            this.generateNewProperties();
        }
    }

    /**
     * Generates a new properties file.
     */
    public void generateNewProperties()
    {
        logger.log(Level.INFO, "Generating new properties file");
        this.saveProperties();
    }

    /**
     * Writes the properties to the properties file.
     */
    public void saveProperties()
    {
        try
        {
            this.serverProperties.store(new FileOutputStream(this.serverPropertiesFile), "Minecraft server properties");
        }
        catch (Exception var2)
        {
            logger.log(Level.WARNING, "Failed to save " + this.serverPropertiesFile, var2);
            this.generateNewProperties();
        }
    }

    /**
     * Returns this PropertyManager's file object used for property saving.
     */
    public File getPropertiesFile()
    {
        return this.serverPropertiesFile;
    }

    /**
     * Returns a string property. If the property doesn't exist the default is returned.
     */
    public String getStringProperty(String par1Str, String par2Str)
    {
        if (!this.serverProperties.containsKey(par1Str))
        {
            this.serverProperties.setProperty(par1Str, par2Str);
            this.saveProperties();
        }

        return this.serverProperties.getProperty(par1Str, par2Str);
    }

    /**
     * Returns an integer property. If the property doesn't exist the default is returned.
     */
    public int getIntProperty(String par1Str, int par2)
    {
        try
        {
            return Integer.parseInt(this.getStringProperty(par1Str, "" + par2));
        }
        catch (Exception var4)
        {
            this.serverProperties.setProperty(par1Str, "" + par2);
            return par2;
        }
    }

    /**
     * Returns a boolean property. If the property doesn't exist the default is returned.
     */
    public boolean getBooleanProperty(String par1Str, boolean par2)
    {
        try
        {
            return Boolean.parseBoolean(this.getStringProperty(par1Str, "" + par2));
        }
        catch (Exception var4)
        {
            this.serverProperties.setProperty(par1Str, "" + par2);
            return par2;
        }
    }

    /**
     * Saves an Object with the given property name
     */
    public void setProperty(String par1Str, Object par2Obj)
    {
        this.serverProperties.setProperty(par1Str, "" + par2Obj);
    }

    public void setProperty(String par1Str, boolean par2)
    {
        this.serverProperties.setProperty(par1Str, "" + par2);
        this.saveProperties();
    }
}
