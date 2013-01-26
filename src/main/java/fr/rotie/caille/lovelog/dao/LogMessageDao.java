package fr.rotie.caille.lovelog.dao;

import java.io.File;

import org.apache.camel.component.file.GenericFile;

import fr.rotie.caille.lovelog.model.LogFile;
import fr.rotie.caille.lovelog.model.LogMessage;

public interface LogMessageDao {

	LogFile getLogfile(GenericFile<File> fileMessage);
	<T extends LogMessage> T createLogMessage(T newInstance);
	<T extends LogMessage> T attachLogFile(T logMessage, LogFile logFile);

}
