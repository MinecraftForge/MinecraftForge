package argo.saj;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;

public final class SajParser
{
    public void parse(Reader par1Reader, JsonListener par2JsonListener) throws InvalidSyntaxException, IOException
    {
        PositionTrackingPushbackReader var3 = new PositionTrackingPushbackReader(par1Reader);
        char var4 = (char)var3.read();

        switch (var4)
        {
            case 91:
                var3.unread(var4);
                par2JsonListener.startDocument();
                this.arrayString(var3, par2JsonListener);
                break;

            case 123:
                var3.unread(var4);
                par2JsonListener.startDocument();
                this.objectString(var3, par2JsonListener);
                break;

            default:
                throw new InvalidSyntaxException("Expected either [ or { but got [" + var4 + "].", var3);
        }

        int var5 = this.readNextNonWhitespaceChar(var3);

        if (var5 != -1)
        {
            throw new InvalidSyntaxException("Got unexpected trailing character [" + (char)var5 + "].", var3);
        }
        else
        {
            par2JsonListener.endDocument();
        }
    }

    private void arrayString(PositionTrackingPushbackReader par1PositionTrackingPushbackReader, JsonListener par2JsonListener) throws InvalidSyntaxException, IOException
    {
        char var3 = (char)this.readNextNonWhitespaceChar(par1PositionTrackingPushbackReader);

        if (var3 != 91)
        {
            throw new InvalidSyntaxException("Expected object to start with [ but got [" + var3 + "].", par1PositionTrackingPushbackReader);
        }
        else
        {
            par2JsonListener.startArray();
            char var4 = (char)this.readNextNonWhitespaceChar(par1PositionTrackingPushbackReader);
            par1PositionTrackingPushbackReader.unread(var4);

            if (var4 != 93)
            {
                this.aJsonValue(par1PositionTrackingPushbackReader, par2JsonListener);
            }

            boolean var5 = false;

            while (!var5)
            {
                char var6 = (char)this.readNextNonWhitespaceChar(par1PositionTrackingPushbackReader);

                switch (var6)
                {
                    case 44:
                        this.aJsonValue(par1PositionTrackingPushbackReader, par2JsonListener);
                        break;

                    case 93:
                        var5 = true;
                        break;

                    default:
                        throw new InvalidSyntaxException("Expected either , or ] but got [" + var6 + "].", par1PositionTrackingPushbackReader);
                }
            }

            par2JsonListener.endArray();
        }
    }

    private void objectString(PositionTrackingPushbackReader par1PositionTrackingPushbackReader, JsonListener par2JsonListener) throws InvalidSyntaxException, IOException
    {
        char var3 = (char)this.readNextNonWhitespaceChar(par1PositionTrackingPushbackReader);

        if (var3 != 123)
        {
            throw new InvalidSyntaxException("Expected object to start with { but got [" + var3 + "].", par1PositionTrackingPushbackReader);
        }
        else
        {
            par2JsonListener.startObject();
            char var4 = (char)this.readNextNonWhitespaceChar(par1PositionTrackingPushbackReader);
            par1PositionTrackingPushbackReader.unread(var4);

            if (var4 != 125)
            {
                this.aFieldToken(par1PositionTrackingPushbackReader, par2JsonListener);
            }

            boolean var5 = false;

            while (!var5)
            {
                char var6 = (char)this.readNextNonWhitespaceChar(par1PositionTrackingPushbackReader);

                switch (var6)
                {
                    case 44:
                        this.aFieldToken(par1PositionTrackingPushbackReader, par2JsonListener);
                        break;

                    case 125:
                        var5 = true;
                        break;

                    default:
                        throw new InvalidSyntaxException("Expected either , or } but got [" + var6 + "].", par1PositionTrackingPushbackReader);
                }
            }

            par2JsonListener.endObject();
        }
    }

