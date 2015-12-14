package net.minecraftforge.fml.client;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.logging.log4j.Level;

import javax.imageio.ImageIO;

import net.minecraft.client.resources.FileResourcePack;
import net.minecraftforge.fml.common.FMLContainerHolder;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.ModContainer;

import com.google.common.base.Charsets;

public class FMLFileResourcePack extends FileResourcePack implements FMLContainerHolder {

    private ModContainer container;

    public FMLFileResourcePack(ModContainer container)
    {
        super(container.getSource());
        this.container = container;
    }

    @Override
    public String getPackName()
    {
        return "FMLFileResourcePack:"+container.getName();
    }
    @Override
    protected InputStream getInputStreamByName(String resourceName) throws IOException
    {
        try
        {
            return super.getInputStreamByName(resourceName);
        }
        catch (IOException ioe)
        {
            if ("pack.mcmeta".equals(resourceName))
            {
                FMLLog.log(container.getName(), Level.DEBUG, "Mod %s is missing a pack.mcmeta file, substituting a dummy one", container.getName());
                return new ByteArrayInputStream(("{\n" +
                        " \"pack\": {\n"+
                        "   \"description\": \"dummy FML pack for "+container.getName()+"\",\n"+
                        "   \"pack_format\": 1\n"+
                        "}\n" +
                        "}").getBytes(Charsets.UTF_8));
            }
            else throw ioe;
        }
    }

    @Override
    public BufferedImage getPackImage() throws IOException
    {
        return ImageIO.read(getInputStreamByName(container.getMetadata().logoFile));
    }

    @Override
    public ModContainer getFMLContainer()
    {
        return container;
    }
}
