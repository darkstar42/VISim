package simulation;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import simulation.element.Particle;
import simulation.emitter.ParticleEmitter;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class ParticleSystem {
    public interface ParticleSimulationCallback {
        void createParticles();
        void removeParticles();
    }

    private List<Particle> particles;

    private ParticleEmitter emitter;

    private ParticleSimulationCallback simulation = new ParticleSimulationCallback() {
        @Override
        public void createParticles() {
            emitter.generateParticles();
        }

        @Override
        public void removeParticles() {
            checkParticleLifetime();
        }
    };

    Geometry geometry;

    public ParticleSystem(ParticleEmitter emitter) {
        particles = new ArrayList<>(1000);

        this.emitter = emitter;

        this.emitter.setHandler(new ParticleEmitter.EmitterHandler() {
            @Override
            public void handleParticleCreated(Particle particle) {
                particles.add(particle);
            }
        });
    }

    public List<Particle> getParticles() {
        return particles;
    }

    public int getNumParticles() {
        return particles.size();
    }

    public Particle getParticle(int index) {
        if (index < 0 || index >= getNumParticles()) return null;

        return particles.get(index);
    }

    public ParticleSimulationCallback getSimulationCallback() {
        return simulation;
    }

    public void resetForces() {
        for (Particle particle : particles) {
            particle.resetForce();
        }
    }

    public Geometry render(AssetManager assetManager) {
        Mesh mesh = new Mesh();
        mesh.setMode(Mesh.Mode.Points);

        mesh.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(getVertices()));

        mesh.updateBound();
        mesh.updateCounts();

        geometry = new Geometry("particles", mesh);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setBoolean("VertexColor", true);
        geometry.setMaterial(mat);

        System.out.println(geometry);

        return geometry;
    }

    public void draw() {
        VertexBuffer vb = geometry.getMesh().getBuffer(VertexBuffer.Type.Position);
        FloatBuffer newBuffer = BufferUtils.createFloatBuffer(getVertices());
        vb.updateData(newBuffer);
        vb.setUpdateNeeded();

        float[] colorArray = new float[getNumParticles() * 4];

        int colorIndex = 0;

        for (int i = 0; i < getNumParticles(); i++) {
            Particle particle = getParticle(i);

            float ratio = 1.0f;

            if (particle.getLifetime() > 0) {
                ratio = (1.0f * particle.getAge()) / particle.getLifetime();
            }

            colorArray[colorIndex++] = ratio;
            colorArray[colorIndex++] = 1.0f - ratio;
            colorArray[colorIndex++] = 0.0f;
            colorArray[colorIndex++] = 1.0f;
        }

        geometry.getMesh().setBuffer(VertexBuffer.Type.Color, 4, colorArray);
    }

    private Vector3f[] getVertices() {
        int particleCount = getNumParticles();

        Vector3f[] vertices = new Vector3f[particleCount];

        for (int i = 0; i < particleCount; i++) {
            Particle particle = getParticle(i);

            vertices[i] = particle.getPosition();
        }

        return vertices;
    }

    protected void checkParticleLifetime() {
        for (int i = 0; i < particles.size(); i++) {
            Particle particle = particles.get(i);

            if (particle.getLifetime() != -1 && particle.getAge() > particle.getLifetime()) {
                particles.remove(i);
                i--;
            }
        }
    }
}
