package fr.rotie.caille.lovelog.db;

import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.joda.time.Days;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;

import fr.rotie.caille.lovelog.model.LogDay;
import fr.rotie.caille.lovelog.model.LogFile;
import fr.rotie.caille.lovelog.model.LogMessage;
import fr.rotie.caille.lovelog.model.emesene.EmeseneLogMessage;

public class CreateDB {
	
	private static Logger logger = Logger.getLogger("fr.rotie.caille.lovelog.db.CreateDB");
	
    private static SessionFactory sessionFactory = null; 
    private static ServiceRegistry serviceRegistry = null; 
       
    private static SessionFactory configureSessionFactory() throws HibernateException { 
        Configuration configuration = new Configuration(); 
        configuration.configure(); 
         
        Properties properties = configuration.getProperties();
         
        serviceRegistry = new ServiceRegistryBuilder().applySettings(properties).buildServiceRegistry();         
        sessionFactory = configuration.buildSessionFactory(serviceRegistry); 
         
        return sessionFactory; 
    }
	
//	@Resource
//	private static SessionFactory sessionFactory;
//
//	public static void setSessionFactory(SessionFactory sessionFactory) {
//		CreateDB.sessionFactory = sessionFactory;
//	}

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
        // Configure the session factory
        configureSessionFactory();
         
        Session session = null;
        Transaction tx=null;
         
        try {
            session = sessionFactory.openSession();
            tx = session.beginTransaction();
            
            // Creating Contact entity that will be save to the database
            // LogFile
            LogFile logFile;
            String fileName = "/lovelog/src/test/resources/20120309.log";
            List<LogFile> logFiles = session
            		.createQuery("From LogFile where fileName=:fileName")
            		.setString("fileName", fileName)
            		.list();
            if (logFiles.size() == 0) {
            	logFile = new LogFile();
            	logFile.setFileName(fileName);
            	logFile.setLogHash(fileName.hashCode());
            	session.save(logFile);
            	session.flush();
            } else {
            	logFile = logFiles.get(0);
            }
            
            // LogMessage
            EmeseneLogMessage expected =  new EmeseneLogMessage();
    		String dateTime = "20120309T19:38:33";
    		String name = "Chacha";
    		String text = "Toc toc ?";
			expected.setStrTime(dateTime);
    		expected.setInstant(Instant.parse(dateTime, DateTimeFormat.forPattern("yyyyMMdd'T'HH:mm:ss")));
			expected.setName(name);
			expected.setText(text.trim());
    		
    		// LogDay
    		LogDay day;
    		Instant referenceInstant = Instant.parse("2000-01-01T05", DateTimeFormat.forPattern("yyyy-MM-dd'T'HH"));
    		Long idDate = new Long (Days.daysBetween(referenceInstant,expected.getInstant()).getDays());
    		List<LogDay> days = (List<LogDay>) session.createQuery("From LogDay where idDate=:idDate").setLong("idDate", idDate).list();
    		if (days.size() == 0) {
	    		day = new LogDay();
				day.setIdDate(idDate);
	    	} else {
	    		day = days.get(0);
	    	}
    		expected.setLogDay(day);
    		
    		logFile.getLogDays().add(day);
    		session.update(logFile);
                		
    		// Recherche d'un éventuel doublon de message.
    		List<LogMessage> search =  session
    				.createQuery("From LogMessage WHERE name=:name AND text LIKE :text AND instant between :time1 AND :time2")
    				.setString("name", name)
    				.setString("text", text)
    				.setParameter("time1", expected.getInstant().minus(Duration.millis(1000*60*5)))
    				.setParameter("time2", expected.getInstant().plus(Duration.millis(1000*60*5)))
    				.list();
    		if (search.size() == 0) {    		
    			session.save(expected);
    		}
             
            // Committing the change in the database.
            tx.commit();

    		logger.info("Message : "+expected);
    		logger.info("jours : "+expected.getLogDay());
    		logger.info("Fichier : "+expected.getLogDay().getLogFiles()); // TODO : voir pourquoi au premier enregistrement, on voit pas les fichiers...
             
            // Fetching saved data
            List<LogMessage> contactList = session.createQuery("from LogMessage").list();
             
            for (LogMessage contact : contactList) {
                logger.info(contact);
            }
             
        } catch (Exception ex) {
            ex.printStackTrace();
             
            // Rolling back the changes to make the data consistent in case of any failure
            // in between multiple database write operations.
            tx.rollback();
        } finally{
            if(session != null) {
                session.close();
            }
        }
    }

}
