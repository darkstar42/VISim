import com.jme3.app.SimpleApplication;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import particle.ParticleEmitter;
import particle.ParticleSystem;
import particle.PointParticleEmitter;

/**
 * Sample 1 - how to get started with the most simple JME 3 application.
 * Display a blue 3D cube and view from all sides by
 * moving the mouse and pressing the WASD keys.
 */
public class Test extends SimpleApplication {
    ParticleSystem particleSystem;

    public static void main(String[] args) {
        Test app = new Test();
        app.start(); // start the game
    }

    @Override
    public void simpleInitApp() {
        ParticleEmitter particleEmitter = new PointParticleEmitter(new Vector3f(0, 0, 0));
        particleSystem = new ParticleSystem(particleEmitter);

        Geometry geometry = particleSystem.getGeometry(assetManager);

        rootNode.attachChild(geometry);
    }

    @Override
    public void update() {
        super.update(); // makes sure to execute AppTasks

        particleSystem.update();
        particleSystem.draw();
    }
}

