package util;

import com.feth.play.module.pa.exceptions.AuthException;
import com.feth.play.module.pa.providers.password.UsernamePasswordAuthUser;

import play.mvc.Call;

public interface CallAuthFactory
{
   /**
    * Should return the {@link Call} to execute if an already existing user
    * tries to signup again 
    * @param authUser
    * @return The {@link Call} to execute.
    */
   Call onUserExists(final UsernamePasswordAuthUser authUser);
   
   /**
    * Should return the {@link Call} to execute if the user exists but is 
    * still unverified.
    * @param authUser
    * @return The {@link Call} to execute.
    */
   Call onUserUnverified(final UsernamePasswordAuthUser authUser);
   
   /**
    * Should return the {@link Call} to execute if the user was created and is
    * requested to verify his account.
    * @param token The <code>Token</code> which is used to verify the user.
    * @return The {@link Call} to execute.
    */
   Call onUserVerify(final String token);
   
   /**
    * Should return the {@link Call} to the login page
    * @return The {@link Call} to execute.
    */
   Call onLogin();
   
   /**
    * Should return the {@link Call} to the page after the user has been logged in
    * if no original URL was saved.
    * @return The {@link Call} to execute.
    */
   Call afterAuth();
   
   /**
    * Should return the {@link Call} to the page after the user has been logged out.
    * @return The {@link Call} to execute.
    */
   Call afterLogout();
   
   /**
    * Should return the {@link Call} to the correct page if the given exception occurs.
    * @return The {@link Call} to execute.
    */
   Call onException(final AuthException e);
   
   /**
    * Should return the {@link Call} to the auth page.
    * @return The {@link Call} to execute.
    */
   Call onAuth(final String provider);
}
