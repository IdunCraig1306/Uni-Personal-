package views;

import java.util.HashMap;
import java.util.List;

import org.h2.mvstore.MVMap;

import model.StoryModel;
import model.StorySceneModel;
import model.UserModel;
import storage.DatabaseInterface;
import storage.FileStoreInterface;
import web.WebRequest;
import web.WebResponse;

public class GamePlayView extends DynamicWebPage{
	
	//constants for passing url parameters
	final static String CURRENT_STORY = "current_story";
	final static String CURRENT_SCENE = "current_scene";
	final static String CHOICE_SELECTED = "choice_selected";
	
	StorySceneModel currentScene;
	MVMap<String, StorySceneModel> storyScenes;
	List<String> storyScenesKeys;
	MVMap<String, StoryModel> stories;
	List<String> storiesKeys;
	
	UserModel currentUser = new UserModel();
	MVMap<String, UserModel> users = db.s.openMap("Users");

	public GamePlayView(DatabaseInterface db, FileStoreInterface fs) {
		super(db, fs);
		// TODO Auto-generated constructor stub
		
		//gets all the stories stored in the database
		stories = db.s.openMap("Story");
		storiesKeys = stories.keyList();
		//check
		System.out.println("storiesKeys.toString()=>" + storiesKeys.toString() + "<");
		
		//gets all the story scenes stored in the database
		storyScenes = db.s.openMap("StoryScene");
		storyScenesKeys = storyScenes.keyList();
		//check
		System.out.println("storyScenesKeys.toString()=>" + storyScenesKeys.toString() + "<");
		
		//creates example story
		InitialStoryCreator();
				
	}

