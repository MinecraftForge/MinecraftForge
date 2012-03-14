package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.input.Keyboard;

public class GuiMultiplayer extends GuiScreen
{
    /** Number of outstanding ThreadPollServers threads */
    private static int threadsPending = 0;

    /** Lock object for use with synchronized() */
    private static Object lock = new Object();

    /**
     * A reference to the screen object that created this. Used for navigating between screens.
     */
    private GuiScreen parentScreen;

    /** Slot container for the server list */
    private GuiSlotServer serverSlotContainer;

    /** List of ServerNBTStorage objects */
    private List serverList = new ArrayList();

    /** Index of the currently selected server */
    private int selectedServer = -1;

    /** The 'Edit' button */
    private GuiButton buttonEdit;

    /** The 'Join Server' button */
    private GuiButton buttonSelect;

    /** The 'Delete' button */
    private GuiButton buttonDelete;

    /** The 'Delete' button was clicked */
    private boolean deleteClicked = false;

    /** The 'Add server' button was clicked */
    private boolean addClicked = false;

    /** The 'Edit' button was clicked */
    private boolean editClicked = false;

    /** The 'Direct Connect' button was clicked */
    private boolean directClicked = false;
    private String field_35350_v = null;

    /**
     * Temporary ServerNBTStorage used by the Edit/Add/Direct Connect dialogs
     */
    private ServerNBTStorage tempServer = null;

    public GuiMultiplayer(GuiScreen par1GuiScreen)
    {
        this.parentScreen = par1GuiScreen;
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen() {}

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        this.loadServerList();
        Keyboard.enableRepeatEvents(true);
        this.controlList.clear();
        this.serverSlotContainer = new GuiSlotServer(this);
        this.initGuiControls();
    }

    /**
     * Load the server list from servers.dat
     */
    private void loadServerList()
    {
        try
        {
            NBTTagCompound var1 = CompressedStreamTools.read(new File(this.mc.mcDataDir, "servers.dat"));
            NBTTagList var2 = var1.getTagList("servers");
            this.serverList.clear();

            for (int var3 = 0; var3 < var2.tagCount(); ++var3)
            {
                this.serverList.add(ServerNBTStorage.createServerNBTStorage((NBTTagCompound)var2.tagAt(var3)));
            }
        }
        catch (Exception var4)
        {
            var4.printStackTrace();
        }
    }

    /**
     * Save the server list to servers.dat
     */
    private void saveServerList()
    {
        try
        {
            NBTTagList var1 = new NBTTagList();

            for (int var2 = 0; var2 < this.serverList.size(); ++var2)
            {
                var1.appendTag(((ServerNBTStorage)this.serverList.get(var2)).getCompoundTag());
            }

            NBTTagCompound var4 = new NBTTagCompound();
            var4.setTag("servers", var1);
            CompressedStreamTools.safeWrite(var4, new File(this.mc.mcDataDir, "servers.dat"));
        }
        catch (Exception var3)
        {
            var3.printStackTrace();
        }
    }

