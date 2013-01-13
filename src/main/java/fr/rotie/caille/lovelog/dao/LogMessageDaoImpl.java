package fr.rotie.caille.lovelog.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.joda.time.Days;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.springframework.stereotype.Repository;

import fr.rotie.caille.lovelog.model.LogDay;
import fr.rotie.caille.lovelog.model.LogEntity;
import fr.rotie.caille.lovelog.model.LogMessage;

@Repository(value="logMessageDao")
public class LogMessageDaoImpl  implements LogMessageDao {

	@SuppressWarnings("unused")
	private final Logger log = Logger.getLogger(this.getClass());
    private final Instant referenceInstant = Instant.parse("2000-01-01T05", DateTimeFormat.forPattern("yyyy-MM-dd'T'HH"));
    static String AND = " AND ";
    static String WHERE = " WHERE ";
	
	@Resource
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    protected SessionFactory sessionFactory;
    
	@Override
	public <T extends LogMessage> T create(T newInstance) {
		save(newInstance);
		return newInstance;
	}

	private <T extends LogEntity> T save(T newInstance) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Long id = (Long) session.save(newInstance);
		newInstance.setId(id);
        session.flush();
        tx.commit();
		return newInstance;
	}

	private <T extends LogEntity> void update(T instance) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		session.update(instance);
        session.flush();
        tx.commit();
	}

	@SuppressWarnings({ "unused", "rawtypes" })
	private <T extends LogEntity> LogEntity get(Class clazz, Long id) {
		Session session = sessionFactory.openSession();
		return (LogEntity) session.get(clazz, id);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private <T extends LogEntity> List<T> getList(Class clazz, HashMap<String, Object> params) {
		Session session = sessionFactory.openSession();
		String queryString = "FROM "+clazz.getName();
		String and = WHERE;
		for (String param : params.keySet()) {
			queryString += and+param+"=:"+getParamName(param);
			and = AND;
		}
		Query q = session.createQuery(queryString);
		for (String param : params.keySet()) {
			q.setParameter(getParamName(param), params.get(param));
		}
		return q.list();
	}
	
	private String getParamName(String param) {
		return "p"+param.hashCode();
	}
	
	public LogDay attachLogDay(LogMessage m) {
		// calcul du nombre de jours depuis la date de référence
		Long idDate = new Long (Days.daysBetween(referenceInstant,m.getInstant()).getDays());
		
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("idDate", idDate);
		List<LogDay> list = getList(LogDay.class, params);
		LogDay day;
		
		// Création du jour
		if (list.size() == 0) {
			day = new LogDay();
			day.setIdDate(idDate);
			List<LogMessage> messages = new ArrayList<LogMessage>();
			messages.add(m);
			day.setLogMessages(messages );
			save(day);
			return day;
		}
		
		// Mise à jour du jour
		else {
			day = list.get(0);
			List<LogMessage> messages = day.getLogMessages();
			messages.add(m);
			day.setLogMessages(messages);
			update(day);
			return day;
		}
	}
	
	

}
