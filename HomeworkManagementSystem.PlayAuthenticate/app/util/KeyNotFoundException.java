package util;

public class KeyNotFoundException extends Exception
{

   /**
    * 
    */
   private static final long serialVersionUID = 7100456376466544159L;

   public KeyNotFoundException(String key)
   {
      super(String.format("The following key could not be found: %s",key));
   }
   
}
