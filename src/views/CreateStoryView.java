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

public class CreateStoryView extends DynamicWebPage{

	MVMap<String, StoryModel> stories;	
	List<String> storiesKeys;
	MVMap<String, StorySceneModel> storyScenes;
	List<String> storyScenesKeys;
	
	UserModel currentUser = new UserModel();
	MVMap<String, UserModel> users = db.s.openMap("Users");
	
	public CreateStoryView(DatabaseInterface db, FileStoreInterface fs) {
		super(db, fs);
		//gets all the stories stored in the database
		stories = db.s.openMap("Story");
		storiesKeys = stories.keyList();
	}

	@Override
	public boolean process(WebRequest toProcess) {
		
		if(toProcess.path.equalsIgnoreCase("CreateStory"))
		{
			//Sets the current user to the logged in user
			currentUser = users.get(toProcess.cookies.get("username"));
			
			String stringToSendToWebBrowser = "";
			
			//Makes a hashmap of the URL parameters
			HashMap<String, String> formParams = toProcess.params;	
			//check
			System.out.println(formParams.toString());
			
			//if story details have been passed into the url params, creates a story
			if (!formParams.isEmpty()) {
							
				stories = db.s.openMap("Story");
				storiesKeys = stories.keyList();
				
				//gets the next available ID
				int biggestID = 0;
				for (String storyKey: storiesKeys) {
					if(stories.get(storyKey).storyID > biggestID)
					{
						biggestID= stories.get(storyKey).storyID;
					}
				}
				
				StoryModel newStory = new StoryModel();
				
				newStory.storyID = ++biggestID;
				newStory.storyTitle = formParams.get("title");
				newStory.storyDescription = formParams.get("desc");
				newStory.storyAuthor = currentUser.username;
				
				switch(formParams.get("category")) {
				case "ACTION": newStory.category = Category.ACTION;
								break;
				case "COMEDY": newStory.category = Category.COMEDY;
								break;
				case "DRAMA": newStory.category = Category.DRAMA;
								break;
				case "HORROR": newStory.category = Category.HORROR;
								break;
				case "ROMANCE": newStory.category = Category.ROMANCE;
								break;
				case "THRILLER": newStory.category = Category.THRILLER;
								break;
					default: 	break;
				}
				
				newStory.noOfLikes = 0;
				newStory.noOfDislikes = 0;
				
				System.out.println("newStory=>" + newStory + "<");				
				stories.put(Integer.toString(newStory.storyID), newStory);
				System.out.println("stories.keyList()=>" + stories.keyList() + "<");
				db.commit();
				
				// Need to create the default scenes for each story scene 0 and scene 99
				storyScenes = db.s.openMap("StoryScene");
				storyScenesKeys = storyScenes.keyList();
				
				//creates the after rating scene that is standard for all stories
				StorySceneModel afterRatingScene = new StorySceneModel();
				afterRatingScene.storyID = newStory.storyID;
				afterRatingScene.sceneID = -1;
				afterRatingScene.combinedID = afterRatingScene.storyID + "_" + afterRatingScene.sceneID;
				afterRatingScene.sceneDescription = " Thank you for rating this Story. <br> Why not head to the Editor to express your own creativity or return to the Play page to choose another fun adventure?";
				afterRatingScene.optionOne = null;
				afterRatingScene.optionTwo = null;
				afterRatingScene.nextSceneID[0] = 0;
				afterRatingScene.nextSceneID[1] = 0;		
				
				storyScenes.put(afterRatingScene.combinedID, afterRatingScene);			
				db.commit();
				
				//redirects to the editor
				stringToSendToWebBrowser += 	"<a href=\"Editor\"><b>Return to Editor</b></a>\r\n" +  
						" <script> window.location.href = \"http://localhost:8080/Editor\"	</script>";								
			}
			else {
			
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
						+ "				<li class=\"nav-item\"><a class=\"nav-link\" href=\"editor\">Editor</a></li>\r\n"
						+ "\r\n" + "				<!-- Forums </-->\r\n"
						+ "				<li class=\"nav-item\"><a class=\"nav-link\" href=\"Forum\">Forums</a></li>\r\n"
						+ "\r\n" + "				<!-- Help </-->\r\n"
						+ "				<li class=\"nav-item\"><a class=\"nav-link\" href=\"help\">Help</a></li>\r\n" + "\r\n"
						+ "				<!-- Profile </-->\r\n"
						+ "				<li class=\"nav-item\"><a class=\"nav-link\" href=\"aboutus\">About Us</a></li>\r\n";
						
						
						if(!(toProcess.cookies.get("username") == null)) {
							stringToSendToWebBrowser += "<li class=\"nav-item\"> <a class=\"nav-link text-primary\" href=\"profile\">"
									+ toProcess.cookies.get("username") + "</a> </li>\r\n";
	        			}else {
	        				stringToSendToWebBrowser += "<li class=\"nav-item\"> <a class=\"nav-link text-primary\" href=\"register\">"
	        						+ "Register" + "</a> </li>\r\n";
	        			}
						
						stringToSendToWebBrowser += "</ul></div>\r\n"
						+ "	</nav>\r\n" + "	<!-- End of Navbar </-->\r\n" + "\r\n" + "\r\n"+
						"  <div class=\"text-left py-1\">\r\n" + 
						"    <div class=\"container\">\r\n" + 
						"      <div class=\"row\">\r\n"+
						"			<h2 class=\"mb-1\">Create Story</h2><br><br><br>\r\n"+
						"		</div>\r\n"+
						"<div class=\"d-flex w-100 justify-content-between\">\r\n" +
						"			<form action=\"CreateStory\" id=\"createStory\" method=\"get\">\r\n"+
						"				<h5 class=\"mb-1\">Title:<br></h5>\r\n"+
						" 					<textarea rows=\"1\" cols=\"75\" name=\"title\" wrap=\"hard\" form=\"createStory\"></textarea><br>\r\n"+
						"				<h5 class=\"mb-1\">Description:<br></h5>\r\n"+
						"				 	<textarea rows=\"5\" cols=\"150\" name=\"desc\" wrap=\"hard\" form=\"createStory\"></textarea><br>\r\n"+
						"				<h5 class=\"mb-1\">Author:<br></h5>\r\n"+
						"				 	<textarea disabled rows=\"1\" cols=\"50\" name=\"author\" wrap=\"hard\" form=\"createStory\">" + currentUser.username +"</textarea><br><br>\r\n"+
						"				<h5 class=\"mb-1\">Category: &nbsp</h5>\r\n"+
						//Drop down with category options
						"		<select name=\"category\">\r\n"+
						"			<option value=\""+ Category.ACTION +"\">"+ Category.ACTION.description() +" </option>\r\n"+
						"			<option value=\""+ Category.COMEDY +"\">"+ Category.COMEDY.description() +" </option>\r\n"+
						"			<option value=\""+ Category.DRAMA +"\">"+ Category.DRAMA.description() +" </option>\r\n"+
						"			<option value=\""+ Category.HORROR +"\">"+ Category.HORROR.description() +" </option>\r\n"+
						"			<option value=\""+ Category.ROMANCE +"\">"+ Category.ROMANCE.description() +" </option>\r\n"+
						"			<option value=\""+ Category.THRILLER +"\">"+ Category.THRILLER.description() +" </option>\r\n"+
						"		</select><br><br>\r\n"+
						"				&nbsp<button type=\"submit\" class=\"btn btn-primary\">Create Story</button><br>\r\n"+
						"			</form>"+
						"		</div>\r\n"+
						"	</div>\r\n"+
						"</div>\r\n"+
						"  <script src=\"https://code.jquery.com/jquery-3.3.1.slim.min.js\" integrity=\"sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo\" crossorigin=\"anonymous\" style=\"\"></script>\r\n" + 
						"  <script src=\"https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js\" integrity=\"sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49\" crossorigin=\"anonymous\" style=\"\"></script>\r\n" + 
						"  <script src=\"https://stackpath.bootstrapcdn.com/bootstrap/4.2.1/js/bootstrap.min.js\" integrity=\"sha384-B0UglyR+jN6CkvvICOB2joaf5I4l3gm9GU6Hc1og6Ls7i6U/mkkaduKaBhlAXv9k\" crossorigin=\"anonymous\" style=\"\"></script>\r\n";
						
						stringToSendToWebBrowser += "</body>\r\n";
						
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