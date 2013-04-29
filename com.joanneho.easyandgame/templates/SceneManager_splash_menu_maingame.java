
import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.util.GLState;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.adt.io.in.IInputStreamOpener;
import org.andengine.util.debug.Debug;
import android.graphics.Color;
import android.graphics.Typeface;


public class SceneManager {
	
	private SceneType currentScene;
	private BaseGameActivity activity;
	private Engine engine;
	private Camera camera;
	private Scene splashScene, mainGameScene, menuScene;
	private BitmapTextureAtlas splashTextureAtlas;
	private TextureRegion splashTextureRegion;
	private ITextureRegion  mstageOneTextureRegion, mstageTwoTextureRegion;
	private Sprite menuOneStage_1, menuOneStage_2;//click to enter game stages one, two, three

	//private Ring ring1, ring2, ring3, ring4;


	
	public enum SceneType
	{
		SPLASH,
		MAINGAME,
		MENU
	}
	
	public SceneManager(BaseGameActivity activity, Engine engine, Camera camera) {
		this.activity = activity;
		this.engine = engine;
		this.camera = camera;		
	}

	//Method loads all of the splash scene resources
	public void loadSplashSceneResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		splashTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.DEFAULT);
		splashTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashTextureAtlas, activity, "loading86.gif", 0, 0);
		splashTextureAtlas.load();
	}
	
	public ITexture setUpBitmapTextures(final String filename){
		ITexture tempITexture = null;
		try {
			tempITexture = new BitmapTexture(activity.getTextureManager(), new IInputStreamOpener() {
			    @Override
			    public InputStream open() throws IOException {
			        return activity.getAssets().open(filename);
			    }
			});
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return tempITexture;
	}

	
	//Method loads all of the resources for the game scenes
	public void loadGameSceneResources() {
        // 1 - Set up bitmap textures
		ITexture backgroundTexture=setUpBitmapTextures("gfx/background.png");

		ITexture stage1Texture=setUpBitmapTextures("gfx/stage1.png");
		ITexture stage2Texture=setUpBitmapTextures("gfx/stage2.png");

		
		
		// 2 - Load bitmap textures into VRAM
		backgroundTexture.load();

		stage1Texture.load();
		stage2Texture.load();
		
		// 3 - Set up texture regions
		

		this.mstageOneTextureRegion = TextureRegionFactory.extractFromTexture(stage1Texture);
		this.mstageTwoTextureRegion = TextureRegionFactory.extractFromTexture(stage2Texture);

	}
	
	//Method creates the Splash Scene
	public Scene createSplashScene() {
		//Create the Splash Scene and set background colour to red and add the splash logo.
		splashScene = new Scene();
		splashScene.setBackground(new Background(1, 1, 1));
		Sprite splash = new Sprite(0, 0, splashTextureRegion, activity.getVertexBufferObjectManager())
		{
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera) 
			{
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		};
		splash.setScale(1.5f);
		splash.setPosition((camera.getWidth() - splash.getWidth()) * 0.5f, (camera.getHeight() - splash.getHeight()) * 0.5f);
		splashScene.attachChild(splash);
		
		return splashScene;
	}
	
	//Method creates all of the Game Scenes
	public void createGameScenes() {		

		
		//Create the Menu Scene and set background colour to green
		menuScene = new Scene();
		menuScene.setBackground(new Background(1, 1, 1));
		
		

		
		menuOneStage_1 = new Sprite(150, 150, this.mstageOneTextureRegion, activity.getVertexBufferObjectManager()){
			    @Override
			    public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		         	//The touch down event when Stage Entry 1 is touched
		         	if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
			        	if (mainGameScene!=null)
			        		mainGameScene.reset();
			    		mainGameScene = new Scene();
			    		mainGameScene.setBackground(new Background(0, 0, 1)); 		
			    		//Create the game scene for stage 1
			    		onCreateScene(mainGameScene, 1);			        	
			        	setCurrentScene(SceneType.MAINGAME);
			        }
			        return true;
			    }
		 };
		 
		menuOneStage_2 = new Sprite(300, 150, this.mstageTwoTextureRegion, activity.getVertexBufferObjectManager()){
			    @Override
			    public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {	         	
		         	 //The touch down event when Stage Entry 2 is touched
			         if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
			        	if (mainGameScene!=null)
			        		mainGameScene.reset();
			    		mainGameScene = new Scene();
			    		mainGameScene.setBackground(new Background(0, 0, 1));			    		
			    		//Create the game scene for stage 2
			    		onCreateScene(mainGameScene, 2);			        	
			    		setCurrentScene(SceneType.MAINGAME);
			        }
			        return true;
			    }
		 };
		 
		 //Register the touch area for Stage Entries on menu
		 menuScene.registerTouchArea(menuOneStage_1);
		 menuScene.registerTouchArea(menuOneStage_2);
		 
		 //Attach the Sprites onto menu
		 menuScene.attachChild(menuOneStage_2);
		 menuScene.attachChild(menuOneStage_1);
		
	}
	
	
	protected Scene onCreateScene(final Scene gameScene, int gameSceneStages) {

		switch (gameSceneStages){
		
		case 1:

			return gameScene;
		case 2:

			return gameScene;
		case 3:
			return gameScene;
		}
		
		//end cases
		return gameScene;
	}

	//Method allows you to get the currently active scene
	public SceneType getCurrentScene() {
		return currentScene;
	}
	
	//Method allows you to set the currently active scene
	public void setCurrentScene(SceneType scene) {
		currentScene = scene;
		switch (scene)
		{
		case SPLASH:
			break;
		case MAINGAME:
			engine.setScene(mainGameScene);
			break;
		case MENU:
			engine.setScene(menuScene);
			break;
		}		
	}
		
}
