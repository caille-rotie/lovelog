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
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;

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

	public static void main(String[] args) {
        // Configure the session factory
        configureSessionFactory();
         
        Session session = null;
        Transaction tx=null;
         
        try {
            session = sessionFactory.openSession();
            tx = session.beginTransaction();
             
            // Creating Contact entity that will be save to the sqlite database
            EmeseneLogMessage expected =  new EmeseneLogMessage();
    		expected.setStrTime("20120309T19:38:33");
    		expected.setInstant(Instant.parse("20120309T19:38:33", DateTimeFormat.forPattern("yyyyMMdd'T'HH:mm:ss")));
    		expected.setName("Chacha");
    		expected.setText("Toc toc ?");
    		logger.info(expected);
    		
             
            // Saving to the database
            session.save(expected);
             
            // Committing the change in the database.
            session.flush();
            tx.commit();
             
            // Fetching saved data
            @SuppressWarnings("unchecked")
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
