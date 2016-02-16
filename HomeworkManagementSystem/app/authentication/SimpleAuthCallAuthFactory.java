package authentication;

import play.mvc.Call;
import play.mvc.Controller;
import util.CallAuthFactory;

import com.feth.play.module.pa.exceptions.AuthException;
import com.feth.play.module.pa.providers.password.UsernamePasswordAuthUser;

import controllers.routes;

public class SimpleAuthCallAuthFactory implements CallAuthFactory
{

   @Override
   public Call onUserExists(UsernamePasswordAuthUser authUser)
   {
      return routes.UserController.userExists();
   }

   @Override
   public Call onUserUnverified(UsernamePasswordAuthUser authUser)
   {
      return routes.UserController.unverified();
   }

   @Override
   public Call onUserVerify(String token)
   {
      return routes.UserController.verify(token);
   }

   @Override
   public Call onLogin()
   {
      return routes.Application.index();
   }

   @Override
   public Call afterAuth()
   {
      return routes.Application.index();
   }

   @Override
   public Call afterLogout()
   {
      return routes.Application.index();
   }

   @Override
   public Call onException(AuthException e)
   {
      Controller.flash("error", e.getMessage());
      return routes.Application.index();
   }

   @Override
   public Call onAuth(final String provider)
   {
      return com.feth.play.module.pa.controllers.routes.Authenticate.authenticate(provider);
   }
}
