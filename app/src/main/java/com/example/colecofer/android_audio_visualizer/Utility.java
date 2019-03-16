package com.example.colecofer.android_audio_visualizer;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.RawRes;
import android.util.Log;
import android.util.Pair;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.example.colecofer.android_audio_visualizer.Constants.DECIBEL_HISTORY_UPDATE_SIZE;
import static com.example.colecofer.android_audio_visualizer.Constants.HIGH_HIBERNATION_TIME;
import static com.example.colecofer.android_audio_visualizer.Constants.HIGH_HIGHLIGHTING_PULSE;
import static com.example.colecofer.android_audio_visualizer.Constants.MAX_DB_LEVEL;
import static com.example.colecofer.android_audio_visualizer.Constants.MAX_DECIBEL_RATIO;
import static com.example.colecofer.android_audio_visualizer.Constants.MEDIUM_HIBERNATION_TIME;
import static com.example.colecofer.android_audio_visualizer.Constants.MEDIUM_HIGHLIGHTING_PULSE;
import static com.example.colecofer.android_audio_visualizer.Constants.REFRESH_DECIBEL_TIME;
import static com.example.colecofer.android_audio_visualizer.VisualizerActivity.decibelHistory;

public class Utility {

    private Context context;
    static boolean highlightingOnHigh               = false;
    static boolean highlightingOnMedium             = false;
    static boolean highlightingHibernation          = false;
    static int highlightingDuration                 = 0;
    private static int highlightingHibernationCount = 0;

      /**
       * Pass context for connecting Resource
       *
       *context* *
       */
      public Utility(Context context) {
            this.context = context;
      }

    /**
     * Takes the real and imaginary parts of an FFT frequency bin and returns the decibels for that bin.
     */
    static double getDBs(byte real, byte imaginary, int n) {
        /** Bit shifting to translate our bytes into doubles */
        double y = (real | imaginary << 8) / 32768.0;

        /** Finds the Root Mean Square and transforms it into dBs, ensures it does not pass
         *  the Log10 a 0.0 which would return Inf */
        if (y != 0.0) {
            return Math.abs(10.0 * Math.log10((4.0 * y * y) / (n * n)));
        } else {
            return 0.0;
        }
    }

