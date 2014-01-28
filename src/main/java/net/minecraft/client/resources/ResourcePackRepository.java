package net.minecraft.client.resources;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreenWorking;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.client.resources.data.PackMetadataSection;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.HttpUtil;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;

@SideOnly(Side.CLIENT)
public class ResourcePackRepository
{
    protected static final FileFilter resourcePackFilter = new FileFilter()
    {
        private static final String __OBFID = "CL_00001088";
        public boolean accept(File par1File)
        {
            boolean flag = par1File.isFile() && par1File.getName().endsWith(".zip");
            boolean flag1 = par1File.isDirectory() && (new File(par1File, "pack.mcmeta")).isFile();
            return flag || flag1;
        }
    };
    private final File dirResourcepacks;
    public final IResourcePack rprDefaultResourcePack;
    private final File field_148534_e;
    public final IMetadataSerializer rprMetadataSerializer;
    private IResourcePack field_148532_f;
    private boolean field_148533_g;
    private List repositoryEntriesAll = Lists.newArrayList();
    private List repositoryEntries = Lists.newArrayList();
    private static final String __OBFID = "CL_00001087";

    public ResourcePackRepository(File p_i45101_1_, File p_i45101_2_, IResourcePack p_i45101_3_, IMetadataSerializer p_i45101_4_, GameSettings p_i45101_5_)
    {
        this.dirResourcepacks = p_i45101_1_;
        this.field_148534_e = p_i45101_2_;
        this.rprDefaultResourcePack = p_i45101_3_;
        this.rprMetadataSerializer = p_i45101_4_;
        this.fixDirResourcepacks();
        this.updateRepositoryEntriesAll();
        Iterator iterator = p_i45101_5_.field_151453_l.iterator();

        while (iterator.hasNext())
        {
            String s = (String)iterator.next();
            Iterator iterator1 = this.repositoryEntriesAll.iterator();

            while (iterator1.hasNext())
            {
                ResourcePackRepository.Entry entry = (ResourcePackRepository.Entry)iterator1.next();

                if (entry.getResourcePackName().equals(s))
                {
                    this.repositoryEntries.add(entry);
                    break;
                }
            }
        }
    }

    private void fixDirResourcepacks()
    {
        if (!this.dirResourcepacks.isDirectory())
        {
            this.dirResourcepacks.delete();
            this.dirResourcepacks.mkdirs();
        }
    }

    private List getResourcePackFiles()
    {
        return this.dirResourcepacks.isDirectory() ? Arrays.asList(this.dirResourcepacks.listFiles(resourcePackFilter)) : Collections.emptyList();
    }

    public void updateRepositoryEntriesAll()
    {
        ArrayList arraylist = Lists.newArrayList();
        Iterator iterator = this.getResourcePackFiles().iterator();

        while (iterator.hasNext())
        {
            File file1 = (File)iterator.next();
            ResourcePackRepository.Entry entry = new ResourcePackRepository.Entry(file1, null);

            if (!this.repositoryEntriesAll.contains(entry))
            {
                try
                {
                    entry.updateResourcePack();
                    arraylist.add(entry);
                }
                catch (Exception exception)
                {
                    arraylist.remove(entry);
                }
            }
            else
            {
                int i = this.repositoryEntriesAll.indexOf(entry);

                if (i > -1 && i < this.repositoryEntriesAll.size())
                {
                    arraylist.add(this.repositoryEntriesAll.get(i));
                }
            }
        }

        this.repositoryEntriesAll.removeAll(arraylist);
        iterator = this.repositoryEntriesAll.iterator();

        while (iterator.hasNext())
        {
            ResourcePackRepository.Entry entry1 = (ResourcePackRepository.Entry)iterator.next();
            entry1.closeResourcePack();
        }

        this.repositoryEntriesAll = arraylist;
    }

    public List getRepositoryEntriesAll()
    {
        return ImmutableList.copyOf(this.repositoryEntriesAll);
    }

    public List getRepositoryEntries()
    {
        return ImmutableList.copyOf(this.repositoryEntries);
    }

    public void func_148527_a(List p_148527_1_)
    {
        this.repositoryEntries.clear();
        this.repositoryEntries.addAll(p_148527_1_);
    }

    public File getDirResourcepacks()
    {
        return this.dirResourcepacks;
    }

