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

package de.tadris.fitness.location;

import android.graphics.drawable.Drawable;

import org.mapsforge.core.graphics.Canvas;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.Point;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.layer.Layer;
import org.mapsforge.map.layer.overlay.Circle;
import org.mapsforge.map.layer.overlay.Marker;

public class MyLocationOverlay extends Layer {

    private final Circle circle;
    private final Marker marker;
    private final LocationListener locationListener;

    private static Paint getDefaultFixedPixelCircleFill() {
        return getPaint(AndroidGraphicFactory.INSTANCE.createColor(255, 0, 0, 255), 0, Style.FILL);
    }

    private static Paint getDefaultOuterFixedPixelCircleFill(){
        return getPaint(AndroidGraphicFactory.INSTANCE.createColor(30, 30, 30, 255), 0, Style.FILL);
    }

    private static Paint getDefaultFixedPixelCircleStroke() {
        return getPaint(AndroidGraphicFactory.INSTANCE.createColor(255, 255, 255, 255), 7, Style.STROKE);
    }

    private static Paint getPaint(int color, int strokeWidth, Style style) {
        Paint paint = AndroidGraphicFactory.INSTANCE.createPaint();
        paint.setColor(color);
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(style);
        return paint;
    }

    public MyLocationOverlay(LocationListener locationListener, Drawable icon) {
        this.locationListener= locationListener;
        this.circle= new Circle(null, 0f, getDefaultFixedPixelCircleFill(), null);
        this.marker= new Marker(null, AndroidGraphicFactory.convertToBitmap(icon), 26, 26);
    }

    @Override
    public synchronized void draw(BoundingBox boundingBox, byte zoomLevel, Canvas canvas, Point topLeftPoint) {
        if (this.circle != null) {
            this.circle.draw(boundingBox, zoomLevel, canvas, topLeftPoint);
        }
        this.marker.draw(boundingBox, zoomLevel, canvas, topLeftPoint);
    }

    @Override
    protected void onAdd() {
        this.circle.setDisplayModel(this.displayModel);
        this.marker.setDisplayModel(this.displayModel);
    }

    @Override
    public void onDestroy() {
        this.marker.onDestroy();
    }

    public void setPosition(double latitude, double longitude, float accuracy) {
        synchronized (this) {
            LatLong latLong = new LatLong(latitude, longitude);
            this.marker.setLatLong(latLong);
            if (this.circle != null) {
                this.circle.setLatLong(latLong);
                this.circle.setRadius(accuracy);
            }
            requestRedraw();
        }
    }

}
