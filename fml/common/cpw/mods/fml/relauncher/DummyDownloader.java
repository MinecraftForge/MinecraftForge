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

public class DummyDownloader implements IDownloadDisplay
{

    @Override
    public void resetProgress(int sizeGuess)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void setPokeThread(Thread currentThread)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateProgress(int fullLength)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean shouldStopIt()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void updateProgressString(String string, Object... data)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public Object makeDialog()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void makeHeadless()
    {
        // TODO Auto-generated method stub

    }

}
