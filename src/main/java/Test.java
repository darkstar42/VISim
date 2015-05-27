import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.PssmShadowRenderer;
import simulation.ParticleSystem;
import simulation.Simulation;
import simulation.element.Cloth;
import simulation.element.Plane;
import simulation.element.Rope;
import simulation.element.Sphere;
import simulation.emitter.PointParticleEmitter;
import simulation.force.AirFriction;
import simulation.force.Gravity;

import java.util.UUID;

public class Test extends SimpleApplication {
    Simulation simulation;

    public static void main(String[] args) {
        Test app = new Test();
        app.start(); // start the game
    }

    @Override
    public void simpleInitApp() {
        initKeys();
        initCrossHairs();

        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(2.3f));
        rootNode.addLight(al);

        PointLight lamp_light = new PointLight();
        lamp_light.setColor(ColorRGBA.Yellow);
        lamp_light.setRadius(10f);
        lamp_light.setPosition(new Vector3f(5.0f, 5.0f, 5.0f));
        rootNode.addLight(lamp_light);

        Plane firstPlane = new Plane(UUID.randomUUID().toString());
        firstPlane.setNormal(new Vector3f(0.0f, 1.0f, -0.5f));
        Plane secondPlane = new Plane(UUID.randomUUID().toString());
        //secondPlane.setNormal(new Vector3f(0.0f, 1.0f, 0.0f));
        //secondPlane.setPosition(new Vector3f(-10, -1.0f, 10));
        simulation = new Simulation();
        simulation.addForce(new Gravity());
        simulation.addForce(new AirFriction());
        //simulation.addElement(firstPlane);
        simulation.addElement(secondPlane);
        /*
        simulation.addElement(new Sphere(UUID.randomUUID().toString(), new Vector3f(1.0f, 1.0f, 1.0f), 0.1f));
        simulation.addElement(new Sphere(UUID.randomUUID().toString(), new Vector3f(1.0f, 2.0f, 1.0f), 0.1f));
        simulation.addElement(new Sphere(UUID.randomUUID().toString(), new Vector3f(1.0f, 3.0f, 1.0f), 0.1f));
        simulation.addElement(new Sphere(UUID.randomUUID().toString(), new Vector3f(1.0f, 4.0f, 1.0f), 0.1f));
        simulation.addElement(new Sphere(UUID.randomUUID().toString(), new Vector3f(1.0f, 5.0f, 1.0f), 0.1f));
        simulation.addElement(new Sphere(UUID.randomUUID().toString(), new Vector3f(2.0f, 0.1f, 1.0f), 0.1f));
        */

        /*
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                for (int z = 0; z < 5; z++) {
                    simulation.addElement(
                            new Sphere(UUID.randomUUID().toString(), new Vector3f(0.2f * x, 0.2f * z + 0.1f, 0.2f * y), 0.1f)
                    );
                }
            }
        }
        */

        /*
        Rope rope = new Rope(UUID.randomUUID().toString(), new Vector3f(0, 5.0f, 0), 10);
        rope.getElement(9).setMass(1.0f);
        simulation.addElement(rope);
        */

        simulation.addElement(new Cloth(UUID.randomUUID().toString()));

        /*
        simulation.addParticleSystem(new ParticleSystem(new PointParticleEmitter(
                new Vector3f(0, 1, 0),
                //new Vector3f(3.0f, 0, 3.0f),
                new Vector3f(0, 3.0f, 0),
                50,
                600,
                0.4f
        )));
        */

        /*
        simulation.addParticleSystem(new ParticleSystem(new PointParticleEmitter(
                new Vector3f(0, 0, 0),
                new Vector3f(0.0f, -2.0f, 0.0f),
                10,
                500,
                0.4f
        )));
        */

        simulation.render(getAssetManager(), getRootNode());
    }

    @Override
    public void update() {
        super.update(); // makes sure to execute AppTasks

        simulation.update();
    }

    private void initKeys() {
        inputManager.addMapping("Shoot",
                new KeyTrigger(KeyInput.KEY_SPACE), // trigger 1: spacebar
                new MouseButtonTrigger(MouseInput.BUTTON_LEFT)); // trigger 2: left-button click
        inputManager.addListener(actionListener, "Shoot");
    }

    private ActionListener actionListener = new ActionListener() {

        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("Shoot") && !keyPressed) {
                Sphere sphere = new Sphere(UUID.randomUUID().toString(), new Vector3f(1.0f, 5.0f, 1.0f), 0.1f);
                sphere.setPosition(cam.getLocation());
                sphere.setVelocity(cam.getDirection().mult(5.0f));

                simulation.addElement(sphere, true);
            }
        }

    };

    protected void initCrossHairs() {
        setDisplayStatView(false);
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText ch = new BitmapText(guiFont, false);
        ch.setSize(guiFont.getCharSet().getRenderedSize() * 2);
        ch.setText("+"); // crosshairs
        ch.setLocalTranslation( // center
                settings.getWidth() / 2 - ch.getLineWidth() / 2, settings.getHeight() / 2 + ch.getLineHeight() / 2, 0);
        guiNode.attachChild(ch);
    }
}

