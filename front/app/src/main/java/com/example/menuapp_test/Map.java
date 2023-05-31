package com.example.menuapp_test;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class Map extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap googleMap;
    private Button list;
    private String token, address;
    private double latitude, longitude;
    private FloatingActionButton home;
    private ArrayList<ListItem> listItems = new ArrayList<ListItem>();
    private ListItem listItem = new ListItem();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);

        Intent getIntent = getIntent();
        token = getIntent.getStringExtra("token");
        address = getIntent.getStringExtra("address");
        latitude = Double.parseDouble(getIntent.getStringExtra("latitude"));
        longitude = Double.parseDouble(getIntent.getStringExtra("longitude"));
        listItems = (ArrayList<ListItem>) getIntent.getSerializableExtra("listItems");

        home = findViewById(R.id.fab);
        home.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("token", token);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        list = findViewById(R.id.btn_list);
        list.setOnClickListener(v -> {
            String Latitude = String.valueOf(latitude);
            String Longitude = String.valueOf(longitude);
            Intent intent = new Intent(this, ListActivity.class);
            intent.putExtra("token", token);
            intent.putExtra("address", address);
            intent.putExtra("latitude", Latitude);
            intent.putExtra("longitude", Longitude);
            startActivity(intent);
        });
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;

        LatLng my = new LatLng(latitude, longitude);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(my);
        markerOptions.title("현재 위치");
        BitmapDrawable bitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.marker_my_2);
        Bitmap b = bitmapDrawable.getBitmap();
        Bitmap mark = Bitmap.createScaledBitmap(b,100, 100, false);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(mark));
        googleMap.addMarker(markerOptions);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(my, 15));

        BitmapDrawable bitmapDrawable1 = (BitmapDrawable) getResources().getDrawable(R.drawable.marker_restaurant_3);
        Bitmap b1 = bitmapDrawable1.getBitmap();
        Bitmap mark1 = Bitmap.createScaledBitmap(b1,100, 100, false);

        for (int i=0; i<listItems.size(); i++) {
            listItem = listItems.get(i);
            double Latitude = Double.parseDouble(listItem.getLatitude());
            double Longitude = Double.parseDouble(listItem.getLongitude());
            String Rid = String.valueOf(listItem.getId());

            MarkerOptions markerOptions1 = new MarkerOptions();
            markerOptions1.position(new LatLng(Latitude, Longitude));
            markerOptions1.title(listItem.getName());
            markerOptions1.snippet(Rid);
            markerOptions1.icon(BitmapDescriptorFactory.fromBitmap(mark1));
            googleMap.addMarker(markerOptions1);
        }

        googleMap.setOnMarkerClickListener(markerClickListener);
    }

    GoogleMap.OnMarkerClickListener markerClickListener = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(@NonNull Marker marker) {
            if(!marker.getTitle().equals("현재 위치")) {
                String rid = marker.getSnippet();
                for (int i = 0; i < listItems.size(); i++) {
                    listItem = listItems.get(i);
                    String Rid = String.valueOf(listItem.getId());
                    if (Rid.equals(rid)) break;
                }
                Intent intent = new Intent(Map.this, PopupMap.class);
                intent.putExtra("token", token);
                intent.putExtra("listItem", listItem);
                intent.putExtra("Rid", rid);
                startActivity(intent);
            }
            return false;
        }
    };
}
