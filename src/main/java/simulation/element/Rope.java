package simulation.element;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import simulation.spook.CollisionPair;
import simulation.spook.GaussSeidelIterator;
import simulation.spook.SphereSphereCollisionPair;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Rope extends Element {
    private static float SPRING_CONSTANT = 100000.0f;
    private static float ELEMENT_DISTANCE = 0.00001f;

    private Node node;
    private List<Element> elements;

    public Rope(String id) {
        this(id, new Vector3f(0, 3.0f, 0), 10);
    }

    public Rope(String id, Vector3f position, int elemCount) {
        super(id, position, new Vector3f(0, 0, 0), 0.1f);

        elemCount = (elemCount > 0) ? elemCount : 5;

        init(elemCount);
    }

    protected void init(int elemCount) {
        node = new Node("Rope-" + getId());

        elements = new ArrayList<>(elemCount);
        for (int i = 0; i < elemCount; i++) {
            Sphere sphere = new Sphere(UUID.randomUUID().toString(), getPosition(), getMass());
            Vector3f position = sphere.getPosition().subtract(new Vector3f(i * 2 * sphere.getRadius(), 0.0f, 0.0f));

            sphere.setPosition(position);

            elements.add(sphere);
        }
    }

    @Override
    public Node render(AssetManager assetManager) {
        for (Element element : elements) {
            node.attachChild(element.render(assetManager));
        }

        return node;
    }

    @Override
    public void draw() {
        for (Element element : elements) {
            element.draw();
        }
    }

    @Override
    public List<Element> getElements() {
        return elements;
    }

    @Override
    public void update(float timestep) {
        for (Element element : elements) {
            element.update(timestep);
        }

        Element rootElement = elements.get(0);
        rootElement.setPosition(getPosition());
        rootElement.setVelocity(new Vector3f(0, 0, 0));

        List<CollisionPair> collisionPairs = new ArrayList<>();

        for (int i = 0; i < elements.size() - 1; i++) {
            collisionPairs.add(new SphereSphereCollisionPair((Sphere) elements.get(i), (Sphere) elements.get(i + 1), timestep, SPRING_CONSTANT, 1, ELEMENT_DISTANCE));
        }

        GaussSeidelIterator gs = new GaussSeidelIterator(collisionPairs, timestep);
        gs.solve();
    }

    @Override
    public void resetForce() {
        for (Element element : elements) {
            element.resetForce();
        }
    }

    public Element getElement(int i) {
        return elements.get(i);
    }
}
