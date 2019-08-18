/*
 * Copyright (c) 2019 Jannis Scheibe <jannis@tadris.de>
 *
 * This file is part of FitoTrack
 *
 * FitoTrack is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     FitoTrack is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.tadris.fitness.map;

import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.download.TileDownloadLayer;

public class MapManager {

    public static TileDownloadLayer setupMap(MapView mapView){
        mapView.setZoomLevelMin((byte) 18);
        mapView.setZoomLevelMax((byte) 18);
        mapView.setBuiltInZoomControls(false);

        TileCache tileCache = AndroidUtil.createTileCache(mapView.getContext(), "mapcache",
                mapView.getModel().displayModel.getTileSize(), 1f,
                mapView.getModel().frameBufferModel.getOverdrawFactor(), true);

        HumanitarianTileSource tileSource = HumanitarianTileSource.INSTANCE;
        tileSource.setUserAgent("mapsforge-android");
        TileDownloadLayer downloadLayer = new TileDownloadLayer(tileCache, mapView.getModel().mapViewPosition, tileSource, AndroidGraphicFactory.INSTANCE);

        mapView.getLayerManager().getLayers().add(downloadLayer);

        mapView.getLayerManager().redrawLayers();

        mapView.setZoomLevel((byte) 18);
        mapView.setCenter(new LatLong(52, 13));

        return downloadLayer;
    }

}