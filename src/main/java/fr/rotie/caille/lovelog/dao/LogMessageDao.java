package fr.rotie.caille.lovelog.dao;

import java.io.File;

import org.apache.camel.component.file.GenericFile;

import fr.rotie.caille.lovelog.model.LogDay;
import fr.rotie.caille.lovelog.model.LogFile;
import fr.rotie.caille.lovelog.model.LogMessage;

public interface LogMessageDao {

	LogFile getLogfile(GenericFile<File> fileMessage);
	<T extends LogMessage> T create(T newInstance);
	LogDay attachLogDay(LogMessage m);

}
