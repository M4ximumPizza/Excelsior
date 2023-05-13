/*
 *  Copyright (c) 2023 M4ximumpizza - All Rights Reserved.
 */
package org.main.excelsior.injection.interfaces;

import org.main.excelsior.feature.map_atlas_generation.MapAtlasTexture;

public interface IMapRenderer {

    MapAtlasTexture getMapAtlasTexture(int id);

    int getAtlasMapping(final int mapId);

}
