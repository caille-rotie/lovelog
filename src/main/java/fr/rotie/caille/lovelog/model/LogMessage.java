package fr.rotie.caille.lovelog.model;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;
import org.joda.time.Instant;

//import org.jadira.usertype.dateandtime.joda.PersistentInstantAsTimestamp;

@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
	    name="logtype",
	    discriminatorType=DiscriminatorType.STRING
	)
	@DiscriminatorValue("LogMessage")
@Table(name = "LogMessage")
public class LogMessage {

    private Integer id;
	private String strTime;
	private Instant instant;
	private String name;
	private String text;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	@Transient
	public String getStrTime() {
		return strTime;
	}
	public void setStrTime(String dateTime) {
		this.strTime = dateTime;
	}

	@Type(type="org.jadira.usertype.dateandtime.joda.PersistentInstantAsTimestamp")
//	@Transient
	public Instant getInstant() {
		return instant;
	}
	public void setInstant(Instant instant) {
		this.instant = instant;
	}
	public String getName() {
		return name;
	}
	public void setName(String from) {
		this.name = from;
	}
	
	@Type(type="text")
//	@Transient
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	@Override
	public String toString() {
		return "logMessage [id="+getId() +", instant=" + getInstant() + ", from=" + getName()  + ", text=" + getText() + "]";
	}
	
	@Override
	public boolean equals(Object obj) {

	    if (obj==this) {
	        return true;
	    }

	    if (obj instanceof LogMessage) {
	    	LogMessage lm = (LogMessage) obj;
	    	return (getInstant().equals(lm.getInstant()) && getName().equals(lm.getName()) && getText().equals(lm.getText()));
	    }
	    return false;
	}
	
	@Override
	public int hashCode() {
	    int result = 11;
	    final int multiplier = 3;
	    result = multiplier*result + getInstant().hashCode();
	    result = multiplier*result + getName().hashCode();
	    result = multiplier*result + getText().hashCode();
	   
	    return result;
	}
	
	
}
