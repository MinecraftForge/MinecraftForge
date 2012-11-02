package cpw.mods.fml.common.asm.transformers;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

public class MCPMerger
{
    private static Hashtable<String, ClassInfo> clients = new Hashtable<String, ClassInfo>();
    private static Hashtable<String, ClassInfo> shared  = new Hashtable<String, ClassInfo>();
    private static Hashtable<String, ClassInfo> servers = new Hashtable<String, ClassInfo>();
    private static HashSet<String> copyToServer = new HashSet<String>();
    private static HashSet<String> copyToClient = new HashSet<String>();
    private static final boolean DEBUG = false;

    public static void main(String[] args)
    {
        if (args.length != 3)
        {
            System.out.println("Usage: MCPMerger <MapFile> <minecraft.jar> <minecraft_server.jar>");
            System.exit(1);
        }

        File map_file = new File(args[0]);
        File client_jar = new File(args[1]);
        File server_jar = new File(args[2]);
        File client_jar_tmp = new File(args[1] + ".MergeBack");
        File server_jar_tmp = new File(args[2] + ".MergeBack");


        if (client_jar_tmp.exists() && !client_jar_tmp.delete())
        {
            System.out.println("Could not delete temp file: " + client_jar_tmp);
        }

        if (server_jar_tmp.exists() && !server_jar_tmp.delete())
        {
            System.out.println("Could not delete temp file: " + server_jar_tmp);
        }

        if (!client_jar.exists())
        {
            System.out.println("Could not find minecraft.jar: " + client_jar);
            System.exit(1);
        }

        if (!server_jar.exists())
        {
            System.out.println("Could not find minecraft_server.jar: " + server_jar);
            System.exit(1);
        }

        if (!client_jar.renameTo(client_jar_tmp))
        {
            System.out.println("Could not rename file: " + client_jar + " -> " + client_jar_tmp);
            System.exit(1);
        }

        if (!server_jar.renameTo(server_jar_tmp))
        {
            System.out.println("Could not rename file: " + server_jar + " -> " + server_jar_tmp);
            System.exit(1);
        }

        if (!readMapFile(map_file))
        {
            System.out.println("Could not read map file: " + map_file);
            System.exit(1);
        }

        try
        {
            processJar(client_jar_tmp, server_jar_tmp, client_jar, server_jar);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.exit(1);
        }

        if (!client_jar_tmp.delete())
        {
            System.out.println("Could not delete temp file: " + client_jar_tmp);
        }

        if (!server_jar_tmp.delete())
        {
            System.out.println("Could not delete temp file: " + server_jar_tmp);
        }
    }

    private static boolean readMapFile(File mapFile)
    {
        try
        {
            FileInputStream fstream = new FileInputStream(mapFile);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = br.readLine()) != null)
            {
		line = line.split("#")[0];
                boolean toClient = line.charAt(0) == '<';
                line = line.substring(1).trim();
                if (toClient) copyToClient.add(line);
                else copyToServer.add(line);
            }

