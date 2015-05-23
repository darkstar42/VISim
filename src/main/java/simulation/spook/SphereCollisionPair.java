package simulation.spook;

import com.jme3.math.Vector3f;
import org.jblas.FloatMatrix;
import simulation.element.Sphere;

public class SphereCollisionPair extends CollisionPair {
    private Sphere element0;
    private Sphere element1;

    private float timestep;
    private float springConstant;
    private int iterationSteps;

    private float a, b, e;

    public SphereCollisionPair(Sphere element0, Sphere element1, float timestep, float springConstant, int iterationSteps) {
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

    public Sphere getSecondElement() {
        return element1;
    }

    public FloatMatrix getInverseMassMatrix() {
        FloatMatrix firstPositionMatrix = getFirstElement().getInverseMassMatrix();
        FloatMatrix firstInertiaTensor = getFirstElement().getInverseInertiaTensor();
        FloatMatrix secondPositionMatrix = getSecondElement().getInverseMassMatrix();
        FloatMatrix secondInertiaTensor = getSecondElement().getInverseInertiaTensor();

        FloatMatrix cM = new FloatMatrix(12, 12);

        cM.put(0, 0, firstPositionMatrix.get(0, 0));
        cM.put(1, 1, firstPositionMatrix.get(1, 1));
        cM.put(2, 2, firstPositionMatrix.get(2, 2));
        cM.put(3, 3, firstInertiaTensor.get(0, 0));
        cM.put(4, 4, firstInertiaTensor.get(1, 1));
        cM.put(5, 5, firstInertiaTensor.get(2, 2));

        cM.put(6, 6, secondPositionMatrix.get(0, 0));
        cM.put(7, 7, secondPositionMatrix.get(1, 1));
        cM.put(8, 8, secondPositionMatrix.get(2, 2));
        cM.put(9, 9, secondInertiaTensor.get(0, 0));
        cM.put(10, 10, secondInertiaTensor.get(1, 1));
        cM.put(11, 11, secondInertiaTensor.get(2, 2));

        return cM;
    }

    public FloatMatrix getJacobian() {
        Vector3f contactNormal = getContactNormal();
        Vector3f r_i = contactNormal.mult(-1.0f);
        Vector3f r_j = contactNormal;

        Vector3f r_i_cross = r_i.cross(contactNormal);
        Vector3f r_j_cross = r_j.cross(contactNormal);

        FloatMatrix j = new FloatMatrix(new float[][]{
                {
                        -1.0f * contactNormal.get(0),
                        -1.0f * contactNormal.get(1),
                        -1.0f * contactNormal.get(2),

                        -1.0f * r_i_cross.get(0),
                        -1.0f * r_i_cross.get(1),
                        -1.0f * r_i_cross.get(2),

                        contactNormal.get(0),
                        contactNormal.get(1),
                        contactNormal.get(2),

                        r_j_cross.get(0),
                        r_j_cross.get(1),
                        r_j_cross.get(2),
                },
        });

        return j;
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
        Vector3f contactNormal = getContactNormal();
        Vector3f r_j = contactNormal;

        Vector3f r_j_cross = r_j.cross(contactNormal);

        return new FloatMatrix(new float[][]{
                {
                        contactNormal.get(0),
                        contactNormal.get(1),
                        contactNormal.get(2),

                        r_j_cross.get(0),
                        r_j_cross.get(1),
                        r_j_cross.get(2),
                }
        });
    }

    public FloatMatrix getInitialForce() {
        Vector3f firstElementForce = getFirstElement().getForce();
        Vector3f secondElementForce = getSecondElement().getForce();

        return new FloatMatrix(new float[]
                {
                        firstElementForce.get(0),
                        firstElementForce.get(1),
                        firstElementForce.get(2),

                        0,
                        0,
                        0,

                        secondElementForce.get(0),
                        secondElementForce.get(1),
                        secondElementForce.get(2),

                        0,
                        0,
                        0
        });
    }

    public Vector3f getContactNormal() {
        Vector3f v = getFirstElement().getPosition().subtract(getSecondElement().getPosition());

        v = v.divide(2.0f);

        return v;
    }

    public float getOverlap() {
        return getContactNormal().length() - getFirstElement().getRadius();
    }

    public float getE() {
        return e;
    }

    public FloatMatrix getCollisionMatrix() {
        Vector3f contactNormal = getContactNormal();
        Vector3f r_i = contactNormal.mult(-1.0f);
        Vector3f r_j = contactNormal;

        FloatMatrix r_i_cross = getCrossProductMatrix(r_i);
        FloatMatrix r_j_cross = getCrossProductMatrix(r_j);
        FloatMatrix inertia_inverse_i = getFirstElement().getInverseInertiaTensor();
        FloatMatrix inertia_inverse_j = getSecondElement().getInverseInertiaTensor();

        FloatMatrix collisionMatrix = new FloatMatrix(3, 3);

        collisionMatrix = collisionMatrix.add(getFirstElement().getInverseMassMatrix());
        collisionMatrix = collisionMatrix.add(getSecondElement().getInverseMassMatrix());

        collisionMatrix = collisionMatrix.sub(
                (r_i_cross.mmul(inertia_inverse_i).mmul(r_i_cross))
                        .add(r_j_cross.mmul(inertia_inverse_j).mmul(r_j_cross))
        );

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

        Vector3f u_f = getFirstElement().getForce().divide(getFirstElement().getMass());
        u_f = u_f.subtract(getSecondElement().getForce().divide(getSecondElement().getMass()));
        u_f = u_f.mult(timestep);

        float left = contactNormal.transpose().mmul(collisionMatrix).mmul(contactNormal).get(0) + e;
        float right = -1.0f * a * getOverlap() - b * getContactVelocity().dot(contactNormalVector) - u_f.dot(contactNormalVector);

        float lambda = right / left;

        if (lambda < 0.0f) lambda = 0.0f;

        return lambda;
    }

    public FloatMatrix getInitialVelocity() {
        Vector3f firstVelocity = getFirstElement().getVelocity();
        Vector3f secondVelocity = getSecondElement().getVelocity();

        FloatMatrix v = new FloatMatrix(12, 1);

        v.put(0, 0, firstVelocity.getX());
        v.put(1, 0, firstVelocity.getY());
        v.put(2, 0, firstVelocity.getZ());
        v.put(3, 0, 0.0f);
        v.put(4, 0, 0.0f);
        v.put(5, 0, 0.0f);

        v.put(6, 0, secondVelocity.getX());
        v.put(7, 0, secondVelocity.getY());
        v.put(8, 0, secondVelocity.getZ());
        v.put(9, 0, 0.0f);
        v.put(10, 0, 0.0f);
        v.put(11, 0, 0.0f);

        return v;
    }

    /*
    public Vector3f getInitialVelocity() {
        Vector3f contactNormal = getContactNormal();

        return contactNormal.mult(getInitialLambda() / getFirstElement().getMass());
    }

    public Vector3f getVelocity() {
        Vector3f v = getInitialVelocity();

        FloatMatrix t = getInverseMassMatrix().mmul(getInitialForce());

        v = v.add(new Vector3f(t.get(0), t.get(1), t.get(2)).mult(timestep));

        return v;
    }
    */

    /*
    public float getResidual() {
        FloatMatrix jacobian = getJacobian();
        Vector3f v = getVelocity();

        float r = jacobian.mmul(new FloatMatrix(new float[]{v.getX(), v.getY(), v.getZ()})).get(0);
        r += getE() * getInitialLambda();
        r -= getOverlap();

        return r;
    }
    */

    public float getD() {
        FloatMatrix jacobian = getJacobian();
        FloatMatrix invMassMatrix = getInverseMassMatrix();
        FloatMatrix transposedJacobian = getJacobian().transpose();

        return jacobian.mmul(invMassMatrix).mmul(transposedJacobian).get(0) + getE();
    }
}
