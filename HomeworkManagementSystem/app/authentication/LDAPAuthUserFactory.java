package authentication;

import java.io.UnsupportedEncodingException;

import models.Roles;
import models.User;

import org.apache.directory.api.ldap.model.cursor.CursorException;
import org.apache.directory.api.ldap.model.cursor.EntryCursor;
import org.apache.directory.api.ldap.model.entry.Attribute;
import org.apache.directory.api.ldap.model.entry.DefaultAttribute;
import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.entry.ModificationOperation;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.message.AddRequest;
import org.apache.directory.api.ldap.model.message.AddRequestImpl;
import org.apache.directory.api.ldap.model.message.AddResponse;
import org.apache.directory.api.ldap.model.message.ResultCodeEnum;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;

import play.Configuration;
import play.Logger;
import provider.SimpleAuthUser;
import util.HashUtil;
import util.IUser;
import util.UserFactory;
import utils.Constants;

public class LDAPAuthUserFactory implements UserFactory
{

   private String ldapURL;
   private int ldapPort;

   public LDAPAuthUserFactory()
   {
      Configuration configuration = Configuration.root();
      ldapURL = configuration.getString(Constants.LDAP_URL_KEY);
      ldapPort = configuration.getInt(Constants.LDAP_PORT_KEY);

      if (ldapURL == null || ldapPort == 0)
      {
         throw new IllegalArgumentException(
               String.format(
                     "The LDAP is not configured correctly. Please configure the keys %s and %s in your configuration!",
                     Constants.LDAP_URL_KEY, Constants.LDAP_PORT_KEY));
      }
   }

   @Override
   public IUser create(SimpleAuthUser user)
   {
      return addUser(user);
   }

   @Override
   public IUser findByAuthUser(SimpleAuthUser user)
   {
      return User.findUser(user.getEmail());
   }

   @Override
   public IUser findByLogin(String user)
   {
      return User.findUser(user);
   }

   @Override
   public IUser findByLoginPassword(String login, String password)
   {
      return User.findUser(login);
   }

   /**
    * Try to find a user via a LDAP connection
    * 
    * @param connection
    * the LDAP connection
    * @param username
    * of the requested user
    * @return Entry with the user, if found, null if no user could be found
    */
   private static org.apache.directory.api.ldap.model.entry.Entry findUser(
         LdapConnection connection, String username) throws LdapException,
         CursorException
   {
      // search for the user
      EntryCursor cursor = connection.search(
            "ou=Users,dc=se,dc=cs,dc=uni-kassel,dc=de,dc=local",
            String.format("(uid=%s)", username), SearchScope.ONELEVEL, "*");
      while (cursor.next())
      {
         return cursor.get();
      }
      return null;
   }

   private IUser addUser(SimpleAuthUser user)
   {
      LdapConnection connection = null;
      try
      {
         // create the connection
         connection = newConnection();

         String username = user.getEmail();

         Entry entry = new DefaultEntry(String.format(
               "uid=%s,ou=Users,dc=se,dc=cs,dc=uni-kassel,dc=de,dc=local", username));
         entry.add(new DefaultAttribute("objectClass", "top"));
         entry.add(new DefaultAttribute("objectClass", "person"));
         entry.add(new DefaultAttribute("objectClass",
               "organizationalPerson"));
         entry.add(new DefaultAttribute("objectClass", "inetOrgPerson"));
         entry.add(new DefaultAttribute("cn", username));
         entry.add(new DefaultAttribute("sn", username));
         entry.add(new DefaultAttribute("mail", user.getEmail()));
         entry.add(new DefaultAttribute("userPassword", HashUtil.computeSha1(user
               .getPassword().getBytes("UTF-8"))));

         AddRequest addRequest = new AddRequestImpl();
         addRequest.setEntry(entry);
         AddResponse response = connection.add(addRequest);

         if (response != null)
         {
            if (ResultCodeEnum.SUCCESS == response.getLdapResult()
                  .getResultCode())
            {
               return User.createUser(user.getEmail(),
                     user.getFirstName(), user.getLastName(),
                     user.getPassword(), user.getMatrikelNumber(), Roles.findRoleByName("User"));
            }
         }
      }
      catch (Exception e)
      {
         Logger.info("Error creating a LDAP user", e);
      }
      finally
      {
         if (connection != null)
         {
            try
            {
               connection.unBind();
               connection.close();
            }
            catch (Exception ex)
            {
               Logger.info("Error while unbinding the LDAP connection", ex);
            }
         }
      }
      return null;
   }

   private boolean checkPassword(String password1, String password2)
   {
      return password1 != null && password1.equals(password2);
   }

   private String findPassword(String username)
   {
      LdapConnection connection = null;
      try
      {
         // create the connection
         connection = newConnection();

         // search for the user
         org.apache.directory.api.ldap.model.entry.Entry user = findUser(
               connection, username);
         if (user != null)
         {
            // get the userPassword attribute for the user
            Attribute ldapPassword = user.get("userPassword");
            return ldapPassword.getString();
         }
      }
      catch (Exception e)
      {
         Logger.info("Error checking a LDAP user", e);
      }
      finally
      {
         if (connection != null)
         {
            try
            {
               connection.unBind();
               connection.close();
            }
            catch (Exception ex)
            {
               Logger.info("Error while unbinding the LDAP connection", ex);
            }
         }
      }
      return null;
   }

   private LdapConnection newConnection() throws LdapException
   {
      // create the connection
      LdapConnection connection = new LdapNetworkConnection(ldapURL, ldapPort);

      // bind the connection anonymous -> open the session
      connection.bind("cn=admin,dc=se,dc=cs,dc=uni-kassel,dc=de,dc=local", "eHe9e1dy4r");
      return connection;
   }

   public void updatePassword(final String login, final String newPassword)
   {
      LdapConnection connection = null;
      try
      {
         // create the connection
         connection = newConnection();

         // search for the user
         org.apache.directory.api.ldap.model.entry.Entry user = findUser(connection, login);
         if (user != null)
         {
            // get the userPassword attribute for the user
            Attribute ldapPassword = user.get("userPassword");
            if (!newPassword.equals(ldapPassword.getString()))
            {
               Entry entry = new DefaultEntry(String.format("uid=%s,ou=Users,dc=se,dc=cs,dc=uni-kassel,dc=de,dc=local",
                     login));
               entry.add(new DefaultAttribute("userPassword", newPassword));
               connection.modify(entry, ModificationOperation.REPLACE_ATTRIBUTE);
            }
         }
      }
      catch (Exception e)
      {
         Logger.info("Error updating the password of a LDAP user", e);
      }
      finally
      {
         if (connection != null)
         {
            try
            {
               connection.unBind();
               connection.close();
            }
            catch (Exception ex)
            {
               Logger.info("Error while unbinding the LDAP connection", ex);
            }
         }
      }
   }

   public boolean checkPassword(SimpleAuthUser user, IUser dbUser)
   {
      if (dbUser != null)
      {
         try
         {
            String hashedPw = HashUtil.computeSha1(user.getPassword().getBytes("UTF-8"));
            String ldapPassword = findPassword(dbUser.getEmail());

            if (ldapPassword == null || ldapPassword.isEmpty())
            {
               if (checkPassword(dbUser.getPassword(), hashedPw))
               {
                  return true;
               }
            }
            else
            {
               if (checkPassword(ldapPassword, hashedPw))
               {
                  return true;
               }
            }
         }
         catch (UnsupportedEncodingException e)
         {
            Logger.info("Error checking an user with LDAP", e);
         }
      }
      return false;
   }
}
