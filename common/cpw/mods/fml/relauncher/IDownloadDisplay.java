package cpw.mods.fml.relauncher;

import javax.swing.JDialog;

public interface IDownloadDisplay
{

    void resetProgress(int sizeGuess);

    void setPokeThread(Thread currentThread);

    void updateProgress(int fullLength);

    boolean shouldStopIt();

    void updateProgressString(String string, Object ... data);

    Object makeDialog();

    void makeHeadless();

}