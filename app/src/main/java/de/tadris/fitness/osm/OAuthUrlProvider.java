package de.tadris.fitness.osm;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;

public class OAuthUrlProvider {

    static private final String CONSUMER_KEY= "jFL9grFmAo5ZS720YDDRXdSOb7F0IZQf9lnY1PHq";
    static private final String CONSUMER_SECRET= "oH969vYW60fZLco6E09UQl3uFXqjl4siQbOL0q9q";

    static OAuthConsumer getDefaultConsumer(){
        return new CommonsHttpOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
    }

    static OAuthProvider getDefaultProvider(){
        return new CommonsHttpOAuthProvider(URL_TOKEN_REQUEST, URL_TOKEN_ACCESS, URL_AUTHORIZE);
    }

    static private final String URL_TOKEN_REQUEST= "https://www.openstreetmap.org/oauth/request_token";
    static private final String URL_TOKEN_ACCESS=  "https://www.openstreetmap.org/oauth/access_token";
    static private final String URL_AUTHORIZE=     "https://www.openstreetmap.org/oauth/authorize";


}
