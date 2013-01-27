package fr.rotie.caille.lovelog.model.emesene;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.camel.Exchange;
import org.apache.camel.spi.DataFormat;
import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;

public class EmeseneLogMessageDataFormat  implements DataFormat {
	
	private Logger logger = Logger.getLogger(this.getClass());

	@Override
	// TODO Fonction non testée
	public void marshal(Exchange exchange, Object graph, OutputStream stream)
			throws Exception {
		EmeseneLogMessage m = (EmeseneLogMessage) graph;
		String ligne = "["+m.getStrTime()+"] "+m.getName()+": "+m.getText(); 
		stream.write(ligne.getBytes());
		
	}

	@Override
	// TODO Fonction non testée
	public Object unmarshal(Exchange exchange, InputStream stream)
			throws Exception {
		String ligne = exchange.getContext().getTypeConverter().mandatoryConvertTo(String.class, stream);
		EmeseneLogMessage lm = parse(ligne);
		logger.trace(lm);
		return lm;
	}
	
	public static EmeseneLogMessage parse(String ligne) throws Exception{
		
		ligne = ligne.replaceAll("\r\n|\n\r|\r", "\n");
		
		Pattern p = Pattern .compile("\\[?([^\\]]+)\\] ([^:]+): ([^$]*)\\Z");
		Matcher m = p.matcher(ligne);

		EmeseneLogMessage lm = new EmeseneLogMessage();
		
		try  {
			m.find();
			lm.setStrTime(m.group(1));
			lm.setInstant(Instant.parse(lm.getStrTime(), DateTimeFormat.forPattern("EEE dd MMM yyyy HH:mm:ss ZZZ")));
			lm.setName(m.group(2));
			lm.setText(m.group(3));
		}
		catch(Exception e) {
			throw new Exception("Message mal formé : ", e);
		}
		
		return lm;
		
	}

}
