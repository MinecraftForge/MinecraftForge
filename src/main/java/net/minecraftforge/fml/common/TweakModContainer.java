package net.minecraftforge.fml.common;

import com.google.common.eventbus.EventBus;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Level;

public class TweakModContainer extends DummyModContainer
{
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String VERSION = "version";
    private static final String DESCRIPTION = "description";
    private static final String URL = "url";
    private static final String AUTHORS = "authors";

    public static TweakModContainer from(InputStream inputStream, File source)
    {
        if (inputStream == null)
        {
            return null;
        }

        InputStreamReader reader = null;
        try
        {
            reader = new InputStreamReader(inputStream);
            JsonObject meta = new JsonParser().parse(reader).getAsJsonObject();
            ModMetadata metadata = new ModMetadata();

            if (!meta.has(ID) || !meta.has(NAME))
            {
                throw new JsonParseException("Missing required element '" + ID + "' and/or '" + NAME + "'");
            }

            metadata.modId = meta.get(ID).getAsString();
            metadata.name = meta.get(NAME).getAsString();
            metadata.version = meta.has(VERSION) ? meta.get(VERSION).getAsString() : "";
            metadata.description = meta.has(DESCRIPTION) ? meta.get(DESCRIPTION).getAsString() : "";
            metadata.url = meta.has(URL) ? meta.get(URL).getAsString() : "";

            JsonArray authors = meta.has(AUTHORS) ? meta.get(AUTHORS).getAsJsonArray() : new JsonArray();
            for (int i = 0; i < authors.size(); i++)
            {
                metadata.authorList.add(authors.get(i).getAsString());
            }

            return new TweakModContainer(metadata, source);
        }
        catch (Exception e)
        {
            FMLLog.log(Level.ERROR, e, "The metadata file in %s cannot be parsed as valid JSON. It will be ignored", source.getName());
            return null;
        }
        finally
        {
            IOUtils.closeQuietly(reader);
        }
    }

    private final File source;

    public TweakModContainer(ModMetadata md, File source)
    {
        super(md);
        this.source = source;
    }

    @Override
    public File getSource()
    {
        return source;
    }

    @Override
    public boolean isImmutable()
    {
        return true;
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller)
    {
        return true;
    }

}