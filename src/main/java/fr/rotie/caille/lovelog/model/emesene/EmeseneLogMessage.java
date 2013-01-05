package fr.rotie.caille.lovelog.model.emesene;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import fr.rotie.caille.lovelog.model.LogMessage;

@Entity
@DiscriminatorValue("Emesene")
public class EmeseneLogMessage extends LogMessage {
	
	

}
