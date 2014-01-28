package net.minecraft.server.gui;

import com.mojang.util.QueueLogAppender;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SideOnly(Side.SERVER)
public class MinecraftServerGui extends JComponent
{
    private static final Font field_164249_a = new Font("Monospaced", 0, 12);
    private static final Logger field_164248_b = LogManager.getLogger();
    private DedicatedServer field_120021_b;
    private static final String __OBFID = "CL_00001789";

    public static void func_120016_a(final DedicatedServer par0DedicatedServer)
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception exception)
        {
            ;
        }

        MinecraftServerGui minecraftservergui = new MinecraftServerGui(par0DedicatedServer);
        JFrame jframe = new JFrame("Minecraft server");
        jframe.add(minecraftservergui);
        jframe.pack();
        jframe.setLocationRelativeTo((Component)null);
        jframe.setVisible(true);
        jframe.addWindowListener(new WindowAdapter()
        {
            private static final String __OBFID = "CL_00001791";
            public void windowClosing(WindowEvent windowevent)
            {
                par0DedicatedServer.initiateShutdown();

                while (!par0DedicatedServer.isServerStopped())
                {
                    try
                    {
                        Thread.sleep(100L);
                    }
                    catch (InterruptedException interruptedexception)
                    {
                        interruptedexception.printStackTrace();
                    }
                }

                System.exit(0);
            }
        });
    }

    public MinecraftServerGui(DedicatedServer par1DedicatedServer)
    {
        this.field_120021_b = par1DedicatedServer;
        this.setPreferredSize(new Dimension(854, 480));
        this.setLayout(new BorderLayout());

        try
        {
            this.add(this.func_120018_d(), "Center");
            this.add(this.func_120019_b(), "West");
        }
        catch (Exception exception)
        {
            field_164248_b.error("Couldn\'t build server GUI", exception);
        }
    }

    private JComponent func_120019_b()
    {
        JPanel jpanel = new JPanel(new BorderLayout());
        jpanel.add(new StatsComponent(this.field_120021_b), "North");
        jpanel.add(this.func_120020_c(), "Center");
        jpanel.setBorder(new TitledBorder(new EtchedBorder(), "Stats"));
        return jpanel;
    }

    private JComponent func_120020_c()
    {
        PlayerListComponent playerlistcomponent = new PlayerListComponent(this.field_120021_b);
        JScrollPane jscrollpane = new JScrollPane(playerlistcomponent, 22, 30);
        jscrollpane.setBorder(new TitledBorder(new EtchedBorder(), "Players"));
        return jscrollpane;
    }

            public void focusGained(FocusEvent var1) {}
    private JComponent func_120018_d()
    {
        JPanel jpanel = new JPanel(new BorderLayout());
        final JTextArea jtextarea = new JTextArea();
        final JScrollPane jscrollpane = new JScrollPane(jtextarea, 22, 30);
        jtextarea.setEditable(false);
        jtextarea.setFont(field_164249_a);
        final JTextField jtextfield = new JTextField();
        jtextfield.addActionListener(new ActionListener()
        {
            private static final String __OBFID = "CL_00001790";
            public void actionPerformed(ActionEvent actionevent)
            {
                String s = jtextfield.getText().trim();

                if (s.length() > 0)
                {
                    MinecraftServerGui.this.field_120021_b.addPendingCommand(s, MinecraftServer.getServer());
                }

                jtextfield.setText("");
            }
        });
        jtextarea.addFocusListener(new FocusAdapter()
        {
            private static final String __OBFID = "CL_00001794";
            public void focusGained(FocusEvent focusevent) {}
        });
        jpanel.add(jscrollpane, "Center");
        jpanel.add(jtextfield, "South");
        jpanel.setBorder(new TitledBorder(new EtchedBorder(), "Log and chat"));
        Thread thread = new Thread(new Runnable()
        {
            private static final String __OBFID = "CL_00001793";
            public void run()
            {
                String s;

                while ((s = QueueLogAppender.getNextLogEvent("ServerGuiConsole")) != null)
                {
                    MinecraftServerGui.this.func_164247_a(jtextarea, jscrollpane, s);
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
        return jpanel;
    }

    public void func_164247_a(final JTextArea p_164247_1_, final JScrollPane p_164247_2_, final String p_164247_3_)
    {
        if (!SwingUtilities.isEventDispatchThread())
        {
            SwingUtilities.invokeLater(new Runnable()
            {
                private static final String __OBFID = "CL_00001792";
                public void run()
                {
                    MinecraftServerGui.this.func_164247_a(p_164247_1_, p_164247_2_, p_164247_3_);
                }
            });
        }
        else
        {
            Document document = p_164247_1_.getDocument();
            JScrollBar jscrollbar = p_164247_2_.getVerticalScrollBar();
            boolean flag = false;

            if (p_164247_2_.getViewport().getView() == p_164247_1_)
            {
                flag = (double)jscrollbar.getValue() + jscrollbar.getSize().getHeight() + (double)(field_164249_a.getSize() * 4) > (double)jscrollbar.getMaximum();
            }

            try
            {
                document.insertString(document.getLength(), p_164247_3_, (AttributeSet)null);
            }
            catch (BadLocationException badlocationexception)
            {
                ;
            }

            if (flag)
            {
                jscrollbar.setValue(Integer.MAX_VALUE);
            }
        }
    }
}