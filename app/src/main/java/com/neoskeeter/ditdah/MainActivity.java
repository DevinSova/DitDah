//TODO: Simple Translator and player UI with Card like Look (See Google Translate for Ideas)


package com.neoskeeter.ditdah;

import android.content.res.ColorStateList;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.neoskeeter.ditdah.Utilities.Translator;

public class MainActivity extends AppCompatActivity {

    //Variables & Tasks
    boolean morseCodePlaying = false;
    MorseCodePlayerTask morseCodePlayerTask;

    //Tones
    int vMorseBeepTone = ToneGenerator.TONE_SUP_RADIO_ACK;
    int vDitDuration = 50;
    int vDahDuration = 200;
    int vMorseBeepToneVolume = 500;

    //Widgets
    private FloatingActionButton mPlayPauseButton;
    private EditText mUserTranslatorInput;
    private TextView mTranslatedText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mPlayPauseButton = findViewById(R.id.fab_playpause);
        mPlayPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!morseCodePlaying) {
                    morseCodePlaying = true;
                    mPlayPauseButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                    mPlayPauseButton.setImageResource(android.R.drawable.ic_media_pause);
                    morseCodePlayerTask = new MorseCodePlayerTask();
                    morseCodePlayerTask.execute("");
                }
                else {
                    morseCodePlayerTask.cancel(true);
                }
            }
        });

        /*
            Automated Translator Portion.
         */
        mTranslatedText = findViewById(R.id.tv_translatedText);

        mUserTranslatorInput = findViewById(R.id.et_userTranslatorInput);
        mUserTranslatorInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            //"before" is old length. "start" is where it started to change. "count" is how many characters after start.
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                mTranslatedText.setText(Translator.stringToMorse(charSequence.toString()));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class MorseCodePlayerTask extends AsyncTask<String, Void, Boolean>
    {
        @Override
        protected Boolean doInBackground(String... strings) {
            ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, vMorseBeepToneVolume);
            toneGenerator.startTone(vMorseBeepTone, vDitDuration);
            toneGenerator.startTone(vMorseBeepTone, vDahDuration);
            //Returning True means it finished the whole morse message
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            mPlayPauseButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
            mPlayPauseButton.setImageResource(android.R.drawable.ic_media_play);
            morseCodePlaying = false;
        }

        @Override
        protected void onCancelled() {
            mPlayPauseButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
            mPlayPauseButton.setImageResource(android.R.drawable.ic_media_play);
            morseCodePlaying = false;
        }
    }

}
