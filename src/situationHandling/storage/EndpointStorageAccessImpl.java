package situationHandling.storage;

import java.net.URL;
import java.util.List;
import java.util.UUID;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

class EndpointStorageAccessImpl implements EndpointStorageAccess {

	EndpointStorageAccessImpl() {
	}

	@Override
	public URL getEndpointURL(Situation situation, Operation operation) {
		return null;
	}

	@Override
	public List<Endpoint> getAllEndpoints() {
		return null;
	}

	@Override
	public int addEndpoint(Operation operation, Situation situation, URL endpointURL) {
		Session session = DatabaseSession.factory.openSession();
		
	      Transaction tx = null;
	      Integer endpointID = null;
	      try{
	         tx = session.beginTransaction();
	         Endpoint endpoint = new Endpoint(endpointURL.toString(), situation, operation);
	         endpointID = (Integer) session.save(endpoint); 
	         tx.commit();
	      }catch (HibernateException e) {
	         if (tx!=null) tx.rollback();
	         e.printStackTrace(); 
	      }finally {
	         session.close(); 
	      }
	      return endpointID;
		
	}

	@Override
	public boolean removeEndpoint(UUID endpointID) {
		return false;
	}

	@Override
	public boolean updateEndpoint(UUID endpointID, Situation situation,
			Operation operation, URL endpoint) {
		return false;
	}

}
