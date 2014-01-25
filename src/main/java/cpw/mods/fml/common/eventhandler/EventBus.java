package cpw.mods.fml.common.eventhandler;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.logging.log4j.Level;



import com.google.common.collect.MapMaker;
import com.google.common.reflect.TypeToken;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

public class EventBus
{
    private static int maxID = 0;

    private ConcurrentHashMap<Object, ArrayList<IEventListener>> listeners = new ConcurrentHashMap<Object, ArrayList<IEventListener>>();
    private Map<Object,ModContainer> listenerOwners = new MapMaker().weakKeys().weakValues().makeMap();
    private final int busID = maxID++;

    public EventBus()
    {
        ListenerList.resize(busID + 1);
    }

    public void register(Object target)
    {
        if (listeners.containsKey(target))
        {
            return;
        }

        ModContainer activeModContainer = Loader.instance().activeModContainer();
        if (activeModContainer == null)
        {
            FMLLog.log(Level.ERROR, new Throwable(), "Unable to determine registrant mod for %s. This is a critical error and should be impossible", target);
            activeModContainer = Loader.instance().getMinecraftModContainer();
        }
        listenerOwners.put(target, activeModContainer);
        Set<? extends Class<?>> supers = TypeToken.of(target.getClass()).getTypes().rawTypes();
        for (Method method : target.getClass().getMethods())
        {
            for (Class<?> cls : supers)
            {
                try
                {
                    Method real = cls.getDeclaredMethod(method.getName(), method.getParameterTypes());
                    if (real.isAnnotationPresent(SubscribeEvent.class))
                    {
                        Class<?>[] parameterTypes = method.getParameterTypes();
                        if (parameterTypes.length != 1)
                        {
                            throw new IllegalArgumentException(
                                "Method " + method + " has @SubscribeEvent annotation, but requires " + parameterTypes.length +
                                " arguments.  Event handler methods must require a single argument."
                            );
                        }

                        Class<?> eventType = parameterTypes[0];

                        if (!Event.class.isAssignableFrom(eventType))
                        {
                            throw new IllegalArgumentException("Method " + method + " has @SubscribeEvent annotation, but takes a argument that is not an Event " + eventType);
                        }

                        register(eventType, target, method, activeModContainer);
                        break;
                    }
                }
                catch (NoSuchMethodException e)
                {
                    ;
                }
            }
        }
    }

    private void register(Class<?> eventType, Object target, Method method, ModContainer owner)
    {
        try
        {
            Constructor<?> ctr = eventType.getConstructor();
            ctr.setAccessible(true);
            Event event = (Event)ctr.newInstance();
            ASMEventHandler listener = new ASMEventHandler(target, method, owner);
            event.getListenerList().register(busID, listener.getPriority(), listener);

            ArrayList<IEventListener> others = listeners.get(target);
            if (others == null)
            {
                others = new ArrayList<IEventListener>();
                listeners.put(target, others);
            }
            others.add(listener);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void unregister(Object object)
    {
        ArrayList<IEventListener> list = listeners.remove(object);
        for (IEventListener listener : list)
        {
            ListenerList.unregisterAll(busID, listener);
        }
    }

    public boolean post(Event event)
    {
        IEventListener[] listeners = event.getListenerList().getListeners(busID);
        for (IEventListener listener : listeners)
        {
            listener.invoke(event);
        }
        return (event.isCancelable() ? event.isCanceled() : false);
    }
}
