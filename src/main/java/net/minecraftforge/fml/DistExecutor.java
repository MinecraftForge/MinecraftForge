/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.fml;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * Use to execute code conditionally based on sidedness.
 * <ul>
 * <li>When you want to call something on one side and return a result {@link #safeCallWhenOn(Dist, Supplier)}</li>
 * <li>When you want to call one thing on one side, another thing on the other and return a result {@link #safeRunForDist(Supplier, Supplier)}</li>
 * <li>When you want to run something on one side {@link #safeRunWhenOn(Dist, Supplier)}</li>
 * </ul>
 */
public final class DistExecutor
{
    private static final Logger LOGGER = LogManager.getLogger();
    private DistExecutor() {}

    /**
     * Run the callable in the supplier only on the specified {@link Side}.
     * This method is NOT sided-safe and special care needs to be taken in code using this method that implicit class
     * loading is not triggered by the Callable.
     *
     * This method can cause unexpected ClassNotFound exceptions.
     *
     * Use {@link #safeCallWhenOn(Dist, Supplier)} where possible.
     *
     * @param dist The dist to run on
     * @param toRun A supplier of the callable to run (Supplier wrapper to ensure classloading only on the appropriate dist)
     * @param <T> The return type from the callable
     * @return The callable's result
     * @deprecated use {@link #safeCallWhenOn(Dist, Supplier)} instead. This remains for advanced use cases.
     */
    @Deprecated
    public static <T> T callWhenOn(Dist dist, Supplier<Callable<T>> toRun) {
        return unsafeCallWhenOn(dist, toRun);
    }

    public static <T> T unsafeCallWhenOn(Dist dist, Supplier<Callable<T>> toRun) {
        if (dist == FMLEnvironment.dist) {
            try
            {
                return toRun.get().call();
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    /**
     * Call the SafeCallable when on the correct {@link Dist}.
     *
     * <strong>The lambda supplied here is required to be a method reference to a method defined in
     * another class, otherwise an invalid SafeReferent error will be thrown</strong>
     * @param dist the dist which this will run on
     * @param toRun the SafeCallable to run and return the result from
     * @param <T> The type of the SafeCallable
     * @return the result of the SafeCallable or null if on the wrong side
     */
    public static <T> T safeCallWhenOn(Dist dist, Supplier<SafeCallable<T>> toRun) {
        validateSafeReferent(toRun);
        return callWhenOn(dist, toRun::get);
    }

    /**
     * Runs the supplied Runnable on the speicified side. Same warnings apply as {@link #callWhenOn(Dist, Supplier)}.
     *
     * This method can cause unexpected ClassNotFound exceptions.
     *
     * @see #callWhenOn(Dist, Supplier)
     * @param dist Dist to run this code on
     * @param toRun The code to run
     * @deprecated use {@link #safeRunWhenOn(Dist, Supplier)} where possible. Advanced uses only.
     */
    @Deprecated
    public static void runWhenOn(Dist dist, Supplier<Runnable> toRun) {
        unsafeRunWhenOn(dist, toRun);
    }
    /**
     * Runs the supplied Runnable on the speicified side. Same warnings apply as {@link #unsafeCallWhenOn(Dist, Supplier)}.
     *
     * This method can cause unexpected ClassNotFoundException problems in common scenarios. Understand the pitfalls of
     * the way the class verifier works to load classes before using this.
     *
     * Use {@link #safeRunWhenOn(Dist, Supplier)} if you can.
     *
     * @see #unsafeCallWhenOn(Dist, Supplier)
     * @param dist Dist to run this code on
     * @param toRun The code to run
     */
    public static void unsafeRunWhenOn(Dist dist, Supplier<Runnable> toRun) {
        if (dist == FMLEnvironment.dist) {
            toRun.get().run();
        }
    }

    /**
     * Call the supplied SafeRunnable when on the correct Dist.
     * @param dist The dist to run on
     * @param toRun The code to run
     */
    public static void safeRunWhenOn(Dist dist, Supplier<SafeRunnable> toRun) {
        validateSafeReferent(toRun);
        if (dist == FMLEnvironment.dist)  {
            toRun.get().run();
        }
    }
    /**
     * Executes one of the two suppliers, based on which side is active.
     *
     * <p>
     *     Example (replacement for old SidedProxy):<br/>
     * {@code Proxy p = DistExecutor.runForDist(()->ClientProxy::new, ()->ServerProxy::new);}
     *
     * NOTE: the double supplier is required to avoid classloading the secondary target.
     *
     * @param clientTarget The supplier supplier to run when on the {@link Dist#CLIENT}
     * @param serverTarget The supplier supplier to run when on the {@link Dist#DEDICATED_SERVER}
     * @param <T> The common type to return
     * @return The returned instance
     * @deprecated Use {@link #safeRunForDist(Supplier, Supplier)}
     */
    @Deprecated
    public static <T> T runForDist(Supplier<Supplier<T>> clientTarget, Supplier<Supplier<T>> serverTarget) {
        return unsafeRunForDist(clientTarget, serverTarget);
    }

    /**
     * Unsafe version of {@link #safeRunForDist(Supplier, Supplier)}. Use only when you know what you're doing
     * and understand why the verifier can cause unexpected ClassNotFoundException crashes even when code is apparently
     * not sided. Ensure you test both sides fully to be confident in using this.
     *
     * @param clientTarget The supplier supplier to run when on the {@link Dist#CLIENT}
     * @param serverTarget The supplier supplier to run when on the {@link Dist#DEDICATED_SERVER}
     * @param <T> The common type to return
     * @return The returned instance
     */
    public static <T> T unsafeRunForDist(Supplier<Supplier<T>> clientTarget, Supplier<Supplier<T>> serverTarget) {
        switch (FMLEnvironment.dist)
        {
            case CLIENT:
                return clientTarget.get().get();
            case DEDICATED_SERVER:
                return serverTarget.get().get();
            default:
                throw new IllegalArgumentException("UNSIDED?");
        }
    }
    /**
     * Executes one of the two suppliers, based on which side is active.
     *
     * <p>
     *     Example (replacement for old SidedProxy):<br/>
     * {@code Proxy p = DistExecutor.safeRunForDist(()->ClientProxy::new, ()->ServerProxy::new);}
     *
     * NOTE: the double supplier is required to avoid classloading the secondary target.
     *
     * @param clientTarget The supplier supplier to run when on the {@link Dist#CLIENT}
     * @param serverTarget The supplier supplier to run when on the {@link Dist#DEDICATED_SERVER}
     * @param <T> The common type to return
     * @return The returned instance
     */
    public static <T> T safeRunForDist(Supplier<SafeSupplier<T>> clientTarget, Supplier<SafeSupplier<T>> serverTarget) {
        validateSafeReferent(clientTarget);
        validateSafeReferent(serverTarget);
        switch (FMLEnvironment.dist)
        {
            case CLIENT:
                return clientTarget.get().get();
            case DEDICATED_SERVER:
                return serverTarget.get().get();
            default:
                throw new IllegalArgumentException("UNSIDED?");
        }
    }

    /**
     * A safe referent. This will assert that it is being called via a separated class method reference. This will
     * avoid the common pitfalls of {@link #callWhenOn(Dist, Supplier)} above.
     *
     * SafeReferents assert that they are defined as a separate method outside the scope of the calling class.
     *
     * <strong>Implementations need to be defined in a separate class to the referring site, with appropriate
     * visibility to be accessible at the callsite (generally, avoid private methods).</strong>
     *
     * <p>
     * Valid:<br/>
     *
     * {@code DistExecutor.safeCallWhenOn(Dist.CLIENT, ()->AnotherClass::clientOnlyMethod);}
     *
     * <p>
     * Invalid:<br/>
     *
     * {@code DistExecutor.safeCallWhenOn(Dist.CLIENT, ()->()->Minecraft.getInstance().world);}
     */
    public interface SafeReferent {}

    /**
     * SafeCallable version of {@link SafeReferent}.
     * @see SafeReferent
     * @param <T> The return type of the Callable
     */
    public interface SafeCallable<T> extends SafeReferent, Callable<T>, Serializable {}

    /**
     * SafeSupplier version of {@link SafeReferent}
     * @param <T> The return type of the Supplier
     */
    public interface SafeSupplier<T> extends SafeReferent, Supplier<T>, Serializable {}

    /**
     * SafeRunnable version of {@link SafeReferent}
     */
    public interface SafeRunnable extends SafeReferent, Runnable, Serializable {}

    private static final void validateSafeReferent(Supplier<? extends SafeReferent> safeReferentSupplier) {
        if (FMLEnvironment.production) return;
        final SafeReferent setter;
        try {
            setter = safeReferentSupplier.get();
        } catch (Exception e) {
            // Typically a class cast exception, just return out, expected.
            return;
        }

        for (Class<?> cl = setter.getClass(); cl != null; cl = cl.getSuperclass()) {
            try {
                Method m = cl.getDeclaredMethod("writeReplace");
                m.setAccessible(true);
                Object replacement = m.invoke(setter);
                if (!(replacement instanceof SerializedLambda))
                    break;// custom interface implementation
                SerializedLambda l = (SerializedLambda) replacement;
                if (Objects.equals(l.getCapturingClass(), l.getImplClass())) {
                    LOGGER.fatal("Detected unsafe referent usage, please view the code at {}",Thread.currentThread().getStackTrace()[3]);
                    throw new RuntimeException("Unsafe Referent usage found in safe referent method");
                }
            } catch (NoSuchMethodException e) {
            } catch (IllegalAccessException | InvocationTargetException e) {
                break;
            }
        }
    }
}
