package net.minecraft.src;

import java.io.*;
import java.util.*;

public final class InfiProps
{
    private String fileName;
    private List fileLines;
    private Map theProps;

    public InfiProps(String s)
    {
        fileLines = new ArrayList();
        theProps = new HashMap();
        fileName = s;
        File file = new File(fileName);
        if (file.exists())
        {
            try
            {
                loadFile();
            }
            catch (IOException ioexception)
            {
                System.out.println((new StringBuilder()).append("[Props] Unable to load ").append(fileName).append("!").toString());
            }
        }
        else
        {
            saveFile();
        }
    }

    public void loadFile()
    throws IOException
    {
        BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF8"));
        fileLines.clear();
        theProps.clear();
        String s;
        while ((s = bufferedreader.readLine()) != null)
        {
            s = new String(s.getBytes(), "UTF-8");
            char c = '\0';
            int i;
            for (i = 0; i < s.length() && Character.isWhitespace(c = s.charAt(i)); i++) { }
            if (s.length() - i == 0 || s.charAt(i) == '#' || s.charAt(i) == '!')
            {
                fileLines.add(s);
            }
            else
            {
                int j = i;
                boolean flag = s.indexOf('\\', i) != -1;
                StringBuffer stringbuffer = flag ? new StringBuffer() : null;
                if (stringbuffer != null)
                {
                    do
                    {
                        if (i >= s.length() || Character.isWhitespace(c = s.charAt(i++)) || c == '=' || c == ':')
                        {
                            break;
                        }
                        if (flag && c == '\\')
                        {
                            if (i == s.length())
                            {
                                s = bufferedreader.readLine();
                                if (s == null)
                                {
                                    s = "";
                                }
                                i = 0;
                                while (++i < s.length() && Character.isWhitespace(c = s.charAt(i))) ;
                            }
                            else
                            {
                                c = s.charAt(i++);
                            }
                        }
                        else
                        {
                            switch (c)
                            {
                                case 110:
                                    stringbuffer.append('\n');
                                    break;

                                case 116:
                                    stringbuffer.append('\t');
                                    break;

                                case 114:
                                    stringbuffer.append('\r');
                                    break;

                                case 117:
                                    if (i + 4 <= s.length())
                                    {
                                        char c1 = (char)Integer.parseInt(s.substring(i, i + 4), 16);
                                        stringbuffer.append(c1);
                                        i += 4;
                                    }
                                    break;

                                case 111:
                                case 112:
                                case 113:
                                case 115:
                                default:
                                    stringbuffer.append('\0');
                                    break;
                            }
                        }
                    }
                    while (true);
                }
                boolean flag1 = c == ':' || c == '=';
                String s1;
                if (flag)
                {
                    s1 = stringbuffer.toString();
                }
                else if (flag1 || Character.isWhitespace(c))
                {
                    s1 = s.substring(j, i - 1);
                }
                else
                {
                    s1 = s.substring(j, i);
                }
                for (; i < s.length() && Character.isWhitespace(c = s.charAt(i)); i++) { }
                if (!flag1 && (c == ':' || c == '='))
                {
                    char c2;
                    for (i++; i < s.length() && Character.isWhitespace(c2 = s.charAt(i)); i++) { }
                }
                if (!flag)
                {
                    fileLines.add(s);
                }
                else
                {
                    StringBuilder stringbuilder = new StringBuilder(s.length() - i);
                    do
                    {
                        if (i >= s.length())
                        {
                            break;
                        }
                        char c3 = s.charAt(i++);
                        if (c3 == '\\')
                        {
                            if (i == s.length())
                            {
                                s = bufferedreader.readLine();
                                if (s == null)
                                {
                                    break;
                                }
                                char c4;
                                for (i = 0; i < s.length() && Character.isWhitespace(c4 = s.charAt(i)); i++) { }
                                stringbuilder.ensureCapacity((s.length() - i) + stringbuilder.length());
                                continue;
                            }
                            char c5 = s.charAt(i++);
                            switch (c5)
                            {
                                case 110:
                                    stringbuilder.append('\n');
                                    break;

                                case 116:
                                    stringbuilder.append('\t');
                                    break;

                                case 114:
                                    stringbuilder.append('\r');
                                    break;

                                case 117:
                                    if (i + 4 <= s.length())
                                    {
                                        char c6 = (char)Integer.parseInt(s.substring(i, i + 4), 16);
                                        stringbuilder.append(c6);
                                        i += 4;
                                        break;
                                    }
                                    continue;

                                case 111:
                                case 112:
                                case 113:
                                case 115:
                                default:
                                    stringbuilder.append('\0');
                                    break;
                            }
                        }
                        stringbuilder.append('\0');
                    }
                    while (true);
                    fileLines.add((new StringBuilder()).append(s1).append("=").append(stringbuilder.toString()).toString());
                }
            }
        }
        bufferedreader.close();
    }

