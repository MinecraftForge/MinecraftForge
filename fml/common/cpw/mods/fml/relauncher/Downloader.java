package cpw.mods.fml.relauncher;

import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Box;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

import cpw.mods.fml.common.FMLLog;

public class Downloader extends JOptionPane implements IDownloadDisplay
{
    private JDialog container;
    private JLabel currentActivity;
    private JProgressBar progress;
    boolean stopIt;
    Thread pokeThread;

    private Box makeProgressPanel()
    {
        Box box = Box.createVerticalBox();
        box.add(Box.createRigidArea(new Dimension(0,10)));
        JLabel welcomeLabel = new JLabel("<html><b><font size='+1'>FML is setting up your minecraft environment</font></b></html>");
        box.add(welcomeLabel);
        welcomeLabel.setAlignmentY(LEFT_ALIGNMENT);
        welcomeLabel = new JLabel("<html>Please wait, FML has some tasks to do before you can play</html>");
        welcomeLabel.setAlignmentY(LEFT_ALIGNMENT);
        box.add(welcomeLabel);
        box.add(Box.createRigidArea(new Dimension(0,10)));
        currentActivity = new JLabel("Currently doing ...");
        box.add(currentActivity);
        box.add(Box.createRigidArea(new Dimension(0,10)));
        progress = new JProgressBar(0, 100);
        progress.setStringPainted(true);
        box.add(progress);
        box.add(Box.createRigidArea(new Dimension(0,30)));
        return box;
    }

    public JDialog makeDialog()
    {
        setMessageType(JOptionPane.INFORMATION_MESSAGE);
        setMessage(makeProgressPanel());
        setOptions(new Object[] { "Stop" });
        addPropertyChangeListener(new PropertyChangeListener()
        {
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                if (evt.getSource() == Downloader.this && evt.getPropertyName()==VALUE_PROPERTY)
                {
                    requestClose("This will stop minecraft from launching\nAre you sure you want to do this?");
                }
            }
        });
        container = new JDialog(null, "Hello", ModalityType.MODELESS);
        container.setResizable(false);
        container.setLocationRelativeTo(null);
        container.add(this);
        this.updateUI();
        container.pack();
        container.setMinimumSize(container.getPreferredSize());
        container.setVisible(true);
        container.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        container.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                requestClose("Closing this window will stop minecraft from launching\nAre you sure you wish to do this?");
            }
        });
        return container;
    }
    protected void requestClose(String message)
    {
        int shouldClose = JOptionPane.showConfirmDialog(container, message, "Are you sure you want to stop?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (shouldClose == JOptionPane.YES_OPTION)
        {
            container.dispose();
        }
        stopIt = true;
        if (pokeThread != null)
        {
            pokeThread.interrupt();
        }
    }

    public void updateProgressString(String progressUpdate, Object... data)
    {
        FMLLog.finest(progressUpdate, data);
        if (currentActivity!=null)
        {
            currentActivity.setText(String.format(progressUpdate,data));
        }
    }

    public void resetProgress(int sizeGuess)
    {
        if (progress!=null)
        {
            progress.getModel().setRangeProperties(0, 0, 0, sizeGuess, false);
        }
    }

    public void updateProgress(int fullLength)
    {
        if (progress!=null)
        {
            progress.getModel().setValue(fullLength);
        }
    }

    public void makeHeadless()
    {
        container = null;
        progress = null;
        currentActivity = null;
    }

    @Override
    public void setPokeThread(Thread currentThread)
    {
        this.pokeThread = currentThread;
    }

    @Override
    public boolean shouldStopIt()
    {
        return stopIt;
    }
}