	@Override
	public boolean process(WebRequest toProcess) {
		if(toProcess.path.equalsIgnoreCase("gameplay"))
		{
			stories = db.s.openMap("Story");
			storiesKeys = stories.keyList();
			
			//Sets the current user to the logged in user
			currentUser = users.get(toProcess.cookies.get("username"));
			
			String currentStory = "";
			StoryModel currentStoryModel = stories.get(currentStory);
			String currentSceneNum = "";
			String choiceSelected = "";
			
			//Makes a hashmap of the URL parameters
			HashMap<String, String> urlParams = toProcess.params;	
			//check
			System.out.println(urlParams.toString());

			if (urlParams.containsKey(CURRENT_STORY)) {
				//gets the story id from the URL parameters
				currentStory = urlParams.get(CURRENT_STORY);
				currentStoryModel = stories.get(currentStory);
			}

			if (urlParams.containsKey(CURRENT_SCENE)) {
				//gets the scene id from the URL parameters
				currentSceneNum = urlParams.get(CURRENT_SCENE);
			}
			System.out.println("currentSceneNum=>"+ currentSceneNum + "<");

			
			if (urlParams.containsKey(CHOICE_SELECTED)) {
				//gets the choice selected from the URL parameters
				choiceSelected = urlParams.get(CHOICE_SELECTED);
			}
			System.out.println("choiceSelected=>"+ choiceSelected + "<");
			
			// retrieve current story, scene to navigate from
			currentScene = storyScenes.get(currentStory + "_" + currentSceneNum);
			
			String sceneToShow = "";
			if (choiceSelected == "") {
				//shows the starting scene if no choice has been passed from a previous scene ie loaded from play description page
				sceneToShow = Integer.toString(currentStoryModel.startScene);
			}
			
			//selects the next scene based on the user choice
			if (choiceSelected.equalsIgnoreCase("A")) {
				sceneToShow = Integer.toString(currentScene.nextSceneID[0]);
				if(!sceneToShow.equalsIgnoreCase("-1"))
				{
					//adds one to the count for how many times this option has been chosen
					currentScene.optionOneCount++;
					storyScenes.put(currentStory + "_" + currentSceneNum, currentScene);
					db.commit();
				}
			}
			
			if (choiceSelected.equalsIgnoreCase("B")) {
				sceneToShow = Integer.toString(currentScene.nextSceneID[1]);
				if(!sceneToShow.equalsIgnoreCase("-1"))
				{
					//adds one to the count for how many times this option has been chosen
					currentScene.optionTwoCount++;
					storyScenes.put(currentStory + "_" + currentSceneNum, currentScene);
					db.commit();
				}
			}
			
			StoryModel storyObjectToUpdate = null;
			
			if (sceneToShow.equalsIgnoreCase("-1")) {
				// retrieve the story object from DB
				storyObjectToUpdate = stories.get(currentStory);
				
				if (choiceSelected.equalsIgnoreCase("A")) {	
					//adds one to likes for this story
					storyObjectToUpdate.noOfLikes++;
				}
				
				if (choiceSelected.equalsIgnoreCase("B")) {
					//adds one to dislikes for this story
					storyObjectToUpdate.noOfDislikes++;
				}				
				// save the story object back to the DB
				stories.put(currentStory, storyObjectToUpdate);
				db.commit();
				System.out.println("storyObjectToUpdate.noOfLikes=>"+ storyObjectToUpdate.noOfLikes + "<");
				System.out.println("storyObjectToUpdate.noOfDislikes=>"+ storyObjectToUpdate.noOfDislikes + "<");
			}
			
			System.out.println("sceneToShow=>"+ sceneToShow + "<");//check
		
			//retrieve current story, scene to display
			currentScene = storyScenes.get(currentStory + "_" + sceneToShow);
			
			//HTML
			String stringToSendToWebBrowser = "<!DOCTYPE html>\r\n" + "<html lang=\"en\">\r\n" + "\r\n" + "<head>\r\n"
					+ "\r\n"
					+ "	<link rel=\"stylesheet\" href=\"https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css\" integrity=\"sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T\" crossorigin=\"anonymous\">\r\n"
					+ "\r\n"
					+ "	<script src=\"https://code.jquery.com/jquery-3.2.1.slim.min.js\" integrity=\"sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN\" crossorigin=\"anonymous\">\r\n"
					+ "	</script>\r\n" + "\r\n"
					+ "	<script src=\"https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js\" integrity=\"sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q\" crossorigin=\"anonymous\">\r\n"
					+ "	</script>\r\n" + "\r\n"
					+ "	<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js\" integrity=\"sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl\" crossorigin=\"anonymous\">\r\n"
					+ "	</script>\r\n" + "\r\n" + "	<title>Quest For Fun</title>\r\n" + "</head>\r\n" + "\r\n" + "<body>\r\n"
					+ "\r\n" + "	<!-- Comment -->\r\n" + "	<!-- Navbar </-->\r\n" + "\r\n"
					+ "	<nav class=\"navbar navbar-expand-lg navbar-light bg-light\">\r\n" + "\r\n"
					+ "		<!-- Start: Navbar Brand Text Button</-->\r\n"
					+ "		<a class=\"navbar-brand\" href=\"home\">Quest for Fun.</a>\r\n"
					+ "		<button class=\"navbar-toggler\" type=\"button\" data-toggle=\"collapse\" data-target=\"#navbarColor03\" aria-controls=\"navbarColor03\" aria-expanded=\"false\" aria-label=\"Toggle navigation\">\r\n"
					+ "			<span class=\"navbar-toggler-icon\"></span>\r\n" + "		</button>\r\n"
					+ "		<!-- End: Navbar Brand Text Button</-->\r\n" + "\r\n"
					+ "		<div class=\"collapse navbar-collapse\" id=\"navbarColor03\">\r\n"
					+ "			<ul class=\"navbar-nav mr-auto\">\r\n" + "\r\n" + "				<!-- Play </-->\r\n"
					+ "				<li class=\"nav-item active\"><a class=\"nav-link\" href=\"Catalogue\">Play\r\n"
					+ "						<span class=\"sr-only\">(current)</span>\r\n"
					+ "					</a></li>\r\n" + "\r\n" + "				<!-- Editor </-->\r\n"
					+ "				<li class=\"nav-item\"><a class=\"nav-link\" href=\"Editor\">Editor</a></li>\r\n"
					+ "\r\n" + "				<!-- Forums </-->\r\n"
					+ "				<li class=\"nav-item\"><a class=\"nav-link\" href=\"Forum\">Forums</a></li>\r\n"
					+ "\r\n" + "				<!-- Help </-->\r\n"
					+ "				<li class=\"nav-item\"><a class=\"nav-link\" href=\"Help\">Help</a></li>\r\n" + "\r\n"
					+ "				<!-- About Us </-->\r\n"
					+ "				<li class=\"nav-item\"><a class=\"nav-link\" href=\"aboutus\">About Us</a></li>\r\n";
					
					
					if(!(toProcess.cookies.get("username") == null)) {
						stringToSendToWebBrowser += "<li class=\"nav-item\"> <a class=\"nav-link text-primary\" href=\"profile\">"
								+ toProcess.cookies.get("username") + "</a> </li>\r\n";
        			}else {
        				stringToSendToWebBrowser += "<li class=\"nav-item\"> <a class=\"nav-link text-primary\" href=\"register\">"
        						+ "Register" + "</a> </li>\r\n";
        			}
					
					stringToSendToWebBrowser += "</ul></div>\r\n"
					+ "	</nav>\r\n" + "	<!-- End of Navbar </-->\r\n" + "\r\n" + "\r\n";

					stringToSendToWebBrowser +="  <div class=\"text-center py-4\">\r\n" + 
					"    <div class=\"container\">\r\n" + 
					"      <div class=\"row\">\r\n" + 
					"        <div class=\"mx-auto col-md-12\">\r\n" + 
					"          <h1 class=\"mb-3\"><b>"+ currentStoryModel.storyTitle + "</b></h1>\r\n" + 
					"        </div>\r\n" + 
					"      </div>\r\n "+
					"    </div>\r\n" + 
					"  </div>\r\n "+
					"<div class=\"text-center py-4\">\r\n" + 
					"    <div class=\"container\">\r\n" + 
					"      <div class=\"row\">\r\n" + 
					"        <div class=\"mx-auto col-md-12\">\r\n";
					if(sceneToShow.equalsIgnoreCase("-1")) {
						//larger text for the after rating page
						stringToSendToWebBrowser += "          <h3 class=\"mb-1\">" + currentScene.sceneDescription +"</h3>\r\n";
					}else {
						stringToSendToWebBrowser += "          <p class=\"text-left\" style=\"	min-height: 30%;\" >" + currentScene.sceneDescription +"</p>\r\n";
					}
					stringToSendToWebBrowser += "        </div>\r\n" + 
					"      </div>\r\n" + 
					"    </div>\r\n" + 
					"  </div>"+
					"</div>";
					//shows the options for the scene if it is not the after rating scene
					if (!sceneToShow.equalsIgnoreCase("-1")) {
						stringToSendToWebBrowser += 
						"<div class=\"text-center py-4\">\r\n" + 
						"    <div class=\"container\">\r\n" + 
						"      <div class=\"row\">\r\n" + 
						"        <div class=\"mx-auto col-md-12 py-3\"><a id = \"button-option-one\" class=\"btn btn-primary btn-block btn-lg\" href=\"gameplay?" + CURRENT_STORY + "=" + currentStory + "&" + CURRENT_SCENE  + "=" + sceneToShow + "&" + CHOICE_SELECTED + "=A\" style=\"	min-height: 10%;\">" + currentScene.optionOne + "</a></div>\r\n" + 
						"      </div>\r\n" + 
						"      <div class=\"row\">\r\n" + 
						"        <div class=\"mx-auto col-md-12 py-3\"><a id = \"button-option-two\"class=\"btn btn-primary btn-block btn-lg\" href=\"gameplay?" + CURRENT_STORY + "=" + currentStory + "&" + CURRENT_SCENE  + "=" + sceneToShow + "&" + CHOICE_SELECTED + "=B\" style=\"	min-height: 10%;\">" + currentScene.optionTwo + "</a></div>\r\n" + 
						"      </div>\r\n" + 
						"    </div>\r\n" + 
						"  </div>"; 
					}
					//shows the likes and dislikes for this story
					if (sceneToShow.equalsIgnoreCase("-1")) {
						stringToSendToWebBrowser += 
						"<div class=\"text-center py-4\">\r\n" + 
						"    <div class=\"container\">\r\n" + 
						"      <div class=\"row\">\r\n" + 
						"<div class=\"d-flex w-100 justify-content-between\">\r\n" +
						"        <div class=\"col-md-5 px-5 mx-auto form-inline\" style= \"\">"+
					    "				<img src=\"Images/thumbsUp.png\" width=\"160\" height=\"120\" class=\"mx-auto img-fluid\">"+
						"          		<h5 class=\"mb-1\">Number of Likes: "+ storyObjectToUpdate.noOfLikes +"</h5>\r\n" + 
						 "		 </div>\r\n" +
						"        <div class=\"col-md-5 px-5 mx-auto form-inline\" style= \"\">"+
						"				<img src=\"Images/thumbsDown.png\" width=\"160\" height=\"120\" class=\"mx-auto img-fluid\">"+
						"          		<h5 class=\"mb-1\">Number of Dislikes: "+ storyObjectToUpdate.noOfDislikes +"</h5>\r\n" +
					    "		 </div>\r\n" +
						"      </div>\r\n" + 
						"    </div>\r\n" + 
						"  </div>\r\n";
					}
					//FOOTER
					stringToSendToWebBrowser += "<div class=\"text-center py-4\">\r\n" + 
					"    <div class=\"container\">\r\n" + 
					"      <div class=\"row\">\r\n" +
					"			</div>" +
					"		</div>\r\n" + 
					"    </div>\r\n" + 
					"  </div>"+
					"  <script src=\"https://code.jquery.com/jquery-3.3.1.slim.min.js\" integrity=\"sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo\" crossorigin=\"anonymous\" style=\"\"></script>\r\n" + 
					"  <script src=\"https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js\" integrity=\"sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49\" crossorigin=\"anonymous\" style=\"\"></script>\r\n" + 
					"  <script src=\"https://stackpath.bootstrapcdn.com/bootstrap/4.2.1/js/bootstrap.min.js\" integrity=\"sha384-B0UglyR+jN6CkvvICOB2joaf5I4l3gm9GU6Hc1og6Ls7i6U/mkkaduKaBhlAXv9k\" crossorigin=\"anonymous\" style=\"\"></script>\r\n" + 
					" <script src=\"gameplay.js\"></script>";
					
					stringToSendToWebBrowser += 
					"</body>\r\n" + 
					"</html>";
			
			toProcess.r = new WebResponse(WebResponse.HTTP_OK, WebResponse.MIME_HTML, stringToSendToWebBrowser);
			
			return true;
		}
		return false;
	}
	
