package net.minecraft.client.resources.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.IRegistry;
import net.minecraft.util.RegistrySimple;

@SideOnly(Side.CLIENT)
public class IMetadataSerializer
{
    private final IRegistry metadataSectionSerializerRegistry = new RegistrySimple();
    private final GsonBuilder gsonBuilder = new GsonBuilder();
    // JAVADOC FIELD $$ field_110507_c
    private Gson gson;
    private static final String __OBFID = "CL_00001101";

    public void registerMetadataSectionType(IMetadataSectionSerializer par1MetadataSectionSerializer, Class par2Class)
    {
        this.metadataSectionSerializerRegistry.putObject(par1MetadataSectionSerializer.getSectionName(), new IMetadataSerializer.Registration(par1MetadataSectionSerializer, par2Class, null));
        this.gsonBuilder.registerTypeAdapter(par2Class, par1MetadataSectionSerializer);
        this.gson = null;
    }

    public IMetadataSection parseMetadataSection(String par1Str, JsonObject par2JsonObject)
    {
        if (par1Str == null)
        {
            throw new IllegalArgumentException("Metadata section name cannot be null");
        }
        else if (!par2JsonObject.has(par1Str))
        {
            return null;
        }
        else if (!par2JsonObject.get(par1Str).isJsonObject())
        {
            throw new IllegalArgumentException("Invalid metadata for \'" + par1Str + "\' - expected object, found " + par2JsonObject.get(par1Str));
        }
        else
        {
            IMetadataSerializer.Registration registration = (IMetadataSerializer.Registration)this.metadataSectionSerializerRegistry.getObject(par1Str);

            if (registration == null)
            {
                throw new IllegalArgumentException("Don\'t know how to handle metadata section \'" + par1Str + "\'");
            }
            else
            {
                return (IMetadataSection)this.getGson().fromJson(par2JsonObject.getAsJsonObject(par1Str), registration.field_110500_b);
            }
        }
    }

    // JAVADOC METHOD $$ func_110505_a
    private Gson getGson()
    {
        if (this.gson == null)
        {
            this.gson = this.gsonBuilder.create();
        }

        return this.gson;
    }

    @SideOnly(Side.CLIENT)
    class Registration
    {
        final IMetadataSectionSerializer field_110502_a;
        final Class field_110500_b;
        private static final String __OBFID = "CL_00001103";

        private Registration(IMetadataSectionSerializer par2MetadataSectionSerializer, Class par3Class)
        {
            this.field_110502_a = par2MetadataSectionSerializer;
            this.field_110500_b = par3Class;
        }

        Registration(IMetadataSectionSerializer par2MetadataSectionSerializer, Class par3Class, Object par4MetadataSerializerEmptyAnon)
        {
            this(par2MetadataSectionSerializer, par3Class);
        }
    }
}