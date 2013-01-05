package fr.rotie.caille.lovelog.test.model;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.junit.Test;

import fr.rotie.caille.lovelog.model.emesene.EmeseneLogMessage;
import fr.rotie.caille.lovelog.model.emesene.EmeseneLogMessageDataFormat;

public class LogMessageDataFormatTest {
	
	private Logger logger = Logger.getLogger(this.getClass());

	@Test
	public void testParseEmesene() throws Exception {
		
		/**
r = 13
l = 10

n = en fonction du système

Windows = rl
Linux = l
Macintosh = r
*/
		String EOL = System.getProperty("line.separator");
		logger.debug("System.getProperty(\"line.separator\") : "+(int)EOL.charAt(0)+" ; \\n : "+(int)'\n'+" ; \\r : "+(int)'\r');
		
		Instant instant = Instant.parse("2012-10-29 23:20:49", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
		logger.debug(instant);
		logger.debug(Instant.parse("lun. 29 oct. 2012 23:20:49 CET", DateTimeFormat.forPattern("EEE dd MMM yyyy HH:mm:ss ZZZ")));

		EmeseneLogMessage expected =  new EmeseneLogMessage();
		expected.setStrTime("lun. 29 oct. 2012 23:20:49 CET");
		expected.setInstant(Instant.parse("2012-10-29 23:20:49", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")));
		assertEquals(instant, expected.getInstant());
		expected.setName("Caille");
		expected.setText("J'admire : l'interface utilisateur de mon gnome3 (interface graphique pour ubuntu que je teste depuis 1 semaine) est écrite en CSS.\nje suis archi fan !");
		String ligne;
		EmeseneLogMessage actual;
		
		ligne="[lun. 29 oct. 2012 23:20:49 CET] Caille: J'admire : l'interface utilisateur de mon gnome3 (interface graphique pour ubuntu que je teste depuis 1 semaine) est écrite en CSS.\rje suis archi fan !";
		actual = EmeseneLogMessageDataFormat.parse(ligne);
		logger.debug("Expected : "+expected.hashCode());
		logger.debug("Actual   : "+actual.hashCode());
		assertEquals(expected, actual);
		
		ligne="lun. 29 oct. 2012 23:20:49 CET] Caille: J'admire : l'interface utilisateur de mon gnome3 (interface graphique pour ubuntu que je teste depuis 1 semaine) est écrite en CSS.\r\nje suis archi fan !";
		actual = EmeseneLogMessageDataFormat.parse(ligne);
		assertEquals(expected, actual);
		
		
	}
	
	@Test
	public void testParse() {
		
	}

}
