package students.molecular.campusinterests.services;

import android.util.Log;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import students.molecular.campusinterests.model.HashTag;
import students.molecular.campusinterests.model.InterestPoint;

/**
 * Created by meradi on 24/01/16.
 */
public class InterestPointImpl implements IInterestPoint {

    Collection<InterestPoint> points = new ArrayList<>();
    Firebase fb = new Firebase("https://vivid-inferno-8779.firebaseio.com//");
    private DataChangedListener listener;

    public InterestPointImpl(DataChangedListener listener) {
        this.listener = listener;
        fb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child: dataSnapshot.getChildren())
                    for(DataSnapshot subChild : child.getChildren()){
                        InterestPoint point = subChild.getValue(InterestPoint.class);
                        points.add(point);
                }
                InterestPointImpl.this.listener.onDataChange();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public Collection<InterestPoint> getPointsOfInterest(String query) {
        Collection<InterestPoint> poi = new ArrayList<>();
        Log.d("getPointsOfInterest", query);
        Log.d("getPointsOfInterest", points.toString());
        for (InterestPoint point : points) {
            if (point.getName() != null && point.getName().toLowerCase().contains(query.toLowerCase())) {
                poi.add(point);
            } else {
                for (HashTag tag : point.getPicture().getTags()) {
                    Log.d("getPointsOfInterest", tag.getName());
                    if (tag.getName().toLowerCase().contains(query.toLowerCase()))
                        poi.add(point);
                }
            }
        }
        System.out.println(poi);
        return poi;
    }

    @Override
    public Collection<InterestPoint> getPointsOfInterest() {
        return points;
    }

    public boolean save(InterestPoint point) {
        fb.child("points").push().setValue(point);
        return true;
    }
}
