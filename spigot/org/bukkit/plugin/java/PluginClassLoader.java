package org.bukkit.plugin.java;

import net.md_5.specialsource.provider.ClassLoaderProvider;
import net.md_5.specialsource.transformer.MavenShade;
import org.bouncycastle.util.io.Streams;
import net.md_5.specialsource.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.plugin.PluginDescriptionFile;

import java.io.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.util.*;
import java.util.concurrent.*; // MCPC+ - Threadsafe classloading

/**
 * A ClassLoader for plugins, to allow shared classes across multiple plugins
 */
public class PluginClassLoader extends URLClassLoader {
    private String nbtTest = "cd";
    private final JavaPluginLoader loader;
    private final ConcurrentMap<String, Class<?>> classes = new ConcurrentHashMap<String, Class<?>>(); // MCPC+ - Threadsafe classloading
    // MCPC+ start
    private JarRemapper remapper;     // class remapper for this plugin, or null
    private RemapperPreprocessor remapperPreprocessor; // secondary; for inheritance & remapping reflection
    private boolean debug;            // classloader debugging

    private static ConcurrentMap<Integer,JarMapping> jarMappings = new ConcurrentHashMap<Integer, JarMapping>();
    private static final int F_USE_GUAVA10      = 1 << 1;
    private static final int F_GLOBAL_INHERIT   = 1 << 2;
    private static final int F_REMAP_OBCPRE     = 1 << 3;
    private static final int F_REMAP_NMS146     = 1 << 4;
    private static final int F_REMAP_OBC146     = 1 << 5;
    private static final int F_REMAP_NMS147     = 1 << 6;
    private static final int F_REMAP_NMS150     = 1 << 7;
    private static final int F_REMAP_NMS151     = 1 << 8;
    private static final int F_REMAP_OBC147     = 1 << 9;
    private static final int F_REMAP_OBC150     = 1 << 10;
    private static final int F_REMAP_NMS152     = 1 << 11;
    private static final int F_REMAP_OBC151     = 1 << 12;
    private static final int F_REMAP_OBC152     = 1 << 13;
    private static final int F_REMAP_NMS161     = 1 << 14;
    private static final int F_REMAP_NMS162     = 1 << 15;
    private static final int F_REMAP_OBC161     = 1 << 16;
    private static final int F_REMAP_OBC162     = 1 << 17;
    private static final int F_REMAP_NMSPRE_MASK= 0x0fff0000;  // "unversioned" NMS plugin version

    // This trick bypasses Maven Shade's package rewriting when using String literals [same trick in jline]
    private static final String org_bukkit_craftbukkit = new String(new char[] {'o','r','g','/','b','u','k','k','i','t','/','c','r','a','f','t','b','u','k','k','i','t'});
    // MCPC+ end

