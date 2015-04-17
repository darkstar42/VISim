package particle;

import com.jme3.math.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Particle {
    private double mass;

    private int lifetime = -1;
    private int age = 0;

    private Vector3f position;
    private Vector3f velocity;

    public Particle(Vector3f position, Vector3f velocity, double mass) {
        this.mass = mass;
        this.velocity = new Vector3f(velocity);
        this.position = new Vector3f(position);
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
        return new Vector3f(position);
    }

    public Vector3f getVelocity() {
        return new Vector3f(velocity);
    }

    public void update(Vector3f position, Vector3f velocity) {
        setPosition(position);
        setVelocity(velocity);

        age++;
    }

    public void setPosition(Vector3f position) {
        this.position = new Vector3f(position);
    }

    public void setVelocity(Vector3f velocity) {
        this.velocity = new Vector3f(velocity);
    }
}