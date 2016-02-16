package authentication;

import models.Roles;
import models.User;
import provider.SimpleAuthUser;
import util.IUser;
import util.UserFactory;

public class SimpleAuthUserFactory implements UserFactory
{

   @Override
   public IUser create(SimpleAuthUser saUser)
   {
      return User.createUser(saUser.getEmail(), saUser.getFirstName(), saUser.getLastName(),
            saUser.getPassword(), saUser.getMatrikelNumber(), Roles.findRoleByName("User"));
   }

   @Override
   public IUser findByAuthUser(SimpleAuthUser saUser)
   {
      return User.findUser(saUser.getEmail());
   }

   @Override
   public IUser findByLoginPassword(String login, String password)
   {
      IUser findUser = findByLogin(login);
      if (findUser != null)
      {
         if (!findUser.getPassword().equals(password))
         {
            return null;
         }
      }
      return findUser;
   }

   @Override
   public IUser findByLogin(String login)
   {
      return User.findUser(login);
   }

   public boolean checkPassword(SimpleAuthUser user, IUser dbUser)
   {
      return user.checkPassword(dbUser.getPassword());
   }

}
