package cpw.mods.fml.common.asm.transformers;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.Map.Entry;
import java.util.zip.*;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

public class MCPMerger
{
    private static Hashtable<String, ClassInfo> clients = new Hashtable<String, ClassInfo>();
    private static Hashtable<String, ClassInfo> shared  = new Hashtable<String, ClassInfo>();
    private static Hashtable<String, ClassInfo> servers = new Hashtable<String, ClassInfo>();
    private static HashSet<String> copyToServer = new HashSet<String>();
    private static HashSet<String> copyToClient = new HashSet<String>();
    
    public static void main(String[] args)
    {
        if (args.length != 3)
        {
            System.out.println("Usage: AccessTransformer <MapFile> <minecraft.jar> <minecraft_server.jar>");
            return;
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
            return;
        }
        
        if (!server_jar.exists())
        {
            System.out.println("Could not find minecraft_server.jar: " + server_jar);
            return;
        }

        if (!client_jar.renameTo(client_jar_tmp))
        {
            System.out.println("Could not rename file: " + client_jar + " -> " + client_jar_tmp);
            return;
        }

        if (!server_jar.renameTo(server_jar_tmp))
        {
            System.out.println("Could not rename file: " + server_jar + " -> " + server_jar_tmp);
            return;
        }
        
        if (!readMapFile(map_file))
        {
            System.out.println("Could not read map file: " + map_file);
            return;
        }

        try
        {
            processJar(client_jar_tmp, server_jar_tmp, client_jar, server_jar);
        }
        catch (IOException e)
        {
            e.printStackTrace();
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
                boolean toClient = line.charAt(0) == '<';
                line = line.substring(1);
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
            
            for (Entry<String, ZipEntry> entry : cClasses.entrySet())
            {
                String name = entry.getKey();
                ZipEntry cEntry = entry.getValue();
                ZipEntry sEntry = sClasses.get(name);
                
                if (sEntry == null)
                {
                    if (!copyToServer.contains(name))
                    {
                        copyEntry(cInJar, cEntry, cOutJar);
                    }
                    else
                    {
                        System.out.println("Copy class c->s : " + name);
                        copyClass(cInJar, cEntry, cOutJar, sOutJar, true);
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
            }
            for (Entry<String, ZipEntry> entry : sClasses.entrySet())
            {                    
                if (!copyToClient.contains(entry.getKey()))
                {
                    copyEntry(sInJar, entry.getValue(), sOutJar);
                }
                else
                {
                    System.out.println("Copy class s->c : " + entry.getKey());
                    copyClass(sInJar, entry.getValue(), cOutJar, sOutJar, false);
                }
            }
            
            for (String name : new String[]{SideOnly.class.getName(), Side.class.getName()})
            {
                byte[] data = getClassBytes(name);
                ZipEntry newEntry = new ZipEntry(name.replace(".", "/").concat(".class"));
                cOutJar.putNextEntry(newEntry);
                cOutJar.write(data);
                sOutJar.putNextEntry(newEntry);
                sOutJar.write(data);
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
        if (classNode.visibleAnnotations == null) classNode.visibleAnnotations = new ArrayList<AnnotationNode>();
        classNode.visibleAnnotations.add(getSideAnn(isClientOnly));
        
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        byte[] data = writer.toByteArray();

        ZipEntry newEntry = new ZipEntry(entry.getName());
        outJar.putNextEntry(newEntry);
        outJar.write(data);
        outJar2.putNextEntry(newEntry);
        outJar2.write(data);
    }
    
    private static AnnotationNode getSideAnn(boolean isClientOnly)
    {
        AnnotationNode ann = new AnnotationNode("L" + Type.getInternalName(SideOnly.class) + ";");
        ann.values = new ArrayList<Object>();
        ann.values.add("side");
        ann.values.add(new String[]{ "L" + Type.getInternalName(Side.class) + ";", (isClientOnly ? "CLIENT" : "SERVER")});
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
                copyEntry(inFile, entry, outFile);
            }
            else
            {
                ret.put(entryName.replace(".class", ""), entry);
            }
        }
        return ret;
    }
    private static void copyEntry(ZipFile inFile, ZipEntry entry, ZipOutputStream outFile) throws IOException
    {   
        ZipEntry newEntry = new ZipEntry(entry.getName());
        outFile.putNextEntry(newEntry);    
        outFile.write(readEntry(inFile, entry));
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
    
    @SuppressWarnings("unchecked")
    private static void processMethods(ClassNode cClass, ClassNode sClass, ClassInfo info)
    {
        List<MethodNode> cMethods = (List<MethodNode>)cClass.methods;
        List<MethodNode> sMethods = (List<MethodNode>)sClass.methods;
        
        int sI = 0;
        for (int x = 0; x < cMethods.size(); x++)
        {
            MethodNode cM = cMethods.get(x);
            if (sI < sMethods.size())
            {
                if (!cM.name.equals(sMethods.get(sI).name) || (cM.name.equals("<init>") && !cM.desc.equals(sMethods.get(sI).desc)))
                {
                    boolean serverHas = false;
                    for (int y = sI + 1; y < sMethods.size(); y++)
                    {
                        if (cM.name.equals(sMethods.get(y).name) && cM.desc.equals(sMethods.get(y).desc))
                        {
                            serverHas = true;
                            break;
                        }
                    }
                    if (serverHas)
                    {
                        boolean clientHas = false;
                        MethodNode sM = sMethods.get(sI);
                        for (int y = x + 1; y < cMethods.size(); y++)
                        {
                            if (sM.name.equals(cMethods.get(y).name) && sM.desc.equals(cMethods.get(y).desc))
                            {
                                clientHas = true;
                                break;
                            }
                        }
                        if (!clientHas)
                        {
                            if  (sM.visibleAnnotations == null) sM.visibleAnnotations = new ArrayList<AnnotationNode>();
                            sM.visibleAnnotations.add(getSideAnn(false));         
                            cMethods.add(x, sM);
                            info.sMethods.add(sM);
                        }
                    }
                    else
                    {
                        if  (cM.visibleAnnotations == null) cM.visibleAnnotations = new ArrayList<AnnotationNode>();
                        cM.visibleAnnotations.add(getSideAnn(true)); 
                        sMethods.add(sI, cM);  
                        info.cMethods.add(cM);
                    }
                }
            } 
            else
            {
                if  (cM.visibleAnnotations == null) cM.visibleAnnotations = new ArrayList<AnnotationNode>();
                cM.visibleAnnotations.add(getSideAnn(true)); 
                sMethods.add(sI, cM);
                info.cMethods.add(cM);
            }            
            sI++;
        }
        if (sMethods.size() != cMethods.size())
        {
            for (int x = cMethods.size(); x < sMethods.size(); x++)
            {
                MethodNode sM = sMethods.get(x);
                if  (sM.visibleAnnotations == null) sM.visibleAnnotations = new ArrayList<AnnotationNode>();
                sM.visibleAnnotations.add(getSideAnn(false)); 
                cMethods.add(x++, sM);
                info.sMethods.add(sM);
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
