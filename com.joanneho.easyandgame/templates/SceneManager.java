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
	private Scene splashScene, titleScene, mainGameScene, menuScene;
	private BitmapTextureAtlas splashTextureAtlas, scoreTextureAtlas, totalScoreTextureAtlas, menuBackgroundTexture;
	private TextureRegion splashTextureRegion, menuBgTexture;
	private ITextureRegion mBackgroundTextureRegion, mStagesTextureRegion, mTowerTextureRegion, mRing1, mRing2, mRing3, mstageClearedTextureRegion, mstageOneTextureRegion, mstageTwoTextureRegion;
	private Sprite mTower1, mTower2, mTower3, stageClearedSprite, menuOneStage_1, menuOneStage_2, menuOneStage_3;//click to enter game stages one, two, three
	private Stack mStack1, mStack2, mStack3;
	//private Ring ring1, ring2, ring3, ring4;
	boolean stageClearFlag = false;
	private Font stageScoreFont, totalScoreFont;
	private int stageScore, totalScore=0;
	private Text stageScoreText, totalScoreText, timeRemainText;
	
	public enum SceneType
	{
		SPLASH,
		TITLE,
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
		/**
		 * 1 - Setup an image from asset and load it from asset.
		 * 		a. Set the path of the image assets (One-time setting). Template:
		 * 			BitmapTextureAtlasTextureRegionFactory.setAssetBasePath(PATH_TO_IMAGE);
		 * 
		 *  	b. Declare an area for putting the text in, the area is called "Bitmap Texturea Atlas".
		 *  	   It has to be declared at top as a global variable, for example: "private BitmapTextureAtlas scoreTextureAtlas;"
		 *  	   Here, assigning it a new BitmapTextureAtlas. Template:
		 * 				Bitmap_Texture_Atlas = new BitmapTextureAtlas(TextureManager, Width, Height, TextureOptions); 
		 * 
		 * 		c. Declare an region for putting the Texture Atlas in, the region is called "Texture Region".
		 * 		   It has to be declared at top as a global variable, for example: "private TextureRegion splashTextureRegion;"
		 * 		   Here, assigning it a new BitmapTextureAtlasTextureRegionFactory. Template:
		 * 				Texture_Region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(Bitmap_Texture_Atlas, BaseGameActivity, FILENAME_OF_IMAGE, POSITION_X, POSITION_Y);
		 * 
		 * 		d.Load the Bitmap Texture Atlas. Template:
		 * 				Bitmap_Texture_Atlas.load();
		 * 
		 * 		Example:
		 * 				BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		 * 				splashTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.DEFAULT);
		 * 				splashTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashTextureAtlas, activity, "loading86.gif", 0, 0);
		 * 				splashTextureAtlas.load();
		**/
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
        
		/**
		 * 1 - Set up bitmap textures and Load bitmap textures into VRAM
		 * 		a. Declare an ITexture for setting up the image from the filesystem. Template:
		 * 				ITexture ITexture_Name =  setUpBitmapTextures(PATH_TO_IMAGE);
		 * 
		 *  	b. Load Texture. Template:
		 *  			ITexture_Name.load();
		 *  
		 *  	c. Set up Texture Region. Declare a region for putting the texture in, the region is called "ITexture Region"
		 *  	   It has to be declare at top as a global variable, for example: "private ITextureRegion mBackgroundTextureRegion;" 
		 *  	   Here, use a Texture Region Factory to extract a ITexture and assign it to a ITexture Region. Template:
		 *  			this.ITexture_Region = TextureRegionFactory.extractFromTexture(ITexture_Name);
		 *  
		 * 		Example:
		 *  			ITexture backgroundTexture=setUpBitmapTextures("gfx/background.png");
		 *  			backgroundTexture.load();
		 *  			this.mBackgroundTextureRegion = TextureRegionFactory.extractFromTexture(backgroundTexture);
		 **/
		
		/**
		 * 2 - Set up Text and load: setup a texture first, then setup a font where it applies to
		 * 		a. Declare an area for putting the text in, the area is called "Bitmap Texturea Atlas" 
		 *  	   It has to be declared at top as a global variable, for example: "private BitmapTextureAtlas scoreTextureAtlas;"
		 *  	   Here, assigning it a new BitmapTextureAtlas. Template:
		 * 				Bitmap_Texture_Atlas = new BitmapTextureAtlas(TextureManager, Width, Height, TextureOptions);
		 * 
		 *  	b. Load Texture Atlas
		 *  	   Use Texture Manager under Engine to load Texture. Template:
		 *  			this.engine.getTextureManager().loadTexture(this.Bitmap_Texture_Atlas);
		 *  
		 *  	c. Declare a Font 
		 * 		   It has to be declared at top as a global variable, for example: "private Font stageScoreFont;"
		 * 		   Here, assigning it a new Font, with its style. Template:
		 * 				Font_Name = new Font(Font_Manager , this.Bitmap_Texture_Atlas, Typeface.create(Typeface, style), Size, Whether_If_Anti_Alias, Color);
		 *  
		 *  	d. Load Font
		 *  	   Use Font Manager under activity to load Font. Template:
		 *  			BaseGameActivity.getFontManager().loadFont(this.Font_Name);
		 *  
		 *  	Example:
		 *      		scoreTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);        
		 *  			this.engine.getTextureManager().loadTexture(this.scoreTextureAtlas);
		 *  			stageScoreFont = new Font(activity.getFontManager(),this.scoreTextureAtlas, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32, true, Color.WHITE);
		 *  			activity.getFontManager().loadFont(this.stageScoreFont);
		**/
	}
	
	//Method creates the Splash Scene
	public Scene createSplashScene() {
		/**
		 * 1 - Setup a Scene (required) and put a Sprite in (optional).
		 * 		a. Declare a Scene for setting background and putting sprites in (if you want to)
		 * 		   It has to be declared at top as a global variable, for example: "private Scene splashScene;"
		 *  	   Here, assigning it a new Scene. Template:
		 *  			Scene_Name = new Scene();
		 *  
		 * 		b. Set background color. Template:
		 * 				Scene_Name.setBackground(new Background(RED_FLOAT, GREEN_FLOAT, BLUE_FLOAT));
		 * 
		 * 		c. (Optional) Declare a Sprite to put a Texture Region on to it, (and pre-draw it since it's a splash Scene). Template:
		 * 				Sprite Sprite_Name = new Sprite(0, 0, Texture_Region, activity.getVertexBufferObjectManager())
		 * 				{
		 * 					@Override
		 * 					protected void preDraw(GLState pGLState, Camera pCamera) {
		 * 						super.preDraw(pGLState, pCamera);
		 * 						pGLState.enableDither();
		 * 					}
		 * 				};
		 * 		d. (Optional) Set the scale of the Sprite. Template:
		 * 				Sprite_Name.setScale(SCALE_FLOAT);
		 * 		e. (Optional) Set the position of the Sprite. Template:
		 * 				Sprite_Name.setPosition((camera.getWidth() - Sprite_Name.getWidth()) * 0.5f, (camera.getHeight() - Sprite_Name.getHeight()) * 0.5f);
		 * 		   
		 * 		f. (Optional) Attach the Sprite to the Scene. Template:
		 * 				Scene_Name.attachChild(Sprite_Name);
		 * 
		 * 		g. Return the Scene_Name. Template:
		 * 				return Scene_Name;
		 * 
		 * 		Example:
		 * 				splashScene = new Scene();
		 * 				splashScene.setBackground(new Background(1, 1, 1));
		 * 				Sprite splash = new Sprite(0, 0, splashTextureRegion, activity.getVertexBufferObjectManager()){
		 * 					@Override
		 * 					protected void preDraw(GLState pGLState, Camera pCamera) {
		 *						super.preDraw(pGLState, pCamera);
		 *							pGLState.enableDither();
		 * 					}
		 * 				};
		 * 				splash.setScale(1.5f);
		 * 				splash.setPosition((camera.getWidth() - splash.getWidth()) * 0.5f, (camera.getHeight() - splash.getHeight()) * 0.5f);
		 * 				splashScene.attachChild(splash);
		 * 				return splashScene;
		 *  
		 */
		return splashScene;
	}
	
	//Method creates all of the Game Scenes
	public void createGameScenes() {		
		/**
		 * 1 - Setup a Scene (required) and put a Sprite or Text in (optional).
		 * 		a. Declare a Scene for setting background and putting sprites in (if you want to)
		 * 		   It has to be declared at top as a global variable, for example: "private Scene splashScene;"
		 *  	   Here, assigning it a new Scene. Template:
		 *  			Scene_Name = new Scene();
		 *  
		 * 		b. Set background color. Template:
		 * 				Scene_Name.setBackground(new Background(RED_FLOAT, GREEN_FLOAT, BLUE_FLOAT));
		 * 
		 *      c. (Optional) Set up background of the Scene as an image.
		 *          - Declare a Sprite to put a Texture Region on to it, with position defined.
		 *          - Set the position of the background image Sprite depending on the screen size
		 *          - Attach the image Sprite to the Scene
		 *          Template:
		 * 				Sprite Sprite_Name = new Sprite(0, 0, Texture_Region, activity.getVertexBufferObjectManager());
		 *				Sprite_Name.setPosition((camera.getWidth() - Sprite_Name.getWidth()) * 0.5f, (camera.getHeight() - Sprite_Name.getHeight()) * 0.5f);
		 *				Scene_Name.attachChild(Sprite_Name);
		 *
		 *		d. (Optional) Set up the text using the Font that is loaded in loadGameSceneResources before.
		 *			- Declare a Text and assign it with the text you want it to be.
		 *			  It has to be declared at top as a global variable, for example: "private Text totalScoreText;"
		 *			- Set the alignment, Horizontal or Vertical.
		 *			- Attach the text to the Scene
		 *			Template:
		 *				Text_Name = new Text(FLOAT_POSIOTION_X, FLOAT_POSITION_Y, this.Font_Name, String_Of_the_Text, activity.getVertexBufferObjectManager());
		 *				Text_Name.setHorizontalAlign(HorizontalAlign.CENTER); 
		 *				Scene_Name.attachChild(Text_Name);
		 *
		 *		Example:
		 *				//Create the Menu Scene and set background colour to green
		 *				menuScene = new Scene();
		 *				menuScene.setBackground(new Background(1, 1, 1));
		 *
		 *				//Set up the Stages Entries background (background of the menu)
		 *				Sprite stagesSprite = new Sprite(0, 0, this.mStagesTextureRegion, activity.getVertexBufferObjectManager());
		 *				stagesSprite.setPosition((camera.getWidth() - stagesSprite.getWidth()) * 0.5f, (camera.getHeight() - stagesSprite.getHeight()) * 0.5f);
		 *				menuScene.attachChild(stagesSprite);
		 *
		 *				//Set up the Total score on menu
		 *				totalScoreText = new Text(600, 10, this.totalScoreFont, "score:\n" + totalScore, activity.getVertexBufferObjectManager());
		 *				totalScoreText.setHorizontalAlign(HorizontalAlign.CENTER);
		 *				menuScene.attachChild(totalScoreText);       
		**/

		/**
		 * 2 - Setup a Game Scene Entry on menu
		 * 		a. Declare a Sprite to put a Texture Region on to it, with position defined. 
		 * 			- It has to be declared at top as a global variable, for example: "private Sprite menuOneStage_1;" 
		 * 			- Define it with a touch area and touch event along.
		 * 			Template:
		 * 				Sprite_Name = new Sprite(150, 150, Texture_Region, activity.getVertexBufferObjectManager()){
		 * 						@Override
		 * 						public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		 * 							//The touch down event when Stage Entry 1 is touched
		 * 							if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
		 * 								mainGameScene = new Scene();
		 * 								mainGameScene.setBackground(new Background(0, 0, 1)); 		
		 * 								//Create the game scene for stage 1
		 * 								onCreateScene(mainGameScene, 1);			        	
		 * 								setCurrentScene(SceneType.MAINGAME);
		 * 							}
		 * 							return true;
		 * 						}
		 * 				};
		 * 		b. Register the touch area for Stage Entries on menu. Template:
		 * 				Scene_Name.registerTouchArea(Sprite_Name);
		 * 		c. Attach the Sprites onto menu. Template:
		 * 				Scene_Name.attachChild(Sprite_Name);
		 * 		Example:
		 * 				menuOneStage_1 = new Sprite(150, 150, this.mstageOneTextureRegion, activity.getVertexBufferObjectManager()){
		 * 						@Override
		 * 						public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		 * 							//The touch down event when Stage Entry 1 is touched
		 * 							if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
		 * 								if (mainGameScene!=null)
		 * 									mainGameScene.reset();
		 * 								mainGameScene = new Scene();
		 * 								mainGameScene.setBackground(new Background(0, 0, 1)); 		
		 * 								//Create the game scene for stage 1
		 * 								onCreateScene(mainGameScene, 1);			        	
		 * 								setCurrentScene(SceneType.MAINGAME);
		 * 							}
		 * 							return true;
		 * 						}
		 * 				};
		 * 				menuScene.registerTouchArea(menuOneStage_1);	
		 * 				menuScene.attachChild(menuOneStage_1);
		**/
	}
	
	
	protected Scene onCreateScene(final Scene gameScene, int gameSceneStages) {
		Sprite backgroundSprite = new Sprite(0, 0, this.mBackgroundTextureRegion, activity.getVertexBufferObjectManager());
		backgroundSprite.setPosition((camera.getWidth() - backgroundSprite.getWidth()) * 0.5f, (camera.getHeight() - backgroundSprite.getHeight()) * 0.5f);
		gameScene.attachChild(backgroundSprite);
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
		case TITLE:
			engine.setScene(titleScene);
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
