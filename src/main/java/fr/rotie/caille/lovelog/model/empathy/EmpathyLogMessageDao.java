package fr.rotie.caille.lovelog.model.empathy;

import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;

public class EmpathyLogMessageDao {
	
	public EmpathyLogMessage parseInstant(EmpathyLogMessage m) {
		m.setInstant(Instant.parse(m.getStrTime(), DateTimeFormat.forPattern("yyyyMMdd'T'HH:mm:ss")));
		return m;
	}
	
}
