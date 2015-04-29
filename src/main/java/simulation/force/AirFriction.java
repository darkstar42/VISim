package simulation.force;

import com.jme3.math.Vector3f;
import simulation.element.Element;

public class AirFriction extends Force {
    public static float AIR_FRICTION = 0.001f;

    @Override
    public String getIdentifier() {
        return "air-friction";
    }

    @Override
    public void applyForce(Element element) {
        Vector3f velocity = element.getVelocity();

        velocity = velocity.add(velocity.mult(-1.0f * AIR_FRICTION));

        element.setVelocity(velocity);
    }
}
