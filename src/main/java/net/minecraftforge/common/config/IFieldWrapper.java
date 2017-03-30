package net.minecraftforge.common.config;

public interface IFieldWrapper
{

    /**
     * @return The type adapter to serialize the values returned by getValue. Null if non-primitive.
     */
    public ITypeAdapter getTypeAdapter();
    
    /**
     * @param field the field about which to retrieve information
     * @param instance The instance whose field shall be queried.
     * @return a list of keys handled by this field
     */
    public String[] getEntries();
    
    public Object getValue(String key);
    
    public void setEntry(String key, Object value);
    
    public boolean hasEntry(String name);
    
    public boolean handlesEntry(String name);
}
