package de.uks.ldap;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.directory.api.ldap.model.cursor.CursorException;
import org.apache.directory.api.ldap.model.cursor.EntryCursor;
import org.apache.directory.api.ldap.model.entry.Attribute;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;
import org.junit.Test;

public class LDAPTest {

	@Test
	public void testCheckPassword() throws Exception {
		assertTrue("Password do not match!", checkPassword("jHess", "{SHA}5en6G6MezRroT3XKqkdPOmY/BfQ="));
	}

	@Test
	public void findjHess() throws Exception {
		LdapConnection connection = null;
		try {
			// create the connection
			connection = new LdapNetworkConnection("localhost", 389);

			// bind the connection anonymous -> open the session
			connection.bind();

			String username = "jHess";
			// search for the user
			Entry user = findUser(connection, username);

			// check that the user exists
			assertNotNull(user);

			if (user != null) {
				// get the userPassword attribute for the user
				Attribute password = user.get("userPassword");
				String passwordString = password.getString();

				// check the password
				System.out.println(String.format("The password of %s is %s",
						username, passwordString));
				assertEquals("{SHA}5en6G6MezRroT3XKqkdPOmY/BfQ=",
						passwordString);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.unBind();
				connection.close();
			}
		}
	}

	@Test
	public void findNobody() throws Exception {
		LdapConnection connection = null;
		try {
			// create the connection
			connection = new LdapNetworkConnection("localhost", 389);

			// bind the connection anonymous -> open the session
			connection.bind();

			String username = "nobody";
			// search for the user
			Entry user = findUser(connection, username);

			// assert that no user could be found
			assertNull(user);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.unBind();
				connection.close();
			}
		}
	}

	/**
	 * Try to find a user via a LDAP connection
	 * 
	 * @param connection
	 *            the LDAP connection
	 * @param username
	 *            of the requested user
	 * @return Entry with the user, if found, null if no user could be found
	 */
	private Entry findUser(LdapConnection connection, String username)
			throws LdapException, CursorException {
		// search for the user
		EntryCursor cursor = connection.search(
				"ou=Users,dc=hds,dc=uniks,dc=org",
				String.format("(uid=%s)", username), SearchScope.ONELEVEL, "*");
		while (cursor.next()) {
			return cursor.get();
		}
		return null;
	}

	private boolean checkPassword(String username, String password)
			throws LdapException, IOException {
		LdapConnection connection = null;
		try {
			// create the connection
			connection = new LdapNetworkConnection("localhost", 389);

			// bind the connection anonymous -> open the session
			connection.bind();

			// search for the user
			Entry user = findUser(connection, username);

			// check that the user exists
			assertNotNull(user);

			if (user != null) {
				// get the userPassword attribute for the user
				Attribute ldapPassword = user.get("userPassword");
				return password.equals(ldapPassword.getString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.unBind();
				connection.close();
			}
		}
		return false;
	}
}
