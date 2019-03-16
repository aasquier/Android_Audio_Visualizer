package com.example.colecofer.android_audio_visualizer;

final class Constants {

    /** Visualizer Switching */
    static final boolean SHOULD_SWITCH_VIS = true;          //Set to false if you do not want to rotate visualizers
    static final boolean SHOULD_LOOP_VIS   = true;          //Set to true if you want the final visualizer to loop back to the first
    static final int SWITCH_VIS_TIME       = 5000;          //Number of milliseconds to wait before switching

    /** VisualizerActivity constants */
    static final boolean USE_SPOTIFY        = false;        //Set to true to use spotify, and false to use media player (local files)
    static final int REQUEST_PERMISSION     = 101;
    static final int REAL_BUCKET_INDEX      = 3;
    static final int IMAGINARY_BUCKET_INDEX = 4;
    static final int MAX_FFT_ARRAY_SIZE     = 1024;
    static final float PIXEL                = 0.0009f;

    /** Lyrics Animation */
    static final boolean DEMO_MODE                 = true;  //Setting to true will display the biggie small lyrics in the same format as the demo0
    static final int MAX_HEIGHT_OFFSET             = 35;    //Amount of pixels the textView is allowed to scroll until it stops
    static final int SCROLL_LYRICS_SPEED           = 1;     //How fast in pixels/update time (~once per 16millis)
    static final int LEFT_PADDING                  = 100;   //LyricsTextView padding locations
    static final int RIGHT_PADDING                 = 100;
    static final int BOTTOM_PADDING                = 100;
    static final float LYRICS_TEXT_SIZE            = 22.0f;
    static final float PERCENT_FROM_TOP            = 0.65f; //Percentage of screen height to display lyrics at
    static final float DISPLAY_MULTILINE_PROXIMITY = 2000.0f; //If time between lyrics are smaller than this amount, then multiple lyrics will display on the screen at the same time
    static final float LYRIC_DISPLAY_OFFSET        = 200.0f;  //Offset of time to display lyrics before they are actually sung in the song

    /** MainActivity constants */
    //TODO: This is Spotify's test account because I don't want to hard code ours into a public repository...
    //Used to verify that we've been redirected back from Spotify after authenticating in browser
    static final String CLIENT_ID                             = "089d841ccc194c10a77afad9e1c11d54";
    static final String REDIRECT_URI                          = "testschema://callback";
    static final String TRACK_BASE_URI                        = "spotify:track:";
    static final String[] SCOPES                              = new String[]{"user-read-private", "playlist-read", "playlist-read-private", "streaming"};
    static final int REQUEST_RECORD_PERMISSION                = 101;
    static final int REQUEST_READ_EXTERNAL_STORAGE_PERMISSION = 102;
    static final int REQUEST_CODE                             = 1337;


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
    static final int MEDIUM_HIBERNATION_TIME = 100;
    static final int HIGH_HIBERNATION_TIME   = 50;
    static final float MAX_DB_LEVEL          = 170.0f;
    static final float MAX_DECIBEL_RATIO     = 1.0f;
    static final long REFRESH_DECIBEL_TIME   = 16L;

    /** Shared Visualizer Constants **/
    static final String GLSL_POSITION_HANDLE     = "a_Position";
    static final String GLSL_COLOR_HANDLE        = "a_Color";
    static final String GLSL_DB_LEVEL            = "a_DB_Level";
    static final String GLSL_MATRIX              = "u_MVPMatrix";
    static final String GLSL_TIME                = "time";
    static final String GLSL_SCALING_LEVEL_ARRAY = "scaling_Level";
    static final String GLSL_SCREEN_RATIO        = "screen_ratio";
    static final float COLOR_SHIFT_FACTOR        = 0.005f;
    static final int HIGH_HIGHLIGHTING_PULSE     = 12;
    static final int MEDIUM_HIGHLIGHTING_PULSE   = 51;

    /** GLLine constants for Vis1*/
    static final int BYTES_PER_FLOAT = 4;
    /** Vis1 constants */
    static final int LINE_AMT                = 100;                 //Number of lines to display on the screen
    static final int DECIBEL_HISTORY_SIZE_V1 = 100;
    static final int VIS1_VERTEX_COUNT       = DECIBEL_HISTORY_SIZE_V1 * 2;
    static final int VIS1_ARRAY_SIZE         = VIS1_VERTEX_COUNT * 7;
    static final int VERTEX_AMOUNT           = 7;                   //x, y, z, r, g, b, a
    static final int POSITION_DATA_SIZE      = 3;
    static final int VIS1_STRIDE_BYTES       = 7 * BYTES_PER_FLOAT;
    static final int POSITION_OFFSET         = 0;
    static final int COLOR_OFFSET            = 3;
    static final int COLOR_DATA_SIZE         = 4;
    static final float LEFT_DRAW_BOUNDARY    = -0.99f;              //Where to start drawing on the left side of the screen
    static final float RIGHT_DRAW_BOUNDARY   = 0.99f;               //Right side of the screen boundary
    static final float AMPLIFIER_V1          = 0.0000095f;
    static final float DEFAULT_LINE_SIZE_V1  = 0.00009f;

    /** Vis2 constants */
    static final int VIS2_STRIDE_BYTES       = (POSITION_DATA_SIZE + COLOR_DATA_SIZE) * BYTES_PER_FLOAT;
    static final int DECIBEL_HISTORY_SIZE_V2 = DECIBEL_HISTORY_SIZE_V1 * 2;
    /** GLDot constants for Vis2 */
    static final int DOT_HEIGHT = 600;
    static final int DOT_WIDTH  = 600;
    static final int DOT_COUNT  = DOT_WIDTH * DOT_HEIGHT;

    /** Vis3 constants */
    static final String GLSL_FRACTAL_STRENGTH = "line_Fractal_Strength";
    static final int DECIBEL_HISTORY_SIZE_V3  = 25;
    static final int VIS3_VERTEX_COUNT        = DECIBEL_HISTORY_SIZE_V3 * 2;
    static final int VIS3_ARRAY_SIZE          = VIS3_VERTEX_COUNT * 7;
    static final int LINE_AMT_V3              = 20;
    static final float AMPLIFIER_V3           = 0.00005f;
    static final float DEFAULT_LINE_SIZE_V3   = 0.002f;

    /** VisualizerModel constants */
    static final String MODEL_TAG = "MODEL_TAG";
}