    /**
     * Keeps a record of recent dBs as large as the screen is tall. It removes the last record and
     * removes the oldest record. If the current dB level exceeds our max setting it uses the max
     *
     * @param newDecibelLevel
     */
    static Pair<Long, Boolean> updateDecibelHistory(double newDecibelLevel, long previousUpdateTime) {
        Pair<Long, Boolean> isTimeToUpdate = isTimeToUpdate(previousUpdateTime);


        /** A check to ensure that the current time has exceeded the desired refresh time */
        if (isTimeToUpdate.second) {
            float newDbRatio = (float) newDecibelLevel / MAX_DB_LEVEL;
            newDbRatio = newDbRatio > MAX_DECIBEL_RATIO ? MAX_DECIBEL_RATIO : newDbRatio;
            float elementToInsert = 0.0f;

            if (VisualizerModel.getInstance().currentVisualizer instanceof VisOne) {
//
//                decibelHistory.removeLast();
//                decibelHistory.removeLast();
//                decibelHistory.removeLast();
//                decibelHistory.removeLast();
//                decibelHistory.removeLast();
                decibelHistory.removeLast();

                float rand = (float)Math.random();

                /** Update the decibel history with the current decibel level */
                if (highlightingOnMedium) {
                    if (highlightingDuration >= DECIBEL_HISTORY_UPDATE_SIZE * 9) {
                        if(highlightingDuration >= MEDIUM_HIGHLIGHTING_PULSE - 4) {
                            elementToInsert = 0.6f - 0.049f * rand;
                        } else {
                            if(rand > 0.5f) {
                                elementToInsert = 0.65f - 0.049f * rand;
                            } else {
                                elementToInsert = 0.60f + 0.34f * rand;
                            }
                        }
                    } else {
                        elementToInsert = 0.67f + 0.33f * rand;
                    }
                } else if (highlightingOnHigh) {
                    if (highlightingDuration >= DECIBEL_HISTORY_UPDATE_SIZE * 4) {
                        if(highlightingDuration >= HIGH_HIGHLIGHTING_PULSE - 3) {
                            elementToInsert = 0.65f - 0.049f * rand;
                        } else {
                            //elementToInsert = 0.7f + 0.33f * rand;
                            if(rand > 0.5f) {
                                elementToInsert = 0.65f - 0.049f * rand;
                            } else {
                                elementToInsert = 0.7f + 0.33f * rand;
                            }
                        }
                    } else {
                        elementToInsert = 0.65f - 0.049f * rand;
                    }
                } else if (highlightingHibernation) {
                    if (newDbRatio > 0.65f) {
                        elementToInsert = 0.6f - 0.049f * rand;
                    } else if (newDbRatio > 0.6f) {
                        elementToInsert = 0.55f - 0.55f * rand;
                    } else {
                        elementToInsert = newDbRatio;
                    }
                } else {
                    // This is if high highlighting is first being seen
                    if(elementToInsert > 0.65f) {
                        elementToInsert = 0.65f - 0.049f * rand;
                    // This is if medium highlighting is first being seen
                    } else if(elementToInsert > 0.6f) {
                        elementToInsert = 0.6f - 0.049f * rand;
                    // Not highlighted at all
                    } else {
                        elementToInsert = newDbRatio;
                    }
                }

//                decibelHistory.addFirst(elementToInsert);
//                decibelHistory.addFirst(elementToInsert);
//                decibelHistory.addFirst(elementToInsert);
//                decibelHistory.addFirst(elementToInsert);
//                decibelHistory.addFirst(elementToInsert);
                decibelHistory.addFirst(elementToInsert);

                if (highlightingOnMedium || highlightingOnHigh) {
                    highlightingDuration -= DECIBEL_HISTORY_UPDATE_SIZE;
                    if (highlightingDuration <= 0) {
                        if (highlightingOnMedium) {
                            highlightingOnMedium = false;
                            highlightingHibernationCount = MEDIUM_HIBERNATION_TIME;
                        } else {
                            highlightingOnHigh = false;
                            highlightingHibernationCount = HIGH_HIBERNATION_TIME;
                        }
                        highlightingHibernation = true;
                    }
                }
                if (highlightingHibernation) {
                    highlightingHibernationCount -= DECIBEL_HISTORY_UPDATE_SIZE;
                    if (highlightingHibernationCount <= 0) {
                        highlightingHibernation = false;
                    }
                }
            } else if(VisualizerModel.getInstance().currentVisualizer instanceof VisTwo){
                if(newDbRatio <= 0.35f) {
                    newDbRatio = 0.5f;
                } else if (newDbRatio <= 0.55f) {
                    newDbRatio = 1.0f;
                } else if (newDbRatio <= 0.75f){
                    newDbRatio = 1.5f;
                } else {
                    newDbRatio = 4.5f;
                }

                decibelHistory.removeLast();
                decibelHistory.addFirst(newDbRatio);
            } else {
                // The decibel history should be more granular for vis3
                decibelHistory.removeLast();
                decibelHistory.removeLast();
                /** Update the decibel history with the current decibel level */
                decibelHistory.addFirst(newDbRatio);
                decibelHistory.addFirst(newDbRatio);
            }
        }

        return isTimeToUpdate;
    }

    /**
     * Checks if it has been our predefined interval since last dB record update
     *
     * @param previousUpdateTime
     * @return
     */
    static Pair<Long, Boolean> isTimeToUpdate(long previousUpdateTime) {
        Boolean success;
        Long currentTime = System.currentTimeMillis();
        if (previousUpdateTime + REFRESH_DECIBEL_TIME <= currentTime) {
            previousUpdateTime = currentTime;
            success = true;
        } else {
            success = false;
        }
        return new Pair(previousUpdateTime, success);
    }

    /**
     * Convert a glsl file into  a string for each visualizer
     *
     * @param id
     */

    public String getStringFromGLSL(@RawRes int id) {
        String str;

        try {
            Resources res = context.getResources();
            InputStream is = res.openRawResource(id);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int i = is.read();
            while (i != -1) {
                byteArrayOutputStream.write(i);
                i = is.read();
            }
            str = byteArrayOutputStream.toString();
            is.close();
        } catch (IOException e) {
            Log.d("GLSL", "Failed to convert GLSL file into a proper String. " + e.getMessage());
            str = "";
        }

        return str;
    }
}