package models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import play.data.format.Formats;
import play.db.ebean.Model;
import util.IUser;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.QueryIterator;
import com.avaje.ebean.annotation.EnumValue;

@Entity
public class Token extends Model {

	public enum Type {
		@EnumValue("EV")
		EMAIL_VERIFICATION,

		@EnumValue("PR")
		PASSWORD_RESET
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Verification time frame (until the user clicks on the link in the email)
	 * in seconds
	 * Defaults to one week
	 */
	private final static long VERIFICATION_TIME = 7 * 24 * 3600;

	@Id
	public Long id;

	@Column(unique = true)
	public String token;

	public Long targetUserID;

	public Type type;

	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date created;

	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date expires;

	public static final Finder<Long, Token> find = new Finder<Long, Token>(
			Long.class, Token.class);

	public static Token findByToken(final String token, final Type type) {
		return find.where().eq("token", token).eq("type", type).findUnique();
	}

	public static void deleteByUser(final IUser u, final Type type) {
		QueryIterator<Token> iterator = find.where().eq("targetUserID", u.getId()).eq("type", type).findIterate();
		Ebean.delete(iterator);
		iterator.close();
	}

	public boolean isValid() {
		return this.expires.after(new Date());
	}

	public static Token create(final Type type, final String token, final IUser targetUser) {
		final Token ua = new Token();
		ua.targetUserID = targetUser.getId();
		ua.token = token;
		ua.type = type;
		final Date created = new Date();
		ua.created = created;
		ua.expires = new Date(created.getTime() + VERIFICATION_TIME * 1000);
		ua.save();
		return ua;
	}
}
