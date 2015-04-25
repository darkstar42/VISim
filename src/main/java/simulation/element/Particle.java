package simulation.element;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;

public class Particle extends Element {
    /**
     * The maximum lifetime of this element in simulation steps.
     * A lifetime of -1 makes the element live forever.
     */
    private int lifetime;

    /**
     * The age of this element in simulation steps.
     */
    private int age;

    /**
     * Creates a new element with infinite lifetime
     *
     * @param id An unique identifier within the simulation environment
     * @param position The position of this element in the simulation space
     * @param velocity The initial velocity vector
     * @param mass The mass of this element
     */
    public Particle(String id, Vector3f position, Vector3f velocity, float mass) {
        super(id, position, velocity, mass);

        this.lifetime = -1;
        this.age = 0;
    }

    /**
     * Creates a new element with a finite lifetime
     *
     * @param id An unique identifier within the simulation environment
     * @param position The position of this element in the simulation space
     * @param velocity The initial velocity vector
     * @param mass The mass of this element
     * @param lifetime The number of simulation steps this element will survive
     */
    public Particle(String id, Vector3f position, Vector3f velocity, float mass, int lifetime) {
        this(id, position, velocity, mass);

        this.lifetime = lifetime;
    }

    public void update(Vector3f position, Vector3f velocity) {
        super.update(position, velocity);

        age++;
    }

    @Override
    public Geometry render(AssetManager assetManager) {
        // Intentionally left blank
        return null;
    }

    @Override
    public void draw() {
        // Intentionally left blank
    }

    public int getAge() {
        return age;
    }

    public int getLifetime() {
        return lifetime;
    }
}
