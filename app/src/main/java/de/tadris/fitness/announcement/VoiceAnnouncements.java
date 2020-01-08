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

package de.tadris.fitness.announcement;

import android.content.Context;
import android.media.AudioManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import java.util.Locale;

import de.tadris.fitness.Instance;
import de.tadris.fitness.data.UserPreferences;
import de.tadris.fitness.recording.WorkoutRecorder;
import de.tadris.fitness.util.unit.UnitUtils;

public class VoiceAnnouncements {

    private TextToSpeech textToSpeech;
    private boolean ttsAvailable;
    private VoiceAnnouncementCallback callback;
    private final AnnouncementManager manager;

    private long lastSpokenUpdateTime = 0;
    private int lastSpokenUpdateDistance = 0;

    private final AnnouncementMode currentMode;
    private final long intervalTime;
    private final int intervalInMeters;

    private final AudioManager audioManager;

    public VoiceAnnouncements(Context context, VoiceAnnouncementCallback callback) {
        this.callback = callback;
        UserPreferences prefs = Instance.getInstance(context).userPreferences;
        textToSpeech = new TextToSpeech(context, this::ttsReady);

        this.intervalTime = 60 * 1000 * prefs.getSpokenUpdateTimePeriod();
        this.intervalInMeters = (int) (1000.0 / UnitUtils.CHOSEN_SYSTEM.getDistanceFromKilometers(1) * prefs.getSpokenUpdateDistancePeriod());

        this.manager = new AnnouncementManager(context);
        this.audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        this.currentMode = AnnouncementMode.getCurrentMode(context);
    }

    private void ttsReady(int status) {
        ttsAvailable = status == TextToSpeech.SUCCESS && textToSpeech.setLanguage(Locale.getDefault()) >= 0;
        if (ttsAvailable) {
            textToSpeech.setOnUtteranceProgressListener(new TextToSpeechListener());
        }
        callback.onVoiceAnnouncementIsReady(ttsAvailable);
    }

    public void check(WorkoutRecorder recorder) {
        if (!ttsAvailable) {
            return;
        } // Cannot speak

        boolean shouldSpeak = false;

        if (intervalTime != 0 && recorder.getDuration() - lastSpokenUpdateTime > intervalTime) {
            shouldSpeak = true;
        }
        if (intervalInMeters != 0 && recorder.getDistanceInMeters() - lastSpokenUpdateDistance > intervalInMeters) {
            shouldSpeak = true;
        }

        if (shouldSpeak) {
            speak(recorder);
        }
    }

    private void speak(WorkoutRecorder recorder) {
        for (Announcement announcement : manager.getAnnouncements()) {
            speak(recorder, announcement);
        }

        lastSpokenUpdateTime = recorder.getDuration();
        lastSpokenUpdateDistance = recorder.getDistanceInMeters();
    }

    private void speak(WorkoutRecorder recorder, Announcement announcement) {
        if (!announcement.isEnabled()) {
            return;
        }
        String text = announcement.getSpoken(recorder);
        if (!text.equals("")) {
            speak(text);
        }
    }

    private int speakId = 1;

    public void speak(String text) {
        if (!ttsAvailable) {
            // Cannot speak
            return;
        }
        if (currentMode == AnnouncementMode.HEADPHONES && !audioManager.isWiredHeadsetOn()) {
            // Not allowed to speak
            return;
        }
        Log.d("Recorder", "TTS speaks: " + text);
        textToSpeech.speak(text, TextToSpeech.QUEUE_ADD, null, "announcement" + (++speakId));
    }

    public void destroy() {
        textToSpeech.shutdown();
    }

    private class TextToSpeechListener extends UtteranceProgressListener {

        @Override
        public void onStart(String utteranceId) {
            audioManager.requestAudioFocus(null, AudioManager.STREAM_SYSTEM, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);
        }

        @Override
        public void onDone(String utteranceId) {
            audioManager.abandonAudioFocus(null);
        }

        @Override
        public void onError(String utteranceId) {
        }
    }

    public interface VoiceAnnouncementCallback {
        void onVoiceAnnouncementIsReady(boolean available);
    }
}
