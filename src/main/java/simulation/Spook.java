package simulation;

import com.jme3.math.Vector3f;
import org.jblas.FloatMatrix;
import org.jblas.Solve;
import simulation.element.Element;
import simulation.element.Plane;
import simulation.element.Sphere;

import java.util.List;

public class Spook {
    private static int STABILIZE_STEPS = 1;
    private static float SPRING_CONSTANT = 1000.0f;

    private float timestep;
    private List<Element> elements;

    private float a, b, e;

    public Spook(float timestep, List<Element> elements) {
        this.timestep = timestep;
        this.elements = elements;

        a = 4.0f / (timestep * (1 + 4.0f * STABILIZE_STEPS));
        b = (4.0f * STABILIZE_STEPS) / (1 + 4.0f * STABILIZE_STEPS);
        e = 4.0f / (timestep * timestep * SPRING_CONSTANT * (1 + 4.0f * STABILIZE_STEPS));
    }

    public FloatMatrix getCrossProductMatrix(Vector3f vector) {
        return new FloatMatrix(new float[][]{
                {0.0f, -1.0f * vector.getZ(), vector.getY()},
                {vector.getZ(), 0.0f, -1.0f * vector.getX()},
                {-1.0f * vector.getY(), vector.getX(), 0.0f}
        });
    }

    public void solve(Plane plane, Sphere sphere) {
        FloatMatrix collisionMatrix = new FloatMatrix(3, 3);

        collisionMatrix = collisionMatrix.add(sphere.getInverseMassMatrix());

        Vector3f r_c = plane.getClosestPoint(sphere);
        Vector3f r_i = r_c.subtract(sphere.getPosition());

        FloatMatrix r_i_cross = getCrossProductMatrix(r_i);
        FloatMatrix inertia_inverse = Solve.pinv(sphere.getInertiaTensor());

        collisionMatrix = collisionMatrix.sub(r_i_cross.mmul(inertia_inverse).mmul(r_i_cross));

        //System.out.println(collisionMatrix);

        FloatMatrix contactNormal = new FloatMatrix(3, 1);
        contactNormal.put(0, 0, r_c.getX());
        contactNormal.put(1, 0, r_c.getY());
        contactNormal.put(2, 0, r_c.getZ());

        float left = contactNormal.transpose().mmul(collisionMatrix).mmul(contactNormal).get(0) + e;

        //System.out.println(left);

        float q_l = r_i.length() - sphere.getRadius();
        Vector3f u_l = sphere.getVelocity();
        Vector3f u_f = sphere.getForce().divide(sphere.getMass()).mult(timestep);

        float right = -1.0f * a * q_l - b * u_l.dot(plane.getNormal()) - u_f.dot(plane.getNormal());

        float lambda = right / left;

        if (lambda < 0.0f) lambda = 0.0f;

        Vector3f v = plane.getNormal().mult(lambda / sphere.getMass());

        System.out.println(v);

        sphere.setVelocity(sphere.getVelocity().add(v));

        /*
        FloatMatrix jacobian = new FloatMatrix(12);

        Vector3f contactNormal = plane.getNormal();
        Vector3f r_c = plane.getClosestPoint(sphere);
        Vector3f r_i = r_c.subtract(sphere.getPosition());
        Vector3f r_j = new Vector3f(0, 0, 0);

        Vector3f jacobianA = contactNormal.mult(-1.0f);
        Vector3f jacobianB = r_i.cross(contactNormal).mult(-1.0f);
        Vector3f jacobianC = contactNormal;
        Vector3f jacobianD = r_j.cross(contactNormal);

        jacobian.put(0, 0, jacobianA.get(0));
        jacobian.put(1, 0, jacobianA.get(1));
        jacobian.put(2, 0, jacobianA.get(2));
        jacobian.put(3, 0, jacobianB.get(0));
        jacobian.put(4, 0, jacobianB.get(1));
        jacobian.put(5, 0, jacobianB.get(2));
        jacobian.put(6, 0, jacobianC.get(0));
        jacobian.put(7, 0, jacobianC.get(1));
        jacobian.put(8, 0, jacobianC.get(2));
        jacobian.put(9, 0, jacobianD.get(0));
        jacobian.put(10, 0, jacobianD.get(1));
        jacobian.put(11, 0, jacobianD.get(2));

        FloatMatrix q = new FloatMatrix(new float[] {
                sphere.getPosition().getX(),
                sphere.getPosition().getY(),
                sphere.getPosition().getZ(),
                0.0f,
                0.0f,
                0.0f,
                r_c.getX(),
                r_c.getY(),
                r_c.getZ(),
                0.0f,
                0.0f,
                0.0f
        });

        FloatMatrix w = new FloatMatrix(new float[] {
                sphere.getVelocity().getX(),
                sphere.getVelocity().getY(),
                sphere.getVelocity().getZ(),
                0.0f,
                0.0f,
                0.0f,

                0.0f,
                0.0f,
                0.0f,
                0.0f,
                0.0f,
                0.0f
        });

        FloatMatrix sphereMassMatrix = sphere.getMassMatrix();
        FloatMatrix sphereInertiaTensor = sphere.getInertiaTensor();

        FloatMatrix m = new FloatMatrix(12, 12);
        m.put(0, 0, sphereMassMatrix.get(0, 0));
        m.put(1, 1, sphereMassMatrix.get(1, 1));
        m.put(2, 2, sphereMassMatrix.get(2, 2));
        m.put(3, 3, sphereInertiaTensor.get(0, 0));
        m.put(4, 4, sphereInertiaTensor.get(1, 1));
        m.put(5, 5, sphereInertiaTensor.get(2, 2));

        FloatMatrix f = new FloatMatrix(new float[] {
                sphere.getForce().getX(),
                sphere.getForce().getY(),
                sphere.getForce().getZ(),
                0.0f,
                0.0f,
                0.0f,

                0.0f,
                0.0f,
                0.0f,
                0.0f,
                0.0f,
                0.0f
        });

        System.out.println(jacobian.dot(Solve.pinv(m)));

        float rightSideA = -1.0f * a * (jacobian.dot(q));
        float rightSideB = -1.0f * b * (jacobian.dot(w));
        float rightSideC = -1.0f * timestep * (jacobian.dot((Solve.pinv(m)).mul(f)));

        float rightSide = rightSideA + rightSideB + rightSideC;

        System.out.println(rightSide);
        */
    }


}
