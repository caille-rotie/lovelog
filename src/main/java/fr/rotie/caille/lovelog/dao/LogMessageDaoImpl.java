package fr.rotie.caille.lovelog.dao;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

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
	public <T extends LogMessage> T create(T newInstance) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Integer id = (Integer) session.save(newInstance);
		newInstance.setId(id);
        session.flush();
        tx.commit();
		return newInstance;
	}
	
	

}
