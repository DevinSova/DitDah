package com.neoskeeter.ditdah;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

//TODO: Make AsyncTask Static or might cause leaks...
import com.neoskeeter.ditdah.Utilities.Translator;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    //Variables & Tasks
    boolean morseCodePlaying = false;
    MorseCodePlayerTask morseCodePlayerTask;
    private char Dit;
    private char Dah;
    private boolean soundEnabled;
    private boolean vibrateEnabled;
    private boolean flashEnabled;
    private CameraManager cameraManager;
    private String cameraID;

    //Tones
    final static int MORSE_BEEP_TONE = ToneGenerator.TONE_SUP_RADIO_ACK;
    final static int MORSE_BEEP_VOLUME = 500;
    final static int DIT_BEEP_DURATION = 50;
    final static int DAH_BEEP_DURATION = 200;


    //Widgets
    private FloatingActionButton mPlayPauseButton;
    private EditText mUserTranslatorInput;
    private TextView mTranslatedText;
    private BottomNavigationView mBottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        cameraManager = (CameraManager) getSystemService(getApplicationContext().CAMERA_SERVICE);
        try {
            cameraID = cameraManager.getCameraIdList()[0];
        } catch (Exception E) {
            //TODO: Create Alert Dialog
            Log.e(TAG, E.getMessage() + ": Unable to obtain CameraID, Flashing unavailable");
        }
        setupSharedPreferences();

        mBottomNavigationView = findViewById(R.id.bottom_navigation);
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.navigation_translator:
                        break;
                    case R.id.navigation_savedcodes:
                        break;
                }
                return true;
            }
        });

        mPlayPauseButton = findViewById(R.id.fab_playpause);
        mPlayPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!morseCodePlaying)
                    startPlayingMorse();
                else
                    stopPlayingMorse();
            }
        });

        /*
         * Automated Translator Portion.
         */
        mTranslatedText = findViewById(R.id.tv_translatedText);

        mUserTranslatorInput = findViewById(R.id.et_userTranslatorInput);
        mUserTranslatorInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Not used
            }

            //"before" is old length. "start" is where it started to change. "count" is how many characters after start.
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                mTranslatedText.setText(Translator.stringToMorse(charSequence.toString(), Dit, Dah));
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //Not used
            }
        });
        /*
         * End of Automated Translator Portion.
         */

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(morseCodePlaying)
        {
            stopPlayingMorse();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingsActivityIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsActivityIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Dit = sharedPreferences.getString(getString(R.string.pref_dit_representation_key), ".").charAt(0);
        Dah = sharedPreferences.getString(getString(R.string.pref_dah_representation_key), "-").charAt(0);
        soundEnabled = true;
        vibrateEnabled = true;
        flashEnabled = cameraID != null;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(getString(R.string.pref_dit_representation_key)))
            Dit = sharedPreferences.getString(getString(R.string.pref_dit_representation_key), ".").charAt(0);
        else if(key.equals(getString(R.string.pref_dah_representation_key)))
            Dah = sharedPreferences.getString(getString(R.string.pref_dah_representation_key), "-").charAt(0);
    }

    public class MorseCodePlayerTask extends AsyncTask<String, Void, Boolean>
    {
        private ToneGenerator toneGenerator;
        private Vibrator vibrator;
        private boolean torchOn;

        @Override
        protected Boolean doInBackground(String... strings) {
            toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, MORSE_BEEP_VOLUME);
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            torchOn = false;
            String morseCode = strings[0];
            for(int i = 0; i < morseCode.length(); i++)
            {
                //Checks if the task was cancelled
                if(isCancelled())
                    break;

                if(morseCode.charAt(i) == '.') {
                    soundVibrateFlash(DIT_BEEP_DURATION);
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        onCancelled();
                        Log.d(TAG, e.getMessage());
                    }
                }
                else if(morseCode.charAt(i) == '-') {
                    soundVibrateFlash(DAH_BEEP_DURATION);
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        onCancelled();
                        Log.d(TAG, e.getMessage());
                    }
                }
                else if(morseCode.charAt(i) == ' ')
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        onCancelled();
                        Log.d(TAG, e.getMessage());
                    }
                else if(morseCode.charAt(i) == '|')
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        onCancelled();
                        Log.d(TAG, e.getMessage());
                    }
            }
            //Returning True means it finished the whole morse message
            return true;
        }
        private void soundVibrateFlash(int duration) {
            if(flashEnabled)
                changeTorchMode();
            if(vibrateEnabled)
                vibrator.vibrate(duration);
            if(soundEnabled)
                toneGenerator.startTone(MORSE_BEEP_TONE, duration);
            if(flashEnabled)
                try {
                    Thread.sleep(duration);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            changeTorchMode();
        }

        private void changeTorchMode() {
            try {
                if(!torchOn) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        cameraManager.setTorchMode(cameraID, true);
                    }
                    torchOn = true;
                }
                else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        cameraManager.setTorchMode(cameraID, false);
                    }
                    torchOn = false;

                }
            } catch (CameraAccessException CAE) {
                Log.e(TAG, CAE.getMessage() + ": Unable to turn on torch.");
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            stopPlayingMorse();
        }

        @Override
        protected void onCancelled() {
            stopPlayingMorse();
        }
    }
    //Helper Methods
    protected void stopPlayingMorse()
    {
        mPlayPauseButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPlayButtonPlay)));
        mPlayPauseButton.setImageResource(android.R.drawable.ic_media_play);
        morseCodePlayerTask.cancel(true);
        morseCodePlaying = false;
    }

    protected void startPlayingMorse()
    {
        if(mUserTranslatorInput.getText().toString().isEmpty()) {
            Toast emptyError = Toast.makeText(this, getString(R.string.err_empty_input), Toast.LENGTH_SHORT);
            emptyError.show();
            return;
        }
        mPlayPauseButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPlayButtonPause)));
        mPlayPauseButton.setImageResource(android.R.drawable.ic_media_pause);
        morseCodePlayerTask = new MorseCodePlayerTask();
        morseCodePlayerTask.execute(mTranslatedText.getText().toString());
        morseCodePlaying = true;
    }
}
