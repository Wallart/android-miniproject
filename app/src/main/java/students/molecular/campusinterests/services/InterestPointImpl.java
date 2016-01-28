package students.molecular.campusinterests.services;

import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import students.molecular.campusinterests.model.GeoPosition;
import students.molecular.campusinterests.model.HashTag;
import students.molecular.campusinterests.model.InterestPoint;
import students.molecular.campusinterests.model.Zone;

/**
 * Created by meradi on 24/01/16.
 */
public class InterestPointImpl implements IInterestPoint {

    Collection<InterestPoint> points = new ArrayList<>();
    Collection<Zone> zones = new ArrayList<>();
    Firebase fb = new Firebase("https://vivid-inferno-8779.firebaseio.com//");
    private DataChangedListener listener;

    public InterestPointImpl(DataChangedListener listener) {
        this.listener = listener;
        fb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                if (it.hasNext())
                    loadPoints(it.next());
                if (it.hasNext())
                    loadZones(it.next());
                InterestPointImpl.this.listener.onDataChange();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void loadPoints(DataSnapshot child) {
        for (DataSnapshot subChild : child.getChildren()) {
            InterestPoint point = subChild.getValue(InterestPoint.class);
            points.add(point);
        }
    }

    public void loadZones(DataSnapshot child) {
        for (DataSnapshot subChild : child.getChildren()) {
            Zone zone = subChild.getValue(Zone.class);
            zones.add(zone);
        }
    }

    public Collection<InterestPoint> getPointsOfInterest(String query) {
        if (query == null || query.isEmpty())
            return points;
        Collection<InterestPoint> poi = new ArrayList<>();
        for (InterestPoint point : points) {
            if (point.getName() != null && point.getName().toLowerCase().contains(query.toLowerCase())) {
                poi.add(point);
            } else {
                if (point.getPicture() != null && point.getPicture().getTags() != null)
                    for (HashTag tag : point.getPicture().getTags()) {
                        Log.d("getPointsOfInterest", tag.getName());
                        if (tag.getName().toLowerCase().contains(query.toLowerCase()))
                            poi.add(point);
                    }
            }
        }
        return poi;
    }

    @Override
    public Collection<InterestPoint> getPointsOfInterest() {
        return points;
    }

    @Override
    public Collection<Zone> getZones() {
        System.out.println(zones);
        return zones;
    }

    public boolean save(InterestPoint point) {
        fb.child("points").push().setValue(point);
        return true;
    }

    @Override
    public void save(Zone zone) {
        fb.child("zones").push().setValue(zone);
    }

    @Override
    public InterestPoint getPointsOfInterestByPosition(LatLng position) {
        for (InterestPoint point : points) {
            if (point.getPosition() != null) {
                GeoPosition pointPosition = point.getPosition();
                if (pointPosition.getLatitude() == position.latitude && pointPosition.getLongtitude() == position.longitude)
                    return point;
            }
        }
        return null;
    }
}
