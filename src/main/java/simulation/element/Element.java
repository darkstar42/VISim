package simulation.element;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import org.jblas.FloatMatrix;
import org.jblas.Solve;
import simulation.spook.CollisionPair;

import java.util.ArrayList;
import java.util.List;

public abstract class Element {
    /**
     * An unique identifier within a simulation environment
     */
    private String id;

    private float mass;

    private Vector3f position;
    private Vector3f velocity;
    private Vector3f force;

    private List<CollisionPair> collisionPairs;
    private List<Element> collisionCandidates;

    public Element(String id) {
        this(id, new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), 0.0f);
    }

    public Element(String id, Vector3f position, Vector3f velocity, float mass) {
        this.id = id;

        setMass(mass);
        setPosition(position);
        setVelocity(velocity);
        setForce(new Vector3f());

        collisionCandidates = new ArrayList<>();
        collisionPairs = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }

    public FloatMatrix getMassMatrix() {
        return new FloatMatrix(new float[][]{
                {getMass(), 0.0f, 0.0f},
                {0.0f, getMass(), 0.0f},
                {0.0f, 0.0f, getMass()}
        });
    }

    public FloatMatrix getInverseMassMatrix() {
        return new FloatMatrix(new float[][]{
                {1.0f / getMass(), 0.0f, 0.0f},
                {0.0f, 1.0f / getMass(), 0.0f},
                {0.0f, 0.0f, 1.0f / getMass()}
        });
    }

    public FloatMatrix getInertiaTensor() {
        return new FloatMatrix(new float[][]{
                {0.0f, 0.0f, 0.0f},
                {0.0f, 0.0f, 0.0f},
                {0.0f, 0.0f, 0.0f}
        });
    }

    public FloatMatrix getInverseMassInertiaTensorMatrix() {
        FloatMatrix inertiaTensor = getInertiaTensor();

        return new FloatMatrix(new float[][]{
                {1.0f / getMass(), 0.0f, 0.0f, 0.0f, 0.0f, 0.0f},
                {0.0f, 1.0f / getMass(), 0.0f, 0.0f, 0.0f, 0.0f},
                {0.0f, 0.0f, 1.0f / getMass(), 0.0f, 0.0f, 0.0f},
                {0.0f, 0.0f, 0.0f, 1.0f / inertiaTensor.get(0, 0), 0.0f, 0.0f},
                {0.0f, 0.0f, 0.0f, 0.0f, 1.0f / inertiaTensor.get(1, 1), 0.0f},
                {0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f / inertiaTensor.get(2, 2)}
        });
    }

    public FloatMatrix getInverseInertiaTensor() {
        return Solve.pinv(getInertiaTensor());
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

    public void updateInternalForces() {
        // Intentionally left blank
    }

    public void findCollisionCandidates(List<Element> elements) {
        for (Element element : elements) {
            if (element instanceof Plane) {
                float distance = element.getDistance(this);

                // TODO
                addCollisionCandidate(element);
            }
        }
    }

    public void addCollisionCandidate(Element element) {
        collisionCandidates.add(element);
    }

    public void resetCollisionCandidates() {
        collisionCandidates.clear();
    }

    protected List<Element> getCollisionCandidates() {
        return collisionCandidates;
    }

    public void update(float timestep) {
        Vector3f newVelocity = getVelocity().add(getForce().mult(timestep));
        Vector3f newPosition = getPosition().add(newVelocity.mult(timestep));

        setPosition(newPosition);
        setVelocity(newVelocity);
    }

    public abstract Node render(AssetManager assetManager);

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

    public void addCollisionPair(CollisionPair collisionPair) {
        collisionPairs.add(collisionPair);
    }

    public List<CollisionPair> getCollisionPairs() {
        return collisionPairs;
    }

    public void resetCollisionPairs() {
        collisionPairs.clear();
    }

    public List<Element> getElements() {
        List<Element> elements = new ArrayList<>(1);
        elements.add(this);

        return elements;
    }
}
