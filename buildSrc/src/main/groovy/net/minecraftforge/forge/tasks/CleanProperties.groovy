package net.minecraftforge.forge.tasks;

import java.util.Properties;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeSet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Set;

/**
 * Eclipse config files are literally just java properties, with the header cleaned up.
 * https://github.com/eclipse/buildship/blob/5b2c7fca7fa86cd74d71b3c099c7b1559eba038e/org.eclipse.buildship.core/src/main/java/org/eclipse/buildship/core/internal/configuration/PreferenceStore.java#L238
 *
 * This does the same thing, as well as sorting alphabetically.
 * It also ignores all comments. We can add them latter if someone cares.
 */
public class CleanProperties extends Properties {
    private static final long serialVersionUID = 1L;
    private static final String LINE_SEP = System.getProperty("line.separator");
    private static final String UNIX_LINE_SEP = "\n";
    private static final Charset ENCODING = StandardCharsets.UTF_8;

    public CleanProperties load(File input) throws IOException {
        if (input.exists()) {
            try (Reader is = new InputStreamReader(new FileInputStream(input), ENCODING)) {
                super.load(is);
            }
        }
        return this;
    }

    public void store(File out) throws IOException {
        if (!out.getParentFile().exists())
            out.getParentFile().mkdirs();

        try (OutputStream os = new FileOutputStream(out)) {
            store(os, null);
        }
    }

    @Override
    public synchronized Enumeration<Object> keys() {
        Set<Object> ret = new TreeSet<>();
        for (Enumeration<?> e = super.keys(); e.hasMoreElements();)
            ret.add(e.nextElement());
        return Collections.enumeration(ret);
    }

    @Override
    public Set<Map.Entry<Object, Object>> entrySet() {
        Set<Map.Entry<Object, Object>> ret = new TreeSet<>((l, r) -> ((String)l.getKey()).compareTo((String)r.getKey()));
        ret.addAll(super.entrySet());
        return ret;
    }

    @Override
    public void store(OutputStream out, String comments) throws IOException {
        out.write(clean().getBytes(ENCODING));
        out.flush();
    }

    @Override
    public void store(Writer out, String comments) throws IOException {
        out.write(clean());
        out.flush();
    }

    private String clean() throws IOException {
        ByteArrayOutputStream tmp = new ByteArrayOutputStream();
        try {
            super.store(tmp, null);
        } finally {
            tmp.close();
        }

        String ret = tmp.toString(ENCODING).replace(LINE_SEP, UNIX_LINE_SEP);
        ret = ret.substring(ret.indexOf(UNIX_LINE_SEP) + 1);
        return ret;
    }
}