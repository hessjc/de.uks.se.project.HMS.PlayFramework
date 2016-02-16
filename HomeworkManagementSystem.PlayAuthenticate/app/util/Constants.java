package util;

import provider.SimpleUserPassAuthProvider;


public interface Constants
{
   String SIMPLE_AUTH_USER_FACTORY_KEY = "simpleauth.userfactory";
   String SIMPLE_AUTH_CALL_AUTH_FACTORY_KEY = "simpleauth.callauthfactory";
   
   String SIMPLE_AUTH_SIGNUP_VERIFCATION_EMAIL_TEMPLATE_BODY_KEY = "simpleauth.email.template.signupverificationbody";
   String SIMPLE_AUTH_PASSWORD_RESET_EMAIL_TEMPLATE_BODY_KEY = "simpleauth.email.template.passwordresetbody";
   String SIMPLE_AUTH_VERIFCATION_EMAIL_TEMPLATE_BODY_KEY = "simpleauth.email.template.verificationbody";
   
   String FLASH_ERROR_KEY = "error";
   
   String DEFAULT_SIGNUP_VERIFICATION_EMAIL_TEMPLATE_PATH = "views.%s.account.signup.email.verify_email";
   String DEFAULT_VERIFICATION_EMAIL_TEMPLATE_PATH = "views.%s.account.email.verify_email";
   String DEFAULT_PASSWORD_RESET_EMAIL_TEMPLATE_PATH = "views.%s.account.email.password_reset";
         
   public static final String EMAIL_TEMPLATE_FALLBACK_LANGUAGE = "en";
   
   public static final String SETTING_KEY_LINK_LOGIN_AFTER_PASSWORD_RESET = "loginAfterPasswordReset";
   public static final String SETTING_KEY_PASSWORD_RESET_LINK_SECURE = SimpleUserPassAuthProvider.SETTING_KEY_EMAIL   + "." + "passwordResetLink.secure";
   public static final String SETTING_KEY_VERIFICATION_LINK_SECURE = SimpleUserPassAuthProvider.SETTING_KEY_EMAIL + "." + "verificationLink.secure";
}
