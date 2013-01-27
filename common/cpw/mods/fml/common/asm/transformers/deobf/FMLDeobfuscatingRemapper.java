package cpw.mods.fml.common.asm.transformers.deobf;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.objectweb.asm.commons.Remapper;

import com.google.common.base.Charsets;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableBiMap.Builder;
import com.google.common.io.CharStreams;
import com.google.common.io.InputSupplier;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.FMLRelaunchLog;

public class FMLDeobfuscatingRemapper extends Remapper {
    public static final FMLDeobfuscatingRemapper INSTANCE = new FMLDeobfuscatingRemapper();

    private BiMap<String, String> classNameBiMap;

    private boolean isLoaded = false;

    private FMLDeobfuscatingRemapper()
    {
    }

    public void setup(File mcDir)
    {
        try
        {
            File libDir = new File(mcDir, "lib");
            File mapData = new File(libDir, "deobfuscation_data.zip");
            ZipFile mapZip = new ZipFile(mapData);
            ZipEntry classData = mapZip.getEntry("class_data.csv");
            ZipInputSupplier zis = new ZipInputSupplier(mapZip, classData);
            InputSupplier<InputStreamReader> classNameSupplier = CharStreams.newReaderSupplier(zis,Charsets.UTF_8);
            List<String> classList = CharStreams.readLines(classNameSupplier);
            Builder<String, String> builder = ImmutableBiMap.<String,String>builder();
            for (String line : classList)
            {
                String[] parts = line.split(",");
                builder.put(parts[0],parts[1]);
            }
            classNameBiMap = builder.build();
        }
        catch (IOException ioe)
        {
            FMLRelaunchLog.log(Level.SEVERE, ioe, "An error occurred loading the class map data");
        }
    }
//    @Override
//    public String mapFieldName(String owner, String name, String desc)
//    {
//        System.out.println("+++" + owner + "."+name+"."+desc);
//        if (owner.indexOf('/')<0)
//        {
//            return "lexManosWasHere"+name;
//        }
//        else
//        {
//            return name;
//        }
//    }
    @Override
    public String map(String typeName)
    {
        if (classNameBiMap == null)
        {
            return typeName;
        }

        String result = classNameBiMap.containsKey(typeName) ? classNameBiMap.get(typeName) : typeName;
        System.out.printf("Remapping %s to %s\n", typeName, result);
        return result;
    }

    public String unmap(String typeName)
    {
        if (classNameBiMap == null)
        {
            return typeName;
        }
        return classNameBiMap.containsValue(typeName) ? classNameBiMap.inverse().get(typeName) : typeName;
    }
}
