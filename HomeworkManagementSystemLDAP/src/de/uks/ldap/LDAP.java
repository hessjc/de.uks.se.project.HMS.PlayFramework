package de.uks.ldap;

import java.io.File;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.ldap.model.schema.SchemaManager;
import org.apache.directory.server.core.api.DirectoryService;
import org.apache.directory.server.core.factory.DefaultDirectoryServiceFactory;
import org.apache.directory.server.core.partition.impl.btree.jdbm.JdbmPartition;
import org.apache.directory.server.ldap.LdapServer;
import org.apache.directory.server.protocol.shared.transport.TcpTransport;

public class LDAP
{
   private DirectoryService service;

   public static void main(String[] args)
   {

      new LDAP();
   }

   public LDAP()
   {
      try
      {
         this.init();
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   private void init() throws Exception
   {

      DefaultDirectoryServiceFactory defaultDirectoryServiceFactory = new DefaultDirectoryServiceFactory();
      File file = new File("hds");
      System.setProperty("workingDirectory", file.getAbsolutePath());
      defaultDirectoryServiceFactory.init("hds");

      service = defaultDirectoryServiceFactory.getDirectoryService();
      service.setAllowAnonymousAccess(true);
      service.getChangeLog().setEnabled(false);

      SchemaManager schemaManager = service.getSchemaManager();

      JdbmPartition jdbmPartition = new JdbmPartition(schemaManager);
      jdbmPartition.setId("hds");
      jdbmPartition.setPartitionPath(file.toURI());
      Dn suffixDn = new Dn(schemaManager, "dc=se,dc=cs,dc=uni-kassel,dc=de,dc=local");
      jdbmPartition.setSuffixDn(suffixDn);

      service.addPartition(jdbmPartition);

      LdapServer ldapService = new LdapServer();
      ldapService.setTransports(new TcpTransport(389));
      ldapService.setDirectoryService(service);

      service.startup();
      ldapService.start();

      Entry entryApache;
      // Inject the apache root entry if it does not already exist
      try
      {
         entryApache = service.getAdminSession().lookup(
               jdbmPartition.getSuffixDn());
      }
      catch (LdapException lnnfe)
      {
         Dn dnApache = new Dn("dc=se,dc=cs,dc=uni-kassel,dc=de,dc=local");
         entryApache = service.newEntry(dnApache);
         entryApache.add("objectClass", "domain", "extensibleObject");
         service.getAdminSession().add(entryApache);
      }

      Hashtable<String, String> env = new Hashtable<String, String>();
      env.put(Context.INITIAL_CONTEXT_FACTORY,
            "com.sun.jndi.ldap.LdapCtxFactory");
      env.put(Context.PROVIDER_URL, "ldap://localhost:389");
      env.put(Context.SECURITY_AUTHENTICATION, "simple");
      env.put(Context.SECURITY_PRINCIPAL, "uid=admin,ou=system");
      env.put(Context.SECURITY_CREDENTIALS, "secret");

      addPartition(env);

      // add users
      addUser(env, "jFast", "juergen@fast.de");
      addUser(env, "jHess", "jan@hess.de");
      addUser(env, "aScharf", "andreas@scharf.de");
      addUser(env, "aKoch", "andreas@koch.de");
      addUser(env, "student1", "student1@student.de");
      addUser(env, "student2", "student2@student.de");
      addUser(env, "student3", "student3@student.de");
      addUser(env, "lectureAdmin1", "lecture1@admin.de");
      addUser(env, "lectureAdmin2", "lecture2@admin.de");
      addUser(env, "proofReader1", "proof1@reader.de");
      addUser(env, "proofReader2", "proof2@reader.de");
      addUser(env, "proofReader3", "proof3reader.de");
   }

   private void addPartition(Hashtable<String, String> env)
   {
      String entryDN = "ou=Users,dc=se,dc=cs,dc=uni-kassel,dc=de,dc=local";
      Attribute oc = new BasicAttribute("objectClass");
      oc.add("top");
      oc.add("organizationalUnit");
      oc.add("extensibleObject");

      DirContext ctx = null;
      try
      {
         // get a handle to an Initial DirContext
         ctx = new InitialDirContext(env);

         // build the entry
         BasicAttributes entry = new BasicAttributes();
         entry.put(oc);

         ctx.createSubcontext(entryDN, entry);

      }
      catch (NamingException e)
      {
         System.err.println("AddUser: error adding entry." + e);
      }
   }

   private void addUser(Hashtable<String, String> env, String username,
         String mailAdress)
   {
      // entry's DN
      String entryDN = String.format(
            "uid=%s,ou=Users,dc=se,dc=cs,dc=uni-kassel,dc=de,dc=local", mailAdress);
      Attribute cn = new BasicAttribute("cn", mailAdress);
      Attribute sn = new BasicAttribute("sn", mailAdress);
      Attribute mail = new BasicAttribute("mail", mailAdress);
      Attribute password = new BasicAttribute("userPassword",
            convertToSHA1Base64("secret2"));
      Attribute oc = new BasicAttribute("objectClass");
      oc.add("top");
      oc.add("person");
      oc.add("organizationalPerson");
      oc.add("inetOrgPerson");

      DirContext ctx = null;
      try
      {
         // get a handle to an Initial DirContext
         ctx = new InitialDirContext(env);

         // build the entry
         BasicAttributes entry = new BasicAttributes();
         entry.put(cn);
         entry.put(sn);
         entry.put(mail);
         entry.put(password);
         entry.put(oc);

         ctx.createSubcontext(entryDN, entry);

      }
      catch (NamingException e)
      {
         System.err.println("AddUser: error adding entry." + e);
      }
   }

   public static String convertToSHA1Base64(
         final String shaCleartextPassword)
   {
      String shaHex = DigestUtils.sha1Hex(shaCleartextPassword);

      String sha1Base64 = null;
      try
      {
         sha1Base64 = "{SHA}"
               + new String(
                     org.apache.commons.codec.binary.Base64
                           .encodeBase64((byte[])new Hex("UTF-8")
                                 .decode(shaHex)));
      }
      catch (final Exception e)
      {
         e.printStackTrace();
      }
      return sha1Base64;
   }
}