    private void aFieldToken(PositionTrackingPushbackReader par1PositionTrackingPushbackReader, JsonListener par2JsonListener) throws InvalidSyntaxException, IOException
    {
        char var3 = (char)this.readNextNonWhitespaceChar(par1PositionTrackingPushbackReader);

        if (34 != var3)
        {
            throw new InvalidSyntaxException("Expected object identifier to begin with [\"] but got [" + var3 + "].", par1PositionTrackingPushbackReader);
        }
        else
        {
            par1PositionTrackingPushbackReader.unread(var3);
            par2JsonListener.startField(this.stringToken(par1PositionTrackingPushbackReader));
            char var4 = (char)this.readNextNonWhitespaceChar(par1PositionTrackingPushbackReader);

            if (var4 != 58)
            {
                throw new InvalidSyntaxException("Expected object identifier to be followed by : but got [" + var4 + "].", par1PositionTrackingPushbackReader);
            }
            else
            {
                this.aJsonValue(par1PositionTrackingPushbackReader, par2JsonListener);
                par2JsonListener.endField();
            }
        }
    }

    private void aJsonValue(PositionTrackingPushbackReader par1PositionTrackingPushbackReader, JsonListener par2JsonListener) throws InvalidSyntaxException, IOException
    {
        char var3 = (char)this.readNextNonWhitespaceChar(par1PositionTrackingPushbackReader);

        switch (var3)
        {
            case 34:
                par1PositionTrackingPushbackReader.unread(var3);
                par2JsonListener.stringValue(this.stringToken(par1PositionTrackingPushbackReader));
                break;

            case 45:
            case 48:
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
                par1PositionTrackingPushbackReader.unread(var3);
                par2JsonListener.numberValue(this.numberToken(par1PositionTrackingPushbackReader));
                break;

            case 91:
                par1PositionTrackingPushbackReader.unread(var3);
                this.arrayString(par1PositionTrackingPushbackReader, par2JsonListener);
                break;

            case 102:
                char[] var6 = new char[4];
                int var7 = par1PositionTrackingPushbackReader.read(var6);

                if (var7 != 4 || var6[0] != 97 || var6[1] != 108 || var6[2] != 115 || var6[3] != 101)
                {
                    par1PositionTrackingPushbackReader.uncount(var6);
                    throw new InvalidSyntaxException("Expected \'f\' to be followed by [[a, l, s, e]], but got [" + Arrays.toString(var6) + "].", par1PositionTrackingPushbackReader);
                }

                par2JsonListener.falseValue();
                break;

            case 110:
                char[] var8 = new char[3];
                int var9 = par1PositionTrackingPushbackReader.read(var8);

                if (var9 != 3 || var8[0] != 117 || var8[1] != 108 || var8[2] != 108)
                {
                    par1PositionTrackingPushbackReader.uncount(var8);
                    throw new InvalidSyntaxException("Expected \'n\' to be followed by [[u, l, l]], but got [" + Arrays.toString(var8) + "].", par1PositionTrackingPushbackReader);
                }

                par2JsonListener.nullValue();
                break;

            case 116:
                char[] var4 = new char[3];
                int var5 = par1PositionTrackingPushbackReader.read(var4);

                if (var5 != 3 || var4[0] != 114 || var4[1] != 117 || var4[2] != 101)
                {
                    par1PositionTrackingPushbackReader.uncount(var4);
                    throw new InvalidSyntaxException("Expected \'t\' to be followed by [[r, u, e]], but got [" + Arrays.toString(var4) + "].", par1PositionTrackingPushbackReader);
                }

                par2JsonListener.trueValue();
                break;

            case 123:
                par1PositionTrackingPushbackReader.unread(var3);
                this.objectString(par1PositionTrackingPushbackReader, par2JsonListener);
                break;

            default:
                throw new InvalidSyntaxException("Invalid character at start of value [" + var3 + "].", par1PositionTrackingPushbackReader);
        }
    }

