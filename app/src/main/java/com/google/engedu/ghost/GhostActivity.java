/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.ghost;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;


public class GhostActivity extends AppCompatActivity {
    private static final String COMPUTER_TURN = "Computer's turn";
    private static final String USER_TURN = "Your turn";
    private GhostDictionary dictionary;
    private boolean userTurn = false;
    private Random random = new Random();
    private Boolean isWord = false;
    private int computerScore, userScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghost);
        if (savedInstanceState != null) {
            userScore = savedInstanceState.getInt("userScore");
            computerScore = savedInstanceState.getInt("computerScore");
            ((TextView)findViewById(R.id.ghostText)).setText(savedInstanceState.getString("wordFragment"));
            ((TextView)findViewById(R.id.gameStatus)).setText(savedInstanceState.getString("gameStatus"));
        }else{
            try {
                InputStream inputStream = getAssets().open("words.txt");
                dictionary = new FastDictionary(inputStream);

            } catch (IOException e) {
                Toast toast = Toast.makeText(this, "Could not load dictionary", Toast.LENGTH_LONG);
                toast.show();
            }
            computerScore = 0;
            userScore = 0;
            onStart(null);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("userScore", userScore);
        savedInstanceState.putInt("comptuerScore", computerScore);
        savedInstanceState.putString("wordFragment",((TextView)findViewById(R.id.ghostText)).getText().toString());
        savedInstanceState.putString("gameStatus", ((TextView) findViewById(R.id.gameStatus)).getText().toString());
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ghost, menu);
        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event){
        if(!(keyCode >= KeyEvent.KEYCODE_A && keyCode <= KeyEvent.KEYCODE_Z)){
            return super.onKeyUp(keyCode, event);
        }else{
            TextView ghostText = (TextView)findViewById(R.id.ghostText);
            String wordFragment = ghostText.getText().toString() + Character.toString((char)event.getUnicodeChar());
            ghostText.setText(wordFragment);
            userTurn = false;
            computerTurn();
            return true;
        }
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

    /**
     * Handler for the "Reset" button.
     * Randomly determines whether the game starts with a user turn or a computer turn.
     * @param view
     * @return true
     */
    public boolean onStart(View view) {
//        userTurn = random.nextBoolean();
        userTurn = true;
        TextView text = (TextView) findViewById(R.id.ghostText);
        text.setText("");
        TextView label = (TextView) findViewById(R.id.gameStatus);
        if (userTurn) {
            label.setText(USER_TURN);
        } else {
            label.setText(COMPUTER_TURN);
            computerTurn();
        }
        return true;
    }

    private void computerTurn() {
        disableChallenge();
        TextView label = (TextView) findViewById(R.id.gameStatus);
        label.setText(COMPUTER_TURN);

        TextView ghostText = (TextView)findViewById(R.id.ghostText);
        String wordFragment = ghostText.getText().toString();

        if(isWordandLength()){
            label.setText("Fragment is a word! Victory! Computer!");
            computerWin();
            return;
        }
        String nextLetter = dictionary.getAnyWordStartingWith(wordFragment);
        Log.d("CTurn", "Next word = " + nextLetter);
        if(nextLetter == null){
            label.setText("Challenge! Fragment is NOT a word! Victory! Computer!");
            computerWin();
            return;
        }else{
            wordFragment += nextLetter.substring(wordFragment.length(), wordFragment.length()+1);
            ghostText.setText(wordFragment);
        }
        userTurn = true;
        label.setText(USER_TURN);
        disableChallenge();
    }

    public boolean isWordandLength(){
        TextView ghostText = (TextView)findViewById(R.id.ghostText);
        String wordFragment = ghostText.getText().toString();
        if (dictionary.isWord(wordFragment) && wordFragment.length() >= 4) {
            return true;
        }
        return false;
    }

    public void disableChallenge(){
        if(!userTurn){
            findViewById(R.id.challengeButton).setEnabled(false);
        }else{
            findViewById(R.id.challengeButton).setEnabled(true);
        }

    }

    public String nextWord(){
        TextView ghostText = (TextView)findViewById(R.id.ghostText);
        String wordFragment = ghostText.getText().toString();
        return dictionary.getAnyWordStartingWith(wordFragment);
    }

    public void challenge(View view) {
        TextView label = (TextView) findViewById(R.id.gameStatus);
        if (isWordandLength()) {
            label.setText("Fragment is a word! Victory! User!");
            userWin();
            return;
        }
        TextView ghostText = (TextView)findViewById(R.id.ghostText);
        String wordFragment = ghostText.getText().toString();
        String nextWord = dictionary.getAnyWordStartingWith(wordFragment);
        if(nextWord == null){
            label.setText("Fragment is NOT a word! Victory! User!");
            userWin();
            return;
        }else{
            label.setText("Fragment is a word! Victory! Computer! Word possible: " + nextWord);
            computerWin();
            return;
        }
    }

    public void userWin(){
        userScore++;
        TextView userScoreView = (TextView)findViewById(R.id.userScoreView);
        userScoreView.setText(Integer.toString(userScore));
    }

    public void computerWin(){
        computerScore++;
        TextView computerScoreView = (TextView)findViewById(R.id.computerScoreView);
        computerScoreView.setText(Integer.toString(computerScore));
    }
}