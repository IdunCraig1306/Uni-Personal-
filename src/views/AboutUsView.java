package views;

import org.h2.mvstore.MVMap;

import model.UserModel;
import storage.DatabaseInterface;
import storage.FileStoreInterface;
import web.WebRequest;
import web.WebResponse;

public class AboutUsView extends DynamicWebPage{
	
	
	UserModel currentUser = new UserModel();
	MVMap<String, UserModel> users = db.s.openMap("Users");

	public AboutUsView(DatabaseInterface db, FileStoreInterface fs) {
		super(db, fs);
		// TODO Auto-generated constructor stub
	}

	public boolean process(WebRequest toProcess)
	{
		if(toProcess.path.equalsIgnoreCase("aboutus"))
		{
			//Sets currentUser model to the user currently signed in
			currentUser = users.get(toProcess.cookies.get("username"));
			
			//Creates Creator models for each person in the group
			model.CreatorModel idun = new model.CreatorModel();
			idun.name = "Idun Craig";
			idun.description = "First year Software Engineering student at Queen's University Belfast";
			idun.filepathToImage = "Images/Idun_Creator.jpg" ;
			
			model.CreatorModel luke = new model.CreatorModel();
			luke.name = "Luke Collingwood";
			luke.description = "First year Software Engineering student at Queen's University Belfast";
			luke.filepathToImage = "Images/Luke_Creator.jpg";
			
			model.CreatorModel matt = new model.CreatorModel();
			matt.name = "Matthew Cohen";
			matt.description = "First year Software Engineering student at Queen's University Belfast";
			matt.filepathToImage = "Images/Matt_Creator.jpg";
			
			model.CreatorModel lewis = new model.CreatorModel();
			lewis.name = "Lewis Creelman";
			lewis.description = "First year Software Engineering student at Queen's University Belfast";
			lewis.filepathToImage = "Images/Lewis_Creator.jpg";
			
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
					+ "				<!-- Profile </-->\r\n"
					+ "				<li class=\"nav-item\"><a class=\"nav-link\" href=\"aboutus\">About Us</a></li>\r\n";
					
					//checks whether someone is logged in
					if(!(toProcess.cookies.get("username") == null)) {
						stringToSendToWebBrowser += "<li class=\"nav-item\"> <a class=\"nav-link text-primary\" href=\"profile\">"
								+ toProcess.cookies.get("username") + "</a> </li>\r\n";
        			}else {
        				stringToSendToWebBrowser += "<li class=\"nav-item\"> <a class=\"nav-link text-primary\" href=\"register\">"
        						+ "Register" + "</a> </li>\r\n";
        			}
					
					stringToSendToWebBrowser += "</ul></div>\r\n"+
					"	</nav>\r\n" + "	<!-- End of Navbar </-->\r\n" + "\r\n" 
					+ "\r\n"+
					"  <div class=\"text-center py-4\">\r\n" + 
					"    <div class=\"container\">\r\n" + 
					"      <div class=\"row\">\r\n" + 
					"        <div class=\"mx-auto col-md-12\">\r\n" + 
					"          <h1 class=\"mb-3\"><b>Meet&nbsp;the team</b></h1>\r\n" + 
					"        </div>\r\n" + 
					"      </div>\r\n" + 
					"      <div class=\"row\">\r\n" + 
					//Displays the details for the creators
					"        <div class=\"col-lg-3 col-6 p-4\"> <img class=\"img-fluid d-block mb-3 mx-auto rounded-circle\" src=" + idun.filepathToImage + " width=\"100\" alt=\"Card image cap\">\r\n" + 
					"          <h4>" + idun.name + "</h4>\r\n" + 
					"          <p class=\"mb-0\">" + idun.description + "</p>\r\n" + 
					"        </div>\r\n" + 
					"        <div class=\"col-lg-3 col-6 p-4\"> <img class=\"img-fluid d-block mb-3 mx-auto rounded-circle\" src=" + luke.filepathToImage + " width=\"100\" alt=\"Card image cap\">\r\n" + 
					"          <h4 class=\"w-100\" style=\"	min-width: 120%;	transform:  translateX(-10%) ;\" >" +luke.name + "</h4>\r\n" + 
					"          <p class=\"mb-0\">"+ luke.description +"</p>\r\n" + 
					"        </div>\r\n" + 
					"        <div class=\"col-lg-3 col-6 p-4\"> <img class=\"img-fluid d-block mb-3 mx-auto rounded-circle\" src=" + matt.filepathToImage + " width=\"100\">\r\n" + 
					"          <h4>"+ matt.name +"</h4>\r\n" + 
					"          <p class=\"mb-0\">" + matt.description + "</p>\r\n" + 
					"        </div>\r\n" + 
					"        <div class=\"col-lg-3 col-6 p-4\"> <img class=\"img-fluid d-block mb-3 mx-auto rounded-circle\" src=" + lewis.filepathToImage + " width=\"100\">\r\n" + 
					"          <h4>" + lewis.name + "</h4>\r\n" + 
					"          <p class=\"mb-0\">" + lewis.description + "</p>\r\n" + 
					"        </div>\r\n" + 
					"      </div>\r\n" + 
					"    </div>\r\n" + 
					"  </div>\r\n" + 
					"  <div class=\"py-4\">\r\n" + 
					"    <div class=\"container\">\r\n" + 
					"      <div class=\"row\">\r\n" + 
					//Shows the location of Queens using google maps
					"         <div class=\"col-lg-12\" style=\"\"><iframe width=\"1200\" height=\"450\" src=\"https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2312.111539896489!2d-5.936237984114509!3d54.58440868025711!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x486108ea57227da7%3A0x3cecfa2a15d642e1!2sQueen's+University+Belfast!5e0!3m2!1sen!2suk!4v1550431568247\" frameborder=\"0\" allowfullscreen=\"\" style=\"	transform:  translateX(-2.5%) ;\" class=\"pr-3\" ></iframe>\r\n"+
					"      </div>\r\n" + 
					"    </div>\r\n" + 
					"  </div>\r\n" + 
					"  <script src=\"https://code.jquery.com/jquery-3.3.1.slim.min.js\" integrity=\"sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo\" crossorigin=\"anonymous\" style=\"\"></script>\r\n" + 
					"  <script src=\"https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js\" integrity=\"sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49\" crossorigin=\"anonymous\" style=\"\"></script>\r\n" + 
					"  <script src=\"https://stackpath.bootstrapcdn.com/bootstrap/4.2.1/js/bootstrap.min.js\" integrity=\"sha384-B0UglyR+jN6CkvvICOB2joaf5I4l3gm9GU6Hc1og6Ls7i6U/mkkaduKaBhlAXv9k\" crossorigin=\"anonymous\" style=\"\"></script>\r\n" +  
					"<footer>\r\n" + 
					"        <div class=\"table-active\">\r\n" + 
					"          <div class=\"col\">\r\n" + 
					"            <ul class=\"list-unstyled row\">\r\n" + 
					"          <div class=\"col\">\r\n" + 
					"<h6>External Site Links</h6>"+
					"              <li><a href=\"https://en.wikipedia.org/wiki/Science_fiction\">What is Sci-Fi?</a></li>\r\n" + 
					"              <li><a href=\"https://paizo.com/starfinder\"> Into RPGs?</a></li>\r\n" +
					"          </div>\r\n" + 
					"          <div class=\"col\">\r\n" +
					"          </div>\r\n" + 
					"          <div class=\"col\">\r\n" +
					"		<a class=\"navbar-brand\" href=\"home\">Quest for Fun</a>\r\n" +
					"          </div>\r\n" + 
					"          <div class=\"col\">\r\n" +
					"          </div>\r\n" + 
					"          <div class=\"col\">\r\n" +
					"<h6>Internal Site Links</h6>"+
					"              <li><a href=\"editprofile\">Edit Profile</a></li>\r\n" + 
					"              <li><a href=\"home\">Home</a></li>\r\n" + 
					"              <li><a href=\"Help\">Help</a></li>\r\n" + 
					"              <li><a href=\"login\">Login</a></li>\r\n" + 
					"	      <li><a href=\"aboutus\">About Us</a></li>\r\n" + 
					"	      <li><a href=\"Forum\">Forums</a></li>\r\n" +
					"          </div>\r\n" +
					"            </ul>\r\n" + 
					"            <p class =\"text-center\">Copyright@Webapp-74</a>.</p>\r\n" +
					"          </div>\r\n" + 
					"        </div>\r\n" +
					"      </footer>" +
					"</body>\r\n" + 
					"\r\n" + 
					"</html>";
			
			toProcess.r = new WebResponse(WebResponse.HTTP_OK, WebResponse.MIME_HTML, stringToSendToWebBrowser);
			
			return true;
		}
		return false;
	}
}
 //Idun Craig 40223245
