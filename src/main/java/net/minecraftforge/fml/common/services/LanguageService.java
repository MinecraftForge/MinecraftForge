package net.minecraftforge.fml.common.services;

import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.ILanguageAdapter;
import org.apache.logging.log4j.Level;

import java.util.HashMap;
import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;

public class LanguageService
{

    private HashMap<String, ILanguageAdapter> adapters = new HashMap<>();

    public LanguageService(ClassLoader classLoader)
    {
        ServiceLoader<ILanguageAdapter> langServ = ServiceLoader.load(ILanguageAdapter.class, classLoader);

        langServ.forEach(la ->
        {
            if (la.provides() != null && !la.provides().isEmpty() && !adapters.containsKey(la.provides()))
            {
                adapters.put(la.provides(), la);

                FMLLog.info("Found a language adapter that provides %s, provided by %s now registering", la.provides(), la.getClass().getCanonicalName());
            } else
            {
                FMLLog.bigWarning("There was a problem registering LanguageAdapter %s check provides", la.getClass().getCanonicalName());
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

    public String getAdapterClassName(String name)
    {
        return getAdapterByLang(name).getClass().getCanonicalName();
    }


}
