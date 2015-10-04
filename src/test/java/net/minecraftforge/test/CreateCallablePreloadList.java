package net.minecraftforge.test;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Ordering;
import com.google.common.io.Files;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import com.google.common.reflect.ClassPath.ResourceInfo;

public class CreateCallablePreloadList
{

    public static void main(String[] args) throws Throwable
    {
        String outFileName;
        if (args.length >= 1)
        {
            outFileName = args[0];
        } else
        {
            outFileName = "./../../src/main/resources/preload_classes.cfg";
        }

        File outFile = new File(outFileName).getCanonicalFile();
        if (outFile.exists() && !outFile.canWrite())
        {
            System.err.println("Cannot write to " + outFile.getAbsolutePath());
            System.exit(-1);
        }
        Files.createParentDirs(outFile); // throws on fail, so exits

        final ClassLoader cl = Thread.currentThread().getContextClassLoader();
        ClassPath cp = ClassPath.from(cl);

        List<Class<?>> classes = FluentIterable
                .from(cp.getResources())
                .filter(Predicates.instanceOf(ClassInfo.class))
                .transform(new Function<ResourceInfo, String>()
                {
                    @Override
                    public String apply(ResourceInfo input)
                    {
                        return ((ClassInfo) input).getName();
                    }
                })
                .filter(new Predicate<String>()
                {
                    @Override
                    public boolean apply(String name)
                    {
                        return name.startsWith("net.minecraft.");
                    }
                })
                .transform(new Function<String, Class<?>>()
                {
                    @Override
                    public Class<?> apply(String name)
                    {
                        try
                        {
                            return Class.forName(name, false, cl);
                        } catch (Throwable t)
                        {
                            return Object.class;
                        }
                    }
                })
                .filter(Predicates.assignableFrom(Callable.class))
                .filter(new Predicate<Class<?>>()
                {
                    @Override
                    public boolean apply(Class<?> clazz)
                    {
                        return clazz.getEnclosingClass() != null;
                    }

                })
                .toSortedList(
                        Ordering.from(String.CASE_INSENSITIVE_ORDER)
                                .onResultOf(new Function<Class<?>, String>()
                                {
                                    @Override
                                    public String apply(Class<?> clazz)
                                    {
                                        return clazz.getName();
                                    }
                                }));

        Multimap<String, Class<?>> map = Multimaps.index(classes,
                new Function<Class<?>, String>()
                {
                    @Override
                    public String apply(Class<?> clazz)
                    {
                        SideOnly annotation = clazz.getEnclosingClass()
                                .getDeclaredAnnotation(SideOnly.class);
                        return annotation == null ? "COM" : annotation.value()
                                .isClient() ? "CLI" : "SRV";
                    }

                });

        String joined = Joiner.on('\n').join(
                FluentIterable.from(map.entries()).transform(
                        new Function<Map.Entry<String, Class<?>>, String>()
                        {
                            @Override
                            public String apply(Entry<String, Class<?>> entry)
                            {
                                return entry.getKey() + ":"
                                        + entry.getValue().getName();
                            }
                        }));

        Files.write(joined, outFile, Charsets.UTF_8);
        System.out.println("Written " + map.size() + " preload classes to "
                + outFile.getAbsolutePath());
    }

}
