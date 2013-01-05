package fr.rotie.caille.lovelog.dao;

import java.util.Properties;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fr.rotie.caille.lovelog.model.LogMessage;

@Repository(value="logMessageDao")
public class LogMessageDaoImpl  implements LogMessageDao {

	private final Logger log = Logger.getLogger(this.getClass());
    
	@Resource
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    protected SessionFactory sessionFactory;
    
    @Override
	public Session getSession() {
    	Session session;
    	try {
    		session = sessionFactory.getCurrentSession();
		} catch (Exception e) {
			// TODO: handle exception
//	        Configuration configuration = new Configuration(); 
//	        configuration.configure(); 
//			Properties properties = configuration.getProperties();
//			ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(properties).buildServiceRegistry();
//			sessionFactory = configuration.buildSessionFactory(serviceRegistry);
			session = sessionFactory.openSession();
		}
    	return session;
    }
    
	@Override
	@Transactional
	public <T extends LogMessage> T create(T newInstance) {
		Integer id = (Integer) getSession().save(newInstance);
		newInstance.setId(id);
		getSession().flush();
		return newInstance;
	}
	
	

}
