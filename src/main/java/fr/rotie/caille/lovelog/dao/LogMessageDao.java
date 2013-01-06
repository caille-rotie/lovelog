package fr.rotie.caille.lovelog.dao;

import fr.rotie.caille.lovelog.model.LogMessage;

public interface LogMessageDao {

	<T extends LogMessage> T create(T newInstance);

}
