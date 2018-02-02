package net.minecraftforge.common.config;

/**
 * The objects are expected to get their wrapped field, the owning class, instance and category string on initialization.
 */
public interface IFieldWrapper
{

    /**
     * @return The type adapter to serialize the values returned by getValue. Null if non-primitive.
     */
    ITypeAdapter getTypeAdapter();
    
    /**
     * @param field the field about which to retrieve information
     * @param instance The instance whose field shall be queried.
     * @return a list of keys handled by this field
     */
    String[] getKeys();
    
    Object getValue(String key);
    
    void setValue(String key, Object value);
    
    boolean hasKey(String name);
    
    boolean handlesKey(String name);
    
    void setupConfiguration(Configuration cfg, String desc, String langKey, boolean reqMCRestart, boolean reqWorldRestart);
    
    /**
     * @return the category name in which the entries should be saved.
     */
    String getCategory();
}
