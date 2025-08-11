package net.minecraftforge.forge.tasks

import java.util.function.BiConsumer

public class ClosureHelper {
    BiConsumer<String, Closure> callback
    
    public ClosureHelper(Closure cl, BiConsumer<String, Closure> callback) {
        this.callback = callback
        apply(this, cl)
    }
    
    
    def methodMissing(String name, Object args) {
        if (!args.class.isArray()) return
        Object[] aargs = (Object[])args
        
        if (aargs.length == 1 && aargs[0] instanceof Closure) {
            this.callback.accept(name, (Closure)aargs[0])
        } else {
            throw new IllegalArgumentException('Unknown method: "' + name + '" with arguments ' + args + ' for ' + this)
        }
    }
    
    static <T> T apply(T obj, Closure cl) {
        cl.delegate = obj
        cl.resolveStrategy = Closure.DELEGATE_FIRST
        cl()
        return obj
    }
}