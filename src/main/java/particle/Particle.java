package particle;

import com.jme3.math.Vector3f;

public class Particle {
    private long id;

    private double mass;

    private int lifetime = -1;
    private int age = 0;

    private Vector3f position;
    private Vector3f velocity;

    public Particle(long id, Vector3f position, Vector3f velocity, double mass) {
        this.id = id;
        this.mass = mass;

        setPosition(position);
        setVelocity(velocity);
    }

    public Particle(long id, Vector3f position, Vector3f velocity, double mass, int lifetime) {
        this(id, position, velocity, mass);

        this.lifetime = lifetime;
    }

    public long getId() {
        return id;
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
        return position;
    }

    public Vector3f getVelocity() {
        return velocity;
    }

    public void update(Vector3f position, Vector3f velocity) {
        setPosition(position);
        setVelocity(velocity);

        age++;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void setVelocity(Vector3f velocity) {
        this.velocity = velocity;
    }

    public float getDistance(Particle distParticle) {
        Vector3f distPositoin = distParticle.getPosition();

        return distPositoin.subtract(position).length();
    }
}