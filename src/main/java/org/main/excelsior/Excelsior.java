/*
 *  Copyright (c) 2023 M4ximumpizza - All Rights Reserved.
 */
package org.main.excelsior;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.main.excelsior.compat.IrisCompat;
import org.main.excelsior.feature.core.ExcelsiorConfig;
import org.main.excelsior.feature.core.ExcelsiorRuntimeConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Unsafe;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.nio.file.Files;

public class Excelsior implements ClientModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("Excelsior");
    public static final Unsafe UNSAFE = getUnsafe();
    public static ExcelsiorConfig config;
    public static ExcelsiorRuntimeConfig runtimeConfig;

    @Override
    public void onInitializeClient() {
        FabricLoader.getInstance().getModContainer("excelsior").ifPresent(modContainer -> {
            LOGGER.info("Loading ImmediatelyFast " + modContainer.getMetadata().getVersion().getFriendlyString());
        });
        if (!Excelsior.config.debug_only_and_not_recommended_disable_mod_conflict_handling) {
            FabricLoader.getInstance().getModContainer("iris").ifPresent(modContainer -> {
                LOGGER.info("Found Iris " + modContainer.getMetadata().getVersion().getFriendlyString() + ". Enabling compatibility.");
                IrisCompat.init();
            });
        }
        //System.load("C:\\Program Files\\RenderDoc\\renderdoc.dll");
    }

    public static void loadConfig() {
        final File configFile = FabricLoader.getInstance().getConfigDir().resolve("excelsior.json").toFile();
        if (configFile.exists()) {
            try {
                config = new Gson().fromJson(new FileReader(configFile), ExcelsiorConfig.class);
            } catch (Throwable e) {
                LOGGER.error("Failed to load ImmediatelyFast config. Resetting it.", e);
            }
        }
        if (config == null) {
            config = new ExcelsiorConfig();
        }
        try {
            Files.writeString(configFile.toPath(), new GsonBuilder().setPrettyPrinting().create().toJson(config));
        } catch (Throwable e) {
            LOGGER.error("Failed to save ImmediatelyFast config.", e);
        }
        resetRuntimeConfig();
    }

    public static void resetRuntimeConfig() {
        runtimeConfig = new ExcelsiorRuntimeConfig(config);
    }

    private static Unsafe getUnsafe() {
        try {
            for (Field field : Unsafe.class.getDeclaredFields()) {
                if (field.getType().equals(Unsafe.class)) {
                    field.setAccessible(true);
                    return (Unsafe) field.get(null);
                }
            }
        } catch (Throwable ignored) {
        }
        throw new IllegalStateException("Unable to get Unsafe instance");
    }

}