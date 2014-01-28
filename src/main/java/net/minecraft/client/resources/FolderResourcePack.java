package net.minecraft.client.resources;

import com.google.common.collect.Sets;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.io.filefilter.DirectoryFileFilter;

@SideOnly(Side.CLIENT)
public class FolderResourcePack extends AbstractResourcePack
{
    private static final String __OBFID = "CL_00001076";

    public FolderResourcePack(File par1File)
    {
        super(par1File);
    }

    protected InputStream getInputStreamByName(String par1Str) throws IOException
    {
        return new BufferedInputStream(new FileInputStream(new File(this.resourcePackFile, par1Str)));
    }

    protected boolean hasResourceName(String par1Str)
    {
        return (new File(this.resourcePackFile, par1Str)).isFile();
    }

    public Set getResourceDomains()
    {
        HashSet hashset = Sets.newHashSet();
        File file1 = new File(this.resourcePackFile, "assets/");

        if (file1.isDirectory())
        {
            File[] afile = file1.listFiles((java.io.FileFilter)DirectoryFileFilter.DIRECTORY);
            int i = afile.length;

            for (int j = 0; j < i; ++j)
            {
                File file2 = afile[j];
                String s = getRelativeName(file1, file2);

                if (!s.equals(s.toLowerCase()))
                {
                    this.logNameNotLowercase(s);
                }
                else
                {
                    hashset.add(s.substring(0, s.length() - 1));
                }
            }
        }

        return hashset;
    }
}