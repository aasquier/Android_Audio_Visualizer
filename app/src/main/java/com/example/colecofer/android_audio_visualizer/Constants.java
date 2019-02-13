package com.example.colecofer.android_audio_visualizer;

final class Constants {


    /** GLCircle constants */
    static final int COUNT = 364;


    /** GLLine constants */
    static final int BYTES_PER_FLOAT = 4;


    /** MainActivity constants */
    //TODO: This is Spotify's test account because I don't want to hard code ours into a public repository...
    //Used to verify that we've been redirected back from Spotify after authenticating in browser
    static final String CLIENT_ID                             = "089d841ccc194c10a77afad9e1c11d54";
    static final int REQUEST_RECORD_PERMISSION                = 101;
    static final int REQUEST_READ_EXTERNAL_STORAGE_PERMISSION = 102;
    static final int REQUEST_CODE                             = 1337;
    static final String REDIRECT_URI                          = "testschema://callback";
    static final String TRACK_BASE_URI                        = "spotify:track:";
    static final String[] SCOPES                              = new String[]{"user-read-private", "playlist-read", "playlist-read-private", "streaming"};


    /** SpotifyClient constants */
    //TODO: Change these to our personal information (this is a public repo...)
    static final String BASE_URL             = "https://api.spotify.com/v1";
    static final String FEATURES_URL         = "/audio-features/";
    static final String SEARCH_URL           = "/search/";
    static final String TRACK_URL            = "/tracks/";
    static final String AUTH_URL             = "https://accounts.spotify.com/api/token";
    static final String SPOTIFY_CLIENT_ID    = "5f0eac9db12042cfa8b9fb95b0f3f4d8";
    static final String CLIENT_SECRET        = "4f0d128f8f1b4776a530292cdef1dd45";
    static final String SPOTIFY_TAG          = "SPOTIFY";
    static final int DEFAULT_PRIMARY_COLOR   = 0xFFF16C4E;
    static final int DEFAULT_SECONDARY_COLOR = 0xFF0FADB6;
    static final int DEFAULT_TERTIARY_COLOR  = 0xFFDEDD64;


    /** Utility constants */
    static final float MAX_DB_LEVEL        = 170.0f;
    static final long REFRESH_DECIBEL_TIME = 16L;
    static final float MAX_DECIBEL_RATIO   = 1.0f;

    /** Shared Visualizer Constants **/
    static final String GLSL_POSITION_HANDLE = "a_Position";
    static final String GLSL_COLOR_HANDLE = "a_Color";
    static final String GLSL_DB_LEVEL = "a_DB_Level";
    static final String GLSL_TIME = "time";

    /** Vis1 constants */
    static final int LINE_AMT              = 10;                  //Number of lines to display on the screen
    static final float AMP_MULT            = 0.000005f;           //Alters the lines horizontal amplitude
    static final int VERTEX_AMOUNT         = 7;                   //x, y, z, r, g, b, a
    static final float LEFT_DRAW_BOUNDARY  = -0.99f;              //Where to start drawing on the left side of the screen
    static final float RIGHT_DRAW_BOUNDARY = 0.99f;               //Right side of the screen boundary
    static final int POSITION_DATA_SIZE    = 3;
    static final int VIS1_STRIDE_BYTES     = 7 * BYTES_PER_FLOAT;
    static final int POSITION_OFFSET       = 0;
    static final int COLOR_OFFSET          = 3;
    static final int COLOR_DATA_SIZE       = 4;

    /** Vis2 constants */
    static final int VIS2_STRIDE_BYTES = (POSITION_DATA_SIZE + COLOR_DATA_SIZE) * BYTES_PER_FLOAT;

    /** GLDot constants for Vis2*/
    static final int DOT_HEIGHT = 600;
    static final int DOT_WIDTH  = 600;
    static final int DOT_COUNT  = DOT_WIDTH * DOT_HEIGHT;

    /** VisualizerActivity constants */
    static final int REQUEST_PERMISSION     = 101;
    static final int REAL_BUCKET_INDEX      = 5;
    static final int IMAGINARY_BUCKET_INDEX = 6;
    static final int MAX_FFT_ARRAY_SIZE     = 1024;
    static final int SCREEN_VERTICAL_HEIGHT = 80;
    static final int VIS1_VERTEX_COUNT = SCREEN_VERTICAL_HEIGHT + SCREEN_VERTICAL_HEIGHT;
    static final int VIS1_ARRAY_SIZE = VIS1_VERTEX_COUNT * 7;
    static final float PIXEL = 0.03f;
    static final float AMPLIFIER = 3.0f;

    /** VisualizerModel constants */
    static final String MODEL_TAG = "MODEL_TAG";
    static final int SWITCH_VIS_TIME = 2000;   //Amount of time to switch from the first visualizer to the second.

}
