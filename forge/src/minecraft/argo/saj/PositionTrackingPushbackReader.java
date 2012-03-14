package argo.saj;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;

final class PositionTrackingPushbackReader implements ThingWithPosition
{
    private final PushbackReader pushbackReader;
    private int characterCount = 0;
    private int lineCount = 1;
    private boolean lastCharacterWasCarriageReturn = false;

    public PositionTrackingPushbackReader(Reader par1Reader)
    {
        this.pushbackReader = new PushbackReader(par1Reader);
    }

    public void unread(char par1) throws IOException
    {
        --this.characterCount;

        if (this.characterCount < 0)
        {
            this.characterCount = 0;
        }

        this.pushbackReader.unread(par1);
    }

    public void uncount(char[] par1ArrayOfCharacter)
    {
        this.characterCount -= par1ArrayOfCharacter.length;

        if (this.characterCount < 0)
        {
            this.characterCount = 0;
        }
    }

    public int read() throws IOException
    {
        int var1 = this.pushbackReader.read();
        this.updateCharacterAndLineCounts(var1);
        return var1;
    }

    public int read(char[] par1ArrayOfCharacter) throws IOException
    {
        int var2 = this.pushbackReader.read(par1ArrayOfCharacter);
        char[] var3 = par1ArrayOfCharacter;
        int var4 = par1ArrayOfCharacter.length;

        for (int var5 = 0; var5 < var4; ++var5)
        {
            char var6 = var3[var5];
            this.updateCharacterAndLineCounts(var6);
        }

        return var2;
    }

    private void updateCharacterAndLineCounts(int par1)
    {
        if (13 == par1)
        {
            this.characterCount = 0;
            ++this.lineCount;
            this.lastCharacterWasCarriageReturn = true;
        }
        else
        {
            if (10 == par1 && !this.lastCharacterWasCarriageReturn)
            {
                this.characterCount = 0;
                ++this.lineCount;
            }
            else
            {
                ++this.characterCount;
            }

            this.lastCharacterWasCarriageReturn = false;
        }
    }

    public int getColumn()
    {
        return this.characterCount;
    }

    public int getRow()
    {
        return this.lineCount;
    }
}
