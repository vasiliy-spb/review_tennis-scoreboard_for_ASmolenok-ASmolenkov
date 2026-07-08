package io.github.asmolenkov.tennismatchscoreboard.utils;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

@Slf4j
@UtilityClass
public class HibernateUtils {
    private static final String INITIAL_SESSION_FAILED_TEMPLATE = "Initial SessionFactory creation failed: {}";
    @Getter
    private static SessionFactory sessionFactory;



    static {
        try {
            sessionFactory = new Configuration().configure().buildSessionFactory();
        }catch (Throwable ex){
            log.error(INITIAL_SESSION_FAILED_TEMPLATE, ex.getMessage(), ex);
        }
    }

    public static Session getSession(){
        return sessionFactory.getCurrentSession();
    }

}
