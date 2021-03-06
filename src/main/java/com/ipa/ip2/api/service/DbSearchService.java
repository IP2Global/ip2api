package com.ipa.ip2.api.service;

import com.ipa.ip2.api.db.HibernateUtils;
import com.ipa.ip2.api.exception.APIException;
import com.ipa.ip2.api.model.DbSearch;
import org.hibernate.Session;

/**
 * Created by amit on 17/02/17.
 */
public class DbSearchService {

    /**
     * Return DbSearch based on the specified id
     * @param id
     * @return
     * @throws APIException
     */
    public static DbSearch get(String id) throws APIException{
        DbSearch dbSearch = null;
        Session session = null;
        try {
            session = HibernateUtils.getInstance().getSessionFactory().openSession();
            dbSearch = (DbSearch) session.createQuery("SELECT d FROM DbSearch d WHERE d.id=" + id).uniqueResult();
        } catch(Exception e) {
            throw new APIException(e.getMessage());
        } finally {
            if(session != null){
                session.close();
            }
        }
        return dbSearch;
    }
}
