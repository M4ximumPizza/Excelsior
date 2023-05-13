/*
 *  Copyright (c) 2023 M4ximumpizza - All Rights Reserved.
 */
package org.main.excelsior.compat;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.lenni0451.reflect.accessor.FieldAccessor;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormat;
import org.main.excelsior.Excelsior;

import java.lang.invoke.*;
import java.util.function.BooleanSupplier;

public class IrisCompat {

    public static boolean IRIS_LOADED = false;

    public static BooleanSupplier isRenderingLevel;
    public static BooleanConsumer renderWithExtendedVertexFormat;
    public static TriConsumer<BufferBuilder, VertexFormat.DrawMode, VertexFormat> iris$beginWithoutExtending;

    public static void init() {
        IRIS_LOADED = true;
        try {
            final MethodHandles.Lookup lookup = MethodHandles.lookup();
            final Class<?> immediateStateClass = Class.forName("net.coderbot.iris.vertices.ImmediateState");
            final Class<?> extendingBufferBuilderClass = Class.forName("net.coderbot.iris.vertices.ExtendingBufferBuilder");

            isRenderingLevel = FieldAccessor.makeGetter(BooleanSupplier.class, null, immediateStateClass.getDeclaredField("isRenderingLevel"));
            renderWithExtendedVertexFormat = FieldAccessor.makeSetter(BooleanConsumer.class, null, immediateStateClass.getDeclaredField("renderWithExtendedVertexFormat"));

            final MethodHandle iris$beginWithoutExtendingMH = lookup.findVirtual(extendingBufferBuilderClass, "iris$beginWithoutExtending", MethodType.methodType(void.class, VertexFormat.DrawMode.class, VertexFormat.class));
            final CallSite iris$beginWithoutExtendingCallSite = LambdaMetafactory.metafactory(lookup, "accept", MethodType.methodType(TriConsumer.class), MethodType.methodType(void.class, Object.class, Object.class, Object.class), iris$beginWithoutExtendingMH, iris$beginWithoutExtendingMH.type());
            iris$beginWithoutExtending = (TriConsumer<BufferBuilder, VertexFormat.DrawMode, VertexFormat>) iris$beginWithoutExtendingCallSite.getTarget().invoke();
        } catch (Throwable e) {
            Excelsior.LOGGER.error("Failed to initialize Iris compatibility. Try updating Iris before reporting this on GitHub", e);
            System.exit(-1);
        }
    }

}
