package simulation.force;

import com.jme3.math.Vector3f;
import simulation.element.Element;
import simulation.element.Particle;

public class DampedSpring extends Force {
    private Particle particleA;
    private Particle particleB;

    private float restLength;
    private float dampingCoefficient;
    private float springConstant;

    public DampedSpring(Particle particleA, Particle particleB, float restLength, float dampingCoefficient, float springConstant) {
        this.particleA = particleA;
        this.particleB = particleB;
        this.restLength = restLength;
        this.dampingCoefficient = dampingCoefficient;
        this.springConstant = springConstant;
    }

    public DampedSpring(Particle particleA, Particle particleB, float restLength) {
        this(particleA, particleB, restLength, 2.0f, 500.0f);
    }

    @Override
    public String getIdentifier() {
        return "damped-spring";
    }

    @Override
    public void applyForce(Element element) {
        // Only calculate and apply the force when called for particle A
        if (element.equals(particleA)) {
            Vector3f connVector = particleA.getPosition().subtract(particleB.getPosition());
            Vector3f relVelocity = particleA.getVelocity().subtract(particleB.getVelocity());

            float distance = connVector.length();
            float springPart = springConstant * (distance - restLength);
            float dampingPart = dampingCoefficient * (relVelocity.dot(connVector) / distance);

            Vector3f springForce = connVector.divide(distance).mult(-1.0f * (springPart + dampingPart));

            Vector3f aForce = particleA.getForce();
            Vector3f bForce = particleB.getForce();

            aForce = aForce.add(springForce);
            bForce = bForce.add(springForce.mult(-1.0f));

            particleA.setForce(aForce);
            particleB.setForce(bForce);
        }
    }
}
