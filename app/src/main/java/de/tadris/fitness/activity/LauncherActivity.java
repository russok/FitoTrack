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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import de.tadris.fitness.Instance;
import de.tadris.fitness.R;

public class LauncherActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Theme_Material_Light_NoActionBar);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onResume(){
        super.onResume();
        new Handler().postDelayed(this::init, 100);
    }

    private void init() {
        Instance.getInstance(this);
        start();
    }

    private void start() {
        startActivity(new Intent(this, ListWorkoutsActivity.class));
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.stay);
    }
}
