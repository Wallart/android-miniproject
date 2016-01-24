package students.molecular.campusinterests.services;

import java.util.Collection;
import java.util.List;

import students.molecular.campusinterests.model.HashTag;
import students.molecular.campusinterests.model.InterestPoint;

/**
 * Created by meradi on 21/01/16.
 */
public interface IInterestPoint {

    Collection<InterestPoint> getPointsOfInterest(String query);

    Collection<InterestPoint> getPointsOfInterest();

    boolean save(InterestPoint point);

    interface DataChangedListener {
        void onDataChange();
    }
}
