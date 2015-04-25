package simulation.force;

import com.jme3.math.Vector3f;
import simulation.element.Element;

public class Gravity extends Force {
    public static float GRAVITATION = 9.80665f;

    @Override
    public String getIdentifier() {
        return "gravity";
    }

    @Override
    public void applyForce(Element element) {
        Vector3f force = element.getForce();

        force = force.add(0, (float) (-1.0 * element.getMass() * GRAVITATION), 0);

        element.setForce(force);
    }
}
