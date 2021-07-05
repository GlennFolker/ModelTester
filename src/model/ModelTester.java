package model;

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.*;
import com.badlogic.gdx.backends.lwjgl3.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.*;
import com.badlogic.gdx.graphics.g3d.environment.*;
import com.badlogic.gdx.graphics.g3d.shaders.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.*;

public class ModelTester extends ApplicationAdapter{
    public static ModelBatch batch;
    public static Environment env;
    public static Camera camera;
    public static AssetManager assets;
    public static ModelInstance model;
    public static float time;

    public static void main(String[] args){
        new Lwjgl3Application(new ModelTester(), new Lwjgl3ApplicationConfiguration(){{
            setMaximized(true);
            setTitle("Model Tester");
        }});
    }

    @Override
    public void create(){
        Gdx.app.log("App", "Application created.");

        batch = new ModelBatch();
        env = new Environment();
        env.set(ColorAttribute.createAmbientLight(0.4f, 0.4f, 0.4f, 0.4f));
        env.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -1f, 0f));

        camera = new OrthographicCamera();
        camera.near = -10000f;
        camera.far = 10000f;

        assets = new AssetManager();

        assets.load("model.g3dj", Model.class);
    }

    @Override
    public void render(){
        if(!assets.isFinished()){
            assets.finishLoading();

            model = new ModelInstance(assets.get("model.g3dj", Model.class));

            Gdx.app.log("App", "Application assets loaded.");
        }else{
            time += Gdx.graphics.getDeltaTime();

            camera.viewportWidth = Gdx.graphics.getWidth();
            camera.viewportHeight = Gdx.graphics.getHeight();
            camera.update();

            Quaternion quat = Pools.obtain(Quaternion.class).idt();
            Vector3 tmp1 = Pools.obtain(Vector3.class).set(0f, 0f, 0f);
            Vector3 tmp2 = Pools.obtain(Vector3.class).set(0f, 0f, 0f);
            model.transform.set(tmp1.set(0f, 0f, 0f), quat.setEulerAngles(time * 60f, 0f, 0f), tmp2.set(96f, 96f, 96f));

            camera.position.set(0f, 0f, 0f);
            camera.up.set(Vector3.Y);

            Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

            batch.begin(camera);
            batch.render(model/*, env*/);
            batch.end();

            Pools.free(quat);
            Pools.free(tmp1);
            Pools.free(tmp2);
        }
    }

    @Override
    public void dispose(){
        Gdx.app.log("App", "Application disposed.");

        assets.dispose();
        batch.dispose();
    }
}
