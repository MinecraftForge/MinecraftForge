package net.minecraft.src;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import net.minecraft.server.MinecraftServer;

public class ServerGUI extends JComponent implements ICommandListener
{
    /** Reference to the logger. */
    public static Logger logger = Logger.getLogger("Minecraft");

    /** Reference to the MinecraftServer object. */
    private MinecraftServer mcServer;

    /**
     * Initialises the GUI components.
     */
    public static void initGui(MinecraftServer par0MinecraftServer)
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception var3)
        {
            ;
        }

        ServerGUI var1 = new ServerGUI(par0MinecraftServer);
        JFrame var2 = new JFrame("Minecraft server");
        var2.add(var1);
        var2.pack();
        var2.setLocationRelativeTo((Component)null);
        var2.setVisible(true);
        var2.addWindowListener(new ServerWindowAdapter(par0MinecraftServer));
    }

    public ServerGUI(MinecraftServer par1MinecraftServer)
    {
        this.mcServer = par1MinecraftServer;
        this.setPreferredSize(new Dimension(854, 480));
        this.setLayout(new BorderLayout());

        try
        {
            this.add(this.getLogComponent(), "Center");
            this.add(this.getStatsComponent(), "West");
        }
        catch (Exception var3)
        {
            var3.printStackTrace();
        }
    }

    /**
     * Returns a new JPanel with a new GuiStatsComponent inside.
     */
    private JComponent getStatsComponent()
    {
        JPanel var1 = new JPanel(new BorderLayout());
        var1.add(new GuiStatsComponent(this.mcServer), "North");
        var1.add(this.getPlayerListComponent(), "Center");
        var1.setBorder(new TitledBorder(new EtchedBorder(), "Stats"));
        return var1;
    }

    /**
     * Returns a new JScrollPane with a new PlayerListBox inside.
     */
    private JComponent getPlayerListComponent()
    {
        PlayerListBox var1 = new PlayerListBox(this.mcServer);
        JScrollPane var2 = new JScrollPane(var1, 22, 30);
        var2.setBorder(new TitledBorder(new EtchedBorder(), "Players"));
        return var2;
    }

    /**
     * Returns a new JPanel with a new GuiStatsComponent inside.
     */
    private JComponent getLogComponent()
    {
        JPanel var1 = new JPanel(new BorderLayout());
        JTextArea var2 = new JTextArea();
        logger.addHandler(new GuiLogOutputHandler(var2));
        JScrollPane var3 = new JScrollPane(var2, 22, 30);
        var2.setEditable(false);
        JTextField var4 = new JTextField();
        var4.addActionListener(new ServerGuiCommandListener(this, var4));
        var2.addFocusListener(new ServerGuiFocusAdapter(this));
        var1.add(var3, "Center");
        var1.add(var4, "South");
        var1.setBorder(new TitledBorder(new EtchedBorder(), "Log and chat"));
        return var1;
    }

    /**
     * Logs the message with a level of INFO.
     */
    public void log(String par1Str)
    {
        logger.info(par1Str);
    }

    /**
     * Gets the players username.
     */
    public String getUsername()
    {
        return "CONSOLE";
    }

    /**
     * Returns the MinecraftServer associated with the ServerGui.
     */
    static MinecraftServer getMinecraftServer(ServerGUI par0ServerGUI)
    {
        return par0ServerGUI.mcServer;
    }
}
