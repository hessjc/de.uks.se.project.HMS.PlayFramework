package provider;

import provider.SimpleUserPassAuthProvider.SimpleAuthSignup;
import util.HashUtil;

import com.feth.play.module.pa.providers.password.UsernamePasswordAuthUser;
import com.feth.play.module.pa.user.FirstLastNameIdentity;

public class SimpleAuthUser extends UsernamePasswordAuthUser implements FirstLastNameIdentity
{

   private static final long serialVersionUID = 5285813409716417553L;

   private final String firstName;

   private final String lastName;

   private final String matrikelNumber;

   public SimpleAuthUser(final SimpleAuthSignup signup)
   {
      this(signup.getEmail(), signup.getSignupPassword(), signup.getFirstName(), signup.getLastName(), signup
            .getMatrikelNumber());
   }

   public SimpleAuthUser(final String login, final String password)
   {
      this(login, password, null, null, null);
   }

   public SimpleAuthUser(final String login, final String password, final String firstName, final String lastName,
         final String matrikelNumber)
   {
      super(password, login);
      this.firstName = firstName;
      this.lastName = lastName;
      this.matrikelNumber = matrikelNumber;
   }

   public boolean checkPassword(final String candidate)
   {
      return checkPassword(getHashedPassword(), candidate);
   }

   @Override
   public boolean checkPassword(String hashed, String candidate)
   {
      if (hashed == null || candidate == null)
      {
         return false;
      }
      return candidate.equals(hashed);
   }

   @Override
   protected String createPassword(String clearString)
   {
      return HashUtil.computeSha1(clearString);
   }

   @Override
   public String getId()
   {
      return getEmail();
   }

   @Override
   public String getFirstName()
   {
      return firstName;
   }

   @Override
   public String getLastName()
   {
      return lastName;
   }

   @Override
   public String getName()
   {
      return String.format("%s %s", getFirstName(), getLastName());
   }

   public String getMatrikelNumber()
   {
      return matrikelNumber;
   }

}
