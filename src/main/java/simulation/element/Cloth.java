package simulation.element;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;

import java.util.UUID;

public class Cloth extends Element {
    private Geometry geometry;

    private Particle[][] particles;

    public Cloth(String id) {
        super(id);

        init();
    }

    protected void init() {
        particles = new Particle[10][10];
        Vector3f initialSpeed = new Vector3f(0, 0, 0);

        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                particles[y][x] = new Particle(UUID.randomUUID().toString(), new Vector3f(x, 5, y), initialSpeed, 0.2f);
            }
        }

        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                Particle particle = particles[y][x];

                for (int i = x - 1; i < x + 1; i++) {
                    if (i < 0 || i > 9) continue;

                    for (int j = y - 1; y < 10; y++) {
                        if (j < 0 || j > 9) continue;

                        Particle otherParticle = particles[j][i];
                        if (otherParticle.getId().equals(particle.getId())) continue;

                        particle.addStaticInteractionNeighbour(otherParticle);
                    }
                }
            }
        }
    }

    @Override
    public Geometry render(AssetManager assetManager) {
        return null;
    }

    @Override
    public void draw() {
        // TODO - update?
    }
}
