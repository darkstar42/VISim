package particle;

import com.jme3.math.Vector3f;

import java.util.Random;

public class PointParticleEmitter implements ParticleEmitter {
    private static Random randomGenerator = new Random();

    private Vector3f location;
    private Vector3f initialVelocity;

    private int particleLifetime;

    public PointParticleEmitter(Vector3f location) {
        this.location = new Vector3f(location);
        this.initialVelocity = new Vector3f(-0.5f, 0, -0.5f);
        this.particleLifetime = 300;
    }

    @Override
    public Particle generateParticle() {
        Vector3f particleLocation = new Vector3f(location);
        Vector3f particleVelocity = new Vector3f();

        float randomX = (float) randomGenerator.nextDouble() - 0.5f;
        float randomY = (float) randomGenerator.nextDouble() - 0.5f;
        float randomZ = (float) randomGenerator.nextDouble() - 0.5f;

        particleVelocity.x = randomX + initialVelocity.x;
        particleVelocity.y = randomY + initialVelocity.y;
        particleVelocity.z = randomZ + initialVelocity.z;

        particleLocation.z += randomZ * 5.0;
        particleLocation.x += randomX * 5.0;
        particleLocation.y += randomY * 5.0;

        return new Particle(particleLocation, particleVelocity, particleLifetime);
    }
}