    public void func_148526_a(String p_148526_1_)
    {
        String s1 = p_148526_1_.substring(p_148526_1_.lastIndexOf("/") + 1);

        if (s1.contains("?"))
        {
            s1 = s1.substring(0, s1.indexOf("?"));
        }

        if (s1.endsWith(".zip"))
        {
            File file1 = new File(this.field_148534_e, s1.replaceAll("\\W", ""));
            this.func_148529_f();
            this.func_148528_a(p_148526_1_, file1);
        }
    }

    private void func_148528_a(String p_148528_1_, File p_148528_2_)
    {
        HashMap hashmap = Maps.newHashMap();
        GuiScreenWorking guiscreenworking = new GuiScreenWorking();
        hashmap.put("X-Minecraft-Username", Minecraft.getMinecraft().getSession().getUsername());
        hashmap.put("X-Minecraft-UUID", Minecraft.getMinecraft().getSession().func_148255_b());
        hashmap.put("X-Minecraft-Version", "1.7.2");
        this.field_148533_g = true;
        Minecraft.getMinecraft().func_147108_a(guiscreenworking);
        HttpUtil.func_151223_a(p_148528_2_, p_148528_1_, new HttpUtil.DownloadListener()
        {
            private static final String __OBFID = "CL_00001089";
            public void func_148522_a(File p_148522_1_)
            {
                if (ResourcePackRepository.this.field_148533_g)
                {
                    ResourcePackRepository.this.field_148533_g = false;
                    ResourcePackRepository.this.field_148532_f = new FileResourcePack(p_148522_1_);
                    Minecraft.getMinecraft().func_147106_B();
                }
            }
        }, hashmap, 52428800, guiscreenworking, Minecraft.getMinecraft().getProxy());
    }

    public IResourcePack func_148530_e()
    {
        return this.field_148532_f;
    }

    public void func_148529_f()
    {
        this.field_148532_f = null;
        this.field_148533_g = false;
    }

    @SideOnly(Side.CLIENT)
    public class Entry
    {
        private final File resourcePackFile;
        private IResourcePack reResourcePack;
        private PackMetadataSection rePackMetadataSection;
        private BufferedImage texturePackIcon;
        private ResourceLocation locationTexturePackIcon;
        private static final String __OBFID = "CL_00001090";

        private Entry(File par2File)
        {
            this.resourcePackFile = par2File;
        }

        public void updateResourcePack() throws IOException
        {
            this.reResourcePack = (IResourcePack)(this.resourcePackFile.isDirectory() ? new FolderResourcePack(this.resourcePackFile) : new FileResourcePack(this.resourcePackFile));
            this.rePackMetadataSection = (PackMetadataSection)this.reResourcePack.getPackMetadata(ResourcePackRepository.this.rprMetadataSerializer, "pack");

            try
            {
                this.texturePackIcon = this.reResourcePack.getPackImage();
            }
            catch (IOException ioexception)
            {
                ;
            }

            if (this.texturePackIcon == null)
            {
                this.texturePackIcon = ResourcePackRepository.this.rprDefaultResourcePack.getPackImage();
            }

            this.closeResourcePack();
        }

        public void bindTexturePackIcon(TextureManager par1TextureManager)
        {
            if (this.locationTexturePackIcon == null)
            {
                this.locationTexturePackIcon = par1TextureManager.getDynamicTextureLocation("texturepackicon", new DynamicTexture(this.texturePackIcon));
            }

            par1TextureManager.bindTexture(this.locationTexturePackIcon);
        }

        public void closeResourcePack()
        {
            if (this.reResourcePack instanceof Closeable)
            {
                IOUtils.closeQuietly((Closeable)this.reResourcePack);
            }
        }

        public IResourcePack getResourcePack()
        {
            return this.reResourcePack;
        }

        public String getResourcePackName()
        {
            return this.reResourcePack.getPackName();
        }

        public String getTexturePackDescription()
        {
            return this.rePackMetadataSection == null ? EnumChatFormatting.RED + "Invalid pack.mcmeta (or missing \'pack\' section)" : this.rePackMetadataSection.getPackDescription();
        }

        public boolean equals(Object par1Obj)
        {
            return this == par1Obj ? true : (par1Obj instanceof ResourcePackRepository.Entry ? this.toString().equals(par1Obj.toString()) : false);
        }

        public int hashCode()
        {
            return this.toString().hashCode();
        }

        public String toString()
        {
            return String.format("%s:%s:%d", new Object[] {this.resourcePackFile.getName(), this.resourcePackFile.isDirectory() ? "folder" : "zip", Long.valueOf(this.resourcePackFile.lastModified())});
        }

        Entry(File par2File, Object par3ResourcePackRepositoryFilter)
        {
            this(par2File);
        }
    }
}