    private String numberToken(PositionTrackingPushbackReader par1PositionTrackingPushbackReader) throws IOException, InvalidSyntaxException
    {
        StringBuilder var2 = new StringBuilder();
        char var3 = (char)par1PositionTrackingPushbackReader.read();

        if (45 == var3)
        {
            var2.append('-');
        }
        else
        {
            par1PositionTrackingPushbackReader.unread(var3);
        }

        var2.append(this.nonNegativeNumberToken(par1PositionTrackingPushbackReader));
        return var2.toString();
    }

    private String nonNegativeNumberToken(PositionTrackingPushbackReader par1PositionTrackingPushbackReader) throws IOException, InvalidSyntaxException
    {
        StringBuilder var2 = new StringBuilder();
        char var3 = (char)par1PositionTrackingPushbackReader.read();

        if (48 == var3)
        {
            var2.append('0');
            var2.append(this.possibleFractionalComponent(par1PositionTrackingPushbackReader));
            var2.append(this.possibleExponent(par1PositionTrackingPushbackReader));
        }
        else
        {
            par1PositionTrackingPushbackReader.unread(var3);
            var2.append(this.nonZeroDigitToken(par1PositionTrackingPushbackReader));
            var2.append(this.digitString(par1PositionTrackingPushbackReader));
            var2.append(this.possibleFractionalComponent(par1PositionTrackingPushbackReader));
            var2.append(this.possibleExponent(par1PositionTrackingPushbackReader));
        }

        return var2.toString();
    }

    private char nonZeroDigitToken(PositionTrackingPushbackReader par1PositionTrackingPushbackReader) throws IOException, InvalidSyntaxException
    {
        char var3 = (char)par1PositionTrackingPushbackReader.read();

        switch (var3)
        {
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
                return var3;

            default:
                throw new InvalidSyntaxException("Expected a digit 1 - 9 but got [" + var3 + "].", par1PositionTrackingPushbackReader);
        }
    }

    private char digitToken(PositionTrackingPushbackReader par1PositionTrackingPushbackReader) throws IOException, InvalidSyntaxException
    {
        char var3 = (char)par1PositionTrackingPushbackReader.read();

        switch (var3)
        {
            case 48:
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
                return var3;

            default:
                throw new InvalidSyntaxException("Expected a digit 1 - 9 but got [" + var3 + "].", par1PositionTrackingPushbackReader);
        }
    }

    private String digitString(PositionTrackingPushbackReader par1PositionTrackingPushbackReader) throws IOException
    {
        StringBuilder var2 = new StringBuilder();
        boolean var3 = false;

        while (!var3)
        {
            char var4 = (char)par1PositionTrackingPushbackReader.read();

            switch (var4)
            {
                case 48:
                case 49:
                case 50:
                case 51:
                case 52:
                case 53:
                case 54:
                case 55:
                case 56:
                case 57:
                    var2.append(var4);
                    break;

                default:
                    var3 = true;
                    par1PositionTrackingPushbackReader.unread(var4);
            }
        }

        return var2.toString();
    }

    private String possibleFractionalComponent(PositionTrackingPushbackReader par1PositionTrackingPushbackReader) throws IOException, InvalidSyntaxException
    {
        StringBuilder var2 = new StringBuilder();
        char var3 = (char)par1PositionTrackingPushbackReader.read();

        if (var3 == 46)
        {
            var2.append('.');
            var2.append(this.digitToken(par1PositionTrackingPushbackReader));
            var2.append(this.digitString(par1PositionTrackingPushbackReader));
        }
        else
        {
            par1PositionTrackingPushbackReader.unread(var3);
        }

        return var2.toString();
    }

    private String possibleExponent(PositionTrackingPushbackReader par1PositionTrackingPushbackReader) throws IOException, InvalidSyntaxException
    {
        StringBuilder var2 = new StringBuilder();
        char var3 = (char)par1PositionTrackingPushbackReader.read();

        if (var3 != 46 && var3 != 69)
        {
            par1PositionTrackingPushbackReader.unread(var3);
        }
        else
        {
            var2.append('E');
            var2.append(this.possibleSign(par1PositionTrackingPushbackReader));
            var2.append(this.digitToken(par1PositionTrackingPushbackReader));
            var2.append(this.digitString(par1PositionTrackingPushbackReader));
        }

        return var2.toString();
    }

