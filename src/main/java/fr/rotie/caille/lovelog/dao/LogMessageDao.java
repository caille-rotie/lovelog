package fr.rotie.caille.lovelog.dao;

import org.hibernate.Session;

import fr.rotie.caille.lovelog.model.LogMessage;

public interface LogMessageDao {

	Session getSession();

	<T extends LogMessage> T create(T newInstance);

}
