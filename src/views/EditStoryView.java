package views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.h2.mvstore.MVMap;

import model.Category;
import model.StoryModel;
import model.StorySceneModel;
import model.UserModel;
import storage.DatabaseInterface;
import storage.FileStoreInterface;
import web.WebRequest;
import web.WebResponse;

public class EditStoryView extends DynamicWebPage
{

	MVMap<String, StoryModel> stories;
	List<String> storiesKeys = null;
	MVMap<String, StorySceneModel> storyScenes;
	List<String> storyScenesKeys;
	UserModel currentUser = new UserModel();
	MVMap<String, UserModel> users = db.s.openMap("Users");

	//constants for passing url parameters
	final static String SELECTED_STORY = "selected_story";
	static final String SELECTED_SCENE = "selected_scene";
	final static String DELETE_SCENE = "delete_scene";

	int currentStoryID;

	public EditStoryView(DatabaseInterface db, FileStoreInterface fs)
	{
		super(db, fs);

		//gets all the stories stored in the database
		stories = db.s.openMap("Story");
		storiesKeys = stories.keyList();
	}

	@Override
	public boolean process(WebRequest toProcess)
	{

		if (toProcess.path.equalsIgnoreCase("EditStory"))
		{
			//Sets the current user to the logged in user
			currentUser = users.get(toProcess.cookies.get("username"));
			String stringToSendToWebBrowser = "";
			
			//Makes a hashmap of the URL parameters
			HashMap<String, String> urlParams = toProcess.params;
			//check
			System.out.println(urlParams.toString());

			currentStoryID = Integer.parseInt(urlParams.get(SELECTED_STORY));
			
			storyScenes = db.s.openMap("StoryScene");
			storyScenesKeys = storyScenes.keyList();
			
			ArrayList<StorySceneModel> storyScene = new ArrayList<StorySceneModel>();
			for (String sceneKey : storyScenesKeys)
			{
				if (storyScenes.get(sceneKey).storyID == currentStoryID)
				{
					storyScene.add(storyScenes.get(sceneKey));
				}
			}

			//Links scenes together
			if (urlParams.containsKey("Scene" + urlParams.get(SELECTED_SCENE) + "Opt1") && urlParams.containsKey("Scene" + urlParams.get(SELECTED_SCENE) + "Opt2"))
			{
				StorySceneModel sceneToUpdate = storyScenes.get(currentStoryID + "_" + urlParams.get(SELECTED_SCENE));
				sceneToUpdate.nextSceneID[0] = Integer.parseInt(urlParams.get("Scene" + sceneToUpdate.sceneID + "Opt1"));
				sceneToUpdate.nextSceneID[1] = Integer.parseInt(urlParams.get("Scene" + sceneToUpdate.sceneID + "Opt2"));
				storyScenes.put(currentStoryID + "_" + urlParams.get(SELECTED_SCENE), sceneToUpdate);
				db.commit();

			}

			 if (urlParams.containsKey("title") && urlParams.containsKey("desc") && urlParams.containsKey("category") && urlParams.containsKey("StartingScene")) {
					StoryModel storyToUpdate = stories.get(Integer.toString(currentStoryID));
					storyToUpdate.storyTitle = urlParams.get("title");
					storyToUpdate.storyDescription = urlParams.get("desc");
					
					switch(urlParams.get("category")) {
					case "ACTION": storyToUpdate.category = Category.ACTION;
									break;
					case "COMEDY": storyToUpdate.category = Category.COMEDY;
									break;
					case "DRAMA": storyToUpdate.category = Category.DRAMA;
									break;
					case "HORROR": storyToUpdate.category = Category.HORROR;
									break;
					case "ROMANCE": storyToUpdate.category = Category.ROMANCE;
									break;
					case "THRILLER": storyToUpdate.category = Category.THRILLER;
									break;
					default: 		break;
					}
					storyToUpdate.startScene = Integer.parseInt(urlParams.get("StartingScene"));
					storyToUpdate.hasStart = true;
					stories.put(Integer.toString(storyToUpdate.storyID), storyToUpdate);
					db.commit();
				 }
			 
			 if(urlParams.containsKey(DELETE_SCENE))
			 {
				 StoryModel st = stories.get(Integer.toString(currentStoryID));
				 if(st.startScene == Integer.parseInt(urlParams.get(DELETE_SCENE)))
				 {
					 //Sets hasStart to false if the starting scene is deleted
					 st.hasStart = false;
				 }
				 storyScenes.remove(currentStoryID +"_"+ urlParams.get(DELETE_SCENE));
				 db.commit();
				 st.noOfScenes--;
				 stories.put(Integer.toString(st.storyID), st);
				 db.commit();
				 stringToSendToWebBrowser += " <script> window.location.href = \"http://localhost:8080/EditStory?" + SELECTED_STORY + "=" + currentStoryID+ "\"	</script>";
			 }
			 

			 stringToSendToWebBrowser += "<!DOCTYPE html>\r\n" + "<html lang=\"en\">\r\n" + "\r\n" + "<head>\r\n"
					+ "\r\n"
					+ "	<link rel=\"stylesheet\" href=\"https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css\" integrity=\"sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T\" crossorigin=\"anonymous\">\r\n"
					+ "\r\n"
					+ "	<script src=\"https://code.jquery.com/jquery-3.2.1.slim.min.js\" integrity=\"sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN\" crossorigin=\"anonymous\">\r\n"
					+ "	</script>\r\n" + "\r\n"
					+ "	<script src=\"https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js\" integrity=\"sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q\" crossorigin=\"anonymous\">\r\n"
					+ "	</script>\r\n" + "\r\n"
					+ "	<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js\" integrity=\"sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl\" crossorigin=\"anonymous\">\r\n"
					+ "	</script>\r\n" + "\r\n" + "	<title>Quest For Fun</title>\r\n" + "</head>\r\n" + "\r\n"
					+ "<body>\r\n" + "\r\n" + "	<!-- Comment -->\r\n" + "	<!-- Navbar </-->\r\n" + "\r\n"
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
					+ "				<li class=\"nav-item\"><a class=\"nav-link\" href=\"Help\">Help</a></li>\r\n"
					+ "\r\n" + "				<!-- About Us </-->\r\n"
					+ "				<li class=\"nav-item\"><a class=\"nav-link\" href=\"aboutus\">About Us</a></li>\r\n";
					
					
					if(!(toProcess.cookies.get("username") == null)) {
						stringToSendToWebBrowser += "<li class=\"nav-item\"> <a class=\"nav-link text-primary\" href=\"profile\">"
								+ toProcess.cookies.get("username") + "</a> </li>\r\n";
        			}else {
        				stringToSendToWebBrowser += "<li class=\"nav-item\"> <a class=\"nav-link text-primary\" href=\"register\">"
        						+ "Register" + "</a> </li>\r\n";
        			}
					
					stringToSendToWebBrowser += "</ul></div>\r\n"+
					"		</div>\r\n"+
					"	</nav>\r\n" + "	<!-- End of Navbar </-->\r\n" + "\r\n" + "\r\n"+
							"  <div class=\"text-left py-1\">\r\n" + 
							"    <div class=\"container\">\r\n" + 
							"      <div class=\"row\">\r\n"+
							"			<h2 class=\"mb-1\">Edit Story: "+ stories.get(Integer.toString(currentStoryID)).storyTitle +"</h2><br><br><br>\r\n"+
							"		</div>\r\n"+
							"<div class=\"d-flex w-100 justify-content-between\">\r\n" + 
							"<form action=\"EditStory?" + SELECTED_STORY + "=" + currentStoryID + "\" id=\"editStoryDetails\" method=\"post\">\r\n"+
							"<h5 class=\"mb-1\">Title:<br></h5>\r\n"+
							" <textarea rows=\"1\" cols=\"75\" name=\"title\" wrap=\"hard\" form=\"editStoryDetails\">" + stories.get(Integer.toString(currentStoryID)).storyTitle + "</textarea><br>\r\n"+
							"<h5 class=\"mb-1\">Description:<br></h5>\r\n"+
							" <textarea rows=\"5\" cols=\"150\" name=\"desc\" wrap=\"hard\" form=\"editStoryDetails\">" + stories.get(Integer.toString(currentStoryID)).storyDescription + "</textarea><br><br>\r\n"+
							"		<h5 class=\"mb-1\">Category:</h5>\r\n"+
							"<select name=\"category\">\r\n";
							
					stringToSendToWebBrowser += GetSelectedCategory(stories.get(Integer.toString(currentStoryID)).category);
							
					stringToSendToWebBrowser +="	</select>\r\n"+
						"<h5 class=\"mb-1\">Starting Scene: </h5>\r\n"+
							 "<select name=\"StartingScene\">\r\n";
					for(StorySceneModel sceneToDisplay: storyScene)
					{
						if(sceneToDisplay.sceneID != -1 && !sceneToDisplay.endScene) {
						stringToSendToWebBrowser +=  "<option value=\""+ sceneToDisplay.sceneID +"\">"+ sceneToDisplay.sceneID +" </option>\r\n";
						}
					}
					stringToSendToWebBrowser +=  "</select><br><br>\r\n"+
							"<button type=\"submit\" class=\"btn btn-primary\">Update Story</button>\r\n"+
							"</form>"+
							 "<br>\r\n"+
							 "</div>\r\n"+
							 "</div>\r\n"+
							 "</div><br><br>\r\n";
					
					for (StorySceneModel scene: storyScene) {
						//LOAD SCENES FROM SELECTED STORY
						if(scene.sceneID != -1)
						{
							stringToSendToWebBrowser += 
								"		<a href=\"EditScene?" + SELECTED_STORY + "=" + currentStoryID + "&" + SELECTED_SCENE + "=" + scene.sceneID + "\" class=\"list-group-item list-group-item-action flex-column align-items-start\">\r\n" + 
								"		<div class=\"d-flex w-100 justify-content-between\">\r\n"+
								"       <h4 class=\"mb-1\">Scene Description: "+ scene.sceneDescription + "</h4><br><br>\r\n";
							if(stories.get(Integer.toString(currentStoryID)).startScene == scene.sceneID)
							{
								stringToSendToWebBrowser += "<h6 class=\"mb-1\">Scene ID: " + scene.sceneID + " (Start)</h6>\r\n";
							}else if(scene.endScene) {
								stringToSendToWebBrowser += "<h6 class=\"mb-1\">Scene ID: " + scene.sceneID + " (Ending)</h6>\r\n";
							}else
							{
								stringToSendToWebBrowser += "<h6 class=\"mb-1\">Scene ID: " + scene.sceneID + "</h6>\r\n";
							}
							stringToSendToWebBrowser += "</div><br>\r\n";
							
							if(scene.endScene)
							{
								stringToSendToWebBrowser += "</a><br>\r\n"+
										" <a class=\"btn btn-primary\"  href=\"EditStory?" + SELECTED_STORY +"="+ currentStoryID +"&"+ DELETE_SCENE +"=" + scene.sceneID + "\">Delete Scene</a><br>\r\n";
								
							}else if(!scene.endScene){ //Only gives linking options for scenes that are not end scenes
							stringToSendToWebBrowser += "<div class=\"row\">\r\n" +
								" 		 <h6 class=\"mb-1\">Option One:&nbsp </h6> <p class=\"mb-1\">"+ scene.optionOne +"</p>\r\n "+	
								"	</div>\r\n"+
								"		<div class=\"d-flex w-100 justify-content-between\">\r\n"+
								"			<p class=\"mb-1\">Chosen " + scene.optionOneCount + " times.</p>\r\n"+ //Displays how many times users have clicked option 1
								"			<p class=\"mb-1\">Links to Scene " + scene.nextSceneID[0] +"</p>\r\n"+
								"		</div><br>\r\n"+
								"<div class=\"row\">\r\n" +
								"		 <h6 class=\"mb-1\">Option Two:&nbsp </h6> <p class=\"mb-1\">" + scene.optionTwo + "</p>\r\n "+
								"</div>\r\n"+
								"		<div class=\"d-flex w-100 justify-content-between\">\r\n"+
								"			<p class=\"mb-1\">Chosen " + scene.optionTwoCount + " times.</p>\r\n"+  //Displays how many times users have clicked option 2
								"			<p class=\"mb-1\">Links to Scene " + scene.nextSceneID[1] +"</p>\r\n"+
								"		</div>\r\n"+
								"	</a><br>\r\n"+
								//Drop downs to pick the scene each option leads to
								"	<form action=\"EditStory?" + SELECTED_STORY + "=" + currentStoryID + "&" + SELECTED_SCENE + "=" + scene.sceneID + "\" id=\"sceneLinker\" method=\"post\">\r\n"+
								"		<p class=\"mb-1\"> &nbsp Option 1 links to Scene &nbsp\r\n"+
								"			<select name=\"Scene"+ scene.sceneID +"Opt1\">\r\n";
								for(StorySceneModel sceneToDisplay: storyScene)
								{
									if(sceneToDisplay.sceneID != -1) {
									stringToSendToWebBrowser +=  "<option value=\""+ sceneToDisplay.sceneID +"\">"+ sceneToDisplay.sceneID +" </option>\r\n";
									}
								}
								stringToSendToWebBrowser +=  "</select>\r\n"+
												"&nbsp &nbsp Option 2 links to Scene &nbsp\r\n"+
												"	<select name=\"Scene"+ scene.sceneID +"Opt2\"></p>\r\n";
								for(StorySceneModel sceneToDisplay: storyScene)
								{
									if(sceneToDisplay.sceneID != -1) {
										stringToSendToWebBrowser += "<option value=\""+ sceneToDisplay.sceneID +"\">" + sceneToDisplay.sceneID +" </option>\r\n";
									}
								}
								stringToSendToWebBrowser +=  "</select><br><br>\r\n"+
											"&nbsp <button type=\"submit\" class=\"btn btn-primary\">Update Scene Links</button>\r\n"+
											"&nbsp <a class=\"btn btn-primary\"  href=\"EditStory?" + SELECTED_STORY +"="+ currentStoryID +"&"+ DELETE_SCENE +"=" + scene.sceneID + "\">Delete Scene</a><br>\r\n"+
											"</form>\r\n"+
											"</div><br>\r\n";
							}
						}
					}

			// ADD NEW SCENE BUTTON
			stringToSendToWebBrowser += "<a class=\"btn btn-outline-grey btn-block btn-lg\" href=\"CreateScene?" + SELECTED_STORY + "=" + currentStoryID + "\">Add Scene</a>\r\n" + "</div>\r\n"
					+ "</div>\r\n <div class=\"text-center py-4\">\r\n" + "<div class=\"container\">\r\n"
					+ "<div class=\"row\">\r\n" + "</div>\r\n" + "</div>\r\n" + "  </div>" + "</div>"
					+ "<script src=\"https://code.jquery.com/jquery-3.3.1.slim.min.js\" integrity=\"sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo\" crossorigin=\"anonymous\" style=\"\"></script>\r\n"
					+ "<script src=\"https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js\" integrity=\"sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49\" crossorigin=\"anonymous\" style=\"\"></script>\r\n"
					+ "<script src=\"https://stackpath.bootstrapcdn.com/bootstrap/4.2.1/js/bootstrap.min.js\" integrity=\"sha384-B0UglyR+jN6CkvvICOB2joaf5I4l3gm9GU6Hc1og6Ls7i6U/mkkaduKaBhlAXv9k\" crossorigin=\"anonymous\" style=\"\"></script>\r\n";
			
			stringToSendToWebBrowser += "</body>\r\n" + "</html>";

			toProcess.r = new WebResponse(WebResponse.HTTP_OK, WebResponse.MIME_HTML, stringToSendToWebBrowser);

			return true;
			}
		return false;
	}
	
