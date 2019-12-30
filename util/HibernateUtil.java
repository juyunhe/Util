package com.rf.gjframe.base.util;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/** 
 * Hibernate初始化类，用于获取Session、SessionFactory 及关闭Session 
 */  
public class HibernateUtil {  
    // SessionFactory对象  
    private static SessionFactory sessionFactory  = null;  
    // 静态块  
    static {  
        try {  
        	ApplicationContext applicationContext =  new ClassPathXmlApplicationContext("framework-resource-spring.xml"); 
        	
        	sessionFactory = (SessionFactory) applicationContext.getBean("sessionFactory");  
        } catch (HibernateException e) {  
            e.printStackTrace();  
        }  
    }  
    /** 
     * 获取Session对象 
     * @return Session对象 
     */  
    public static Session getSession() {  
        //如果SessionFacroty不为空，则开启Session  
        Session session = (sessionFactory  != null) ? sessionFactory .openSession() : null;  
        return session;  
    }  
    /** 
     * 获取SessionFactory对象 
     * @return SessionFactory对象 
     */  
    public static SessionFactory getSessionFactory() {  
        return sessionFactory;  
    }  
    /** 
     * 关闭Session 
     * @param session对象 
     */  
    public static void closeSession(Session session) {  
        if (session != null) {  
            if (session.isOpen()) {  
                session.close(); // 关闭Session  
            }  
        }  
    }  
}  
