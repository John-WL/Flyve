package util.bezier_curve;

import java.util.List;

public interface PathComposite extends CurveSegment {
    CurveSegment getComponent(int i);
    int getNumberOfComponents();
}