	public String GetSelectedCategory(Category category)
	{
		switch(category)
		{
		case ACTION: 		return 	"<option value=\""+ Category.ACTION +"\" selected>"+ Category.ACTION.description() +"</option>\r\n"+
												"		<option value=\""+ Category.COMEDY +"\">"+ Category.COMEDY.description() +" </option>\r\n"+
												"		<option value=\""+ Category.DRAMA +"\">"+ Category.DRAMA.description() +" </option>\r\n"+
												"		<option value=\""+ Category.HORROR +"\">"+ Category.HORROR.description() +" </option>\r\n"+
												"		<option value=\""+ Category.ROMANCE +"\">"+ Category.ROMANCE.description() +" </option>\r\n"+
												"		<option value=\""+ Category.THRILLER +"\">"+ Category.THRILLER.description() +" </option>\r\n";
		
		case COMEDY: 		return 	"<option value=\""+ Category.ACTION +"\">"+ Category.ACTION.description() +" </option>\r\n"+
												"		<option value=\""+ Category.COMEDY +"\" selected>"+ Category.COMEDY.description() +"</option>\r\n"+
												"		<option value=\""+ Category.DRAMA +"\">"+ Category.DRAMA.description() +" </option>\r\n"+
												"		<option value=\""+ Category.HORROR +"\">"+ Category.HORROR.description() +" </option>\r\n"+
												"		<option value=\""+ Category.ROMANCE +"\">"+ Category.ROMANCE.description() +" </option>\r\n"+
												"		<option value=\""+ Category.THRILLER +"\">"+ Category.THRILLER.description() +" </option>\r\n";
		
		case DRAMA: 		return 	"<option value=\""+ Category.ACTION +"\">"+ Category.ACTION.description() +" </option>\r\n"+
												"		<option value=\""+ Category.COMEDY +"\">"+ Category.COMEDY.description() +" </option>\r\n"+
												"		<option value=\""+ Category.DRAMA +"\" selected>"+ Category.DRAMA.description() +" </option>\r\n"+
												"		<option value=\""+ Category.HORROR +"\">"+ Category.HORROR.description() +" </option>\r\n"+
												"		<option value=\""+ Category.ROMANCE +"\">"+ Category.ROMANCE.description() +" </option>\r\n"+
												"		<option value=\""+ Category.THRILLER +"\">"+ Category.THRILLER.description() +" </option>\r\n";
		
		case HORROR: 		return "<option value=\""+ Category.ACTION +"\">"+ Category.ACTION.description() +" >/option>\r\n"+
												"		<option value=\""+ Category.COMEDY +"\">"+ Category.COMEDY.description() +" </option>\r\n"+
												"		<option value=\""+ Category.DRAMA +"\">"+ Category.DRAMA.description() +" </option>\r\n"+
												"		<option value=\""+ Category.HORROR +"\" selected>"+ Category.HORROR.description() +"</option>\r\n"+
												"		<option value=\""+ Category.ROMANCE +"\">"+ Category.ROMANCE.description() +" </option>\r\n"+
												"		<option value=\""+ Category.THRILLER +"\">"+ Category.THRILLER.description() +" </option>\r\n";
							
		case ROMANCE: 		return "<option value=\""+ Category.ACTION +"\">"+ Category.ACTION.description() +" </option>\r\n"+
												"		<option value=\""+ Category.COMEDY +"\">"+ Category.COMEDY.description() +" </option>\r\n"+
												"		<option value=\""+ Category.DRAMA +"\">"+ Category.DRAMA.description() +" </option>\r\n"+
												"		<option value=\""+ Category.HORROR +"\">"+ Category.HORROR.description() +" </option>\r\n"+
												"		<option value=\""+ Category.ROMANCE +"\" selected>"+ Category.ROMANCE.description() +"</option>\r\n"+
												"		<option value=\""+ Category.THRILLER +"\">"+ Category.THRILLER.description() +" </option>\r\n";
							
		case THRILLER: 		return "<option value=\""+ Category.ACTION +"\">"+ Category.ACTION.description() +" </option>\r\n"+
												"		<option value=\""+ Category.COMEDY +"\">"+ Category.COMEDY.description() +" </option>\r\n"+
												"		<option value=\""+ Category.DRAMA +"\">"+ Category.DRAMA.description() +" </option>\r\n"+
												"		<option value=\""+ Category.HORROR +"\">"+ Category.HORROR.description() +" </option>\r\n"+
												"		<option value=\""+ Category.ROMANCE +"\">"+ Category.ROMANCE.description() +" </option>\r\n"+
												"		<option value=\""+ Category.THRILLER +"\" selected>"+ Category.THRILLER.description() +"</option>\r\n";
							
		default: 			return "<option value=\""+ Category.ACTION +"\">"+ Category.ACTION.description() +" </option>\r\n"+
												"		<option value=\""+ Category.COMEDY +"\">"+ Category.COMEDY.description() +" </option>\r\n"+
												"		<option value=\""+ Category.DRAMA +"\">"+ Category.DRAMA.description() +" </option>\r\n"+
												"		<option value=\""+ Category.HORROR +"\">"+ Category.HORROR.description() +" </option>\r\n"+
												"		<option value=\""+ Category.ROMANCE +"\">"+ Category.ROMANCE.description() +" </option>\r\n"+
												"		<option value=\""+ Category.THRILLER +"\">"+ Category.THRILLER.description() +" </option>\r\n";
											
		}
	}
}
//Idun Craig 40223245
