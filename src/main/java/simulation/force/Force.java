package simulation.force;

import simulation.element.Element;
import simulation.element.Particle;

import java.util.List;

public abstract class Force {
    /**
     * Returns the name of force
     *
     * @return The name of the fore
     */
    public abstract String getIdentifier();

    /**
     * Applies the force on the given element
     *
     * @param element The element to apply force on
     */
    public abstract void applyForce(Element element);

    /**
     * Applies the force on the given list of elements
     *
     * @param elements The elements to apply force on
     */
    public void applyForce(List<Element> elements) {
        for (Element element : elements) {
            applyForce(element);
        }
    }
}
