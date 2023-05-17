/*
 *  Copyright (c) 2023 M4ximumpizza - All Rights Reserved.
 */

package org.main.excelsior.feature.core;

public class ExcelsiorRuntimeConfig {

    public boolean hud_batching;

    public ExcelsiorRuntimeConfig(final ExcelsiorConfig config) {
        this.hud_batching = config.hud_batching;
    }
}