    public PluginClassLoader(final JavaPluginLoader loader, final URL[] urls, final ClassLoader parent, PluginDescriptionFile pluginDescriptionFile) { // MCPC+ - add PluginDescriptionFile
        super(urls, parent);

        this.loader = loader;

        // MCPC+ start

        String pluginName = pluginDescriptionFile.getName();

        // configure default remapper settings
        YamlConfiguration config = ((CraftServer)Bukkit.getServer()).configuration;
        boolean useCustomClassLoader = config.getBoolean("mcpc.plugin-settings.default.custom-class-loader", true);
        debug = config.getBoolean("mcpc.plugin-settings.default.debug", false);
        boolean useGuava10 = config.getBoolean("mcpc.plugin-settings.default.use-guava10", true);
        boolean remapNMS162 = config.getBoolean("mcpc.plugin-settings.default.remap-nms-v1_6_R2", true);
        boolean remapNMS161 = config.getBoolean("mcpc.plugin-settings.default.remap-nms-v1_6_R1", true);
        boolean remapNMS152 = config.getBoolean("mcpc.plugin-settings.default.remap-nms-v1_5_R3", true);
        boolean remapNMS151 = config.getBoolean("mcpc.plugin-settings.default.remap-nms-v1_5_R2", true);
        boolean remapNMS150 = config.getBoolean("mcpc.plugin-settings.default.remap-nms-v1_5_R1", true);
        boolean remapNMS147 = config.getBoolean("mcpc.plugin-settings.default.remap-nms-v1_4_R1", true);
        boolean remapNMS146 = config.getBoolean("mcpc.plugin-settings.default.remap-nms-v1_4_6", true);
        String remapNMSPre = config.getString("mcpc.plugin-settings.default.remap-nms-pre", "false");
        boolean remapOBC162 = config.getBoolean("mcpc.plugin-settings.default.remap-obc-v1_6_R2", false);
        boolean remapOBC161 = config.getBoolean("mcpc.plugin-settings.default.remap-obc-v1_6_R1", false);
        boolean remapOBC152 = config.getBoolean("mcpc.plugin-settings.default.remap-obc-v1_5_R3", true);
        boolean remapOBC151 = config.getBoolean("mcpc.plugin-settings.default.remap-obc-v1_5_R2", true);
        boolean remapOBC150 = config.getBoolean("mcpc.plugin-settings.default.remap-obc-v1_5_R1", true);
        boolean remapOBC147 = config.getBoolean("mcpc.plugin-settings.default.remap-obc-v1_4_R1", false);
        boolean remapOBC146 = config.getBoolean("mcpc.plugin-settings.default.remap-obc-v1_4_6", false);
        boolean remapOBCPre = config.getBoolean("mcpc.plugin-settings.default.remap-obc-pre", false);
        boolean globalInherit = config.getBoolean("mcpc.plugin-settings.default.global-inheritance", true);
        boolean pluginInherit = config.getBoolean("mcpc.plugin-settings.default.plugin-inheritance", true);
        boolean reflectFields = config.getBoolean("mcpc.plugin-settings.default.remap-reflect-field", true);
        boolean reflectClass = config.getBoolean("mcpc.plugin-settings.default.remap-reflect-class", true);
        boolean allowFuture = config.getBoolean("mcpc.plugin-settings.default.remap-allow-future", false);

        // plugin-specific overrides
        useCustomClassLoader = config.getBoolean("mcpc.plugin-settings."+pluginName+".custom-class-loader", useCustomClassLoader);
        debug = config.getBoolean("mcpc.plugin-settings."+pluginName+".debug", debug);
        useGuava10 = config.getBoolean("mcpc.plugin-settings."+pluginName+".use-guava10", useGuava10);
        remapNMS162 = config.getBoolean("mcpc.plugin-settings."+pluginName+".remap-nms-v1_6_R2", remapNMS162);
        remapNMS161 = config.getBoolean("mcpc.plugin-settings."+pluginName+".remap-nms-v1_6_R1", remapNMS161);
        remapNMS152 = config.getBoolean("mcpc.plugin-settings."+pluginName+".remap-nms-v1_5_R3", remapNMS152);
        remapNMS151 = config.getBoolean("mcpc.plugin-settings."+pluginName+".remap-nms-v1_5_R2", remapNMS151);
        remapNMS150 = config.getBoolean("mcpc.plugin-settings."+pluginName+".remap-nms-v1_5_R1", remapNMS150);
        remapNMS147 = config.getBoolean("mcpc.plugin-settings."+pluginName+".remap-nms-v1_4_R1", remapNMS147);
        remapNMS146 = config.getBoolean("mcpc.plugin-settings."+pluginName+".remap-nms-v1_4_6", remapNMS146);
        remapNMSPre = config.getString("mcpc.plugin-settings."+pluginName+".remap-nms-pre", remapNMSPre);
        remapOBC162 = config.getBoolean("mcpc.plugin-settings."+pluginName+".remap-obc-v1_6_R2", remapOBC162);
        remapOBC161 = config.getBoolean("mcpc.plugin-settings."+pluginName+".remap-obc-v1_6_R1", remapOBC161);
        remapOBC152 = config.getBoolean("mcpc.plugin-settings."+pluginName+".remap-obc-v1_5_R3", remapOBC152);
        remapOBC151 = config.getBoolean("mcpc.plugin-settings."+pluginName+".remap-obc-v1_5_R2", remapOBC151);
        remapOBC150 = config.getBoolean("mcpc.plugin-settings."+pluginName+".remap-obc-v1_5_R1", remapOBC150);
        remapOBC147 = config.getBoolean("mcpc.plugin-settings."+pluginName+".remap-obc-v1_4_R1", remapOBC147);
        remapOBC146 = config.getBoolean("mcpc.plugin-settings."+pluginName+".remap-obc-v1_4_6", remapOBC146);
        remapOBCPre = config.getBoolean("mcpc.plugin-settings."+pluginName+".remap-obc-pre", remapOBCPre);
        globalInherit = config.getBoolean("mcpc.plugin-settings."+pluginName+".global-inheritance", globalInherit);
        pluginInherit = config.getBoolean("mcpc.plugin-settings."+pluginName+".plugin-inheritance", pluginInherit);
        reflectFields = config.getBoolean("mcpc.plugin-settings."+pluginName+".remap-reflect-field", reflectFields);
        reflectClass = config.getBoolean("mcpc.plugin-settings."+pluginName+".remap-reflect-class", reflectClass);
        allowFuture = config.getBoolean("mcpc.plugin-settings."+pluginName+".remap-allow-future", allowFuture);

        /*if (!allowFuture) {
            if (cpw.mods.fml.relauncher.FMLInjectionData.obf151()) {
                remapNMS152 = false;
                remapOBC152 = false;
            }
        }*/

        if (debug) {
            System.out.println("PluginClassLoader debugging enabled for "+pluginName);
        }

        if (!useCustomClassLoader) {
            remapper = null;
            return;
        }

        int flags = 0;
        if (useGuava10) flags |= F_USE_GUAVA10;
        if (remapNMS162) flags |= F_REMAP_NMS162;
        if (remapNMS161) flags |= F_REMAP_NMS161;
        if (remapNMS152) flags |= F_REMAP_NMS152;
        if (remapNMS151) flags |= F_REMAP_NMS151;
        if (remapNMS150) flags |= F_REMAP_NMS150;
        if (remapNMS147) flags |= F_REMAP_NMS147;
        if (remapNMS146) flags |= F_REMAP_NMS146;
        if (!remapNMSPre.equals("false")) {
            if      (remapNMSPre.equals("1.6.2")) flags |= 0x01620000;
            else if (remapNMSPre.equals("1.6.1")) flags |= 0x01610000;
            else if (remapNMSPre.equals("1.5.2")) flags |= 0x01520000;
            else if (remapNMSPre.equals("1.5.1")) flags |= 0x01510000;
            else if (remapNMSPre.equals("1.5.0")) flags |= 0x01500000;
            else if (remapNMSPre.equals("1.5"))   flags |= 0x01500000;
            else if (remapNMSPre.equals("1.4.7")) flags |= 0x01470000;
            else if (remapNMSPre.equals("1.4.6")) flags |= 0x01460000;
            else if (remapNMSPre.equals("1.4.5")) flags |= 0x01450000;
            else if (remapNMSPre.equals("1.4.4")) flags |= 0x01440000;
            else if (remapNMSPre.equals("1.4.2")) flags |= 0x01420000;
            else if (remapNMSPre.equals("1.3.2")) flags |= 0x01320000;
            else if (remapNMSPre.equals("1.3.1")) flags |= 0x01310000;
            else if (remapNMSPre.equals("1.2.5")) flags |= 0x01250000;
            else {
                System.out.println("Unsupported nms-remap-pre version '"+remapNMSPre+"', disabling");
            }
        }
        if (remapOBC162) flags |= F_REMAP_OBC162;
        if (remapOBC161) flags |= F_REMAP_OBC161;
        if (remapOBC152) flags |= F_REMAP_OBC152;
        if (remapOBC151) flags |= F_REMAP_OBC151;
        if (remapOBC150) flags |= F_REMAP_OBC150;
        if (remapOBC147) flags |= F_REMAP_OBC147;
        if (remapOBC146) flags |= F_REMAP_OBC146;
        if (remapOBCPre) flags |= F_REMAP_OBCPRE;
        if (globalInherit) flags |= F_GLOBAL_INHERIT;

        JarMapping jarMapping = getJarMapping(flags);

        // Load inheritance map
        if ((flags & F_GLOBAL_INHERIT) != 0) {
            if (debug) {
                System.out.println("Enabling global inheritance remapping");
                //ClassLoaderProvider.verbose = debug; // TODO: changed in https://github.com/md-5/SpecialSource/commit/132584eda4f0860c9d14f4c142e684a027a128b8#L3L48
            }
            jarMapping.setInheritanceMap(loader.getGlobalInheritanceMap());
            jarMapping.setFallbackInheritanceProvider(new ClassLoaderProvider(this));
        }

        remapper = new JarRemapper(jarMapping);

        if (pluginInherit || reflectFields || reflectClass) {
            remapperPreprocessor = new RemapperPreprocessor(
                    pluginInherit ? loader.getGlobalInheritanceMap() : null,
                    (reflectFields || reflectClass) ? jarMapping : null);

            remapperPreprocessor.setRemapReflectField(reflectFields);
            remapperPreprocessor.setRemapReflectClass(reflectClass);
            remapperPreprocessor.debug = debug;
        } else {
            remapperPreprocessor = null;
        }
    }

