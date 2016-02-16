package functional;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import pages.IndexPage;
import funcy.FunctionalTest;

public class IndexTest extends FunctionalTest
{

   @Test
   public void testIndex()
   {
      assertTrue(true);
      IndexPage indexPage = new IndexPage();
      indexPage = indexPage.login("jan@hess.de", "secret");
//      indexPage = indexPage.clickReload();
   }
}