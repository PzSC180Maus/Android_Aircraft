package com.example.aircraft_android;

import android.app.Activity;
import android.os.Bundle;
import edu.hitsz.application.GameView;

public class MainActivity extends Activity {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(new GameView(this));
    }
}
