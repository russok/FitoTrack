/*
 * Copyright (c) 2020 Jannis Scheibe <jannis@tadris.de>
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

package de.tadris.fitness.activity;

import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import de.tadris.fitness.R;

public abstract class InformationActivity extends FitoTrackActivity {

    ViewGroup root;

    protected void addTitle(String title) {
        TextView textView = new TextView(this);
        textView.setText(title);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        textView.setTextColor(getThemePrimaryColor());
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setAllCaps(true);
        textView.setPadding(0, 20, 0, 0);

        root.addView(textView);
    }

    protected TextView addText(String text, boolean themeColor) {
        TextView textView = createTextView(text, themeColor);
        root.addView(textView);

        return textView;
    }

    protected TextView createTextView(String text) {
        return createTextView(text, false);
    }

    protected TextView createTextView(String text, boolean themeColor) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        if (themeColor) {
            textView.setTextColor(getThemePrimaryColor());
        } else {
            textView.setTextColor(getResources().getColor(R.color.textLighterBlack));
        }
        textView.setPadding(0, 20, 0, 0);

        return textView;
    }

    protected void addKeyValue(String key1, String value1) {
        addKeyValue(key1, value1, "", "");
    }

    protected void addKeyValue(String key1, String value1, String key2, String value2) {
        View v = getLayoutInflater().inflate(R.layout.show_entry, root, false);

        TextView title1 = v.findViewById(R.id.v1title);
        TextView title2 = v.findViewById(R.id.v2title);
        TextView v1 = v.findViewById(R.id.v1value);
        TextView v2 = v.findViewById(R.id.v2value);

        title1.setText(key1);
        title2.setText(key2);
        v1.setText(value1);
        v2.setText(value2);

        root.addView(v);
    }

    protected KeyValueLine addKeyValueLine(String key) {
        return addKeyValueLine(key, "");
    }

    protected KeyValueLine addKeyValueLine(String key, String value) {
        return addKeyValueLine(key, null, value);
    }

    protected KeyValueLine addKeyValueLine(String key, @Nullable View view) {
        return addKeyValueLine(key, view, "");
    }

    protected KeyValueLine addKeyValueLine(String key, @Nullable View view, String value) {
        View v = getLayoutInflater().inflate(R.layout.enter_workout_line, root, false);

        TextView keyView = v.findViewById(R.id.lineKey);
        TextView valueView = v.findViewById(R.id.lineValue);
        LinearLayout customViewRoot = v.findViewById(R.id.lineViewRoot);

        keyView.setText(key);
        valueView.setText(value);

        if (view != null) {
            customViewRoot.addView(view);
        }

        root.addView(v);
        return new KeyValueLine(v, keyView, valueView, view);
    }

    public static class KeyValueLine {
        public View lineRoot;
        public TextView key;
        public TextView value;
        public View customView;

        public KeyValueLine(View lineRoot, TextView key, TextView value, View customView) {
            this.lineRoot = lineRoot;
            this.key = key;
            this.value = value;
            this.customView = customView;
        }
    }

    abstract void initRoot();


}
