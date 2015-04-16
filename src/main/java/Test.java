import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.shape.Box;
import com.jme3.util.BufferUtils;
import particle.ParticleEmitter;
import particle.ParticleSystem;
import particle.PointParticleEmitter;

/**
 * Sample 1 - how to get started with the most simple JME 3 application.
 * Display a blue 3D cube and view from all sides by
 * moving the mouse and pressing the WASD keys.
 */
public class Test extends SimpleApplication {

    public static void main(String[] args) {
        Test app = new Test();
        app.start(); // start the game
    }

    @Override
    public void simpleInitApp() {
        ParticleEmitter particleEmitter = new PointParticleEmitter(new Vector3f(0, 0, 0));
        ParticleSystem particleSystem = new ParticleSystem(particleEmitter);

        Geometry geometry = particleSystem.getGeometry(assetManager);

        rootNode.attachChild(geometry);

        particleSystem.update();
        particleSystem.draw();

        for (int i = 0; i < 1000; i++) {
            particleSystem.update();
        }

        particleSystem.draw();
    }
}

