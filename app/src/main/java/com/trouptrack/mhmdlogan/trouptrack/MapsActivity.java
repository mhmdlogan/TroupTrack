package com.trouptrack.mhmdlogan.trouptrack;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Location;
import android.support.annotation.DrawableRes;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private String email;

    DatabaseReference onlineRef , currentRef,refDatabase,Alllocations;
    DatabaseReference locations;

    Double lat,lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Ref to firebase first
        locations = FirebaseDatabase.getInstance().getReference("Locations");

        //Get Intent
        if(getIntent()!=null)
        {
            email = getIntent().getStringExtra("email");
            lat = getIntent().getDoubleExtra("lat",0);
            lng = getIntent().getDoubleExtra("lng",0);
        }
        if(!TextUtils.isEmpty(email))
            loadLocationForThisUser(email);
            
    }

    private void loadLocationForThisUser(String email) {
        Query user_location = locations.orderByChild("email").equalTo(email);

        user_location.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapShot:dataSnapshot.getChildren())
                {
                    Tracking tracking = postSnapShot.getValue(Tracking.class);

                    //add marker for friend location
                    LatLng friendLocation = new LatLng(Double.parseDouble(tracking.getLat()),
                            Double.parseDouble(tracking.getLng()));

                    //Creat location from user coordinates
                    Location currentUser = new Location("");
                    currentUser.setLatitude(lat);
                    currentUser.setLongitude(lng);

                    //Clear all old markers
                    mMap.clear();

                    //Create location from friend coordinates
                    Location friend = new Location("");
                    friend.setLatitude(Double.parseDouble(tracking.getLat()));
                    friend.setLongitude(Double.parseDouble(tracking.getLng()));

                    //Add Friend marker on Map
                    mMap.addMarker(new MarkerOptions()
                    .position(friendLocation)
                    .title(tracking.getEmail())
                    .snippet("Distance"+new DecimalFormat("#.#").format((currentUser.distanceTo(friend))/1000)+" km")
                    .icon(BitmapDescriptorFactory.fromBitmap(
                            createCustomMarker(MapsActivity.this,R.drawable.img2))));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lng),12.0f));

                    ////Add Friend marker on Map
                    //mMap.addMarker(new MarkerOptions()
                      //      .position(friendLocation)
                      //      .title(tracking.getEmail())
                      //      .snippet("Distance"+new DecimalFormat("#.#").format((currentUser.distanceTo(friend))/1000)+" km")
                      //      .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                    //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lng),12.0f));
                }
                //Create marker for current user
                LatLng current = new LatLng(lat,lng);
                mMap.addMarker(new MarkerOptions().position(current).title(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                        .icon(BitmapDescriptorFactory.fromBitmap(createCustomMarker2(MapsActivity.this,R.drawable.img))));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lng),12.0f));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private double distance(Location currentUser, Location friend) {
        double theta = currentUser.getLongitude() - friend.getLongitude();
        double dist = Math.sin(deg2rad(currentUser.getLatitude()))
                * Math.sin(deg2rad(friend.getLatitude()))
                * Math.cos(deg2rad(currentUser.getLatitude()))
                * Math.cos(deg2rad(friend.getLatitude()))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return dist;
    }

    private double rad2deg(double red) {
        return (red * Math.PI/180.0);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI/180.0);
    }

    private void collectlats(Map<String, Object> value) {
        ArrayList<String> lats = new ArrayList<>();
        ArrayList<String> lngs = new ArrayList<>();
        ArrayList<String> nms = new ArrayList<>();
        int i = 0;
        int size = value.size();
        Toast.makeText(MapsActivity.this, "There are "+size+" friends locations", Toast.LENGTH_SHORT).show();
        for (Map.Entry<String, Object> entry : value.entrySet()){

            //Get user map
            Map singleUser = (Map) entry.getValue();
            //Get last Location field and append to list
            lats.add((String) singleUser.get("lat"));
            lngs.add((String) singleUser.get("lng"));
            nms.add((String) singleUser.get("email"));
            //Toast.makeText(MapsActivity.this, lats.get(i) +"|"+lngs.get(i), Toast.LENGTH_SHORT).show();
            LatLng cod = new LatLng(Double.parseDouble( lats.get(i)),Double.parseDouble( lngs.get(i)));
            mMap.addMarker(new MarkerOptions()
                    .position(cod)
                    .title(nms.get(i))
                    .icon(BitmapDescriptorFactory.fromBitmap(
                            createCustomMarker(MapsActivity.this,R.drawable.img2))));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble( lats.get(0)),Double.parseDouble( lngs.get(0))),6.0f));
            i++;
        }
    }
    private ArrayList<String> collectlngs(Map<String, Object> value) {
        ArrayList<String> lngs = new ArrayList<>();
        int i = 0;
        for (Map.Entry<String, Object> entry : value.entrySet()){

            //Get user map
            Map singleUser = (Map) entry.getValue();
            //Get lat field and append to list
            lngs.add((String) singleUser.get("lng"));
            //Toast.makeText(MapsActivity.this, lngs.get(i), Toast.LENGTH_SHORT).show();
            i++;
        }
        System.out.println(lngs.toString());
        return lngs;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        Intent intent = getIntent();

        if(intent.hasExtra("chk"))
        {
        DatabaseReference refDatabase1 = FirebaseDatabase.getInstance().getReference().child("Locations");

        refDatabase1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                collectlats((Map<String, Object>) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

//            refDatabase1.addChildEventListener(new ChildEventListener() {
//
//                @Override
//                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//
////                    LatLng newLocation = new LatLng(
////                            dataSnapshot.child("lat").getValue(Long.class),
////                            dataSnapshot.child("lng").getValue(Long.class)
////                    );
////                    mMap.addMarker(new MarkerOptions()
////                            .position(newLocation)
////                            .title(dataSnapshot.getKey()));
////                    Toast.makeText(MapsActivity.this, "working", Toast.LENGTH_SHORT).show();
//
//                }
//
//                @Override
//                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//
////                    for (DataSnapshot child : dataSnapshot.child("Locations").getChildren())
////                    {
////                        User lokasi3 = dataSnapshot.getValue(User.class);
////                        String latitude = child.child("lat").getValue().toString();
////                        String longitude = child.child("lng").getValue().toString();
////                        double loclatitude = Double.parseDouble(latitude);
////                        double loclongitude = Double.parseDouble(longitude);
////                        LatLng cod = new LatLng(loclatitude, loclongitude);
////                        Toast.makeText(MapsActivity.this, "working", Toast.LENGTH_SHORT).show();
////                        googleMap.addMarker(new MarkerOptions().position(cod).title(""));
//
////                    Iterator<DataSnapshot> dataSnapshotsChat =  dataSnapshot.getChildren().iterator();
////
////                    while (dataSnapshotsChat.hasNext()) {
////                        DataSnapshot dataSnapshotChild = dataSnapshotsChat.next();
////                        double latitude =    dataSnapshotChild.child("lat").getValue(Double.class);
////                        double longitude = dataSnapshotChild.child("lng").getValue(Double.class);
////                        LatLng local = new LatLng(latitude, longitude);
////                        mMap.addMarker(new MarkerOptions()
////                                .position(local)
////                                .title(dataSnapshot.getKey()));
////                        Toast.makeText(MapsActivity.this, (int) latitude, Toast.LENGTH_SHORT).show();
////                    }
////                }
//
//            }
//
//                @Override
//                public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//                }
//
//                @Override
//                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//    //}
//    });
    }

    public static Bitmap createCustomMarker(Context context, @DrawableRes int resource) {

        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);

        ImageView markerImage = (ImageView) marker.findViewById(R.id.Frnimg);
        markerImage.setImageResource(resource);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        marker.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        marker.draw(canvas);

        return bitmap;
    }

    public static Bitmap createCustomMarker2(Context context, @DrawableRes int resource) {

        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker2_layout, null);

        ImageView markerImage = (ImageView) marker.findViewById(R.id.Meimg);
        markerImage.setImageResource(resource);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        marker.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        marker.draw(canvas);

        return bitmap;
    }

}
