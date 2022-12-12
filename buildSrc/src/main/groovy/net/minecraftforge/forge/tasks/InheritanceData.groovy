package net.minecraftforge.forge.tasks

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import groovy.transform.CompileStatic
import groovy.transform.TupleConstructor

@CompileStatic
@TupleConstructor
final class InheritanceData implements Annotatable {
    String name
    int access
    String superName
    List<String> interfaces = []
    Map<String, Method> methods = [:]
    Map<String, Field> fields = [:]
    List<Annotation> annotations = []

    @TupleConstructor
    static final class Method implements Annotatable {
        int access
        String override
        List<Annotation> annotations = []
    }

    @TupleConstructor
    static final class Field implements Annotatable {
        int access
        String desc
        List<Annotation> annotations = []
    }

    @TupleConstructor
    static final class Annotation {
        String desc
    }

    private static final Gson GSON = new Gson()
    static Map<String, InheritanceData> parse(File file) {
        try (final reader = file.newReader()) {
            return GSON.fromJson(reader, new TypeToken<Map<String, InheritanceData>>() {})
        }
    }
}

@CompileStatic
interface Annotatable {
    List<InheritanceData.Annotation> getAnnotations()
}
