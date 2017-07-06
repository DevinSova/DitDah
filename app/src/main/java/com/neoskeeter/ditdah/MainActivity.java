//TODO: Dit and DAh sounds to "raw" folder
//TODO: Simple Translator and player UI with Card like Look (See Google Translate for Ideas)


package com.neoskeeter.ditdah;

import android.content.res.ColorStateList;
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
                mPlayPauseButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                mPlayPauseButton.setImageResource(android.R.drawable.ic_media_pause);
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
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

}
