package models;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import play.db.ebean.Model;

@MappedSuperclass
public class HMSModelElement extends Model
{
   @Version
   public long modTime;
}
