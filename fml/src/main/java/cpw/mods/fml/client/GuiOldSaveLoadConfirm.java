package cpw.mods.fml.client;

import java.io.File;
import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.gui.GuiYesNo;
import org.apache.logging.log4j.Level;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.ZipperUtil;

public class GuiOldSaveLoadConfirm extends GuiYesNo {

    private String dirName;
    private String saveName;
    private File zip;
    public GuiOldSaveLoadConfirm(String dirName, String saveName, GuiScreen parent)
    {
        super(parent, "", "", 0);
        this.dirName = dirName;
        this.saveName = saveName;
        this.zip = new File(FMLClientHandler.instance().getClient().field_71412_D,String.format("%s-%2$td%2$tm%2$ty%2$tH%2$tM%2$tS.zip", saveName, System.currentTimeMillis()));
    }
    @Override
    public void func_73863_a(int p_73863_1_, int p_73863_2_, float p_73863_3_)
    {
        this.func_146276_q_();
        this.func_73732_a(this.field_146289_q, String.format("The world %s contains pre-update modding data", saveName), this.field_146294_l / 2, 50, 16777215);
        this.func_73732_a(this.field_146289_q, String.format("There may be problems updating it to this version"), this.field_146294_l / 2, 70, 16777215);
        this.func_73732_a(this.field_146289_q, String.format("FML will save a zip to %s", zip.getName()), this.field_146294_l / 2, 90, 16777215);
        this.func_73732_a(this.field_146289_q, String.format("Do you wish to continue loading?"), this.field_146294_l / 2, 110, 16777215);
        int k;

        for (k = 0; k < this.field_146292_n.size(); ++k)
        {
            ((GuiButton)this.field_146292_n.get(k)).func_146112_a(this.field_146297_k, p_73863_1_, p_73863_2_);
        }

        for (k = 0; k < this.field_146293_o.size(); ++k)
        {
            ((GuiLabel)this.field_146293_o.get(k)).func_146159_a(this.field_146297_k, p_73863_1_, p_73863_2_);
        }
    }
    @Override
    protected void func_146284_a(GuiButton p_146284_1_)
    {
        if (p_146284_1_.field_146127_k == 1)
        {
            ObfuscationReflectionHelper.setPrivateValue(GuiSelectWorld.class, (GuiSelectWorld)field_146355_a, false, "field_"+"146634_i");
            FMLClientHandler.instance().showGuiScreen(field_146355_a);
        }
        else
        {
            FMLLog.info("Capturing current state of world %s into file %s", saveName, zip.getAbsolutePath());
            try
            {
                ZipperUtil.zip(new File(FMLClientHandler.instance().getSavesDir(), dirName), zip);
            } catch (IOException e)
            {
                FMLLog.log(Level.WARN, e, "There was a problem saving the backup %s. Please fix and try again", zip.getName());
                FMLClientHandler.instance().showGuiScreen(new GuiBackupFailed(field_146355_a, zip));
                return;
            }
            FMLClientHandler.instance().showGuiScreen(null);
            FMLClientHandler.instance().launchIntegratedServerCallback(dirName, saveName);
        }

    }

}
