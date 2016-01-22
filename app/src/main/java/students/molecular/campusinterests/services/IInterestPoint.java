package students.molecular.campusinterests.services;

import java.util.List;

import students.molecular.campusinterests.model.HashTag;
import students.molecular.campusinterests.model.InterestPoint;

/**
 * Created by meradi on 21/01/16.
 */
public interface IInterestPoint {

    List<InterestPoint> getPointsOfInterest(List<HashTag> tags);

    boolean save(InterestPoint point);
}
