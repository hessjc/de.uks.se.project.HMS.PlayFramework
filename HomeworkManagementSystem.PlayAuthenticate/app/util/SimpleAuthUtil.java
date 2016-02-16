package util;

import models.Token;
import models.Token.Type;

public class SimpleAuthUtil
{
   /**
    * Returns a token object if valid, null if not
    * 
    * @param token
    * @param type
    * @return
    */
   public static Token tokenIsValid(final String token, final Type type) {
      Token ret = null;
      if (token != null && !token.trim().isEmpty()) {
         final Token ta = Token.findByToken(token, type);
         if (ta != null && ta.isValid()) {
            ret = ta;
         }
      }

      return ret;
   }
   
   public static void verifyUser(final IUser unverified)
   {
      unverified.setEmailValidated(true);
      unverified.save();
      Token.deleteByUser(unverified, Type.EMAIL_VERIFICATION);
   }
   
}
