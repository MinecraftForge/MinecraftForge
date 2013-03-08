/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     cpw - implementation
 */

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
