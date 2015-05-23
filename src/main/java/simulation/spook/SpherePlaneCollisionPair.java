package simulation.spook;

import com.jme3.math.Vector3f;
import org.jblas.FloatMatrix;
import org.jblas.Solve;
import simulation.element.Plane;
import simulation.element.Sphere;

public class SpherePlaneCollisionPair extends CollisionPair {
    private Sphere element0;
    private Plane element1;

    private float timestep;
    private float springConstant;
    private int iterationSteps;

    private float a, b, e;

    public SpherePlaneCollisionPair(Sphere element0, Plane element1, float timestep, float springConstant, int iterationSteps) {
        this.element0 = element0;
        this.element1 = element1;

        this.timestep = timestep;
        this.springConstant = springConstant;
        this.iterationSteps = iterationSteps;

        a = 4.0f / (timestep * (1 + 4.0f * iterationSteps));
        b = (4.0f * iterationSteps) / (1 + 4.0f * iterationSteps);
        e = 4.0f / (timestep * timestep * springConstant * (1 + 4 * iterationSteps));
    }

    public Sphere getFirstElement() {
        return element0;
    }

    public Plane getSecondElement() {
        return element1;
    }

    public FloatMatrix getInverseMassMatrix() {
        FloatMatrix firstPositionMatrix = getFirstElement().getInverseMassMatrix();
        FloatMatrix firstInertiaTensor = getFirstElement().getInverseInertiaTensor();

        FloatMatrix cM = new FloatMatrix(12, 12);

        cM.put(0, 0, firstPositionMatrix.get(0, 0));
        cM.put(1, 1, firstPositionMatrix.get(1, 1));
        cM.put(2, 2, firstPositionMatrix.get(2, 2));
        cM.put(3, 3, firstInertiaTensor.get(0, 0));
        cM.put(4, 4, firstInertiaTensor.get(1, 1));
        cM.put(5, 5, firstInertiaTensor.get(2, 2));

        return cM;
    }

    public FloatMatrix getJacobian() {
        Vector3f contactNormal = getContactNormal();
        Vector3f r_i = contactNormal.mult(-1.0f);

        Vector3f r_i_cross = r_i.cross(contactNormal);

        return new FloatMatrix(new float[][]{
                {
                        -1.0f * contactNormal.get(0),
                        -1.0f * contactNormal.get(1),
                        -1.0f * contactNormal.get(2),

                        -1.0f * r_i_cross.get(0),
                        -1.0f * r_i_cross.get(1),
                        -1.0f * r_i_cross.get(2),

                        0,
                        0,
                        0,

                        0,
                        0,
                        0
                },
        });
    }

    public FloatMatrix getFirstJacobian() {
        Vector3f contactNormal = getContactNormal();
        Vector3f r_i = contactNormal.mult(-1.0f);

        Vector3f r_i_cross = r_i.cross(contactNormal);

        return new FloatMatrix(new float[][]{
                {
                        -1.0f * contactNormal.get(0),
                        -1.0f * contactNormal.get(1),
                        -1.0f * contactNormal.get(2),

                        -1.0f * r_i_cross.get(0),
                        -1.0f * r_i_cross.get(1),
                        -1.0f * r_i_cross.get(2)
                }
        });
    }

    public FloatMatrix getSecondJacobian() {
        return new FloatMatrix(1, 6);
    }

    public FloatMatrix getInitialForce() {
        Vector3f firstElementForce = getFirstElement().getForce();

        return new FloatMatrix(new float[]
                {
                        firstElementForce.get(0),
                        firstElementForce.get(1),
                        firstElementForce.get(2),

                        0,
                        0,
                        0,

                        0,
                        0,
                        0,

                        0,
                        0,
                        0
        });
    }

    public Vector3f getContactNormal() {
        return getSecondElement().getNormal();
    }

    public float getOverlap() {
        Vector3f r_c = getSecondElement().getClosestPoint(getFirstElement());
        Vector3f r_i = r_c.subtract(getFirstElement().getPosition());

        //return r_i.length() - getFirstElement().getRadius();
        return r_i.length();
    }

    public float getE() {
        return e;
    }

    public FloatMatrix getCollisionMatrix() {
        FloatMatrix collisionMatrix = new FloatMatrix(3, 3);

        collisionMatrix = collisionMatrix.add(getFirstElement().getInverseMassMatrix());

        Vector3f r_c = getSecondElement().getClosestPoint(getFirstElement());
        Vector3f r_i = r_c.subtract(getFirstElement().getPosition());
        FloatMatrix r_i_cross = getCrossProductMatrix(r_i);
        FloatMatrix inertia_inverse = Solve.pinv(getFirstElement().getInertiaTensor());

        collisionMatrix = collisionMatrix.sub(r_i_cross.mmul(inertia_inverse).mmul(r_i_cross));

        return collisionMatrix;
    }

    public FloatMatrix getCrossProductMatrix(Vector3f vector) {
        return new FloatMatrix(new float[][]{
                {0.0f, -1.0f * vector.getZ(), vector.getY()},
                {vector.getZ(), 0.0f, -1.0f * vector.getX()},
                {-1.0f * vector.getY(), vector.getX(), 0.0f}
        });
    }

    public Vector3f getContactVelocity() {
        return getFirstElement().getVelocity().subtract(getSecondElement().getVelocity());
    }

    public float getInitialLambda() {
        Vector3f contactNormalVector = getContactNormal();
        FloatMatrix collisionMatrix = getCollisionMatrix();

        FloatMatrix contactNormal = new FloatMatrix(3, 1);
        contactNormal.put(0, 0, contactNormalVector.getX());
        contactNormal.put(1, 0, contactNormalVector.getY());
        contactNormal.put(2, 0, contactNormalVector.getZ());

        Vector3f r_c = getSecondElement().getClosestPoint(getFirstElement());
        Vector3f r_i = r_c.subtract(getFirstElement().getPosition());

        float left = contactNormal.transpose().mmul(collisionMatrix).mmul(contactNormal).get(0) + e;

        float q_l = r_i.length() - getFirstElement().getRadius();
        Vector3f u_l = getFirstElement().getVelocity();
        Vector3f u_f = getFirstElement().getForce().divide(getFirstElement().getMass()).mult(timestep);

        float right = -1.0f * a * q_l - b * u_l.dot(getSecondElement().getNormal()) - u_f.dot(getSecondElement().getNormal());

        float lambda = right / left;

        if (lambda < 0.0f) lambda = 0.0f;

        return lambda;
    }

    public FloatMatrix getInitialVelocity() {
        Vector3f firstVelocity = getFirstElement().getVelocity();

        FloatMatrix v = new FloatMatrix(12, 1);

        v.put(0, 0, firstVelocity.getX());
        v.put(1, 0, firstVelocity.getY());
        v.put(2, 0, firstVelocity.getZ());
        v.put(3, 0, 0.0f);
        v.put(4, 0, 0.0f);
        v.put(5, 0, 0.0f);

        return v;
    }

    public float getD() {
        FloatMatrix jacobian = getJacobian();
        FloatMatrix invMassMatrix = getInverseMassMatrix();
        FloatMatrix transposedJacobian = getJacobian().transpose();

        return jacobian.mmul(invMassMatrix).mmul(transposedJacobian).get(0) + getE();
    }

    public boolean isActive() {
        return getOverlap() < 0.1;
    }
}
