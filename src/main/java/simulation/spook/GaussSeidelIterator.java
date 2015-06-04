package simulation.spook;

import com.jme3.math.Vector3f;
import org.jblas.FloatMatrix;
import org.jblas.Solve;
import org.lwjgl.Sys;

import java.util.List;

public class GaussSeidelIterator {
    private List<CollisionPair> collisionPairs;
    private float timestep;

    public GaussSeidelIterator(List<CollisionPair> collisionPairs, float timestep) {
        this.collisionPairs = collisionPairs;
        this.timestep = timestep;
    }

    public void solve() {
        for (int i = 0; i < 10; i++) {
            for (CollisionPair collisionPair : collisionPairs) {

                if (collisionPair.isActive()) {
                    //System.out.println(collisionPair.getOverlap());

                    FloatMatrix v_i = new FloatMatrix(6, 1);
                    FloatMatrix v_j = new FloatMatrix(6, 1);

                    FloatMatrix firstJacobian = collisionPair.getFirstJacobian();
                    FloatMatrix secondJacobian = collisionPair.getSecondJacobian();

                    FloatMatrix jacobian = collisionPair.getJacobian();

                    FloatMatrix firstInverseMassMatrix = collisionPair.getFirstElement().getInverseMassInertiaTensorMatrix();
                    FloatMatrix secondInverseMassMatrix = collisionPair.getSecondElement().getInverseMassInertiaTensorMatrix();

                    //System.out.println(firstJacobian);
                    //System.out.println(secondInverseMassMatrix);

                    float e = collisionPair.getE();
                    float D = 0.0f;
                    D += (firstJacobian.mmul(firstInverseMassMatrix).mmul(firstJacobian.transpose())).get(0);
                    D += (secondJacobian.mmul(secondInverseMassMatrix).mmul(secondJacobian.transpose())).get(0);
                    D += e;

                    //System.out.println(D);

                    // We only have one constraint, so skip the loop??

                    // v = 12 x 1
                    FloatMatrix v = collisionPair.getInitialVelocity();

                    //System.out.println("invMassMatrix: " + collisionPair.getInverseMassMatrix().rows + " x " + collisionPair.getInverseMassMatrix().columns);
                    //System.out.println("initialForce: " + collisionPair.getInitialForce().rows + " x " + collisionPair.getInitialForce().columns);

                    v = v.add(collisionPair.getInverseMassMatrix().mmul(collisionPair.getInitialForce()).mul(timestep));

                    float lambda = collisionPair.getInitialLambda();
                    float r = jacobian.mmul(v).get(0) + e * lambda - collisionPair.getOverlap();

                    float z = (-1.0f / D) * r;

                    FloatMatrix d_i = firstInverseMassMatrix.mmul(firstJacobian.transpose()).mul(z);
                    FloatMatrix d_j = secondInverseMassMatrix.mmul(secondJacobian.transpose()).mul(z);

                    v_i = v_i.add(d_i);
                    v_j = v_j.add(d_j);

                    collisionPair.getFirstElement().setVelocity(collisionPair.getFirstElement().getVelocity().add(new Vector3f(v_i.get(0), v_i.get(1), v_i.get(2))));
                    collisionPair.getSecondElement().setVelocity(collisionPair.getSecondElement().getVelocity().add(new Vector3f(v_j.get(0), v_j.get(1), v_j.get(2))));
                }
            }
        }
    }


    /*
    public void solve() {
        float[] residuals = new float[collisionPairs.size()];

        for (int i = 0; i < collisionPairs.size(); i++) {
            CollisionPair collisionPair = collisionPairs.get(i);

            residuals[i] = collisionPair.getResidual();
        }

        for (int i = 0; i < collisionPairs.size(); i++) {
            CollisionPair collisionPair = collisionPairs.get(i);

            float z = (-1.0f / collisionPair.getD()) * collisionPair.getResidual();
            residuals[i] += collisionPair.getE();

            FloatMatrix invMassMatrix = collisionPair.getInverseMassMatrix();
            FloatMatrix jacobian = collisionPair.getJacobian();
            FloatMatrix transposedJacobian = jacobian.transpose();

            FloatMatrix d = invMassMatrix.mmul(transposedJacobian).mul(z);

            residuals[i] += jacobian.mmul(d).get(0);

            Vector3f firstVelocity = collisionPair.getFirstElement().getVelocity();

            //collisionPair.getFirstElement().setVelocity(firstVelocity.a);
        }
    }

    */
}
