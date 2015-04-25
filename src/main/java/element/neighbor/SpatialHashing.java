package element.neighbor;

import com.jme3.math.Vector3f;
import element.Particle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpatialHashing {
    private static int NUM_BUCKETS = 512 * 512 * 512;
    private static float CELL_SIZE = 0.01f;
    private static float INTERACTION_DISTANCE = 0.01f;

    private static Vector3f[] offsets;

    private Map<Integer, List<Particle>> positions;
    private Map<Long, List<Particle>> neighbors;

    static {
        offsets = new Vector3f[27];

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    offsets[(dx + 1) + 3 * (dy + 1) + 9 * (dz + 1)] = new Vector3f(dx, dy, dz);
                }
            }
        }
    }

    public SpatialHashing() {
        this.positions = new HashMap<>();
        this.neighbors = new HashMap<>();
    }

    public void updateNeighbors(List<Particle> particles) {
        this.positions.clear();
        this.neighbors.clear();

        for (int i = 0; i < particles.size(); i++) {
            Particle particle = particles.get(i);
            Vector3f cell = getCell(particle.getPosition(), CELL_SIZE);
            int cellIdx = getHashBucketIndex(cell);

            List<Particle> particleNeighbors;

            if (neighbors.containsKey(particle.getId())) {
                particleNeighbors = neighbors.get(particle.getId());
                particleNeighbors.clear();
            } else {
                particleNeighbors = new ArrayList<>();
                neighbors.put(particle.getId(), particleNeighbors);
            }

            /*
            for (int j = 0; j < offsets.length; j++) {
                Vector3f offsetPosition = cell.add(offsets[j]);
                int bucketIdx = getHashBucketIndex(offsetPosition);

                if (!positions.containsKey(cellIdx)) {
                    positions.put(cellIdx, new ArrayList<Particle>());
                }

                positions.get(cellIdx).add(element);
            }
            */

            for (int j = 0; j < offsets.length; j++) {
                Vector3f offsetPosition = cell.add(offsets[j]);
                int bucketIdx = getHashBucketIndex(offsetPosition);

                if (positions.containsKey(bucketIdx)) {
                    List<Particle> cellParticles = positions.get(bucketIdx);

                    for (int c = 0; c < cellParticles.size(); c++) {
                        Particle cellParticle = cellParticles.get(c);

                        if (cellParticle.getId() == particle.getId()) continue;

                        if (particle.getDistance(cellParticle) < INTERACTION_DISTANCE) {
                            particleNeighbors.add(cellParticle);
                        }
                    }
                }
            }

            if (!positions.containsKey(cellIdx)) {
                positions.put(cellIdx, new ArrayList<Particle>());
            }

            positions.get(cellIdx).add(particle);
        }
    }

    protected Vector3f getCell(Vector3f position, float cellSize) {
        return new Vector3f(
                position.x / cellSize,
                position.y / cellSize,
                position.z / cellSize
        );
    }

    protected int getHashBucketIndex(Vector3f position) {
        int h1 = 0x8da6b343;
        int h2 = 0xd8163841;
        int h3 = 0xcb1ab31f;

        int idx = Math.round(h1 * position.x + h2 * position.y + h3 * position.z) % NUM_BUCKETS;

        if (idx < 0) {
            idx += NUM_BUCKETS;
        }

        return idx;
    }
}