    public void saveFile()
    {
        FileOutputStream fileoutputstream = null;
        try
        {
            fileoutputstream = new FileOutputStream(fileName);
        }
        catch (FileNotFoundException filenotfoundexception)
        {
            System.out.println((new StringBuilder()).append("[Props] Unable to open ").append(fileName).append("!").toString());
        }
        PrintStream printstream = null;
        try
        {
            printstream = new PrintStream(fileoutputstream, true, "UTF-8");
        }
        catch (UnsupportedEncodingException unsupportedencodingexception)
        {
            System.out.println((new StringBuilder()).append("[Props] Unable to write to ").append(fileName).append("!").toString());
        }
        ArrayList arraylist = new ArrayList();
        for (Iterator iterator = fileLines.iterator(); iterator.hasNext();)
        {
            String s = (String)iterator.next();
            if (s.trim().length() == 0)
            {
                printstream.println(s);
            }
            else if (s.charAt(0) == '#')
            {
                printstream.println(s);
            }
            else if (s.contains("="))
            {
                int i = s.indexOf('=');
                String s1 = s.substring(0, i).trim();
                if (theProps.containsKey(s1))
                {
                    String s2 = (String)theProps.get(s1);
                    printstream.println((new StringBuilder()).append(s1).append("=").append(s2).toString());
                    arraylist.add(s1);
                }
                else
                {
                    printstream.println(s);
                }
            }
            else
            {
                printstream.println(s);
            }
        }

        Iterator iterator1 = theProps.entrySet().iterator();
        do
        {
            if (!iterator1.hasNext())
            {
                break;
            }
            java.util.Map.Entry entry = (java.util.Map.Entry)iterator1.next();
            if (!arraylist.contains(entry.getKey()))
            {
                printstream.println((new StringBuilder()).append((String)entry.getKey()).append("=").append((String)entry.getValue()).toString());
            }
        }
        while (true);
        printstream.close();
        try
        {
            theProps.clear();
            fileLines.clear();
            loadFile();
        }
        catch (IOException ioexception)
        {
            System.out.println((new StringBuilder()).append("[Props] Unable to load ").append(fileName).append("!").toString());
        }
    }

    public Map returnMap()
    throws Exception
    {
        HashMap hashmap = new HashMap();
        BufferedReader bufferedreader = new BufferedReader(new FileReader(fileName));
        do
        {
            String s;
            if ((s = bufferedreader.readLine()) == null)
            {
                break;
            }
            if (s.trim().length() != 0 && s.charAt(0) != '#' && s.contains("="))
            {
                int i = s.indexOf('=');
                String s1 = s.substring(0, i).trim();
                String s2 = s.substring(i + 1).trim();
                hashmap.put(s1, s2);
            }
        }
        while (true);
        bufferedreader.close();
        return hashmap;
    }

    public boolean containsKey(String s)
    {
        for (Iterator iterator = fileLines.iterator(); iterator.hasNext();)
        {
            String s1 = (String)iterator.next();
            if (s1.trim().length() != 0 && s1.charAt(0) != '#' && s1.contains("="))
            {
                int i = s1.indexOf('=');
                String s2 = s1.substring(0, i);
                if (s2.equals(s))
                {
                    return true;
                }
            }
        }

        return false;
    }