    /**
     * Get the "native" obfuscation version, from our Maven shading version.
     */
    public static String getNativeVersion() {
        // see https://github.com/mbax/VanishNoPacket/blob/master/src/main/java/org/kitteh/vanish/compat/NMSManager.java
        final String packageName = org.bukkit.craftbukkit.CraftServer.class.getPackage().getName();

        return packageName.substring(packageName.lastIndexOf('.')  + 1);
    }

    /**
     * Load NMS mappings from CraftBukkit mc-dev to repackaged srgnames for FML runtime deobf
     *
     * @param jarMapping An existing JarMappings instance to load into
     * @param obfVersion CraftBukkit version with internal obfuscation counter identifier
     *                   >=1.4.7 this is the major version + R#. v1_4_R1=1.4.7, v1_5_R1=1.5, v1_5_R2=1.5.1..
     *                   For older versions (including pre-safeguard) it is the full Minecraft version number
     * @throws IOException
     */
    private void loadNmsMappings(JarMapping jarMapping, String obfVersion) throws IOException {
        Map<String, String> relocations = new HashMap<String, String>();
        // mc-dev jar to CB, apply version shading (aka plugin safeguard)
        relocations.put("net.minecraft.server", "net.minecraft.server." + obfVersion);

        jarMapping.loadMappings(
                new BufferedReader(new InputStreamReader(loader.getClass().getClassLoader().getResourceAsStream("mappings/"+obfVersion+"/cb2numpkg.srg"))),
                new MavenShade(relocations),
                null, false);

        // resolve naming conflict in FML/CB
        jarMapping.methods.put("net/minecraft/server/"+obfVersion+"/PlayerConnection/getPlayer ()Lorg/bukkit/craftbukkit/"+getNativeVersion()+"/entity/CraftPlayer;", "getPlayerB");

        // remap bouncycastle to Forge's included copy, not the vanilla obfuscated copy (not in MCPC+), see #133
        //jarMapping.packages.put("net/minecraft/"+obfVersion+"/org/bouncycastle", "org/bouncycastle"); No longer needed
    }

