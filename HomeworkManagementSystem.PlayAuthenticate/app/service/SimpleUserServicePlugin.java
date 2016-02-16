package service;

import play.Application;
import play.Logger;
import provider.SimpleAuthUser;
import provider.SimpleUserPassAuthProvider;
import util.IUser;
import util.UserFactory;

import com.feth.play.module.pa.service.UserServicePlugin;
import com.feth.play.module.pa.user.AuthUser;
import com.feth.play.module.pa.user.AuthUserIdentity;

public class SimpleUserServicePlugin extends UserServicePlugin
{

   public SimpleUserServicePlugin(Application app)
   {
      super(app);
   }

   @Override
   public Object save(AuthUser authUser)
   {
      if (authUser instanceof SimpleAuthUser)
      {
         UserFactory userFactory = getUserFactory();
         IUser foundUser = userFactory.findByLogin(((SimpleAuthUser)authUser).getEmail());
         if (foundUser == null)
         {
            userFactory.create((SimpleAuthUser)authUser);
         }
      }
      return null;
   }

   private UserFactory getUserFactory()
   {
      return SimpleUserPassAuthProvider.getProvider().getUserFactory();
   }

   @Override
   public Object getLocalIdentity(AuthUserIdentity identity)
   {
      IUser user = getUserFactory().findByLogin(identity.getId());
      if (user == null)
      {
         Logger.info(String.format("Failed login with: %s", identity.getId()));
         return null;
      }
      else
      {
         return user.getId();
      }
   }

   @Override
   public AuthUser merge(AuthUser newUser, AuthUser oldUser)
   {
      // Unsupported
      return null;
   }

   @Override
   public AuthUser link(AuthUser oldUser, AuthUser newUser)
   {
      // Unsupported
      return null;
   }

}
