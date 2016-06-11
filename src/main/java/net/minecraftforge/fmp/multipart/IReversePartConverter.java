package net.minecraftforge.fmp.multipart;

/**
 * Interface used to allow reverse conversion of multiparts into blocks.
 *
 * @see IMultipart
 * @see MultipartRegistry
 */
public interface IReversePartConverter
{
    
    /**
     * Called when a part changes in a container. Check if the parts that will be converted into blocks are in the
     * container and <b>NO OTHERS<b/>, and if so, convert it to a block and return true.
     */
    public boolean convertToBlock(IMultipartContainer container);

}