	public void InitialStoryCreator() {
		
		if(stories.keyList().size() == 0) {	  
		  StoryModel story = new StoryModel();
		  story.storyID = -1; 
		  story.storyAuthor = "Admin"; 
		  story.storyTitle = "Invasion";
		  story.storyDescription = "Aliens have invaded Earth. Everyone must fight to survive.";
		  story.noOfDislikes = 0; 
		  story.noOfLikes = 0;
		  story.hasStart = true;
		  story.published = true;
		  stories.put(Integer.toString(story.storyID), story);
		  db.commit(); 
		  
		  
		  storyScenes = db.s.openMap("StoryScene");
		  storyScenesKeys = storyScenes.keyList(); 
		  System.out.println("storyScenesKeys.toString()=>" + storyScenesKeys.toString() + "<"); 
		  
		  
		  StorySceneModel scene1 = new StorySceneModel(); 
		  scene1.storyID = -1;
		  scene1.sceneID = 1; 
		  scene1.combinedID = scene1.storyID + "_" + scene1.sceneID; 
		  scene1.sceneDescription = "You turn on the TV to the news to see images of space crafts descending on the world's capitals."; 
		  scene1.optionOne = "Rush Outside"; 
		  scene1.optionTwo = "Remain Indoors"; 
		  scene1.nextSceneID[0] =  3; 
		  scene1.nextSceneID[1] = 2;
		  
		  storyScenes.put(scene1.combinedID, scene1); db.commit();
		  storyScenesKeys = storyScenes.keyList();
		  
		  
		  StorySceneModel scene2 = new StorySceneModel(); 
		  scene2.storyID = -1;
		  scene2.sceneID = 2; 
		  scene2.combinedID = scene2.storyID + "_" + scene2.sceneID; 
		  scene2.sceneDescription = "You rummage the house to find a weapon."; 
		  scene2.optionOne = "Grab the fire axe."; 
		  scene2.optionTwo = "Grab the baseball bat."; 
		  scene2.nextSceneID[0] = 4; 
		  scene2.nextSceneID[1] = 5;
		  
		  storyScenes.put(scene2.combinedID, scene2); db.commit();
		  storyScenesKeys = storyScenes.keyList();
		  
		  StorySceneModel scene3 = new StorySceneModel(); 
		  scene3.storyID = -1;
		  scene3.sceneID = 3; 
		  scene3.combinedID = scene3.storyID + "_" + scene3.sceneID; 
		  scene3.sceneDescription = "Outside, all you hear and see is chaos. Explosions and screams fill the air."; 
		  scene3.optionOne = "Run back inside."; 
		  scene3.optionTwo = "Get in the car and drive to find your family."; 
		  scene3.nextSceneID[0] =  2; 
		  scene3.nextSceneID[1] = 7;
		  
		  storyScenes.put(scene3.combinedID, scene3); db.commit();
		  storyScenesKeys = storyScenes.keyList();
		  
		  StorySceneModel scene4 = new StorySceneModel(); 
		  scene4.storyID = -1;
		  scene4.sceneID = 4; 
		  scene4.combinedID = scene4.storyID + "_" + scene4.sceneID; 
		  scene4.sceneDescription = "Suddenly an alien bursts through your front door."; 
		  scene4.optionOne = "Fight the alien."; 
		  scene4.optionTwo = "Try to run for help."; 
		  scene4.nextSceneID[0] = 6; 
		  scene4.nextSceneID[1] = 8;
		  
		  storyScenes.put(scene4.combinedID, scene4); db.commit();
		  storyScenesKeys = storyScenes.keyList();
		  
		  StorySceneModel scene5 = new StorySceneModel(); 
		  scene5.storyID = -1;
		  scene5.sceneID = 5; 
		  scene5.combinedID = scene5.storyID + "_" + scene5.sceneID; 
		  scene5.sceneDescription = "Suddenly an alien bursts through your front door."; 
		  scene5.optionOne = "Fight the alien."; 
		  scene5.optionTwo = "Try to run for help."; 
		  scene5.nextSceneID[0] = 9; 
		  scene5.nextSceneID[1] = 8;
		  
		  storyScenes.put(scene5.combinedID, scene5); db.commit();
		  storyScenesKeys = storyScenes.keyList();
		  
		  StorySceneModel ending1 = new StorySceneModel(); 
		  ending1.storyID = -1;
		  ending1.sceneID = 6; 
		  ending1.combinedID = ending1.storyID + "_" + ending1.sceneID; 
		  ending1.sceneDescription = "You swing at it with your fireaxe, splitting its grotespue head in two. Locking the door, you stay safe.";
		  ending1.optionOne = "Click here if you liked this Story"; 
		  ending1.optionTwo = "Click here if you did not like this Story"; 
		  ending1.nextSceneID[0] = -1;
		  ending1.nextSceneID[1] = -1; 
		  ending1.endScene = true;
		  
		  storyScenes.put(ending1.combinedID, ending1); db.commit();
		  storyScenesKeys = storyScenes.keyList();
		  
		  StorySceneModel ending2 = new StorySceneModel(); 
		  ending2.storyID = -1;
		  ending2.sceneID = 7; 
		  ending2.combinedID = ending2.storyID + "_" + ending2.sceneID; 
		  ending2.sceneDescription = "Your car is hit by a plasma beam. You were vapourised. RIP";
		  ending2.optionOne = "Click here if you liked this Story"; 
		  ending2.optionTwo = "Click here if you did not like this Story"; 
		  ending2.nextSceneID[0] = -1;
		  ending2.nextSceneID[1] = -1; 
		  ending2.endScene = true;
		  
		  storyScenes.put(ending2.combinedID, ending2); db.commit();
		  storyScenesKeys = storyScenes.keyList();
		  
		  StorySceneModel ending3 = new StorySceneModel(); 
		  ending3.storyID = -1;
		  ending3.sceneID = 8; 
		  ending3.combinedID = ending3.storyID + "_" + ending3.sceneID; 
		  ending3.sceneDescription = "The alien catches you, eating you alive. RIP";
		  ending3.optionOne = "Click here if you liked this Story"; 
		  ending3.optionTwo = "Click here if you did not like this Story"; 
		  ending3.nextSceneID[0] = -1;
		  ending3.nextSceneID[1] = -1; 
		  ending3.endScene = true;
		  
		  storyScenes.put(ending3.combinedID, ending3); db.commit();
		  storyScenesKeys = storyScenes.keyList();
		  
		  StorySceneModel ending4 = new StorySceneModel(); 
		  ending4.storyID = -1;
		  ending4.sceneID = 9; 
		  ending4.combinedID = ending4.storyID + "_" + ending4.sceneID; 
		  ending4.sceneDescription = "The bat breaks over the aliens head, it eats you alive. RIP";
		  ending4.optionOne = "Click here if you liked this Story"; 
		  ending4.optionTwo = "Click here if you did not like this Story"; 
		  ending4.nextSceneID[0] = -1;
		  ending4.nextSceneID[1] = -1; 
		  ending4.endScene = true;
		  
		  storyScenes.put(ending3.combinedID, ending3); db.commit();
		  storyScenesKeys = storyScenes.keyList();
		  
		  StorySceneModel afterRating = new StorySceneModel();
		  afterRating.storyID = -1;
		  afterRating.sceneID = -1;
		  afterRating.combinedID = afterRating.storyID + "_" +
		  afterRating.sceneID; 
		  afterRating.sceneDescription = " Thank you for rating this Story. <br> Why not head to the Editor to express your own creativity or return to the Play page to choose another fun adventure?"; 
		  afterRating.optionOne = null;
		  afterRating.optionTwo = null; 
		  afterRating.nextSceneID[0] = 0;
		  afterRating.nextSceneID[1] = 0;
		  
		  storyScenes.put(afterRating.combinedID, afterRating); 
		  db.commit();
		  
		  storyScenesKeys = storyScenes.keyList();
		}
		 
	}
}
//Idun Craig 40223245