package cpw.mods.fml.common.asm.transformers.deobf;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.commons.Remapper;

import com.google.common.base.Charsets;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableBiMap.Builder;
import com.google.common.io.CharStreams;
import com.google.common.io.InputSupplier;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.FMLRelaunchLog;
import cpw.mods.fml.relauncher.RelaunchClassLoader;

public class FMLDeobfuscatingRemapper extends Remapper {
    public static final FMLDeobfuscatingRemapper INSTANCE = new FMLDeobfuscatingRemapper();

    private BiMap<String, String> classNameBiMap;
    private Map<String,Map<String,String>> rawFieldMaps;
    private Map<String,Map<String,String>> rawMethodMaps;

    private Map<String,Map<String,String>> fieldNameMaps;
    private Map<String,Map<String,String>> methodNameMaps;

    private RelaunchClassLoader classLoader;

    private FMLDeobfuscatingRemapper()
    {
    }

    public void setup(File mcDir, RelaunchClassLoader classLoader)
    {
        this.classLoader = classLoader;
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

            ZipEntry methodData = mapZip.getEntry("method_data.csv");
            zis = new ZipInputSupplier(mapZip, methodData);
            InputSupplier<InputStreamReader> methodNameSupplier = CharStreams.newReaderSupplier(zis,Charsets.UTF_8);
            List<String> methodList = CharStreams.readLines(methodNameSupplier);
            rawMethodMaps = Maps.newHashMap();
            for (String line : methodList)
            {
                String[] parts = line.split(",");
                String oldSrg = parts[0];
                int lastOld = oldSrg.lastIndexOf('/');
                String cl = oldSrg.substring(0,lastOld);
                String oldName = oldSrg.substring(lastOld+1);
                String sig = parts[1];
                String newSrg = parts[2];
                int lastNew = newSrg.lastIndexOf('/');
                String newName = newSrg.substring(lastNew+1);
                if (!rawMethodMaps.containsKey(cl))
                {
                    rawMethodMaps.put(cl, Maps.<String,String>newHashMap());
                }
                rawMethodMaps.get(cl).put(oldName+sig, newName);
            }
        }
        catch (IOException ioe)
        {
            FMLRelaunchLog.log(Level.SEVERE, ioe, "An error occurred loading the deobfuscation map data");
        }
        methodNameMaps = Maps.newHashMapWithExpectedSize(rawMethodMaps.size());
    }
    @Override
    public String mapFieldName(String owner, String name, String desc)
    {
        return name;
    }

    @Override
    public String map(String typeName)
    {
        if (classNameBiMap == null)
        {
            return typeName;
        }

        String result = classNameBiMap.containsKey(typeName) ? classNameBiMap.get(typeName) : typeName;
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


    @Override
    public String mapMethodName(String owner, String name, String desc)
    {
        if (classNameBiMap==null)
        {
            return name;
        }
        Map<String, String> methodMap = getMethodMap(owner);
        String methodDescriptor = name+desc;
        return methodMap!=null && methodMap.containsKey(methodDescriptor) ? methodMap.get(methodDescriptor) : name;
    }

    private Map<String,String> getMethodMap(String className)
    {
        if (!methodNameMaps.containsKey(className))
        {
            findAndMergeSuperMaps(className);
        }
        return methodNameMaps.get(className);
    }

    private void findAndMergeSuperMaps(String name)
    {
        try
        {
            byte[] classBytes = classLoader.getClassBytes(name);
            if (classBytes == null)
            {
                return;
            }
            ClassReader cr = new ClassReader(classBytes);
            String superName = cr.getSuperName();
            String[] interfaces = cr.getInterfaces();
            if (interfaces == null)
            {
                interfaces = new String[0];
            }
            mergeSuperMaps(name, superName, interfaces);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    public void mergeSuperMaps(String name, String superName, String[] interfaces)
    {
        if (classNameBiMap == null)
        {
            return;
        }
        List<String> allParents = ImmutableList.<String>builder().add(superName).addAll(Arrays.asList(interfaces)).build();
        for (String parentThing : allParents)
        {
            if (superName != null && classNameBiMap.containsKey(superName) && !methodNameMaps.containsKey(superName))
            {
                findAndMergeSuperMaps(superName);
            }
        }
        System.out.printf("Merging maps for %s (%s , %s)\n", name, superName, Arrays.asList(interfaces));
        Map<String, String> methodMap = Maps.<String,String>newHashMap();
        for (String parentThing : allParents)
        {
            if (methodNameMaps.containsKey(parentThing))
            {
                methodMap.putAll(methodNameMaps.get(parentThing));
            }
        }
        if (rawMethodMaps.containsKey(name))
        {
            methodMap.putAll(rawMethodMaps.get(name));
        }
        methodNameMaps.put(name, ImmutableMap.copyOf(methodMap));
    }
}
