package com.lin.read;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends Activity {

    Button getBookInfoButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getBookInfoButton= (Button) findViewById(R.id.get_book_info);
        getBookInfoButton.setOnClickListener(null);
    }
}
