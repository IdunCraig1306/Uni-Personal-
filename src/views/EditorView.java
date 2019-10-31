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

public class EditorView extends DynamicWebPage{
	
	MVMap<String, StoryModel> stories;	
	List<String> storiesKeys = null;
	
	UserModel currentUser = new UserModel();
	MVMap<String, UserModel> users = db.s.openMap("Users");
	
	//constants for passing url parameters
	final static String FROM_EDITOR = "from_editor";
	final static String SELECTED_STORY = "selected_story";
	final static String NEED_MORE_SCENES = "need_more_scenes";

	public EditorView(DatabaseInterface db, FileStoreInterface fs) {
		super(db, fs);
		
		//gets all the stories stored in the database
		stories = db.s.openMap("Story");
		storiesKeys = stories.keyList();
		System.out.println("storiesKeys.size()=>" + storiesKeys.size() + "<");
		System.out.println("storiesKeys.toString()=>" + storiesKeys.toString() + "<");

	}

	public boolean process(WebRequest toProcess)
	{
		if(toProcess.path.equalsIgnoreCase("Editor"))
		{
			String stringToSendToWebBrowser = "";
			
			//Creates a hashmap of the url parameters
			HashMap<String, String> urlParams = toProcess.params;
			
			if(toProcess.cookies.get("username") == null)
			{
				//if a user is not logged in, redirect to the editor
				stringToSendToWebBrowser += 	"<a href=\"Login\"><b>Login</b></a>\r\n" +  
						" <script> window.location.href = \"http://localhost:8080/Login?"+ FROM_EDITOR + "=true\"	</script>";
			}else {
			
				//sets the current user to the logged in user 
			currentUser = users.get(toProcess.cookies.get("username"));
			
			stories = db.s.openMap("Story");
			storiesKeys = stories.keyList();
			
			//Makes an array list of the stories written by the logged in user
			ArrayList<StoryModel> userStories = new ArrayList<StoryModel>();
			for (String storyKey: storiesKeys) {
				if (stories.get(storyKey).storyAuthor.equalsIgnoreCase(currentUser.username)) {
					userStories.add(stories.get(storyKey));
				}				
			}
			
			if(urlParams.containsKey("published"))
			{
				StoryModel storyToUpdate = stories.get(urlParams.get(SELECTED_STORY));
				
				if(urlParams.get("published").equalsIgnoreCase("yes"))
				{
					//Publishes the story if it meets the requirements
					if(storyToUpdate.noOfScenes >= 3 && storyToUpdate.hasStart) {
					storyToUpdate.published = true;	
					stories.put(urlParams.get(SELECTED_STORY), storyToUpdate);
					db.commit();
					}else if(!(storyToUpdate.hasStart)){
						stringToSendToWebBrowser += " <script> window.location.href = \"http://localhost:8080/Editor?"+ SELECTED_STORY + "="+ storyToUpdate.storyID + "&nostart=true\"</script>";
					}
					else {
						stringToSendToWebBrowser += " <script> window.location.href = \"http://localhost:8080/Editor?"+ SELECTED_STORY + "="+ storyToUpdate.storyID + "&" + NEED_MORE_SCENES + "=true\"</script>";
					}
				}
				else if (urlParams.get("published").equalsIgnoreCase("no"))
				{
					storyToUpdate.published = false;
					stories.put(urlParams.get(SELECTED_STORY), storyToUpdate);
					db.commit();
				}
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
					+ "				<li class=\"nav-item\"><a class=\"nav-link\" href=\"help\">Help</a></li>\r\n" + "\r\n"
					+ "				<!-- Profile </-->\r\n"
					+ "				<li class=\"nav-item\"><a class=\"nav-link\" href=\"aboutus\">About Us</a></li>\r\n";
					
					//Checks if someone is logged in
					if(!(toProcess.cookies.get("username") == null)) {
						stringToSendToWebBrowser += "<li class=\"nav-item\"> <a class=\"nav-link text-primary\" href=\"profile\">"
								+ toProcess.cookies.get("username") + "</a> </li>\r\n";
        			}else {
        				stringToSendToWebBrowser += "<li class=\"nav-item\"> <a class=\"nav-link text-primary\" href=\"register\">"
        						+ "Register" + "</a> </li>\r\n";
        			}
					
					stringToSendToWebBrowser += "</ul></div>\r\n"+
					"	</nav>\r\n" + "	<!-- End of Navbar </-->\r\n" + "\r\n" + "\r\n" +
					"  <div class=\"text-center py-4\">\r\n" + 
					"    <div class=\"container\">\r\n" + 
					"      <div class=\"row\">\r\n"+
					"			<h2 class=\"mb-1\">"+ currentUser.username +"'s Stories</h2><br><br>\r\n"+
					"		</div>\r\n"+
					"      <div class=\"row\">\r\n";
					
					//LOAD LIST OF STORIES FOR THAT USER
					for (StoryModel story: userStories) {
							stringToSendToWebBrowser += "            <a href=\"EditStory?" + SELECTED_STORY + "=" + story.storyID + "\" class=\"list-group-item list-group-item-action flex-column align-items-start\">\r\n" + 
									"<div class=\"d-flex w-100 justify-content-between\">\r\n" + 
									"       <h5 class=\"mb-1\">"+ story.storyTitle + "</h5> <small class=\"text-muted\">Story ID: " + story.storyID + "</small>\r\n" + 
									"</div>\r\n" + 
									"            <p class=\"mb-1\">" + story.storyDescription +"</p> <small class=\"text-muted\">Category: Sci-Fi/" + story.category.description() + " </small>\r\n"+
									"<div class=\"d-flex w-100 justify-content-between\">\r\n"+
									"		<small class=\"text-muted\">Likes: " + story.noOfLikes + "<br>Dislikes: " + story.noOfDislikes + " </small>\r\n"+
									"<form action=\"Editor?" + SELECTED_STORY + "=" + story.storyID + "\" id=\"publish\" method=\"post\">\r\n";
									
									if(urlParams.containsKey(NEED_MORE_SCENES) && Integer.parseInt(urlParams.get(SELECTED_STORY)) == story.storyID)
									{
										stringToSendToWebBrowser += "		<small class=\"text-muted\">Please enter more scenes before publishing.  Publish? &nbsp</small>\r\n";
									}
									else if(urlParams.containsKey("nostart") && Integer.parseInt(urlParams.get(SELECTED_STORY)) == story.storyID) {
										stringToSendToWebBrowser += "		<small class=\"text-muted\">Please give your story a start scene before publising.  Publish? &nbsp</small>\r\n";
									}
									else{
									stringToSendToWebBrowser +="		<small class=\"text-muted\">Publish? &nbsp</small>\r\n";
									}
									if(story.published) {
										stringToSendToWebBrowser +=  "		<input type=\"radio\" name=\"published\" value=\"yes\" checked>Yes &nbsp\r\n"
									+ "		<input type=\"radio\" name=\"published\" value=\"no\">No &nbsp\r\n";
									} else if(!story.published)
									{
										stringToSendToWebBrowser += "		<input type=\"radio\" name=\"published\" value=\"yes\">Yes &nbsp\r\n"
												+ "		<input type=\"radio\" name=\"published\" value=\"no\" checked>No &nbsp\r\n";
									}
									stringToSendToWebBrowser += "<button type=\"submit\" class=\"btn btn-primary\">Update Story</button><br>\r\n"+
									"</form>\r\n"+
									"</div>\r\n"
									+ "</a>\r\n";	
						}
					
					//CREATE NEW STORY BUTTON 			 		
					stringToSendToWebBrowser += "	</div>\r\n"+
					"		<br><a class=\"btn btn-primary\" href=\"CreateStory\">Create New Story</a>\r\n"+
					"  </div>\r\n"+
					"</div>\r\n"+
					"       <div class=\"text-center py-4\">\r\n" + 
					"    <div class=\"container\">\r\n" + 
					"      <div class=\"row\">\r\n" + 
					"      </div>\r\n" + 
					"    </div>\r\n" + 
					"  </div>"+ 
					"</div>"+
					"  <script src=\"https://code.jquery.com/jquery-3.3.1.slim.min.js\" integrity=\"sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo\" crossorigin=\"anonymous\" style=\"\"></script>\r\n" + 
					"  <script src=\"https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js\" integrity=\"sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49\" crossorigin=\"anonymous\" style=\"\"></script>\r\n" + 
					"  <script src=\"https://stackpath.bootstrapcdn.com/bootstrap/4.2.1/js/bootstrap.min.js\" integrity=\"sha384-B0UglyR+jN6CkvvICOB2joaf5I4l3gm9GU6Hc1og6Ls7i6U/mkkaduKaBhlAXv9k\" crossorigin=\"anonymous\" style=\"\"></script>\r\n" ;
					
					stringToSendToWebBrowser += "</body>\r\n" + 
					"</html>";
					
			}
			toProcess.r = new WebResponse(WebResponse.HTTP_OK, WebResponse.MIME_HTML, stringToSendToWebBrowser);
			
			return true;
		}
		return false;
	}
}
//Idun Craig 40223245