    public String getProperty(String s)
    {
        for (Iterator iterator = fileLines.iterator(); iterator.hasNext();)
        {
            String s1 = (String)iterator.next();
            if (s1.trim().length() != 0 && s1.charAt(0) != '#' && s1.contains("="))
            {
                int i = s1.indexOf('=');
                String s2 = s1.substring(0, i).trim();
                String s3 = s1.substring(i + 1);
                if (s2.equals(s))
                {
                    return s3;
                }
            }
        }

        return "";
    }

    public void removeKey(String s)
    {
        Boolean boolean1 = Boolean.valueOf(false);
        if (theProps.containsKey(s))
        {
            theProps.remove(s);
            boolean1 = Boolean.valueOf(true);
        }
        try
        {
            for (int i = 0; i < fileLines.size(); i++)
            {
                String s1 = (String)fileLines.get(i);
                if (s1.trim().length() != 0 && s1.charAt(0) != '#' && s1.contains("="))
                {
                    int j = s1.indexOf('=');
                    String s2 = s1.substring(0, j).trim();
                    if (s2.equals(s))
                    {
                        fileLines.remove(i);
                        boolean1 = Boolean.valueOf(true);
                    }
                }
            }
        }
        catch (ConcurrentModificationException concurrentmodificationexception)
        {
            removeKey(s);
            return;
        }
        if (boolean1.booleanValue())
        {
            saveFile();
        }
    }

    public boolean doesKeyExist(String s)
    {
        try
        {
            return containsKey(s);
        }
        catch (Exception exception)
        {
            return false;
        }
    }

    public String readString(String s)
    {
        if (containsKey(s))
        {
            return getProperty(s);
        }
        else
        {
            return "";
        }
    }

    public String accessString(String s, String s1)
    {
        if (containsKey(s))
        {
            return getProperty(s);
        }
        else
        {
            writeString(s, s1);
            return s1;
        }
    }

    public void writeString(String s, String s1)
    {
        theProps.put(s, s1);
        saveFile();
    }

    public int readInt(String s)
    {
        if (containsKey(s))
        {
            return Integer.parseInt(getProperty(s));
        }
        else
        {
            return 0;
        }
    }

    public int accessInt(String s, int i)
    {
        if (containsKey(s))
        {
            return Integer.parseInt(getProperty(s));
        }
        else
        {
            writeInt(s, i);
            return i;
        }
    }

    public void writeInt(String s, int i)
    {
        theProps.put(s, String.valueOf(i));
        saveFile();
    }

    public double readDouble(String s)
    {
        if (containsKey(s))
        {
            return Double.parseDouble(getProperty(s));
        }
        else
        {
            return 0.0D;
        }
    }

    public double accessDouble(String s, double d)
    {
        if (containsKey(s))
        {
            return Double.parseDouble(getProperty(s));
        }
        else
        {
            writeDouble(s, d);
            return d;
        }
    }

    public void writeDouble(String s, double d)
    {
        theProps.put(s, String.valueOf(d));
        saveFile();
    }

    public long readLong(String s)
    {
        if (containsKey(s))
        {
            return Long.parseLong(getProperty(s));
        }
        else
        {
            return 0L;
        }
    }

    public long accessLong(String s, long l)
    {
        if (containsKey(s))
        {
            return Long.parseLong(getProperty(s));
        }
        else
        {
            writeLong(s, l);
            return l;
        }
    }

    public void writeLong(String s, long l)
    {
        theProps.put(s, String.valueOf(l));
        saveFile();
    }

    public boolean readBoolean(String s)
    {
        if (containsKey(s))
        {
            return Boolean.parseBoolean(getProperty(s));
        }
        else
        {
            return false;
        }
    }

    public boolean accessBoolean(String s, boolean flag)
    {
        if (containsKey(s))
        {
            return Boolean.parseBoolean(getProperty(s));
        }
        else
        {
            writeBoolean(s, flag);
            return flag;
        }
    }

    public void writeBoolean(String s, boolean flag)
    {
        theProps.put(s, String.valueOf(flag));
        saveFile();
    }
}
