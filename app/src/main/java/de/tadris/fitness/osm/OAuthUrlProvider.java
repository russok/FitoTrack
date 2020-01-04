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

package de.tadris.fitness.osm;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;

class OAuthUrlProvider {

    static private final String CONSUMER_KEY= "jFL9grFmAo5ZS720YDDRXdSOb7F0IZQf9lnY1PHq";
    static private final String CONSUMER_SECRET= "oH969vYW60fZLco6E09UQl3uFXqjl4siQbOL0q9q";

    static OAuthConsumer getDefaultConsumer(){
        return new DefaultOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
    }

    static OAuthProvider getDefaultProvider(){
        return new DefaultOAuthProvider(URL_TOKEN_REQUEST, URL_TOKEN_ACCESS, URL_AUTHORIZE);
    }

    static private final String URL_TOKEN_REQUEST= "https://www.openstreetmap.org/oauth/request_token";
    static private final String URL_TOKEN_ACCESS=  "https://www.openstreetmap.org/oauth/access_token";
    static private final String URL_AUTHORIZE=     "https://www.openstreetmap.org/oauth/authorize";


}
