package comp6231.a1.users;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Logger;

import comp6231.a1.common.LoggerHelper;
import comp6231.a1.common.users.AdminOperations;
import comp6231.a1.common.users.CampusUser;
import comp6231.a1.common.users.StudentOperations;

public abstract class ClientUserFactory {
	static
	{
		System.setProperty("java.security.policy", "file:./src/comp6231/security.policy");
		if (System.getSecurityManager() == null)
			System.setSecurityManager(new SecurityManager());		
	}
	
	public static StudentClient createStudentClient(CampusUser user) throws SecurityException, IOException, NotBoundException
	{
		if (!user.isStudent())
			return null;
		Logger logger = LoggerHelper.getCampusUserLogger(user);
		Registry registry = LocateRegistry.getRegistry();
		StudentOperations remote_stub = (StudentOperations)registry.lookup(user.getCampus());
		return new StudentClient(user, logger, remote_stub);
	}
	
	public static AdminClient createAdminClient(CampusUser user) throws SecurityException, IOException, NotBoundException
	{
		if (!user.isAdmin())
			return null;
		Logger logger = LoggerHelper.getCampusUserLogger(user);
		Registry registry = LocateRegistry.getRegistry();
		AdminOperations remote_stub = (AdminOperations)registry.lookup(user.getCampus());
		return new AdminClient(user, logger, remote_stub);
	}
}
