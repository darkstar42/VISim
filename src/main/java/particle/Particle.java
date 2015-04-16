package particle;

import com.jme3.math.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Particle {
    private double mass;

    private int lifetime = -1;
    private int age = 0;

    private List<Vector3f> positions = new ArrayList<>();
    private List<Vector3f> velocities = new ArrayList<>();

    public Particle(Vector3f position, Vector3f velocity, double mass) {
        this.mass = mass;

        positions.add(position);
        velocities.add(velocity);
    }

    public Particle(Vector3f position, Vector3f velocity, double mass, int lifetime) {
        this(position, velocity, mass);

        this.lifetime = lifetime;
    }

    public double getMass() {
        return mass;
    }

    public int getLifetime() {
        return lifetime;
    }

    public int getAge() {
        return age;
    }

    public Vector3f getPosition() {
        return positions.get(0);
    }

    public Vector3f getVelocity() {
        return velocities.get(0);
    }
}