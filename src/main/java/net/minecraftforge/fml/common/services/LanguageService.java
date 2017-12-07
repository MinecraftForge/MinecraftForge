package net.minecraftforge.fml.common.services;

import net.minecraftforge.fml.common.ILanguageAdapter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;

public class LanguageService
{

    private HashMap<String, ILanguageAdapter> adapters = new HashMap<>();

    public void findAdapters()
    {
        ServiceLoader<ILanguageAdapter> langServ = ServiceLoader.load(ILanguageAdapter.class);

        langServ.forEach(la ->
        {
            if (la.getClass().isAnnotationPresent(LanguageProvider.class))
            {
                String name = la.getClass().getAnnotation(LanguageProvider.class).provides();

                if (!name.isEmpty() && !adapters.containsKey(name))
                {
                   adapters.put(name, la);
                }

            }
        });
    }

    public Set<String> getLanguageAdapters()
    {
        return adapters.keySet();
    }

    public Set<ILanguageAdapter> getLanguageAdaters()
    {
        return new HashSet<>(adapters.values());
    }


    public ILanguageAdapter getAdapterByLang(String name)
    {
        return adapters.get(name);
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface LanguageProvider
    {
        String provides();
    }


}
