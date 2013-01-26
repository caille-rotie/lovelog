package fr.rotie.caille.lovelog.dao;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.apache.camel.component.file.GenericFile;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.joda.time.Days;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fr.rotie.caille.lovelog.model.FileDay;
import fr.rotie.caille.lovelog.model.LogEntity;
import fr.rotie.caille.lovelog.model.LogFile;
import fr.rotie.caille.lovelog.model.LogMessage;

@Repository(value="logMessageDao")
public class LogMessageDaoImpl  implements LogMessageDao {

	/***************************************************************************
	 * Méthodes de DAO (privées
	 ***************************************************************************/

	@SuppressWarnings("unused")
	private final Logger log = Logger.getLogger(this.getClass());
    private final Instant referenceInstant = Instant.parse("2000-01-01T05", DateTimeFormat.forPattern("yyyy-MM-dd'T'HH"));
    private static final String AND = " AND ";
    private static final String WHERE = " WHERE ";
    /**
     * Précision sur la date lors de la recherche des doublons de messages
     */
	private static final int DOUBLON_TIME = 1000*60*5;
	
	@Resource
	private void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
	private SessionFactory sessionFactory;
    
    public Session getSession() {
    	return sessionFactory.getCurrentSession();
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
	
	private <T extends LogMessage> LogFile getLogFile (T logMessage){
		LogFile logfile = logMessage.getLogfile();
		log.info("getLogFile ; logFile : "+ logfile);
		log.info("getLogFile ; logFile : "+ logfile.getId());
		return (LogFile) getSession().get(LogFile.class, logfile.getId());
	}
	
	@SuppressWarnings("unchecked")
	private <T extends LogMessage> FileDay getDay(T logMessage) {
		FileDay day;
		Integer idDate = Days.daysBetween(referenceInstant,logMessage.getInstant()).getDays();
		day = new FileDay();
		day.setIdDay(idDate);
		day.setFile(logMessage.getLogfile());
		logMessage.getLogfile().getFileDays().add(day);
		getSession().update(logMessage.getLogfile());
		return day;
	}
	
//	private LogDay attachLogDay(LogMessage m) {
//		Session session = sessionFactory.openSession();
//		Transaction tx = session.beginTransaction();
//		
//		// calcul du nombre de jours depuis la date de référence
//		Long idDate = new Long (Days.daysBetween(referenceInstant,m.getInstant()).getDays());
//		
//		HashMap<String, Object> params = new HashMap<String, Object>();
//		params.put("idDate", idDate);
//		List<LogDay> list = getList(LogDay.class, params);
//		LogDay day;
//		
//		// Création du jour
//		if (list.size() == 0) {
//			day = new LogDay();
//			day.setIdDate(idDate);
//			List<LogMessage> messages = new ArrayList<LogMessage>();
//			messages.add(m);
//			day.setLogMessages(messages );
//			save(day);
//		}
//		
//		// Mise à jour du jour
//		else {
//			day = list.get(0);
//			List<LogMessage> messages = day.getLogMessages();
//			messages.add(m);
//			day.setLogMessages(messages);
//			update(day);
//		}
//		
//		m.getLogfile().getIdDays().add(day);
//        session.flush();
//        tx.commit();
//		return day;
//	}

	/**
	 * Recherche un doubon à logMessage dans la base, à quelques minutes près.
	 * @param logMessage
	 * @return
	 */
	private <T extends LogMessage> boolean exist(T logMessage) {
		@SuppressWarnings("unchecked")
		List<LogMessage> search =  getSession()
				.createQuery("From LogMessage WHERE name=:name AND text LIKE :text AND instant between :time1 AND :time2")
				.setString("name", logMessage.getName())
				.setString("text", logMessage.getText())
				.setParameter("time1", logMessage.getInstant().minus(Duration.millis(DOUBLON_TIME)))
				.setParameter("time2", logMessage.getInstant().plus(Duration.millis(DOUBLON_TIME)))
				.list();
		if (search.size() == 0) {    		
			return false;
		} else {
			return true;
		}
	}
	
	/***************************************************************************
	 * Méthodes de services (appellées dans les routes et transactionnelles
	 ***************************************************************************/
	
	
	@Override
	@Transactional
	public LogFile getLogfile(GenericFile<File> fileMessage) {
        LogFile logFile;
        String fileName = fileMessage.getFileName();
        int logHash = fileMessage.getBody().hashCode();
        @SuppressWarnings("unchecked")
		List<LogFile> logFiles = getSession()
        		.createQuery("From LogFile where fileName=:fileName and logHash=:logHash ")
        		.setString("fileName", fileName)
        		.setInteger("logHash", logHash)
        		.list();
        if (logFiles.size() == 0) {
        	logFile = new LogFile();
        	logFile.setFileName(fileName);
			logFile.setLogHash(logHash);
			getSession().save(logFile);
			getSession().flush();
        } else {
        	logFile = logFiles.get(0);
        }
        return logFile;
	}
	
	@Override
	@Transactional
	public <T extends LogMessage> T attachLogFile(T logMessage, LogFile logFile) {
		logMessage.setLogfile(logFile);
		return logMessage;
	}
    
	@Override
	@Transactional
	public <T extends LogMessage> T createLogMessage(T logMessage) {
		FileDay day = getDay(logMessage);
		logMessage.setIdDay(day.getIdDay());
		
//		LogFile logFile = getLogFile(logMessage);
//		logFile.getFileDays().add(idDay);
//		getSession().update(logFile);

		// Recherche d'un éventuel doublon de message.
		if (!exist(logMessage)) {
			getSession().save(logMessage);
		}
		return logMessage;
	}
	
	

}
