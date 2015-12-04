

package net.minecraftforge.fml.common.asm;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.security.Permission;
import java.util.Map;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.LaunchClassLoader;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;

public class ASMTransformerWrapper
{
    private static final Map<String, String> wrapperModMap = Maps.newHashMap();
    private static final Map<String, String> wrapperParentMap = Maps.newHashMap();

    private static final LoadingCache<String, byte[]> wrapperCache = CacheBuilder.newBuilder()
        .maximumSize(30)
        .weakValues()
        .build(new CacheLoader<String, byte[]>()
        {
            public byte[] load(String file) throws Exception
            {
                return makeWrapper(file);
            }
        });

    private static final URL asmGenRoot;
    private static boolean injected = false;

    static
    {
        try
        {
            asmGenRoot = new URL("asmgen", null, -1, "/", new ASMGenHandler());
        }
        catch(MalformedURLException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static class ASMGenHandler extends URLStreamHandler
    {
        protected URLConnection openConnection(URL url) throws IOException
        {
            String file = url.getFile();
            if(file.equals("/"))
            {
                return new URLConnection(url)
                {
                    public void connect() throws IOException
                    {
                        throw new UnsupportedOperationException();
                    }
                };
            }
            if(!file.startsWith("/")) throw new RuntimeException("Malformed URL: " + url);
            file = file.substring(1);
            if(wrapperModMap.containsKey(file))
            {
                return new ASMGenConnection(url, file);
            }
            return null;
        }
    }

    private static class ASMGenConnection extends URLConnection
    {
        private final String file;

        protected ASMGenConnection(URL url, String file)
        {
            super(url);
            this.file = file;
        }

        public void connect() throws IOException
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public InputStream getInputStream()
        {
            return new ByteArrayInputStream(wrapperCache.getUnchecked(file));
        }

        @Override
        public Permission getPermission()
        {
            return null;
        }
    }

    public static String getTransformerWrapper(LaunchClassLoader launchLoader, String parentClass, String coreMod)
    {
        if(!injected)
        {
            injected = true;
            launchLoader.addURL(asmGenRoot);
        }

        String name = getWrapperName(parentClass);
        String fileName = name.replace('.', '/') + ".class";
        wrapperModMap.put(fileName, coreMod);
        wrapperParentMap.put(fileName, parentClass);
        return name;
    }

    private static byte[] makeWrapper(String fileName)
    {
        if(!wrapperModMap.containsKey(fileName) || !wrapperParentMap.containsKey(fileName) || !fileName.endsWith(".class"))
        {
            throw new IllegalArgumentException("makeWrapper called with strange argument: " + fileName);
        }
        String name = fileName.substring(0, fileName.length() - ".class".length());

        try
        {
            Type wrapper = Type.getType(TransformerWrapper.class);

            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

            writer.visit(Opcodes.V1_6, Opcodes.ACC_PUBLIC, name, null, wrapper.getInternalName(), null);

            Method m = Method.getMethod("void <init> ()");
            GeneratorAdapter mg = new GeneratorAdapter(Opcodes.ACC_PUBLIC, m, null, null, writer);
            mg.loadThis();
            mg.invokeConstructor(wrapper, m);
            mg.returnValue();
            mg.endMethod();

            m = Method.getMethod("java.lang.String getParentClass ()");
            mg = new GeneratorAdapter(Opcodes.ACC_PROTECTED, m, null, null, writer);
            mg.push(wrapperParentMap.get(fileName));
            mg.returnValue();
            mg.endMethod();

            m = Method.getMethod("java.lang.String getCoreMod ()");
            mg = new GeneratorAdapter(Opcodes.ACC_PROTECTED, m, null, null, writer);
            mg.push(wrapperModMap.get(fileName));
            mg.returnValue();
            mg.endMethod();

            writer.visitEnd();

            return writer.toByteArray();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    private static String getWrapperName(String parentClass)
    {
        return "$wrapper." + parentClass;
    }

    @SuppressWarnings("unused")
	private static class WrapperVisitor extends ClassVisitor
    {
        private final String name;
        private final String parentClass;

        public WrapperVisitor(ClassVisitor cv, String name, String parentClass)
        {
            super(Opcodes.ASM5, cv);
            this.name = name.replace('.', '/');
            this.parentClass = parentClass;
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
        {
            super.visit(version, access, this.name, signature, superName, interfaces);
        }

        @Override
        public FieldVisitor visitField(int access, String name, String desc, String signature, Object value)
        {
            if(name.equals("parentClass"))
            {
                return super.visitField(access, name, desc, signature, parentClass);
            }
            return super.visitField(access, name, desc, signature, value);
        }
    }

    public static abstract class TransformerWrapper implements IClassTransformer
    {
        private final IClassTransformer parent;

        public TransformerWrapper()
        {
            try
            {
                this.parent = (IClassTransformer)this.getClass().getClassLoader().loadClass(getParentClass()).newInstance();
            }
            catch(Exception e)
            {
                throw new RuntimeException(e);
            }
        }

        public byte[] transform(String name, String transformedName, byte[] basicClass)
        {
            try
            {
                return parent.transform(name, transformedName, basicClass);
            }
            catch(Throwable e)
            {
                throw new TransformerException("Exception in class transformer " + parent + " from coremod " + getCoreMod(), e);
            }
        }

        @Override
        public String toString()
        {
            return "TransformerWrapper(" + getParentClass() + ", " + getCoreMod() + ")";
        }

        protected abstract String getParentClass();

        protected abstract String getCoreMod();
    }

    static class TransformerException extends RuntimeException
    {
        private static final long serialVersionUID = -6616232415696157218L;

        public TransformerException(String message, Throwable cause)
        {
            super(message, cause);
        }
    }
}
