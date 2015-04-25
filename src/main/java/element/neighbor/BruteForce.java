package element.neighbor;

import com.jme3.math.Vector3f;
import element.Particle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BruteForce {
    private Map<Long, List<Particle>> neighbors;

    private static float INTERACTION_RANGE = 1.0f;

    public void updateNeighbors(List<Particle> particles) {
        neighbors = new HashMap<>(particles.size());

        for (int i = 0; i < particles.size(); i++) {
            Particle particle = particles.get(i);
            Vector3f position = particle.getPosition();

            List<Particle> inRange = new ArrayList<>();

            for (int j = 0; j < particles.size(); j++) {
                if (i == j) continue;

                Particle checkParticle = particles.get(j);
                Vector3f checkPosition = checkParticle.getPosition();

                if (position.subtract(checkPosition).length() < INTERACTION_RANGE) {
                    inRange.add(checkParticle);
                }
            }

            neighbors.put(particle.getId(), inRange);
        }
    }

    public List<Particle> getNeighbors(Particle particle) {
        if (neighbors.containsKey(particle.getId())) {
            return neighbors.get(particle.getId());
        } else {
            return new ArrayList<>();
        }
    }
}
