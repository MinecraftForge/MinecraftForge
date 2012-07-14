package cpw.mods.fml.common;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.ModContainer.SourceType;

public class ModDiscoverer
{
    private class ClassFilter implements FileFilter
    {
        @Override
        public boolean accept(File file)
        {
            return (file.isFile() && modClass.matcher(file.getName()).find()) || file.isDirectory();
        }
    }

    private class ModCandidate
    {
        private File classPathRoot;
        private File modContainer;
        private String className;
        private SourceType sourceType;

        public ModCandidate(File classPathRoot, File modContainer, String className, SourceType sourceType)
        {
            this.classPathRoot = classPathRoot;
            this.modContainer = modContainer;
            this.className = className;
            this.sourceType = sourceType;
        }
    }

    private Logger log;

    private File modsDir;

    private static Pattern zipJar = Pattern.compile("(.+).(zip|jar)$");
    private static Pattern classFile = Pattern.compile("([^\\s$]+).class$");
    private static Pattern modClass = Pattern.compile("(.+/|)(mod\\_[^\\s$]+).class$");

    private List<ModCandidate> candidates = Lists.newArrayList();

    private void attemptDirLoad(File modDir, String path, SourceType sourceType, File dirRoot)
    {
        if (path.length() == 0)
        {
            dirRoot = modDir;
        }
        boolean foundAModClass = false;
        File[] content = modDir.listFiles(new ClassFilter());

        // Always sort our content
        Arrays.sort(content);
        for (File file : content)
        {
            if (file.isDirectory())
            {
                log.finest(String.format("Recursing into package %s", path + file.getName()));
                attemptDirLoad(file, path + file.getName() + ".", sourceType, dirRoot);
                continue;
            }
            Matcher fname = modClass.matcher(file.getName());
            if (!fname.find())
            {
                continue;
            }
            String clazzName = path + fname.group(2);
            log.fine(String.format("Candidate mod %s found in directory %s", clazzName, dirRoot.getName()));
            candidates.add(new ModCandidate(dirRoot, dirRoot, clazzName, sourceType));
            foundAModClass = true;
        }
    }

    private void attemptFileLoad(File modFile, SourceType sourceType)
    {
        ZipFile jar = null;
        try
        {
            jar = new ZipFile(modFile);

            ZipEntry modInfo = jar.getEntry("mcmod.info");
            if (modInfo != null)
            {

            }
            for (ZipEntry ze : Collections.list(jar.entries()))
            {
                Matcher match = classFile.matcher(ze.getName());
                if (match.matches())
                {
                    AnnotationFinder finder = new AnnotationFinder(jar.getInputStream(ze));
                }

                match = modClass.matcher(ze.getName());

                if (match.matches())
                {
                    String pkg = match.group(1).replace('/', '.');
                    String clazzName = pkg + match.group(2);
                    log.fine(String.format("Candidate mod %s found in mod file %s", clazzName, modFile.getName()));
                    candidates.add(new ModCandidate(modFile, modFile, clazzName, sourceType));
                }
            }
        }
        catch (Exception e)
        {
            log.warning(String.format("Zip file %s failed to read properly, it will be ignored", modFile.getName()));
        }
        finally
        {
            if (jar != null)
            {
                try
                {
                    jar.close();
                }
                catch (Exception e)
                {
                }
            }
        }
    }


    public static class AnnotationFinder extends ClassVisitor
    {

        public AnnotationFinder(InputStream stream) throws IOException
        {
            super(Opcodes.ASM4);
            ClassReader reader = new ClassReader(stream);
            reader.accept(this, ClassReader.SKIP_CODE);
        }

        @Override
        public void visit(int arg0, int arg1, String arg2, String arg3, String arg4, String[] arg5)
        {
            System.out.printf("ClassVisit.visit %d %d %s %s %s %s\n", arg0, arg1, arg2, arg3, arg4, Arrays.asList(arg5));
            super.visit(arg0, arg1, arg2, arg3, arg4, arg5);
        }

        @Override
        public void visitOuterClass(String arg0, String arg1, String arg2)
        {
            System.out.printf("ClassVisit.visitOuterClass %s %s %s\n", arg0, arg1, arg2);
            super.visitOuterClass(arg0, arg1, arg2);
        }

        @Override
        public void visitAttribute(Attribute arg0)
        {
            System.out.printf("ClassVisit.visitAttribute %s\n", arg0);
            super.visitAttribute(arg0);
        }

        @Override
        public FieldVisitor visitField(int arg0, String arg1, String arg2, String arg3, Object arg4)
        {
            System.out.printf("ClassVisit.visitField %d %s %s %s %s\n", arg0, arg1, arg2, arg3, arg4);
            return new FieldVisitor(Opcodes.ASM4)
            {
                @Override
                public AnnotationVisitor visitAnnotation(String arg0, boolean arg1)
                {
                    System.out.printf("FieldVisitor.visit %s %s\n", arg0, arg1);
                    return new AnnotationVisitor(Opcodes.ASM4)
                    {
                        @Override
                        public void visit(String arg0, Object arg1)
                        {
                            System.out.printf("FieldAnnotationVisitor.visit %s %s\n", arg0, arg1);
                            super.visit(arg0, arg1);
                        }
                    };

                }
            };
        }

        @Override
        public AnnotationVisitor visitAnnotation(String arg0, boolean arg1)
        {
            System.out.printf("ClassVisit.visitAnnotation %s %b\n", arg0, arg1);
            return new AnnotationVisitor(Opcodes.ASM4)
            {
                @Override
                public void visit(String arg0, Object arg1)
                {
                    System.out.printf("ClassAnnotationVisitor.visit %s %s\n", arg0, arg1);
                    super.visit(arg0, arg1);
                }

            };
        }
    }
}
