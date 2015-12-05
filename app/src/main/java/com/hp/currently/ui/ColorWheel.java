package com.hp.currently.ui;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by HP Labs on 8/14/2015.
 */
public class ColorWheel {
    private ArrayList<String> mColors;
    private Random random;

    public static final String TAG = ColorWheel.class.getSimpleName();

    public ColorWheel(Context context) {
        mColors = new ArrayList<String>();
        random = new Random();

        try {
            AssetManager manager = context.getAssets();
            InputStream inputStream = manager.open("backgroundcolors.txt");

            Scanner reader = new Scanner(inputStream);
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                mColors.add(line);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getBackgroundColor() {
        String randomColor = mColors.get(random.nextInt(mColors.size()));
        int backgroundColor = Color.parseColor(randomColor);

        return backgroundColor;
    }
}