    private String possibleSign(PositionTrackingPushbackReader par1PositionTrackingPushbackReader) throws IOException
    {
        StringBuilder var2 = new StringBuilder();
        char var3 = (char)par1PositionTrackingPushbackReader.read();

        if (var3 != 43 && var3 != 45)
        {
            par1PositionTrackingPushbackReader.unread(var3);
        }
        else
        {
            var2.append(var3);
        }

        return var2.toString();
    }

    private String stringToken(PositionTrackingPushbackReader par1PositionTrackingPushbackReader) throws InvalidSyntaxException, IOException
    {
        StringBuilder var2 = new StringBuilder();
        char var3 = (char)par1PositionTrackingPushbackReader.read();

        if (34 != var3)
        {
            throw new InvalidSyntaxException("Expected [\"] but got [" + var3 + "].", par1PositionTrackingPushbackReader);
        }
        else
        {
            boolean var4 = false;

            while (!var4)
            {
                char var5 = (char)par1PositionTrackingPushbackReader.read();

                switch (var5)
                {
                    case 34:
                        var4 = true;
                        break;

                    case 92:
                        char var6 = this.escapedStringChar(par1PositionTrackingPushbackReader);
                        var2.append(var6);
                        break;

                    default:
                        var2.append(var5);
                }
            }

            return var2.toString();
        }
    }

    private char escapedStringChar(PositionTrackingPushbackReader par1PositionTrackingPushbackReader) throws IOException, InvalidSyntaxException
    {
        char var3 = (char)par1PositionTrackingPushbackReader.read();
        char var2;

        switch (var3)
        {
            case 34:
                var2 = 34;
                break;

            case 47:
                var2 = 47;
                break;

            case 92:
                var2 = 92;
                break;

            case 98:
                var2 = 8;
                break;

            case 102:
                var2 = 12;
                break;

            case 110:
                var2 = 10;
                break;

            case 114:
                var2 = 13;
                break;

            case 116:
                var2 = 9;
                break;

            case 117:
                var2 = (char)this.hexadecimalNumber(par1PositionTrackingPushbackReader);
                break;

            default:
                throw new InvalidSyntaxException("Unrecognised escape character [" + var3 + "].", par1PositionTrackingPushbackReader);
        }

        return var2;
    }

    private int hexadecimalNumber(PositionTrackingPushbackReader par1PositionTrackingPushbackReader) throws IOException, InvalidSyntaxException
    {
        char[] var2 = new char[4];
        int var3 = par1PositionTrackingPushbackReader.read(var2);

        if (var3 != 4)
        {
            throw new InvalidSyntaxException("Expected a 4 digit hexidecimal number but got only [" + var3 + "], namely [" + String.valueOf(var2, 0, var3) + "].", par1PositionTrackingPushbackReader);
        }
        else
        {
            try
            {
                int var4 = Integer.parseInt(String.valueOf(var2), 16);
                return var4;
            }
            catch (NumberFormatException var6)
            {
                par1PositionTrackingPushbackReader.uncount(var2);
                throw new InvalidSyntaxException("Unable to parse [" + String.valueOf(var2) + "] as a hexidecimal number.", var6, par1PositionTrackingPushbackReader);
            }
        }
    }

    private int readNextNonWhitespaceChar(PositionTrackingPushbackReader par1PositionTrackingPushbackReader) throws IOException
    {
        boolean var3 = false;
        int var2;

        do
        {
            var2 = par1PositionTrackingPushbackReader.read();

            switch (var2)
            {
                case 9:
                case 10:
                case 13:
                case 32:
                    break;

                default:
                    var3 = true;
            }
        }
        while (!var3);

        return var2;
    }
}
