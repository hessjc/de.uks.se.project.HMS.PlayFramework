package utils;

import play.i18n.Messages;

public interface Constants
{
   String LDAP_URL_KEY = "simpleauth.ldap.url";
   String LDAP_PORT_KEY = "simpleauth.ldap.port";
   
   String MAIL_CONFIGURATION_KEY = "play-authenticate.password.mail";
   
   String UPLOAD_BASE_FOLDER = "/../hms_uploads/";
   
   String ASSIGNMENT_COMPLETED = Messages.get("assignment.completed");
}
