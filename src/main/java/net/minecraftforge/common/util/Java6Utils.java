package net.minecraftforge.common.util;

import org.apache.commons.io.IOUtils;

import javax.annotation.Nullable;
import java.io.Closeable;
import java.io.IOException;
import java.util.zip.ZipFile;

public class Java6Utils
{
    /**
     * {@link ZipFile} does not implement {@link Closeable} on Java 6.
     * This method is the same as {@link IOUtils#closeQuietly(Closeable)} but works on {@link ZipFile} on Java 6.
     */
    public static void closeZipQuietly(@Nullable ZipFile file)
    {
        try
        {
            if (file != null)
            {
                file.close();
            }
        }
        catch (IOException ioe)
        {
            // ignore
        }
    }
}
