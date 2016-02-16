package security;
import static play.libs.F.Promise.pure;
import models.User;
import play.libs.F.Promise;
import play.mvc.Http;
import play.mvc.SimpleResult;
import be.objectify.deadbolt.core.models.Subject;
import be.objectify.deadbolt.java.AbstractDeadboltHandler;
import be.objectify.deadbolt.java.DynamicResourceHandler;

import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.user.AuthUser;

public class DeadboltHandler extends AbstractDeadboltHandler
{
   private static DeadboltHandler INSTANCE;
   
   public static final DeadboltHandler get()
   {
      if(INSTANCE == null)
      {
         INSTANCE = new DeadboltHandler();
      }
      return INSTANCE;
   }

   @Override
   public Promise<SimpleResult> beforeAuthCheck(final Http.Context context)
   {
      return null;
   }

   @Override
   public Subject getSubject(final Http.Context context)
   {
      AuthUser authUser = PlayAuthenticate.getUser(context.session());
      if (authUser != null)
      {
         return User.findUser(authUser.getId());
      }
      return null;
   }

   @Override
   public DynamicResourceHandler getDynamicResourceHandler(
         final Http.Context context)
   {
      return new HDSDynamicResourceHandler();
   }

   @Override
   public Promise<SimpleResult> onAuthFailure(final Http.Context context, final String content)
   {
      // if the user has a cookie with a valid user and the local user has
      // been deactivated/deleted in between, it is possible that this gets
      // shown. You might want to consider to sign the user out in this case.
      return pure((SimpleResult)forbidden(views.html.errors.e403.render()));
   }
}
