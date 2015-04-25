package simulation.emitter;

import com.jme3.math.Vector3f;
import simulation.element.Particle;

import java.util.Random;
import java.util.UUID;

public class PointParticleEmitter extends ParticleEmitter {
    private static Random randomGenerator = new Random();

    public PointParticleEmitter() {
        super();
    }

    public PointParticleEmitter(Vector3f location, Vector3f initialVelocity, int spawningRate, int particleLifetime, float particleMass) {
        super(location, initialVelocity, spawningRate, particleLifetime, particleMass);
    }

    protected void generateParticle() {
        float randomX, randomY, randomZ;

        Vector3f particleLocation = new Vector3f(getInitialLocation());
        Vector3f particleVelocity = new Vector3f();

        randomX = (float) randomGenerator.nextDouble() - 0.5f;
        randomY = (float) randomGenerator.nextDouble() - 0.5f;
        randomZ = (float) randomGenerator.nextDouble() - 0.5f;

        particleVelocity.x = randomX + getInitialVelocity().x;
        particleVelocity.y = randomY + getInitialVelocity().y;
        particleVelocity.z = randomZ + getInitialVelocity().z;

        String uid = UUID.randomUUID().toString();

        Particle particle = new Particle(uid, particleLocation, particleVelocity, getParticleMass(), getParticleLifetime());

        if (getHandler() != null) {
            getHandler().handleParticleCreated(particle);
        }
    }
}
