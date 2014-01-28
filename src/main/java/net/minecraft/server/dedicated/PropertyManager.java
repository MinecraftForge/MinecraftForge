package net.minecraft.server.dedicated;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SideOnly(Side.SERVER)
public class PropertyManager
{
    private static final Logger field_164440_a = LogManager.getLogger();
    private final Properties properties = new Properties();
    private final File associatedFile;
    private static final String __OBFID = "CL_00001782";

    public PropertyManager(File p_i45278_1_)
    {
        this.associatedFile = p_i45278_1_;

        if (p_i45278_1_.exists())
        {
            FileInputStream fileinputstream = null;

            try
            {
                fileinputstream = new FileInputStream(p_i45278_1_);
                this.properties.load(fileinputstream);
            }
            catch (Exception exception)
            {
                field_164440_a.warn("Failed to load " + p_i45278_1_, exception);
                this.logMessageAndSave();
            }
            finally
            {
                if (fileinputstream != null)
                {
                    try
                    {
                        fileinputstream.close();
                    }
                    catch (IOException ioexception)
                    {
                        ;
                    }
                }
            }
        }
        else
        {
            field_164440_a.warn(p_i45278_1_ + " does not exist");
            this.logMessageAndSave();
        }
    }

    // JAVADOC METHOD $$ func_73666_a
    public void logMessageAndSave()
    {
        field_164440_a.info("Generating new properties file");
        this.saveProperties();
    }

    // JAVADOC METHOD $$ func_73668_b
    public void saveProperties()
    {
        FileOutputStream fileoutputstream = null;

        try
        {
            fileoutputstream = new FileOutputStream(this.associatedFile);
            this.properties.store(fileoutputstream, "Minecraft server properties");
        }
        catch (Exception exception)
        {
            field_164440_a.warn("Failed to save " + this.associatedFile, exception);
            this.logMessageAndSave();
        }
        finally
        {
            if (fileoutputstream != null)
            {
                try
                {
                    fileoutputstream.close();
                }
                catch (IOException ioexception)
                {
                    ;
                }
            }
        }
    }

    // JAVADOC METHOD $$ func_73665_c
    public File getPropertiesFile()
    {
        return this.associatedFile;
    }

    // JAVADOC METHOD $$ func_73671_a
    public String getProperty(String par1Str, String par2Str)
    {
        if (!this.properties.containsKey(par1Str))
        {
            this.properties.setProperty(par1Str, par2Str);
            this.saveProperties();
            this.saveProperties();
        }

        return this.properties.getProperty(par1Str, par2Str);
    }

    // JAVADOC METHOD $$ func_73669_a
    public int getIntProperty(String par1Str, int par2)
    {
        try
        {
            return Integer.parseInt(this.getProperty(par1Str, "" + par2));
        }
        catch (Exception exception)
        {
            this.properties.setProperty(par1Str, "" + par2);
            this.saveProperties();
            return par2;
        }
    }

    // JAVADOC METHOD $$ func_73670_a
    public boolean getBooleanProperty(String par1Str, boolean par2)
    {
        try
        {
            return Boolean.parseBoolean(this.getProperty(par1Str, "" + par2));
        }
        catch (Exception exception)
        {
            this.properties.setProperty(par1Str, "" + par2);
            this.saveProperties();
            return par2;
        }
    }

    // JAVADOC METHOD $$ func_73667_a
    public void setProperty(String par1Str, Object par2Obj)
    {
        this.properties.setProperty(par1Str, "" + par2Obj);
    }
}