package com.example.wordle;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    int letters = 0;
    HashMap<Integer, List<String>> h;
    HashSet<String> dictionary;
    List<TextView> textViews;
    String thought;
    boolean win;
    int guess;

    Button b1;
    Button b2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prepareDictionary();

        win = false;
        guess = 1;

        b1 = findViewById(R.id.button1);
        b2 = findViewById(R.id.button2);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(letters < 2 || letters > 15)
                {
                    getWordLength();
                }
                else
                {
                    play();
                }
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        getWordLength();
    }

    public void prepareDictionary()
    {
        h = new HashMap<>();
        dictionary = new HashSet<>();
        for(int i = 2; i <= 15; i++)
        {
            h.put(i, new ArrayList<>());
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open("CSW2021.txt"), "UTF-8"));
            while(true)
            {
                String s = reader.readLine();
                if(s == null)
                {
                    break;
                }
                else
                {
                    (h.get(s.length())).add(s);
                    dictionary.add(s);
                }
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getWordLength()
    {
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        final View yourCustomView = inflater.inflate(R.layout.input, null);

        EditText e1 = yourCustomView.findViewById(R.id.edittext1);

        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Word length")
                .setView(yourCustomView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            letters = Integer.parseInt((e1.getText()).toString());
                            if(letters < 2 || letters > 15)
                            {
                                Toast.makeText(MainActivity.this, "Enter a value between 2 and 15", Toast.LENGTH_LONG).show();
                                getWordLength();
                            }
                            else
                            {
                                start();
                            }
                        }
                    }).create();
            dialog.show();
    }

    public void start()
    {
        GridLayout g = findViewById(R.id.gridlayout1);
        g.setColumnCount(letters);
        g.setRowCount(6);

        textViews = new ArrayList<>();

        for(int j = 0; j < 6 * letters; j++)
        {
            TextView t = new TextView(this);
            t.setHeight(50);
            t.setWidth(50);
            t.setId(j);
            t.setTypeface(null, Typeface.BOLD);
            g.addView(t);

            textViews.add(t);
        }

        List<String> words = h.get(letters);
        int count = words.size();
        Random rand = new Random();
        int random = rand.nextInt(count + 1);
        thought = words.get(random);

        play();
    }

    public void play()
    {
        if(!win && guess <= 6)
        {
            LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
            final View myCustomView = inflater.inflate(R.layout.guess, null);

            EditText e1 = myCustomView.findViewById(R.id.edittext2);

            AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Guess " + Integer.toString(guess))
                    .setView(myCustomView)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String word = ((e1.getText()).toString()).toUpperCase();

                            boolean valid = true;
                            for(char c:word.toCharArray())
                            {
                                int a = (int) c;
                                if(a < 65 || a > 90)
                                {
                                    valid = false;
                                    break;
                                }
                            }

                            if(!valid)
                            {
                                Toast.makeText(MainActivity.this, "Enter a valid word", Toast.LENGTH_LONG).show();
                                play();
                            }
                            else if(word.length() != letters)
                            {
                                Toast.makeText(MainActivity.this, "Enter a " + Integer.toString(letters) + " letter word", Toast.LENGTH_LONG).show();
                                play();
                            }
                            else if(!dictionary.contains(word))
                            {
                                Toast.makeText(MainActivity.this, "Word is not present in dictionary", Toast.LENGTH_LONG).show();
                                play();
                            }
                            else
                            {
                                if(word.equals(thought))
                                {
                                    win = true;
                                    b2.setEnabled(true);
                                }

                                HashMap<Character, Integer> h1 = new HashMap<>();
                                HashMap<Character, Integer> h2 = new HashMap<>();

                                for(int z = 0; z < letters; z++)
                                {
                                    char x = word.charAt(z);
                                    char y = thought.charAt(z);
                                    if(x != y)
                                    {
                                        if(h1.containsKey(x))
                                        {
                                            h1.put(x, h1.get(x) + 1);
                                        }
                                        else
                                        {
                                            h1.put(x, 1);
                                        }

                                        if(h2.containsKey(y))
                                        {
                                            h2.put(y, h2.get(y) + 1);
                                        }
                                        else
                                        {
                                            h2.put(y, 1);
                                        }
                                    }
                                }

                                for(int k = 0; k < letters; k++)
                                {
                                    char e = word.charAt(k);
                                    char f = thought.charAt(k);

                                    TextView v = textViews.get(((guess - 1) * letters) + k);
                                    v.setText(Character.toString(e));

                                    if(e == f)
                                    {
                                        v.setBackgroundColor(Color.rgb(0, 255, 0));
                                    }
                                    else if(!h2.containsKey(e))
                                    {
                                        v.setBackgroundColor(Color.rgb(192, 192, 192));
                                    }
                                    else
                                    {
                                        v.setBackgroundColor(Color.rgb(255, 255, 0));

                                        if(h1.get(e) == 1)
                                        {
                                            h1.remove(e);
                                        }
                                        else
                                        {
                                            h1.put(e, h1.get(e) - 1);
                                        }

                                        if(h2.get(e) == 1)
                                        {
                                            h2.remove(e);
                                        }
                                        else
                                        {
                                            h2.put(e, h2.get(e) - 1);
                                        }
                                    }
                                }

                                guess++;
                                play();
                            }
                        }
                    }).create();
            dialog.show();
        }
        else if(guess > 6 && !win)
        {
            Toast.makeText(MainActivity.this, thought, Toast.LENGTH_LONG).show();
            b2.setEnabled(true);
        }
    }
}