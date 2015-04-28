package simulation.element;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import simulation.force.DampedSpring;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

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

    private List<Particle> staticInteractionNeighbours;

    private List<DampedSpring> springForces;

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

        staticInteractionNeighbours = new ArrayList<>();
        springForces = new ArrayList<>();
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

    public void update(float timestep) {
        super.update(timestep);

        for (Element element : getCollisionCandidates()) {
            if (element instanceof Plane) {
                float distance = ((Plane) element).getDistance(getPosition());
                float direction = ((Plane) element).getNormal().dot(getVelocity());

                Vector3f planeNormal = ((Plane) element).getNormal();

                Vector3f collisionPoint = ((Plane) element).getCollisionPoint(this);
                float width = ((Plane) element).getWidth() / 2.0f;

                Vector3f distancePoint = collisionPoint.subtract(getPosition());


                if (Math.abs(distance) < 0.1f) {
                    if (direction < 0.0f) {
                        Vector3f normalVelocity = planeNormal.mult(planeNormal.dot(getVelocity()));
                        Vector3f tangentialVelocity = getVelocity().subtract(normalVelocity);

                        Vector3f newVelocity = tangentialVelocity.subtract(normalVelocity.mult(0.7f));
                        setVelocity(newVelocity);
                    }

                    if (distance < 0.0f) {

                        setPosition(collisionPoint);
                        //setPosition(getPosition().setY(collisionPoint.y));
                    }
                }
            }
        }

        age++;
    }

    /*
    public void update(float timestep) {
        float halfTimestep = timestep / 2.0f;

        setVelocity(getVelocity().add(getForce().mult(halfTimestep)));

        for (Element element : getCollisionCandidates()) {
            if (element instanceof Plane) {
                float distance = ((Plane) element).getDistance(getPosition());

                if (distance < 0.001) {
                    float contact = ((Plane) element).getNormal().dot(getVelocity());

                    if (Math.abs(contact) < 0.005) {
                        System.out.println("Resting contact");
                    } else if (contact < 0) {
                        System.out.println(distance);
                        setVelocity(new Vector3f(0, 10, 0));
                        //setVelocity(((Plane) element).getNormal().mult(-2.0f * getVelocity().dot(((Plane) element).getNormal())).add(getVelocity()));
                    }
                }
            }
        }

        setVelocity(getVelocity().add(getForce().mult(halfTimestep)));
        setPosition(getPosition().add(getVelocity().mult(halfTimestep)));

        for (Element element : getCollisionCandidates()) {
            if (element instanceof Plane) {
                float contact = ((Plane) element).getNormal().dot(getVelocity());

                if (Math.abs(contact) < 0.005) {
                    System.out.println("Resting contact");
                } else if (getPosition().y < 0.0f && contact < 0) {
                    //setVelocity(getVelocity().set(0, 0, 0));
                }
            }
        }

        setPosition(getPosition().add(getVelocity().mult(timestep)));

        if (getPosition().y < 0.0f) {
            setPosition(getPosition().setY(0.0f));
        }

        age++;
    }
    */

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

    public List<Particle> getStaticInteractionNeighbours() {
        return staticInteractionNeighbours;
    }

    public void addStaticInteractionNeighbour(Particle particle) {
        staticInteractionNeighbours.add(particle);
    }

    public List<DampedSpring> getSpringForces() {
        return springForces;
    }

    public void addSpringForce(DampedSpring springForce) {
        springForces.add(springForce);
    }

    @Override
    public void updateInternalForces() {
        for (DampedSpring spring : springForces) {
            spring.applyForce(this);
        }
    }
}