    private JarMapping getJarMapping(int flags) {
        JarMapping jarMapping = jarMappings.get(flags);

        if (jarMapping != null) {
            if (debug) {
                System.out.println("Mapping reused for "+Integer.toHexString(flags));
            }
            return jarMapping;
        }

        jarMapping = new JarMapping();
        try {

            if ((flags & F_USE_GUAVA10) != 0) {
                // Guava 10 is part of the Bukkit API, so plugins can use it, but FML includes Guava 12
                // To resolve this conflict, remap plugin usages to Guava 10 in a separate package
                // Most plugins should keep this enabled, unless they want a newer Guava
                jarMapping.packages.put("com/google/common", "guava10/com/google/common");
            }

            if ((flags & F_REMAP_NMS162) != 0) {
                loadNmsMappings(jarMapping, "v1_6_R2");
            }

            if ((flags & F_REMAP_NMS161) != 0) {
                loadNmsMappings(jarMapping, "v1_6_R1");
            }

            if ((flags & F_REMAP_NMS152) != 0) {
                loadNmsMappings(jarMapping, "v1_5_R3");
            }

            if ((flags & F_REMAP_NMS151) != 0) {
                loadNmsMappings(jarMapping, "v1_5_R2");
            }

            if ((flags & F_REMAP_NMS150) != 0) {
                loadNmsMappings(jarMapping, "v1_5_R1");
            }


            if ((flags & F_REMAP_NMS147) != 0) {
                loadNmsMappings(jarMapping, "v1_4_R1");
            }

            if ((flags & F_REMAP_NMS146) != 0) {
                loadNmsMappings(jarMapping, "v1_4_6");
            }

            if ((flags & F_REMAP_OBC162) != 0) {
                jarMapping.packages.put(org_bukkit_craftbukkit+"/v1_6_R2", org_bukkit_craftbukkit+"/"+getNativeVersion());
            }

            if ((flags & F_REMAP_OBC161) != 0) {
                jarMapping.packages.put(org_bukkit_craftbukkit+"/v1_6_R1", org_bukkit_craftbukkit+"/"+getNativeVersion());
            }

            if ((flags & F_REMAP_OBC152) != 0) {
                jarMapping.packages.put(org_bukkit_craftbukkit+"/v1_5_R3", org_bukkit_craftbukkit+"/"+getNativeVersion());
            }

            if ((flags & F_REMAP_OBC151) != 0) {
                jarMapping.packages.put(org_bukkit_craftbukkit+"/v1_5_R2", org_bukkit_craftbukkit+"/"+getNativeVersion());
            }

            if ((flags & F_REMAP_OBC150) != 0) {
                jarMapping.packages.put(org_bukkit_craftbukkit+"/v1_5_R1", org_bukkit_craftbukkit+"/"+getNativeVersion());
            }

            if ((flags & F_REMAP_OBC147) != 0) {
                jarMapping.packages.put(org_bukkit_craftbukkit+"/v1_4_R1", org_bukkit_craftbukkit+"/"+getNativeVersion());
            }

            if ((flags & F_REMAP_OBC146) != 0) {
                // Remap OBC v1_4_6  to v1_4_R1 (or current) for 1.4.6 plugin compatibility
                // Note this should only be mapped statically - since plugins MAY use reflection to determine the OBC version
                jarMapping.packages.put(org_bukkit_craftbukkit+"/v1_4_6", org_bukkit_craftbukkit+"/"+getNativeVersion());
            }

            if ((flags & F_REMAP_OBCPRE) != 0) {
                // enabling unversioned obc not currently compatible with versioned obc plugins (overmapped) -
                // admins should enable remap-obc-pre on a per-plugin basis, as needed
                //jarMapping.packages.put(org_bukkit_craftbukkit+"/v1_4_R1", org_bukkit_craftbukkit+"/v1_4_R1");

                // then map unversioned to current version
                jarMapping.packages.put(org_bukkit_craftbukkit+"/libs/org/objectweb/asm", "org/objectweb/asm"); // ?
                jarMapping.packages.put(org_bukkit_craftbukkit, org_bukkit_craftbukkit+"/"+getNativeVersion());
            }

            if ((flags & F_REMAP_NMSPRE_MASK) != 0) {
                String filename;
                switch (flags & F_REMAP_NMSPRE_MASK)
                {
                    case 0x01620000: filename = "mappings/v1_6_R2/cb2numpkg.srg"; break;
                    case 0x01610000: filename = "mappings/v1_6_R1/cb2numpkg.srg"; break;
                    case 0x01510000: filename = "mappings/v1_5_R2/cb2numpkg.srg"; break;
                    case 0x01500000: filename = "mappings/v1_5_R1/cb2numpkg.srg"; break;
                    case 0x01470000: filename = "mappings/v1_4_R1/cb2numpkg.srg"; break;
                    case 0x01460000: filename = "mappings/v1_4_6/cb2numpkg.srg"; break;
                    case 0x01450000: filename = "mappings/v1_4_5/cb2numpkg.srg"; break;
                    case 0x01440000: filename = "mappings/v1_4_4/cb2numpkg.srg"; break;
                    case 0x01420000: filename = "mappings/v1_4_2/cb2numpkg.srg"; break;
                    case 0x01320000: filename = "mappings/v1_3_2/cb2numpkg.srg"; break;
                    case 0x01310000: filename = "mappings/v1_3_1/cb2numpkg.srg"; break;
                    case 0x01250000: filename = "mappings/v1_2_5/cb2numpkg.srg"; break;
                    default: throw new IllegalArgumentException("Invalid unversioned mapping flags: "+Integer.toHexString(flags & F_REMAP_NMSPRE_MASK)+" in "+Integer.toHexString(flags));
                }

                jarMapping.loadMappings(
                        new BufferedReader(new InputStreamReader(loader.getClass().getClassLoader().getResourceAsStream(filename))),
                        null, // no version relocation!
                        null, false);
            }

            System.out.println("Mapping loaded "+jarMapping.packages.size()+" packages, "+jarMapping.classes.size()+" classes, "+jarMapping.fields.size()+" fields, "+jarMapping.methods.size()+" methods, flags "+Integer.toHexString(flags));

            JarMapping currentJarMapping = jarMappings.putIfAbsent(flags, jarMapping);
            return currentJarMapping == null ? jarMapping : currentJarMapping;
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }
    // MCPC+ end

    @Override
    public void addURL(URL url) { // Override for access level!
        super.addURL(url);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return findClass(name, true);
    }

    protected Class<?> findClass(String name, boolean checkGlobal) throws ClassNotFoundException {
        if (name.startsWith("org.bukkit.") || name.startsWith("net.minecraft.")) {
            if (debug) {
                System.out.println("Unexpected plugin findClass on OBC/NMS: name="+name+", checkGlobal="+checkGlobal+"; returning not found");
            }
            throw new ClassNotFoundException(name);
        }
        // MCPC+ start - custom loader, if enabled, threadsafety
        Class<?> result;
        synchronized (name.intern()) {
            result = classes.get(name);

            if (result == null) {
                if (checkGlobal) {
                    result = loader.getClassByName(name);
                }

                if (result == null) {
                    if (remapper == null) {
                        result = super.findClass(name);
                    } else {
                        result = remappedFindClass(name);
                    }

                    if (result != null) {
                        loader.setClass(name, result);
                    }
                }

                if (result != null) {
                    Class<?> old = classes.putIfAbsent(name, result);
                    if (old != null && old != result) {
                        System.err.println("Defined class " + name + " twice as different classes, " + result + " and " + old);
                        result = old;
                    }
                }
            }
        }
        // MCPC+ end

        return result;
    }

    public Set<String> getClasses() {
        return classes.keySet();
    }

    // MCPC+ start
    private Class<?> remappedFindClass(String name) throws ClassNotFoundException {
        Class<?> result = null;

        try {
            // Load the resource to the name
            String path = name.replace('.', '/').concat(".class");
            URL url = this.findResource(path);
            if (url != null) {
                InputStream stream = url.openStream();
                if (stream != null) {
                    byte[] bytecode = null;

                    // Reflection remap and inheritance extract
                    if (remapperPreprocessor != null) {
                        // add to inheritance map
                        bytecode = remapperPreprocessor.preprocess(stream);
                        if (bytecode == null) stream = url.openStream();
                    }

                    if (bytecode == null) {
                        bytecode = Streams.readAll(stream);
                    }

                    // Remap the classes
                    byte[] remappedBytecode = remapper.remapClassFile(bytecode, null);

                    if (debug) {
                        File file = new File("remapped-plugin-classes/"+name+".class");
                        file.getParentFile().mkdirs();
                        try {
                            FileOutputStream fileOutputStream = new FileOutputStream(file);
                            fileOutputStream.write(remappedBytecode);
                            fileOutputStream.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }

                    // Define (create) the class using the modified byte code
                    // The top-child class loader is used for this to prevent access violations
                    // Set the codesource to the jar, not within the jar, for compatibility with
                    // plugins that do new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI()))
                    // instead of using getResourceAsStream - see https://github.com/MinecraftPortCentral/MCPC-Plus/issues/75
                    JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection(); // parses only
                    URL jarURL = jarURLConnection.getJarFileURL();
                    CodeSource codeSource = new CodeSource(jarURL, new CodeSigner[0]);

                    result = this.defineClass(name, remappedBytecode, 0, remappedBytecode.length, codeSource);
                    if (result != null) {
                        // Resolve it - sets the class loader of the class
                        this.resolveClass(result);
                    }
                }
            }
        } catch (Throwable t) {
            if (debug) {
                System.out.println("remappedFindClass("+name+") exception: "+t);
                t.printStackTrace();
            }
            throw new ClassNotFoundException("Failed to remap class "+name, t);
        }

        return result;
    }
    // MCPC+ end
}
