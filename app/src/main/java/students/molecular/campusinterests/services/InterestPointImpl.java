package students.molecular.campusinterests.services;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
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

    public InterestPointImpl() {
        fb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                points = dataSnapshot.getValue(Map.class).values();
                System.out.println(points);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public Collection<InterestPoint> getPointsOfInterest(String query) {
        Collection<InterestPoint> poi = new ArrayList<>();
        for (InterestPoint point : points)
            for(HashTag tag : point.getPicture().getTags())
                if (tag.getName().contains(query))
                    poi.add(point);
        return poi;
    }

    public boolean save(InterestPoint point) {
        fb.child("points").push().setValue(point);
        return true;
    }
}
