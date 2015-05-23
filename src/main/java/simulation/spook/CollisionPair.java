package simulation.spook;

import org.jblas.FloatMatrix;
import simulation.element.Element;

public abstract class CollisionPair {
    public abstract float getD();

    public abstract float getE();

    public abstract FloatMatrix getInverseMassMatrix();

    public abstract FloatMatrix getJacobian();

    public abstract FloatMatrix getFirstJacobian();

    public abstract FloatMatrix getSecondJacobian();

    public abstract Element getFirstElement();

    public abstract Element getSecondElement();

    public abstract FloatMatrix getInitialVelocity();

    public abstract FloatMatrix getInitialForce();

    public abstract float getInitialLambda();

    public abstract float getOverlap();

    public abstract boolean isActive();
}
