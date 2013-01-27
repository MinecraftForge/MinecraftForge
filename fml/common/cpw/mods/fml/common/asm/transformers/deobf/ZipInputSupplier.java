package cpw.mods.fml.common.asm.transformers.deobf;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.google.common.io.InputSupplier;

public class ZipInputSupplier implements InputSupplier<InputStream> {
    private ZipFile zipFile;
    private ZipEntry zipEntry;

    public ZipInputSupplier(ZipFile zip, ZipEntry entry)
    {
        this.zipFile = zip;
        this.zipEntry = entry;
    }

    @Override
    public InputStream getInput() throws IOException
    {
        return zipFile.getInputStream(zipEntry);
    }

}
