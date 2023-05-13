/*
 *  Copyright (c) 2023 M4ximumpizza - All Rights Reserved.
 */
package org.main.excelsior.injection.mixins.fast_text_lookup;

import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SinglePreparationResourceReloader.class)
public interface ISinglePreparationResourceReloader {

    @Invoker
    <T> T invokePrepare(ResourceManager manager, Profiler profiler);

    @Invoker
    void invokeApply(Object prepared, ResourceManager manager, Profiler profiler);

}
