package views;

import java.util.ArrayList;
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

public class CreateSceneView extends DynamicWebPage{

	MVMap<String, StorySceneModel> storyScenes;
	List<String> storyScenesKeys;
	MVMap<String, StoryModel> stories;
	List<String> storiesKeys;
	
	//constants for passing url parameters
	final static String SELECTED_STORY = "selected_story";
	
	UserModel currentUser = new UserModel();
	MVMap<String, UserModel> users = db.s.openMap("Users");
	int currentStoryID;
	
	public CreateSceneView(DatabaseInterface db, FileStoreInterface fs) {
		super(db, fs);
		
		// Retrieves the story scenes from the database
		storyScenes = db.s.openMap("StoryScene");
		storyScenesKeys = storyScenes.keyList();
		
		//gets all the stories stored in the database
		stories = db.s.openMap("Story");
		storiesKeys = stories.keyList();
	}

	@Override
	public boolean process(WebRequest toProcess) {

		if(toProcess.path.equalsIgnoreCase("CreateScene"))
		{
			String stringToSendToWebBrowser = "";
			
			//Makes a hashmap of the URL parameters
			HashMap<String, String> urlParams = toProcess.params;			
			System.out.println(urlParams.toString()); //Check
			
			//Gets the ID of the story the new scene will be added to from the URL parameters
			currentStoryID = Integer.parseInt(urlParams.get(SELECTED_STORY));
			
			//If there is more than 1 URL parameter i.e. details of a new scene have been entered
			if (urlParams.size()>1) {
				
				storyScenes = db.s.openMap("StoryScene");
				storyScenesKeys = storyScenes.keyList();
				int biggestID = 0;
				
				//gets the next available ID for the scene 
				for (String sceneKey: storyScenesKeys) {
					if(storyScenes.get(sceneKey).storyID==currentStoryID)
					{
						if(storyScenes.get(sceneKey).sceneID > biggestID)
						{
							biggestID= storyScenes.get(sceneKey).sceneID;
						}
					}
				}

				//Sets the current user to the logged in user
				currentUser = users.get(toProcess.cookies.get("username"));
				
				//New Scene object created and saved to the database
				StorySceneModel newScene = new StorySceneModel();
				
				
				StoryModel currentStory = stories.get(Integer.toString(currentStoryID));
				newScene.storyID = currentStory.storyID;
				newScene.sceneID = ++biggestID;
				newScene.combinedID = newScene.storyID+"_"+newScene.sceneID;
				newScene.sceneDescription = urlParams.get("desc");
				
				if(urlParams.get("endScene").equalsIgnoreCase("yes"))
				{
					//sets standard options for end scene
					newScene.optionOne = "Click here if you liked this story.";
					newScene.optionTwo = "Click here if you did not like this story.";
					newScene.nextSceneID[0] = -1;
					newScene.nextSceneID[1] = -1;
					newScene.endScene = true;
				}else {
					//sets options to input for non-end scenes
					newScene.optionOne = urlParams.get("opt1");
					newScene.optionTwo = urlParams.get("opt2");
				}
				currentStory.noOfScenes++;
				stories.put(Integer.toString(currentStory.storyID), currentStory);
				db.commit();
			
				storyScenes.put(newScene.combinedID , newScene);
				System.out.println("storyScenes.keyList()=>" + storyScenes.keyList() + "<"); //check
				db.commit();
				//Redirects back to the editor
				stringToSendToWebBrowser +=  " <script> window.location.href = \"http://localhost:8080/EditStory?" + SELECTED_STORY + "=" + newScene.storyID + "\"	</script>";								
			}
			else {
			currentUser = users.get(toProcess.cookies.get("username"));
			
			 stringToSendToWebBrowser += "<!DOCTYPE html>\r\n" + "<html lang=\"en\">\r\n" + "\r\n" + "<head>\r\n"
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
					+ "				<!-- Profile </-->\r\n"
					+ "				<li class=\"nav-item\"><a class=\"nav-link\" href=\"aboutus\">About Us</a></li>\r\n";
					
					//checks if a user is logged in
					if(!(toProcess.cookies.get("username") == null)) {
						stringToSendToWebBrowser += "<li class=\"nav-item\"> <a class=\"nav-link text-primary\" href=\"profile\">"
								+ toProcess.cookies.get("username") + "</a> </li>\r\n";
        			}else {
        				stringToSendToWebBrowser += "<li class=\"nav-item\"> <a class=\"nav-link text-primary\" href=\"register\">"
        						+ "Register" + "</a> </li>\r\n";
        			}
					
					stringToSendToWebBrowser += "</ul></div>\r\n"
					+ "	</nav>\r\n" + "	<!-- End of Navbar </-->\r\n" + "\r\n" + "\r\n" +
					"  <div class=\"text-left py-1\">\r\n" + 
					"    <div class=\"container\">\r\n" + 
					"      <div class=\"row\">\r\n"+
					"			<h2 class=\"mb-1\">Create Scene</h2>\r\n"+
					"		</div>\r\n"+
					"	`	<div class=\"row\">\r\n"+
					"		<form action=\"CreateScene?" + SELECTED_STORY + "=" + currentStoryID + "\" id=\"createScene\" method=\"post\">\r\n"+
					//INPUT SCENE DESCRIPTION
					"<h5 class=\"mb-1\">&nbsp Scene Description:<br></h5>\r\n"+
					"&nbsp <textarea rows=\"8\" cols=\"100\" name=\"desc\" wrap=\"hard\" form=\"createScene\"></textarea><br><br>\r\n"+
					//INPUT OPTION ONE
					"<h5 class=\"mb-1\">&nbsp Option One:<br></h5>\r\n"+
					"<textarea rows=\"4\" cols=\"100\" name=\"opt1\" wrap=\"hard\" form=\"createScene\"></textarea><br><br>\r\n"+
					//INPUT OPTION TWO
					"<h5 class=\"mb-1\">&nbsp Option Two:<br></h5>\r\n"+
					"&nbsp <textarea rows=\"4\" cols=\"100\" name=\"opt2\" wrap=\"hard\" form=\"createScene\"></textarea><br><br>\r\n"+
					"		</div>\r\n"+
					"		<div class=\"row\">\r\n"+
					//Select whether the scene is an end scene
					"			<p class=\"mb-1\">Is this an end scene? &nbsp</p>\r\n"+			
					"			<input type=\"radio\" name=\"endScene\" value=\"yes\">Yes &nbsp<br>\r\n"+
					"			<input type=\"radio\" name=\"endScene\" value=\"no\" checked>No &nbsp\r\n"+
					"			<button type=\"submit\" class=\"btn btn-primary\">Create Scene</button><br>\r\n"+
					"		</div>\r\n"+
					"		</form>"+
					"	</div>\r\n"+
					"</div>\r\n"+
					"  <script src=\"https://code.jquery.com/jquery-3.3.1.slim.min.js\" integrity=\"sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo\" crossorigin=\"anonymous\" style=\"\"></script>\r\n" + 
					"  <script src=\"https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js\" integrity=\"sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49\" crossorigin=\"anonymous\" style=\"\"></script>\r\n" + 
					"  <script src=\"https://stackpath.bootstrapcdn.com/bootstrap/4.2.1/js/bootstrap.min.js\" integrity=\"sha384-B0UglyR+jN6CkvvICOB2joaf5I4l3gm9GU6Hc1og6Ls7i6U/mkkaduKaBhlAXv9k\" crossorigin=\"anonymous\" style=\"\"></script>\r\n" + 
					"</body>\r\n";
					
					stringToSendToWebBrowser +=
					"</html>";
			   }
			toProcess.r = new WebResponse(WebResponse.HTTP_OK, WebResponse.MIME_HTML, stringToSendToWebBrowser);
			
			return true;
		  
		}
		
		return false;
	}

}
//Idun Craig 40223245