    /**
     * Populate the GuiScreen controlList
     */
    public void initGuiControls()
    {
        StringTranslate var1 = StringTranslate.getInstance();
        this.controlList.add(this.buttonEdit = new GuiButton(7, this.width / 2 - 154, this.height - 28, 70, 20, var1.translateKey("selectServer.edit")));
        this.controlList.add(this.buttonDelete = new GuiButton(2, this.width / 2 - 74, this.height - 28, 70, 20, var1.translateKey("selectServer.delete")));
        this.controlList.add(this.buttonSelect = new GuiButton(1, this.width / 2 - 154, this.height - 52, 100, 20, var1.translateKey("selectServer.select")));
        this.controlList.add(new GuiButton(4, this.width / 2 - 50, this.height - 52, 100, 20, var1.translateKey("selectServer.direct")));
        this.controlList.add(new GuiButton(3, this.width / 2 + 4 + 50, this.height - 52, 100, 20, var1.translateKey("selectServer.add")));
        this.controlList.add(new GuiButton(8, this.width / 2 + 4, this.height - 28, 70, 20, var1.translateKey("selectServer.refresh")));
        this.controlList.add(new GuiButton(0, this.width / 2 + 4 + 76, this.height - 28, 75, 20, var1.translateKey("gui.cancel")));
        boolean var2 = this.selectedServer >= 0 && this.selectedServer < this.serverSlotContainer.getSize();
        this.buttonSelect.enabled = var2;
        this.buttonEdit.enabled = var2;
        this.buttonDelete.enabled = var2;
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
    }

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        if (par1GuiButton.enabled)
        {
            if (par1GuiButton.id == 2)
            {
                String var2 = ((ServerNBTStorage)this.serverList.get(this.selectedServer)).name;

                if (var2 != null)
                {
                    this.deleteClicked = true;
                    StringTranslate var3 = StringTranslate.getInstance();
                    String var4 = var3.translateKey("selectServer.deleteQuestion");
                    String var5 = "\'" + var2 + "\' " + var3.translateKey("selectServer.deleteWarning");
                    String var6 = var3.translateKey("selectServer.deleteButton");
                    String var7 = var3.translateKey("gui.cancel");
                    GuiYesNo var8 = new GuiYesNo(this, var4, var5, var6, var7, this.selectedServer);
                    this.mc.displayGuiScreen(var8);
                }
            }
            else if (par1GuiButton.id == 1)
            {
                this.joinServer(this.selectedServer);
            }
            else if (par1GuiButton.id == 4)
            {
                this.directClicked = true;
                this.mc.displayGuiScreen(new GuiScreenServerList(this, this.tempServer = new ServerNBTStorage(StatCollector.translateToLocal("selectServer.defaultName"), "")));
            }
            else if (par1GuiButton.id == 3)
            {
                this.addClicked = true;
                this.mc.displayGuiScreen(new GuiScreenAddServer(this, this.tempServer = new ServerNBTStorage(StatCollector.translateToLocal("selectServer.defaultName"), "")));
            }
            else if (par1GuiButton.id == 7)
            {
                this.editClicked = true;
                ServerNBTStorage var9 = (ServerNBTStorage)this.serverList.get(this.selectedServer);
                this.mc.displayGuiScreen(new GuiScreenAddServer(this, this.tempServer = new ServerNBTStorage(var9.name, var9.host)));
            }
            else if (par1GuiButton.id == 0)
            {
                this.mc.displayGuiScreen(this.parentScreen);
            }
            else if (par1GuiButton.id == 8)
            {
                this.mc.displayGuiScreen(new GuiMultiplayer(this.parentScreen));
            }
            else
            {
                this.serverSlotContainer.actionPerformed(par1GuiButton);
            }
        }
    }

    /**
     * Deletes the selected world.
     */
    public void deleteWorld(boolean par1, int par2)
    {
        if (this.deleteClicked)
        {
            this.deleteClicked = false;

            if (par1)
            {
                this.serverList.remove(par2);
                this.saveServerList();
            }

            this.mc.displayGuiScreen(this);
        }
        else if (this.directClicked)
        {
            this.directClicked = false;

            if (par1)
            {
                this.joinServer(this.tempServer);
            }
            else
            {
                this.mc.displayGuiScreen(this);
            }
        }
        else if (this.addClicked)
        {
            this.addClicked = false;

            if (par1)
            {
                this.serverList.add(this.tempServer);
                this.saveServerList();
            }

            this.mc.displayGuiScreen(this);
        }
        else if (this.editClicked)
        {
            this.editClicked = false;

            if (par1)
            {
                ServerNBTStorage var3 = (ServerNBTStorage)this.serverList.get(this.selectedServer);
                var3.name = this.tempServer.name;
                var3.host = this.tempServer.host;
                this.saveServerList();
            }

            this.mc.displayGuiScreen(this);
        }
    }

    private int parseIntWithDefault(String par1Str, int par2)
    {
        try
        {
            return Integer.parseInt(par1Str.trim());
        }
        catch (Exception var4)
        {
            return par2;
        }
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char par1, int par2)
    {
        if (par1 == 13)
        {
            this.actionPerformed((GuiButton)this.controlList.get(2));
        }
    }

    /**
     * Called when the mouse is clicked.
     */
    protected void mouseClicked(int par1, int par2, int par3)
    {
        super.mouseClicked(par1, par2, par3);
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        this.field_35350_v = null;
        StringTranslate var4 = StringTranslate.getInstance();
        this.drawDefaultBackground();
        this.serverSlotContainer.drawScreen(par1, par2, par3);
        this.drawCenteredString(this.fontRenderer, var4.translateKey("multiplayer.title"), this.width / 2, 20, 16777215);
        super.drawScreen(par1, par2, par3);

        if (this.field_35350_v != null)
        {
            this.func_35325_a(this.field_35350_v, par1, par2);
        }
    }

    /**
     * Join server by slot index
     */
    private void joinServer(int par1)
    {
        this.joinServer((ServerNBTStorage)this.serverList.get(par1));
    }

    /**
     * Join server by ServerNBTStorage
     */
    private void joinServer(ServerNBTStorage par1ServerNBTStorage)
    {
        String var2 = par1ServerNBTStorage.host;
        String[] var3 = var2.split(":");

        if (var2.startsWith("["))
        {
            int var4 = var2.indexOf("]");

            if (var4 > 0)
            {
                String var5 = var2.substring(1, var4);
                String var6 = var2.substring(var4 + 1).trim();

                if (var6.startsWith(":") && var6.length() > 0)
                {
                    var6 = var6.substring(1);
                    var3 = new String[] {var5, var6};
                }
                else
                {
                    var3 = new String[] {var5};
                }
            }
        }

        if (var3.length > 2)
        {
            var3 = new String[] {var2};
        }

        this.mc.displayGuiScreen(new GuiConnecting(this.mc, var3[0], var3.length > 1 ? this.parseIntWithDefault(var3[1], 25565) : 25565));
    }

    /**
     * Poll server for MOTD, lag, and player count/max
     */
    private void pollServer(ServerNBTStorage par1ServerNBTStorage) throws IOException
    {
        String var2 = par1ServerNBTStorage.host;
        String[] var3 = var2.split(":");

        if (var2.startsWith("["))
        {
            int var4 = var2.indexOf("]");

            if (var4 > 0)
            {
                String var5 = var2.substring(1, var4);
                String var6 = var2.substring(var4 + 1).trim();

                if (var6.startsWith(":") && var6.length() > 0)
                {
                    var6 = var6.substring(1);
                    var3 = new String[] {var5, var6};
                }
                else
                {
                    var3 = new String[] {var5};
                }
            }
        }

        if (var3.length > 2)
        {
            var3 = new String[] {var2};
        }

        String var29 = var3[0];
        int var30 = var3.length > 1 ? this.parseIntWithDefault(var3[1], 25565) : 25565;
        Socket var31 = null;
        DataInputStream var7 = null;
        DataOutputStream var8 = null;

        try
        {
            var31 = new Socket();
            var31.setSoTimeout(3000);
            var31.setTcpNoDelay(true);
            var31.setTrafficClass(18);
            var31.connect(new InetSocketAddress(var29, var30), 3000);
            var7 = new DataInputStream(var31.getInputStream());
            var8 = new DataOutputStream(var31.getOutputStream());
            var8.write(254);

            if (var7.read() != 255)
            {
                throw new IOException("Bad message");
            }

            String var9 = Packet.readString(var7, 256);
            char[] var10 = var9.toCharArray();
            int var11;

            for (var11 = 0; var11 < var10.length; ++var11)
            {
                if (var10[var11] != 167 && ChatAllowedCharacters.allowedCharacters.indexOf(var10[var11]) < 0)
                {
                    var10[var11] = 63;
                }
            }

            var9 = new String(var10);
            var3 = var9.split("\u00a7");
            var9 = var3[0];
            var11 = -1;
            int var12 = -1;

            try
            {
                var11 = Integer.parseInt(var3[1]);
                var12 = Integer.parseInt(var3[2]);
            }
            catch (Exception var27)
            {
                ;
            }

            par1ServerNBTStorage.motd = "\u00a77" + var9;

            if (var11 >= 0 && var12 > 0)
            {
                par1ServerNBTStorage.playerCount = "\u00a77" + var11 + "\u00a78/\u00a77" + var12;
            }
            else
            {
                par1ServerNBTStorage.playerCount = "\u00a78???";
            }
        }
        finally
        {
            try
            {
                if (var7 != null)
                {
                    var7.close();
                }
            }
            catch (Throwable var26)
            {
                ;
            }

            try
            {
                if (var8 != null)
                {
                    var8.close();
                }
            }
            catch (Throwable var25)
            {
                ;
            }

            try
            {
                if (var31 != null)
                {
                    var31.close();
                }
            }
            catch (Throwable var24)
            {
                ;
            }
        }
    }

    protected void func_35325_a(String par1Str, int par2, int par3)
    {
        if (par1Str != null)
        {
            int var4 = par2 + 12;
            int var5 = par3 - 12;
            int var6 = this.fontRenderer.getStringWidth(par1Str);
            this.drawGradientRect(var4 - 3, var5 - 3, var4 + var6 + 3, var5 + 8 + 3, -1073741824, -1073741824);
            this.fontRenderer.drawStringWithShadow(par1Str, var4, var5, -1);
        }
    }

    /**
     * Return the List of ServerNBTStorage objects
     */
    static List getServerList(GuiMultiplayer par0GuiMultiplayer)
    {
        return par0GuiMultiplayer.serverList;
    }

    /**
     * Set index of the currently selected server
     */
    static int setSelectedServer(GuiMultiplayer par0GuiMultiplayer, int par1)
    {
        return par0GuiMultiplayer.selectedServer = par1;
    }

    /**
     * Return index of the currently selected server
     */
    static int getSelectedServer(GuiMultiplayer par0GuiMultiplayer)
    {
        return par0GuiMultiplayer.selectedServer;
    }

    /**
     * Return buttonSelect GuiButton
     */
    static GuiButton getButtonSelect(GuiMultiplayer par0GuiMultiplayer)
    {
        return par0GuiMultiplayer.buttonSelect;
    }

    /**
     * Return buttonEdit GuiButton
     */
    static GuiButton getButtonEdit(GuiMultiplayer par0GuiMultiplayer)
    {
        return par0GuiMultiplayer.buttonEdit;
    }

    /**
     * Return buttonDelete GuiButton
     */
    static GuiButton getButtonDelete(GuiMultiplayer par0GuiMultiplayer)
    {
        return par0GuiMultiplayer.buttonDelete;
    }

    /**
     * Join server by slot index (called on double click from GuiSlotServer)
     */
    static void joinServer(GuiMultiplayer par0GuiMultiplayer, int par1)
    {
        par0GuiMultiplayer.joinServer(par1);
    }

    /**
     * Get lock object for use with synchronized()
     */
    static Object getLock()
    {
        return lock;
    }

    /**
     * Return number of outstanding ThreadPollServers threads
     */
    static int getThreadsPending()
    {
        return threadsPending;
    }

    /**
     * Increment number of outstanding ThreadPollServers threads by 1
     */
    static int incrementThreadsPending()
    {
        return threadsPending++;
    }

    /**
     * Poll server for MOTD, lag, and player count/max
     */
    static void pollServer(GuiMultiplayer par0GuiMultiplayer, ServerNBTStorage par1ServerNBTStorage) throws IOException
    {
        par0GuiMultiplayer.pollServer(par1ServerNBTStorage);
    }

    /**
     * Decrement number of outstanding ThreadPollServers threads by 1
     */
    static int decrementThreadsPending()
    {
        return threadsPending--;
    }

    static String func_35327_a(GuiMultiplayer par0GuiMultiplayer, String par1Str)
    {
        return par0GuiMultiplayer.field_35350_v = par1Str;
    }
}
