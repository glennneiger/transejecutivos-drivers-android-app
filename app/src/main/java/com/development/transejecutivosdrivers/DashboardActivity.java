package com.development.transejecutivosdrivers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import com.development.transejecutivosdrivers.adapters.DashboardMenuAdapter;
import com.development.transejecutivosdrivers.models.DashboardMenu;

public class DashboardActivity extends ActivityBase implements AdapterView.OnItemClickListener {
    private GridView gridView;
    private DashboardMenuAdapter adaptador;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        validateSession();

        context = getApplicationContext();

        gridView = (GridView) findViewById(R.id.gridView_menu);
        adaptador = new DashboardMenuAdapter(this);
        gridView.setAdapter(adaptador);

        gridView.setOnItemClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        isLocationServiceEnabled();
        checkGooglePlayServices();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        DashboardMenu item = (DashboardMenu) parent.getItemAtPosition(position);
        Intent i;
        switch (item.getId()) {
            case 0:
                i = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(i);
                break;

            case 1:
                i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                break;

            case 2:
                i = new Intent(getApplicationContext(), SearchserviceActivity.class);
                startActivity(i);
                break;

            case 3:
                session.logoutUser();
                break;

            default:
                break;
        }
    }
}

