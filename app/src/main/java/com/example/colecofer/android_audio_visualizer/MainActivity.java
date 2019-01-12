package com.example.colecofer.android_audio_visualizer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;

public class MainActivity extends AppCompatActivity implements Player.NotificationCallback, ConnectionStateCallback {

    private final String MAIN_TAG = "MAIN_ACTIVITY";

    //TODO: This is Spotify's test account because I don't want to hard code ours into a public repository...
    private static final String CLIENT_ID    = "089d841ccc194c10a77afad9e1c11d54";
    private static final String REDIRECT_URI = "testschema://callback";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public void onLoggedIn() {
        log("Login complete");
    } 

    @Override
    public void onLoggedOut() {
        log("Logout complete");
    }

    @Override
    public void onLoginFailed(Error error) {
        log("On login failed: " + error);
    }

    @Override
    public void onTemporaryError() {
        log("Temporary error occured.");
    }

    @Override
    public void onConnectionMessage(String s) {
        log("On connection message: " + s);
    }

    @Override
    public void onPlaybackError(Error error) {
        log("onPlaybackError: " + error);
    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {

    }

    /**
     * Removes the need to specify the TAG each time you log.
     * @param message The message to log
     */
    public void log(String message) { Log.d(MAIN_TAG, message);}

}
