package net.minecraftforge.fml.client;

import java.io.File;
import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.StartupQuery;
import net.minecraftforge.fml.common.ZipperUtil;

import org.apache.logging.log4j.Level;

public class GuiOldSaveLoadConfirm extends GuiYesNo implements GuiYesNoCallback {

    private String dirName;
    private String saveName;
    private File zip;
    private GuiScreen parent;
    public GuiOldSaveLoadConfirm(String dirName, String saveName, GuiScreen parent)
    {
        super(null, "", "", 0);
        this.parent = parent;
        this.dirName = dirName;
        this.saveName = saveName;
        this.zip = new File(FMLClientHandler.instance().getClient().mcDataDir,String.format("%s-%2$td%2$tm%2$ty%2$tH%2$tM%2$tS.zip", dirName, System.currentTimeMillis()));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, String.format("The world %s contains pre-update modding data", saveName), this.width / 2, 50, 16777215);
        this.drawCenteredString(this.fontRendererObj, String.format("There may be problems updating it to this version"), this.width / 2, 70, 16777215);
        this.drawCenteredString(this.fontRendererObj, String.format("FML will save a zip to %s", zip.getName()), this.width / 2, 90, 16777215);
        this.drawCenteredString(this.fontRendererObj, String.format("Do you wish to continue loading?"), this.width / 2, 110, 16777215);
        int k;

        for (k = 0; k < this.buttonList.size(); ++k)
        {
            ((GuiButton)this.buttonList.get(k)).drawButton(this.mc, mouseX, mouseY);
        }

        for (k = 0; k < this.labelList.size(); ++k)
        {
            ((GuiLabel)this.labelList.get(k)).drawLabel(this.mc, mouseX, mouseY);
        }
    }
    @Override
    protected void actionPerformed(GuiButton button)
    {
        if (button.id == 1)
        {
            ObfuscationReflectionHelper.setPrivateValue(GuiSelectWorld.class, (GuiSelectWorld)parentScreen, false, "field_"+"146634_i");
            FMLClientHandler.instance().showGuiScreen(parent);
        }
        else
        {
            FMLLog.info("Capturing current state of world %s into file %s", saveName, zip.getAbsolutePath());
            try
            {
                String skip = System.getProperty("fml.doNotBackup");
                if (skip == null || !"true".equals(skip))
                {
                    ZipperUtil.zip(new File(FMLClientHandler.instance().getSavesDir(), dirName), zip);
                }
                else
                {
                    for (int x = 0; x < 10; x++)
                        FMLLog.severe("!!!!!!!!!! UPDATING WORLD WITHOUT DOING BACKUP !!!!!!!!!!!!!!!!");
                }
            } catch (IOException e)
            {
                FMLLog.log(Level.WARN, e, "There was a problem saving the backup %s. Please fix and try again", zip.getName());
                FMLClientHandler.instance().showGuiScreen(new GuiBackupFailed(parent, zip));
                return;
            }
            FMLClientHandler.instance().showGuiScreen(null);

            try
            {
                mc.launchIntegratedServer(dirName, saveName, (WorldSettings)null);
            }
            catch (StartupQuery.AbortedException e)
            {
                // ignore
            }
        }
    }
}
