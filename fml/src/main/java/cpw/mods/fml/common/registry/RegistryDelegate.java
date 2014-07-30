package cpw.mods.fml.common.registry;


/**
 * A registry delegate for holding references to items or blocks
 * 
 * @author cpw
 *
 * @param <T> the type of thing we're holding onto
 */
public interface RegistryDelegate<T> {
    T get();
    String name();
    Class<T> type();
    
    final class Delegate<T> implements RegistryDelegate<T>
    {
        private T referant;
        private String name;
        private final Class<T> type;
        
        public Delegate(T referant, Class<T> type) {
            this.referant = referant;
            this.type = type;
        }
        
        @Override
        public T get() {
            return referant;
        }

        @Override
        public String name() {
            return name;
        }

        public Class<T> type()
        {
            return this.type;
        }
        
        void changeReference(T newTarget)
        {
            this.referant = newTarget;
        }
        
        void setName(String name)
        {
            this.name = name;
        }
    }
}
