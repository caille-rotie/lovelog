package fr.rotie.caille.lovelog.model;

import javax.persistence.*;
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
public class LogMessage extends LogEntity {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentInstantAsTimestamp")
	private Instant instant;
	
    private String name;
	
    @Type(type="text")
    private String text;
    
    @ManyToOne(cascade=javax.persistence.CascadeType.ALL)
    @JoinColumn(name="idLogDay", nullable=false)
    private LogDay logDay;
    
    @Transient
	private String strTime;

	
	public String getStrTime() {
		return strTime;
	}
	public void setStrTime(String dateTime) {
		this.strTime = dateTime;
	}

	
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
	
	
//	@Transient
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public LogDay getLogDay() {
		return logDay;
	}
	public void setLogDay(LogDay logDay) {
		this.logDay = logDay;
	}
	
//	@Override
//	public String toString() {
//		return "logMessage [id="+getId() +", instant=" + getInstant() + ", from=" + getName()  + ", text=" + getText() + "]";
//	}

	@Override
	public String toString() {
		return "LogMessage [id="+getId() +", instant=" + getInstant() + ", name="
				+ getName() + ", text=" + getText() + ", logDay=" + logDay.getId() + "]";
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
