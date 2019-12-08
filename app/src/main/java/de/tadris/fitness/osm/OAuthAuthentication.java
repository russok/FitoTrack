package de.tadris.fitness.osm;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.provider.Browser;
import android.text.InputType;
import android.widget.EditText;

import de.tadris.fitness.R;
import de.tadris.fitness.view.ProgressDialogController;
import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.exception.OAuthException;

public class OAuthAuthentication {

    private OAuthConsumer oAuthConsumer= OAuthUrlProvider.getDefaultConsumer();
    private OAuthProvider oAuthProvider= OAuthUrlProvider.getDefaultProvider();

    private Handler handler;
    private Activity activity;
    private ProgressDialogController dialogController;
    private SharedPreferences preferences;
    private OAuthAuthenticationListener listener;

    public OAuthAuthentication(Handler handler, Activity activity, OAuthAuthenticationListener listener) {
        this.handler = handler;
        this.activity = activity;
        dialogController= new ProgressDialogController(activity, activity.getString(R.string.uploading));
        this.preferences= activity.getSharedPreferences("osm_oauth", Context.MODE_PRIVATE);
        this.listener= listener;
    }

    public void authenticateIfNecessary(){
        if(isAuthenticated()){
            loadAccessToken();
            listener.authenticationComplete(oAuthConsumer);
        }else{
            retrieveRequestToken();
        }
    }

    private boolean isAuthenticated(){
        return preferences.getBoolean("authenticated", false);
    }

    private void retrieveRequestToken(){
        dialogController.show();
        dialogController.setIndeterminate(true);
        new Thread(() -> {
            try {
                String authUrl = oAuthProvider.retrieveRequestToken(oAuthConsumer, OAuth.OUT_OF_BAND);
                handler.post(() -> {
                    Intent intent= new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl));
                    intent.putExtra(Browser.EXTRA_APPLICATION_ID, activity.getPackageName());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    activity.startActivity(intent);
                    showEnterVerificationCodeDialog();
                    dialogController.cancel();
                });
            } catch (OAuthException e) {
                e.printStackTrace();
                handler.post(() -> {
                    dialogController.cancel();
                    listener.authenticationFailed();
                });
            }
        }).start();
    }

    private void showEnterVerificationCodeDialog(){
        EditText editText= new EditText(activity);
        editText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        editText.setSingleLine(true);

        AlertDialog dialog= new AlertDialog.Builder(activity)
                .setTitle(R.string.enterVerificationCode).setView(editText).setPositiveButton(R.string.okay, (dialogInterface, i) -> {
                    new Thread(() -> retrieveAccessToken(editText.getText().toString().trim())).start();
                    dialogInterface.cancel();
                }).setNegativeButton(R.string.cancel, null).setCancelable(false).create();
        dialog.show();
    }

    private void loadAccessToken(){
        oAuthConsumer.setTokenWithSecret(preferences.getString("accessToken", ""),
                preferences.getString("tokenSecret", ""));
    }

    private void saveAccessToken(){
        preferences.edit()
                .putString("accessToken", oAuthConsumer.getToken())
                .putString("tokenSecret", oAuthConsumer.getTokenSecret())
                .putBoolean("authenticated", true).apply();
    }

    public void clearAccessToken(){
        preferences.edit().putBoolean("authenticated", false).apply();
    }

    private void retrieveAccessToken(String verificationCode){
        handler.post(() -> dialogController.show());
        try{
            oAuthProvider.retrieveAccessToken(oAuthConsumer, verificationCode);
            handler.post(() -> {
                dialogController.cancel();
                saveAccessToken();
                listener.authenticationComplete(oAuthConsumer);
            });
        }catch (OAuthException e){
            handler.post(() -> {
                dialogController.cancel();
                listener.authenticationFailed();
            });
            e.printStackTrace();
        }
    }

    public interface OAuthAuthenticationListener{

        void authenticationFailed();

        void authenticationComplete(OAuthConsumer consumer);

    }

}
