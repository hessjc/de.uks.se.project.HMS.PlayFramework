package pages;

import play.mvc.Result;
import funcy.Form;
import funcy.Page;

public class IndexPage extends Page
{

   public IndexPage()
   {
      this(Page.get("/"));
   }

   public IndexPage(Result result)
   {
      super(result, "/");
   }

   public IndexPage login(String username, String password)
   {
      Form form = form(0);
      form.set("login", username);
      form.set("password", password);
      return new IndexPage(form.submit());
   }

   public IndexPage clickReload()
   {
      return new IndexPage(clickName("Reload"));
   }
}