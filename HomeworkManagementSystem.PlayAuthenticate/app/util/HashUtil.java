package util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;

public class HashUtil
{
   public static String computeSha1(final String message) throws UnsupportedOperationException
   {
      try
      {
         return computeSha1(message.getBytes("UTF-8"));
      }
      catch (UnsupportedEncodingException ex)
      {
         throw new UnsupportedOperationException(ex);
      }
   }

   public static String computeSha1(final byte[] message) throws UnsupportedOperationException
   {
      try
      {
         MessageDigest md = MessageDigest.getInstance("SHA-1");
         md.update(message);
         String res = Base64.encodeBase64String(md.digest());
         return "{SHA}" + res;
      }
      catch (NoSuchAlgorithmException ex)
      {
         throw new UnsupportedOperationException(ex);
      }
   }
}
