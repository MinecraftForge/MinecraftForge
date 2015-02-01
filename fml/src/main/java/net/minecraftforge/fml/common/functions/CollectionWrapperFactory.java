package net.minecraftforge.fml.common.functions;

import java.util.Collection;
import java.util.List;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

public class CollectionWrapperFactory {
    /**
     * Return a read only cast view of the supplied ungeneric collection, based on the element type given
     * @param coll The collection to cast
     * @param elementType the supertype contained in the collection
     * @return a collection asserting that type relationship
     */
    public static <T> Collection<T> wrap(@SuppressWarnings("rawtypes") Collection coll, Class<T> elementType)
    {
        Collection<?> asGeneric = coll;
        return Collections2.transform(asGeneric, new TypeCastFunction<T>(elementType));
    }

    /**
     * Return a read only cast view of the supplied ungeneric list, based on the element type given
     * @param list The list to cast
     * @param elementType the supertype contained in the list
     * @return a list asserting that type relationship
     */
    public static <T> List<T> wrap(@SuppressWarnings("rawtypes") List list, Class<T> elementType)
    {
        List<?> asGeneric = list;
        return Lists.transform(asGeneric, new TypeCastFunction<T>(elementType));
    }
}
