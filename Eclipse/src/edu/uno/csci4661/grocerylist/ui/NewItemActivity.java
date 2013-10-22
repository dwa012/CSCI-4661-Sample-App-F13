package edu.uno.csci4661.grocerylist.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;

import edu.uno.csci4661.grocerylist.R;

public class NewItemActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item);

        // Get the intent that started this activity
        Intent intent = getIntent();
//        Uri data = intent.getData();

        Log.d("edu.grocery", intent.getType());

        // Figure out what to do based on the intent type
        if (intent.getType().indexOf("image/") != -1) {
            Uri imageUri = (Uri) getIntent().getExtras().get(Intent.EXTRA_STREAM);
            ImageView image = (ImageView) this.findViewById(R.id.image);
            image.setImageURI(imageUri);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_item, menu);
        return true;
    }

}