            in.close();
            return true;
        }
        catch (Exception e)
        {
            System.err.println("Error: " + e.getMessage());
            return false;
        }
    }

    public static void processJar(File clientInFile, File serverInFile, File clientOutFile, File serverOutFile) throws IOException
    {
        ZipFile cInJar = null;
        ZipFile sInJar = null;
        ZipOutputStream cOutJar = null;
        ZipOutputStream sOutJar = null;

        try
        {
            try
            {
                cInJar = new ZipFile(clientInFile);
                sInJar = new ZipFile(serverInFile);
            }
            catch (FileNotFoundException e)
            {
                throw new FileNotFoundException("Could not open input file: " + e.getMessage());
            }
            try
            {
                cOutJar = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(clientOutFile)));
                sOutJar = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(serverOutFile)));
            }
            catch (FileNotFoundException e)
            {
                throw new FileNotFoundException("Could not open output file: " + e.getMessage());
            }
            Hashtable<String, ZipEntry> cClasses = getClassEntries(cInJar, cOutJar);
            Hashtable<String, ZipEntry> sClasses = getClassEntries(sInJar, sOutJar);
            HashSet<String> cAdded = new HashSet<String>();
            HashSet<String> sAdded = new HashSet<String>();

            for (Entry<String, ZipEntry> entry : cClasses.entrySet())
            {
                String name = entry.getKey();
                ZipEntry cEntry = entry.getValue();
                ZipEntry sEntry = sClasses.get(name);

                if (sEntry == null)
                {
                    if (!copyToServer.contains(name))
                    {
                        copyClass(cInJar, cEntry, cOutJar, null, true);
                        cAdded.add(name);
                    }
                    else
                    {
                        if (DEBUG)
                        {
                            System.out.println("Copy class c->s : " + name);
                        }
                        copyClass(cInJar, cEntry, cOutJar, sOutJar, true);
                        cAdded.add(name);
                        sAdded.add(name);
                    }
                    continue;
                }

                sClasses.remove(name);
                ClassInfo info = new ClassInfo(name);
                shared.put(name, info);

                byte[] cData = readEntry(cInJar, entry.getValue());
                byte[] sData = readEntry(sInJar, sEntry);
                byte[] data = processClass(cData, sData, info);

                ZipEntry newEntry = new ZipEntry(cEntry.getName());
                cOutJar.putNextEntry(newEntry);
                cOutJar.write(data);
                sOutJar.putNextEntry(newEntry);
                sOutJar.write(data);
                cAdded.add(name);
                sAdded.add(name);
            }
            for (Entry<String, ZipEntry> entry : sClasses.entrySet())
            {
                if (!copyToClient.contains(entry.getKey()))
                {
                    copyClass(sInJar, entry.getValue(), null, sOutJar, false);
                }
                else
                {
                    if (DEBUG)
                    {
                        System.out.println("Copy class s->c : " + entry.getKey());
                    }
                    copyClass(sInJar, entry.getValue(), cOutJar, sOutJar, false);
                }
            }

            for (String name : new String[]{SideOnly.class.getName(), Side.class.getName()})
            {
                String eName = name.replace(".", "/");
                byte[] data = getClassBytes(name);
                ZipEntry newEntry = new ZipEntry(name.replace(".", "/").concat(".class"));
                if (!cAdded.contains(eName))
                {
                    cOutJar.putNextEntry(newEntry);
                    cOutJar.write(data);
                }
                if (!sAdded.contains(eName))
                {
                    sOutJar.putNextEntry(newEntry);
                    sOutJar.write(data);
                }
            }

        }
        finally
        {
            if (cInJar != null)
            {
                try { cInJar.close(); } catch (IOException e){}
            }

            if (sInJar != null)
            {
                try { sInJar.close(); } catch (IOException e) {}
            }
            if (cOutJar != null)
            {
                try { cOutJar.close(); } catch (IOException e){}
            }

            if (sOutJar != null)
            {
                try { sOutJar.close(); } catch (IOException e) {}
            }
        }
    }

    private static void copyClass(ZipFile inJar, ZipEntry entry, ZipOutputStream outJar, ZipOutputStream outJar2, boolean isClientOnly) throws IOException
    {
        ClassReader reader = new ClassReader(readEntry(inJar, entry));
        ClassNode classNode = new ClassNode();

        reader.accept(classNode, 0);

        if (!classNode.name.equals("bdz")) //Special case CodecMus so I dont have to make a new patch, anyone who uses this in production code is.. bad.
        {
            if (classNode.visibleAnnotations == null) classNode.visibleAnnotations = new ArrayList<AnnotationNode>();
            classNode.visibleAnnotations.add(getSideAnn(isClientOnly));
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        byte[] data = writer.toByteArray();

        ZipEntry newEntry = new ZipEntry(entry.getName());
        if (outJar != null)
        {
            outJar.putNextEntry(newEntry);
            outJar.write(data);
        }
        if (outJar2 != null)
        {
            outJar2.putNextEntry(newEntry);
            outJar2.write(data);
        }
    }

    private static AnnotationNode getSideAnn(boolean isClientOnly)
    {
        AnnotationNode ann = new AnnotationNode(Type.getDescriptor(SideOnly.class));
        ann.values = new ArrayList<Object>();
        ann.values.add("value");
        ann.values.add(new String[]{ Type.getDescriptor(Side.class), (isClientOnly ? "CLIENT" : "SERVER")});
        return ann;
    }

    @SuppressWarnings("unchecked")
    private static Hashtable<String, ZipEntry> getClassEntries(ZipFile inFile, ZipOutputStream outFile) throws IOException
    {
        Hashtable<String, ZipEntry> ret = new Hashtable<String, ZipEntry>();
        for (ZipEntry entry : Collections.list((Enumeration<ZipEntry>)inFile.entries()))
        {
            if (entry.isDirectory())
            {
                outFile.putNextEntry(entry);
                continue;
            }
            String entryName = entry.getName();
            if (!entryName.endsWith(".class") || entryName.startsWith("."))
            {
                ZipEntry newEntry = new ZipEntry(entry.getName());
                outFile.putNextEntry(newEntry);
                outFile.write(readEntry(inFile, entry));
            }
            else
            {
                ret.put(entryName.replace(".class", ""), entry);
            }
        }
        return ret;
    }
    private static byte[] readEntry(ZipFile inFile, ZipEntry entry) throws IOException
    {
        return readFully(inFile.getInputStream(entry));
    }
    private static byte[] readFully(InputStream stream) throws IOException
    {
        byte[] data = new byte[4096];
        ByteArrayOutputStream entryBuffer = new ByteArrayOutputStream();
        int len;
        do
        {
            len = stream.read(data);
            if (len > 0)
            {
                entryBuffer.write(data, 0, len);
            }
        } while (len != -1);

        return entryBuffer.toByteArray();
    }
    private static class ClassInfo
    {
        public String name;
        public ArrayList<FieldNode> cField = new ArrayList<FieldNode>();
        public ArrayList<FieldNode> sField = new ArrayList<FieldNode>();
        public ArrayList<MethodNode> cMethods = new ArrayList<MethodNode>();
        public ArrayList<MethodNode> sMethods = new ArrayList<MethodNode>();
        public ClassInfo(String name){ this.name = name; }
        public boolean isSame() { return (cField.size() == 0 && sField.size() == 0 && cMethods.size() == 0 && sMethods.size() == 0); }
    }

    public static byte[] processClass(byte[] cIn, byte[] sIn, ClassInfo info)
    {
        ClassNode cClassNode = getClassNode(cIn);
        ClassNode sClassNode = getClassNode(sIn);

        processFields(cClassNode, sClassNode, info);
        processMethods(cClassNode, sClassNode, info);

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cClassNode.accept(writer);
        return writer.toByteArray();
    }

    private static ClassNode getClassNode(byte[] data)
    {
        ClassReader reader = new ClassReader(data);
        ClassNode classNode = new ClassNode();
        reader.accept(classNode, 0);
        return classNode;
    }

    @SuppressWarnings("unchecked")
    private static void processFields(ClassNode cClass, ClassNode sClass, ClassInfo info)
    {
        List<FieldNode> cFields = cClass.fields;
        List<FieldNode> sFields = sClass.fields;

        int sI = 0;
        for (int x = 0; x < cFields.size(); x++)
        {
            FieldNode cF = cFields.get(x);
            if (sI < sFields.size())
            {
                if (!cF.name.equals(sFields.get(sI).name))
                {
                    boolean serverHas = false;
                    for (int y = sI + 1; y < sFields.size(); y++)
                    {
                        if (cF.name.equals(sFields.get(y).name))
                        {
                            serverHas = true;
                            break;
                        }
                    }
                    if (serverHas)
                    {
                        boolean clientHas = false;
                        FieldNode sF = sFields.get(sI);
                        for (int y = x + 1; y < cFields.size(); y++)
                        {
                            if (sF.name.equals(cFields.get(y).name))
                            {
                                clientHas = true;
                                break;
                            }
                        }
                        if (!clientHas)
                        {
                            if  (sF.visibleAnnotations == null) sF.visibleAnnotations = new ArrayList<AnnotationNode>();
                            sF.visibleAnnotations.add(getSideAnn(false));
                            cFields.add(x++, sF);
                            info.sField.add(sF);
                        }
                    }
                    else
                    {
                        if  (cF.visibleAnnotations == null) cF.visibleAnnotations = new ArrayList<AnnotationNode>();
                        cF.visibleAnnotations.add(getSideAnn(true));
                        sFields.add(sI, cF);
                        info.cField.add(cF);
                    }
                }
            }
            else
            {
                if  (cF.visibleAnnotations == null) cF.visibleAnnotations = new ArrayList<AnnotationNode>();
                cF.visibleAnnotations.add(getSideAnn(true));
                sFields.add(sI, cF);
                info.cField.add(cF);
            }
            sI++;
        }
        if (sFields.size() != cFields.size())
        {
            for (int x = cFields.size(); x < sFields.size(); x++)
            {
                FieldNode sF = sFields.get(x);
                if  (sF.visibleAnnotations == null) sF.visibleAnnotations = new ArrayList<AnnotationNode>();
                sF.visibleAnnotations.add(getSideAnn(true));
                cFields.add(x++, sF);
                info.sField.add(sF);
            }
        }
    }

    private static class MethodWrapper
    {
        private MethodNode node;
        public boolean client;
        public boolean server;
        public MethodWrapper(MethodNode node)
        {
            this.node = node;
        }
        @Override
        public boolean equals(Object obj)
        {
            if (obj == null || !(obj instanceof MethodWrapper)) return false;
            MethodWrapper mw = (MethodWrapper) obj;
            boolean eq = Objects.equal(node.name, mw.node.name) && Objects.equal(node.desc, mw.node.desc);
            if (eq)
            {
                mw.client = this.client | mw.client;
                mw.server = this.server | mw.server;
                this.client = this.client | mw.client;
                this.server = this.server | mw.server;
                if (DEBUG)
                {
                    System.out.printf(" eq: %s %s\n", this, mw);
                }
            }
            return eq;
        }

        @Override
        public int hashCode()
        {
            return Objects.hashCode(node.name, node.desc);
        }
        @Override
        public String toString()
        {
            return Objects.toStringHelper(this).add("name", node.name).add("desc",node.desc).add("server",server).add("client",client).toString();
        }
    }
    @SuppressWarnings("unchecked")
    private static void processMethods(ClassNode cClass, ClassNode sClass, ClassInfo info)
    {
        List<MethodNode> cMethods = (List<MethodNode>)cClass.methods;
        List<MethodNode> sMethods = (List<MethodNode>)sClass.methods;
        LinkedHashSet<MethodWrapper> allMethods = Sets.newLinkedHashSet();

        int cPos = 0;
        int sPos = 0;
        int cLen = cMethods.size();
        int sLen = sMethods.size();
        String clientName = "";
        String lastName = clientName;
        String serverName = "";
        while (cPos < cLen || sPos < sLen)
        {
            do
            {
                if (sPos>=sLen)
                {
                    break;
                }
                MethodNode sM = sMethods.get(sPos);
                serverName = sM.name;
                if (!serverName.equals(lastName) && cPos != cLen)
                {
                    if (DEBUG)
                    {
                        System.out.printf("Server -skip : %s %s %d (%s %d) %d [%s]\n", sClass.name, clientName, cLen - cPos, serverName, sLen - sPos, allMethods.size(), lastName);
                    }
                    break;
                }
                MethodWrapper mw = new MethodWrapper(sM);
                mw.server = true;
                allMethods.add(mw);
                if (DEBUG)
                {
                    System.out.printf("Server *add* : %s %s %d (%s %d) %d [%s]\n", sClass.name, clientName, cLen - cPos, serverName, sLen - sPos, allMethods.size(), lastName);
                }
                sPos++;
            }
            while (sPos < sLen);
            do
            {
                if (cPos>=cLen)
                {
                    break;
                }
                MethodNode cM = cMethods.get(cPos);
                lastName = clientName;
                clientName = cM.name;
                if (!clientName.equals(lastName) && sPos != sLen)
                {
                    if (DEBUG)
                    {
                        System.out.printf("Client -skip : %s %s %d (%s %d) %d [%s]\n", cClass.name, clientName, cLen - cPos, serverName, sLen - sPos, allMethods.size(), lastName);
                    }
                    break;
                }
                MethodWrapper mw = new MethodWrapper(cM);
                mw.client = true;
                allMethods.add(mw);
                if (DEBUG)
                {
                    System.out.printf("Client *add* : %s %s %d (%s %d) %d [%s]\n", cClass.name, clientName, cLen - cPos, serverName, sLen - sPos, allMethods.size(), lastName);
                }
                cPos++;
            }
            while (cPos < cLen);
        }

        cMethods.clear();
        sMethods.clear();

        for (MethodWrapper mw : allMethods)
        {
            if (DEBUG)
            {
                System.out.println(mw);
            }
            cMethods.add(mw.node);
            sMethods.add(mw.node);
            if (mw.server && mw.client)
            {
                // no op
            }
            else
            {
                if (mw.node.visibleAnnotations == null) mw.node.visibleAnnotations = Lists.newArrayListWithExpectedSize(1);
                mw.node.visibleAnnotations.add(getSideAnn(mw.client));
                if (mw.client)
                {
                    info.sMethods.add(mw.node);
                }
                else
                {
                    info.cMethods.add(mw.node);
                }
            }
        }
    }

    public static byte[] getClassBytes(String name) throws IOException
    {
        InputStream classStream = null;
        try
        {
            classStream = MCPMerger.class.getResourceAsStream("/" + name.replace('.', '/').concat(".class"));
            return readFully(classStream);
        }
        finally
        {
            if (classStream != null)
            {
                try
                {
                    classStream.close();
                }
                catch (IOException e){}
            }
        }
    }
}
