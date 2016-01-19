package com.szswj.sinamenu;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private SJToggleMenuOverlay mMenuOverlay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mMenuOverlay = (SJToggleMenuOverlay) findViewById(R.id.menuOverlay);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mMenuOverlay.setOItemClickListener(new SJToggleMenuOverlay.OnItemClickListener() {
            @Override
            public void onItemClick(View view) {
                switch (view.getId()) {
                    case R.id.id_toggle_menu_1:
                        Toast.makeText(MainActivity.this,"1",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.id_toggle_menu_2:
                        Toast.makeText(MainActivity.this,"2",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.id_toggle_menu_3:
                        Toast.makeText(MainActivity.this,"3",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.id_toggle_menu_4:
                        Toast.makeText(MainActivity.this,"4",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.id_toggle_menu_5:
                        Toast.makeText(MainActivity.this,"5",Toast.LENGTH_SHORT).show();
                        break;

                }
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
