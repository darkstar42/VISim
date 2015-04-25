package simulation.element;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;

public abstract class Element {
    /**
     * An unique identifier within a simulation environment
     */
    private String id;

    private float mass;

    private Vector3f position;
    private Vector3f velocity;
    private Vector3f force;

    public Element(String id) {
        this(id, new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), 0.0f);
    }

    public Element(String id, Vector3f position, Vector3f velocity, float mass) {
        this.id = id;

        setMass(mass);
        setPosition(position);
        setVelocity(velocity);
        setForce(new Vector3f());
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector3f velocity) {
        this.velocity = velocity;
    }

    public Vector3f getForce() {
        return force;
    }

    public void setForce(Vector3f force) {
        this.force = force;
    }

    public void resetForce() {
        setForce(getForce().set(0, 0, 0));
    }

    public void update(Vector3f position, Vector3f velocity) {
        setPosition(position);
        setVelocity(velocity);
    }

    public abstract Geometry render(AssetManager assetManager);

    public abstract void draw();

    /**
     * Returns the distance to another element
     *
     * @param otherElement The element to calculate the distance for
     * @return The distance to the given element
     */
    public float getDistance(Element otherElement) {
        Vector3f otherPosition = otherElement.getPosition();

        return otherPosition.subtract(getPosition()).length();
    }
}
