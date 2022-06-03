package com.example.words;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    int k = 0, k1 = 0;
    float[] array = {0, 1, 0.5F, 1, 1};
    Dictionary dict = new Hashtable();
    String word, wordFinal, word1 = "";
    List<String> linesDB = new ArrayList<String>();
    boolean gameOver = false, gameOverLayout = false;
    public static boolean winner = false;
    public int gamesPlayed;
    public int gamesWon;

    void changeGameOverScreen() {
        getStats();
    }

    void handleGameStatisticsGameOver() {
        SharedPreferences settings = getApplicationContext().getSharedPreferences("MyPrefs", 0);
        SharedPreferences.Editor editor = settings.edit();

        if (!settings.contains("GamesPlayed")) {
            Log.d(MenuActivity.class.getSimpleName(), "Log1");
            editor.putInt("GamesPlayed", 1);
            if(winner)
                gamesWon = 1;
            else
                gamesWon = 0;
            editor.putInt("GamesWon", gamesWon);

            gamesPlayed = 1;

            Log.d(MenuActivity.class.getSimpleName(), String.format("Got games played: %d, games won: %d", gamesPlayed, gamesWon));
        } else {
            Log.d(MenuActivity.class.getSimpleName(), "Log0");
            //gamesPlayed = settings.getInt("GamesPlayed", 0);
            gamesWon = settings.getInt("GamesWon", 0);
            Log.d(MenuActivity.class.getSimpleName(), Integer.toString(gamesPlayed));

            if(winner)
                gamesWon += 1;
            editor.putInt("GamesWon", gamesWon);

            Log.d(MenuActivity.class.getSimpleName(), String.format("Got games played: %d, games won: %d", gamesPlayed, gamesWon));
        }
        editor.apply();
    }

    void handleGameStatisticsInit() {
        SharedPreferences settings = getApplicationContext().getSharedPreferences("MyPrefs", 0);
        SharedPreferences.Editor editor = settings.edit();

        if(settings.contains("GamesPlayed")) {
            gamesPlayed = settings.getInt("GamesPlayed", 0);
            Log.d(MenuActivity.class.getSimpleName(), Integer.toString(gamesPlayed));

            gamesPlayed += 1;
            editor.putInt("GamesPlayed", gamesPlayed);

            Log.d(MenuActivity.class.getSimpleName(), String.format("Got games played: %d", gamesPlayed));
        }
        else {
            gamesPlayed = 1;
            editor.putInt("GamesPlayed", 1);
            editor.putInt("GamesWon", 0);

            Log.d(MenuActivity.class.getSimpleName(), String.format("Got games played: %d, games won: %d", gamesPlayed, gamesWon));
        }
        editor.apply();
    }

    void getStats() {
        LinearLayout layoutWon = (LinearLayout) findViewById(R.id.layoutGameOver);

        final TextView textViewWonOrLost = (TextView) findViewById(R.id.textViewWonOrLost);
        if(winner)
            textViewWonOrLost.setText("Congrats!");
        else
            textViewWonOrLost.setText("You lost!");

        String word = "READY";
        final TextView textViewWord = (TextView) findViewById(R.id.textViewWord);
        textViewWord.setText("The word was " + wordFinal);

        handleGameStatisticsGameOver();
        final TextView textViewGamesPlayed = (TextView) findViewById(R.id.textViewGamesPlayed);
        textViewGamesPlayed.setText("You played " + Integer.toString(gamesPlayed) + " games");

        final TextView textViewGamesWon = (TextView) findViewById(R.id.textViewGamesWon);
        textViewGamesWon.setText("You won " + Integer.toString(gamesWon) + " games");

        int winRate = (int)(gamesWon * 100 / gamesPlayed);
        final TextView textViewWinRate = (TextView) findViewById(R.id.textViewWinRate);
        textViewWinRate.setText("Your winrate is " + Integer.toString(winRate) + "%");
        layoutWon.setVisibility(View.VISIBLE);
    }

    void setImgButtonVisibility(int visible) {
        LinearLayout layoutParent = (LinearLayout) findViewById(R.id.ButtonLayouts);
        LinearLayout layoutCurrentLine = (LinearLayout) findViewById(layoutParent.getChildAt(k1).getId());

        ImageButton imgButton= (ImageButton) findViewById(layoutCurrentLine.getChildAt(5).getId());
        imgButton.setVisibility(visible);
    }

    void checkWord() {
        //if the word doesn't exist in the database
        if(!linesDB.contains(word)) {
            wrongAnimation();
            boolean d = false;
            word1 = "";
            for(int i = 0; i < 5; i++) {
                for(char j = 'A'; j <= 'Z'; j++) {
                    word1 = "";
                    for(int k = 0; k < 5; k++) {
                        if(k == i) {
                            word1 = word1 + j;
                        }
                        else
                            word1 = word1 + word.charAt(k);
                    }
                    if(linesDB.contains(word1)) {
                        d = true;
                        j = 'Z';
                        i = 5;
                    }
                }
            }

            //if there is a similar word in the database
            if(d == true) {
                //show button
                setImgButtonVisibility(View.VISIBLE);
            }
        }
        else {
            float array[] = new float[5];

            //create an array that contains 0 if the character at that position is wrong, 0.5 if it's correct
            // but in the wrong position and 1 if it's correct and at the right position
            Log.d(MainActivity.class.getSimpleName(), String.format("Word: " + wordFinal + " " + word));
            for(int i = 0; i < 5; i++) {
                if(word.charAt(i) == wordFinal.charAt(i))
                    array[i] = 1.0f;
                else if(wordFinal.indexOf(word.charAt(i)) >= 0)
                    array[i] = 0.5f;
                else
                    array[i] = 0.0f;
            }

            int nrWrongLetters = 0;

            LinearLayout layoutParent = (LinearLayout) findViewById(R.id.ButtonLayouts);
            LinearLayout layoutCurrentLine = (LinearLayout) findViewById(layoutParent.getChildAt(k1).getId());

            for (int i = 0; i < array.length; i++) {
                Button b1 = (Button) findViewById(layoutCurrentLine.getChildAt(i).getId());
                if (array[i] == 0) {
                    //if it's incorrect change the color
                    b1.setBackgroundColor(getResources().getColor(R.color.incorrectColor));

                    //get the id of the button with the letter that is at the current index
                    String buttonID = "button" + b1.getText().toString();
                    //get the resource id of the letter
                    int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                    //get the button with the id of the current letter
                    Button b2 = (Button) findViewById(resID);

                    //if the letter's position hasn't already been found
                    if (dict.get(buttonID) != "1" && dict.get(buttonID) != "0.5") {
                        //change the color of the button
                        b2.setBackgroundColor(getResources().getColor(R.color.incorrectColor));
                        dict.put(buttonID, "0");
                    }

                    nrWrongLetters++;
                } else if (array[i] == 0.5F) {
                    //if the position is incorrect change the color
                    b1.setBackgroundColor(getResources().getColor(R.color.incorrectPositionColor));

                    //get the id of the button with the letter that is at the current index
                    String buttonID = "button" + b1.getText().toString();
                    //get the resource id of the letter
                    int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                    //get the button with the id of the current letter
                    Button b2 = (Button) findViewById(resID);

                    //if the letter's position hasn't already been found
                    if (dict.get(buttonID) != "1") {
                        //change the color of the button
                        b2.setBackgroundColor(getResources().getColor(R.color.incorrectPositionColor));
                        dict.put(buttonID, "0");
                    }

                    nrWrongLetters++;
                } else {
                    //if it's correct change the color
                    b1.setBackgroundColor(getResources().getColor(R.color.correctColor));

                    //get the id of the button with the letter that is at the current index
                    String buttonID = "button" + b1.getText().toString();
                    //get the resource id of the letter
                    int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                    //get the button with the id of the current letter
                    Button b2 = (Button) findViewById(resID);

                    //change the color of the button
                    b2.setBackgroundColor(getResources().getColor(R.color.correctColor));
                    dict.put(buttonID, "1");
                }
            }

            //if the player has guessed the word
            if(nrWrongLetters == 0) {
                gameOver = true;
                winner = true;
                changeGameOverScreen();
            }
            else if(k1 == 5) {
                gameOver = true;
                winner = false;
                changeGameOverScreen();
            }
            word = "";
            k = 0;
            k1 = k1 + 1;
        }
    }

    void initializeDataBase() {
        BufferedReader reader;

        try{
            final InputStream file = getAssets().open("words_db.txt");
            reader = new BufferedReader(new InputStreamReader(file));
            String line = reader.readLine();

            //save all the words from the file to linesDB
            while(line != null){
                linesDB.add(line.toUpperCase(Locale.ROOT));
                line = reader.readLine();
            }
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }

    void getWord() {
        //get a random word from the database
        Random rand = new Random();
        int intRand = rand.nextInt(linesDB.size());

        wordFinal = linesDB.get(intRand);
        Log.d(MainActivity.class.getSimpleName(), String.format("Word " + wordFinal));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeDataBase();
        getWord();

        //initialize the dictionary
        char a = 'A';
        for(int i = 0; i < 26; i++) {
            String buttonID = "button" + a;
            a += 1;
            dict.put(buttonID, "-1");
        }
        
        word = "";

        handleGameStatisticsInit();
    }

    public void buttonOnClick(View view) throws JSONException {
        if(!gameOver) {
            switch (view.getId()) {
                case R.id.plusButton6:
                case R.id.plusButton5:
                case R.id.plusButton4:
                case R.id.plusButton3:
                case R.id.plusButton2:
                case R.id.plusButton1:
                    LinearLayout layout = (LinearLayout) findViewById(R.id.layoutHint);
                    layout.setVisibility(View.VISIBLE);

                    TextView textView = (TextView) findViewById(R.id.textViewHint);
                    textView.setText("The word you entered is incorrect. Did you mean " + word1 + "?");

                    gameOver = true;
                    //Toast.makeText(MainActivity.this,"The word you entered is incorrect. Did you mean " + word1 + "?", Toast.LENGTH_LONG).show();
                    break;
                case R.id.buttonDel:
                    setImgButtonVisibility(View.INVISIBLE);
                    //if there are buttons that aren't empty on the current line
                    if (k > 0) {
                        //get the layout object of the current line and save it in the variable layoutCurrentLine
                        LinearLayout layoutParent = (LinearLayout) findViewById(R.id.ButtonLayouts);
                        LinearLayout layoutCurrentLine = (LinearLayout) findViewById(layoutParent.getChildAt(k1).getId());

                        k = k - 1;
                        //get the last button that contains a letter
                        Button b1 = (Button) findViewById(layoutCurrentLine.getChildAt(k).getId());

                        //make the button empty
                        b1.setText("");
                        if (word.length() != 0)
                            word = word.substring(0, word.length() - 1);
                    }
                    break;

                case R.id.buttonDone:
                    //if the user has already chosen 5 letters
                    if (k == 5) {
                        //check if 6 words have already been entered
                        if (k1 < 6) {
                            Log.d(MainActivity.class.getSimpleName(), String.format("Word: %s", word));
                            checkWord();
                        }
                    } else {
                        wrongAnimation();
                    }
                    break;

                default:
                    //if the player has already guessed 6 incorrect words he lost
                    if(k1 == 6) {
                        gameOver = true;
                        winner = true;
                        changeGameOverScreen();
                    }
                    else {
                        //if the user hasn't already entered the 5th and last letter
                        if (k < 5) {
                            //get the layout object of the current line and save it in the variable layoutCurrentLine
                            LinearLayout layoutParent = (LinearLayout) findViewById(R.id.ButtonLayouts);
                            LinearLayout layoutCurrentLine = (LinearLayout) findViewById(layoutParent.getChildAt(k1).getId());

                            //get the first empty button
                            Button b1 = (Button) findViewById(layoutCurrentLine.getChildAt(k).getId());
                            k = k + 1;

                            //change the text of the first empty button based on the key that was clicked
                            String a = ((Button) view).getText().toString();
                            b1.setText(a);
                            word = word + a;
                        }
                        break;
                    }
            }
        }
        else {
            switch (view.getId()) {
                case R.id.buttonHintNo:
                    LinearLayout layout = (LinearLayout) findViewById(R.id.layoutHint);
                    layout.setVisibility(View.INVISIBLE);

                    setImgButtonVisibility(View.INVISIBLE);
                    gameOver = false;
                    break;
                case R.id.buttonHintYes:
                    LinearLayout layout1 = (LinearLayout) findViewById(R.id.layoutHint);
                    layout1.setVisibility(View.INVISIBLE);

                    setImgButtonVisibility(View.INVISIBLE);

                    LinearLayout layoutParent = (LinearLayout) findViewById(R.id.ButtonLayouts);
                    LinearLayout layoutCurrentLine = (LinearLayout) findViewById(layoutParent.getChildAt(k1).getId());

                    for (int i = 0; i < array.length; i++) {
                        Button b1 = (Button) findViewById(layoutCurrentLine.getChildAt(i).getId());
                        b1.setText("" + word1.charAt(i));
                    }
                    word = word1;

                    checkWord();

                    gameOver = false;
                    break;
            }
        }
    }

    public void wrongAnimation() {
        LinearLayout layoutParent = (LinearLayout) findViewById(R.id.ButtonLayouts);
        LinearLayout layoutCurrentLine = (LinearLayout) findViewById(layoutParent.getChildAt(k1).getId());

        for (int i = 0; i < array.length; i++) {
            Button b = (Button) findViewById(layoutCurrentLine.getChildAt(i).getId());
            b.setBackgroundColor(getResources().getColor(R.color.redColor));
        }

        TranslateAnimation animation = new TranslateAnimation(-25.0f, 25.0f, 0.0f, 0.0f); // new TranslateAnimation (float fromXDelta,float toXDelta, float fromYDelta, float toYDelta)

        animation.setDuration(100); // animation duration
        animation.setRepeatCount(10); // animation repeat count
        animation.setRepeatMode(2);
        layoutCurrentLine.startAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                for (int i = 0; i < array.length; i++) {
                    int id = layoutCurrentLine.getChildAt(i).getId();
                    Button b = (Button) findViewById(layoutCurrentLine.getChildAt(i).getId());

                    if(dict.get(id) == "1")
                        b.setBackgroundColor(getResources().getColor(R.color.correctColor));
                    else if(dict.get(id) == "0.5")
                        b.setBackgroundColor(getResources().getColor(R.color.incorrectPositionColor));
                    else if(dict.get(id) == "0")
                        b.setBackgroundColor(getResources().getColor(R.color.incorrectColor));
                    else
                        b.setBackgroundColor(getResources().getColor(R.color.keyColor));
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void homeIconOnClick(View view) {
        if(gameOverLayout)
            openMenuActivity();
        else if(!gameOver)
            openMenuActivity();
    }

    public void helpIconOnClick(View view) {
        if(!gameOver) {
            LinearLayout layoutHelp = (LinearLayout) findViewById(R.id.layoutHelp);
            layoutHelp.setVisibility(View.VISIBLE);

            ImageView img = (ImageView) findViewById(R.id.helpBackgroundImg);
            img.setVisibility(View.VISIBLE);

            gameOver = true;
        }
    }

    public void helpBackOnClick(View view) {
        LinearLayout layoutHelp = (LinearLayout) findViewById(R.id.layoutHelp);
        layoutHelp.setVisibility(View.INVISIBLE);

        ImageView img = (ImageView) findViewById(R.id.helpBackgroundImg);
        img.setVisibility(View.INVISIBLE);
        gameOver = false;
    }

    public void openMenuActivity(){
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }

    public void gameOverBackOnClick(View view) {
        LinearLayout layoutHelp = (LinearLayout) findViewById(R.id.layoutGameOver);
        layoutHelp.setVisibility(View.INVISIBLE);

        gameOver = true;
        gameOverLayout = true;
    }
}