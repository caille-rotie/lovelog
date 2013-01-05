package fr.rotie.caille.lovelog.model.empathy;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import fr.rotie.caille.lovelog.model.LogMessage;

@Entity
@DiscriminatorValue("Empathy")
public class EmpathyLogMessage extends LogMessage {
	
	private String email;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	
}
