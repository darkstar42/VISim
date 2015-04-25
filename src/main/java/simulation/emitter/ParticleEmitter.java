package simulation.emitter;

import com.jme3.math.Vector3f;
import simulation.element.Particle;

import java.util.Random;

public abstract class ParticleEmitter {
    private static Random randomGenerator = new Random();

    private Vector3f location;
    private Vector3f initialVelocity;

    private int spawningRate;
    private int particleLifetime;
    private float particleMass;

    private EmitterHandler handler;

    public interface EmitterHandler {
        /**
         * Called whenever a new element is created
         *
         * @param particle The new element
         */
        void handleParticleCreated(Particle particle);
    }

    public ParticleEmitter() {
        this(new Vector3f());
    }

    public ParticleEmitter(Vector3f location, Vector3f initialVelocity, int spawningRate, int particleLifetime, float particleMass) {
        this.location = location;
        this.initialVelocity = initialVelocity;
        this.spawningRate = spawningRate;
        this.particleLifetime = particleLifetime;
        this.particleMass = particleMass;
    }

    public ParticleEmitter(Vector3f location) {
        this(location, new Vector3f(-0.5f, 0, -0.5f), 50, 300, 0.2f);
    }

    protected Vector3f getInitialLocation() {
        return location;
    }

    protected Vector3f getInitialVelocity() {
        return initialVelocity;
    }

    protected int getParticleLifetime() {
        return particleLifetime;
    }

    protected float getParticleMass() {
        return particleMass;
    }

    protected EmitterHandler getHandler() {
        return handler;
    }

    public void setHandler(EmitterHandler handler) {
        this.handler = handler;
    }

    public void generateParticles() {
        for (int i = 0; i < spawningRate; i++) {
            generateParticle();
        }

    }

    protected abstract void generateParticle();
}
