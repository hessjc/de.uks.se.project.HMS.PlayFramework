package util;


public interface IUser
{
   /**
    * Checks whether the user already completed the email verification process 
    * @return <code>true</code> if the validation process is complete <code>false</code> otherwise
    */
   boolean isEmailValidated();
   
   void setEmailValidated(final boolean validated);
   
   /**
    * Returns the users name, usually his first and last name.
    * @return
    */
   String getName();
   
   String getEmail();
   
   String getPassword();
   
   Long getId();
   
   void save();
}
