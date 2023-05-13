/*
 *  Copyright (c) 2023 M4ximumpizza - All Rights Reserved.
 */
package org.main.excelsior.feature.core;

public class ExcelsiorConfig {

    // Regular config values
    public boolean font_atlas_resizing = true;
    public boolean map_atlas_generation = true;
    public boolean hud_batching = true;
    public boolean fast_text_lookup = true;
    public boolean fast_buffer_upload = true;

    // Cosmetic config values
    public boolean dont_add_info_into_debug_hud = false;

    // Experimental config values
    public boolean experimental_item_hud_batching = false;
    public boolean experimental_disable_error_checking = false;

    // Debug config values
    public boolean debug_only_and_not_recommended_disable_universal_batching = false;
    public boolean debug_only_and_not_recommended_disable_mod_conflict_handling = false;
}