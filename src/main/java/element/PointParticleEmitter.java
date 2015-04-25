package element;

import com.jme3.math.Vector3f;

import java.util.Random;

public class PointParticleEmitter implements ParticleEmitter {
    private static Random randomGenerator = new Random();

    private Vector3f location;
    private Vector3f initialVelocity;

    private int particleLifetime;
    private float particleMass;

    public PointParticleEmitter(Vector3f location) {
        this.location = new Vector3f(location);
        this.initialVelocity = new Vector3f(-0.5f, 0, -0.5f);
        this.particleLifetime = 300;
        this.particleMass = 0.2f;
    }

    @Override
    public Particle generateParticle(long id) {
        float randomX, randomY, randomZ;

        Vector3f particleLocation = new Vector3f(location);
        Vector3f particleVelocity = new Vector3f();

        randomX = (float) randomGenerator.nextDouble() - 0.5f;
        randomY = (float) randomGenerator.nextDouble() - 0.5f;
        randomZ = (float) randomGenerator.nextDouble() - 0.5f;

        particleVelocity.x = randomX + initialVelocity.x;
        particleVelocity.y = randomY + initialVelocity.y;
        particleVelocity.z = randomZ + initialVelocity.z;

        return new Particle(id, particleLocation, particleVelocity, particleMass, particleLifetime);
    }
}
