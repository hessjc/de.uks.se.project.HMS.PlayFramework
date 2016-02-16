package util;

import provider.SimpleAuthUser;

public interface UserFactory
{
   /**
    * Should return the {@link IUser} for the given {@link SimpleAuthUser}. 
    * @param simpleAuthUser
    * @return The {@link IUser} if found, <code>null</code> otherwise.
    */
   IUser findByAuthUser(final SimpleAuthUser simpleAuthUser);
   
   /**
   * Should create and return the {@link IUser} for the given {@link SimpleAuthUser}.
   * @param simpleAuthUser
   * @return The {@link IUser} if successfully created, <code>null</code> otherwise.
   */
   IUser create(final SimpleAuthUser simpleAuthUser);

   /**
    * Should return the {@link IUser} for the given <code>login</code> <b>and</b> <code>password</code>.
    * If the user can be found but passwords don't match, this method is expected to return <code>null</code>. 
    * @param login   The login to use for searching for the user
    * @param password The password to match
    * @return The {@link IUser} if found, <code>null</code> otherwise.
    */
   IUser findByLoginPassword(String login, String password);
   
   /**
    * Should return the {@link IUser} for the given <code>login</code>. 
    * @param login The login to use for searching for the user
    * @return The {@link IUser} if found, <code>null</code> otherwise.
    */
   IUser findByLogin(String login);

   /**
    * Should return true if the given <code>user</code> and the found <code>dbUser</code> have matching passwords
    * @param user the given {@link SimpleAuthUser}
    * @param dbUser the found {@link IUser} from the database
    * @return true if passwords match, false otherwise.
    */
   boolean checkPassword(SimpleAuthUser user, IUser dbUser);
}
