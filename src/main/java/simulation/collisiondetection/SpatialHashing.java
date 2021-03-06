package simulation.collisiondetection;

import com.jme3.math.Vector3f;
import simulation.Simulation;
import simulation.element.Element;
import simulation.element.Plane;
import simulation.element.Sphere;
import simulation.spook.CollisionPair;
import simulation.spook.SpherePlaneCollisionPair;
import simulation.spook.SphereSphereCollisionPair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpatialHashing {
    private static int H1 = 0x8da6b343;
    private static int H2 = 0xd8163841;
    private static int H3 = 0xcb1ab31f;

    private static int NUM_BUCKETS = 1024;

    private Simulation simulation;
    private Map<Integer, List<Element>> buckets;

    private List<CollisionPair> collisionPairs;

    public SpatialHashing(Simulation simulation) {
        this.simulation = simulation;

        buckets = new HashMap<>(NUM_BUCKETS);
        collisionPairs = new ArrayList<>();
    }

    private int getBucketIndex(Vector3f position) {
        int n = Math.round(H1 * Math.round(position.getX()) + H2 * Math.round(position.getY()) + H3 * Math.round(position.getZ()));

        n = n % NUM_BUCKETS;
        if (n < 0) n += NUM_BUCKETS;

        return n;
    }

    private int[] getBucketIndices(Vector3f position) {
        int[] indices = new int[9];
        int i = 0;

        for (int z = -1; z < 1; z++) {
            for (int y = -1; y < 1; y++) {
                for (int x = -1; x < 1; x++) {
                    indices[i] = getBucketIndex(position.add(new Vector3f(0.5f * x, 0.5f * y, 0.5f * z)));
                    i++;
                }
            }
        }

        return indices;
    }

    public void findCollisionPairs() {
        List<Element> elements = simulation.getElements();
        Plane plane = null;
        for (int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);

            if (element instanceof Plane) {
                plane = (Plane) element;
                break;
            }
        }

        for (List<Element> list : buckets.values()) {
            list.add(plane);

            for (Element firstElement : list) {

                for (Element secondElement : list) {
                    if (firstElement.equals(secondElement)) continue;

                    if (firstElement instanceof Sphere && secondElement instanceof Plane) {
                        createCollisionPair((Sphere) firstElement, (Plane) secondElement);
                    } else if (firstElement instanceof Sphere && secondElement instanceof Sphere) {
                        if (firstElement.getId().compareTo(secondElement.getId()) > 0) continue;

                        createCollisionPair((Sphere) firstElement, (Sphere) secondElement);
                    }
                }
            }
        }
    }

    private void createCollisionPair(Sphere sphereA, Sphere sphereB) {
        CollisionPair collisionPair = new SphereSphereCollisionPair(sphereA, sphereB, simulation.getTimestep(), 10000.0f, 3);

        sphereA.addCollisionPair(collisionPair);
        sphereB.addCollisionPair(collisionPair);
        collisionPairs.add(collisionPair);
    }

    private void createCollisionPair(Sphere sphere, Plane plane) {
        CollisionPair collisionPair = new SpherePlaneCollisionPair(sphere, plane, simulation.getTimestep(), 10000.0f, 3);

        sphere.addCollisionPair(collisionPair);
        plane.addCollisionPair(collisionPair);
        collisionPairs.add(collisionPair);
    }

    public void hash() {
        List<Element> elements = simulation.getElements();

        for (Element element : elements) {
            if (element.getElements().size() > 0) {
                for (Element innerElement : element.getElements()) {
                    int[] bucketIndices = getBucketIndices(innerElement.getPosition());

                    addToBucket(bucketIndices, innerElement);
                }
            } else {
                int[] bucketIndices = getBucketIndices(element.getPosition());

                addToBucket(bucketIndices, element);
            }
        }
    }

    private void addToBucket(int[] bucketIndices, Element element) {
        for (int i = 0; i < bucketIndices.length; i++) {
            addToBucket(bucketIndices[i], element);
        }
    }

    private void addToBucket(int bucketIndex, Element element) {
        List<Element> list = buckets.get(bucketIndex);

        if (list == null) {
            list = new ArrayList<>();
            buckets.put(bucketIndex, list);
        }

        list.add(element);
    }

    public List<CollisionPair> getCollisionPairs() {
        return collisionPairs;
    }
}
