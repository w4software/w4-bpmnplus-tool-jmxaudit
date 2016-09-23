package eu.w4.contrib.tools;

import java.io.IOException;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.TabularData;

public class W4DbPoolMonitor extends AbstractJmxMonitor
{
  private static String DATABASE_CONNECTION_MBEAN = "eu.w4.engine:instance=%INSTANCE%,type=DatabaseConnectionPool,name=Engine";

  private String _instanceName;

  public W4DbPoolMonitor(final String instanceName)
  {
    _instanceName = instanceName;
  }

  protected String getMBeanName()
  {
    return DATABASE_CONNECTION_MBEAN.replaceAll("%INSTANCE%", _instanceName);
  }

  protected String getTitle()
  {
    return "Database Connections";
  }

  @Override
  public void dump()
    throws IOException, ReflectionException, InstanceNotFoundException, MalformedObjectNameException,
    AttributeNotFoundException, MBeanException
  {
    getWriter().printTitle(getTitle());
    final ObjectName databaseConnectionBeanName = new ObjectName(getMBeanName());
    final int numberOfFreeConnections = (Integer) getMbeanServer().getAttribute(databaseConnectionBeanName,
                                                                            "NumberOfFreeConnections");
    final int poolMaximumSize = (Integer) getMbeanServer().getAttribute(databaseConnectionBeanName, "PoolMaximumSize");
    final int poolSize = (Integer) getMbeanServer().getAttribute(databaseConnectionBeanName, "PoolSize");
    final TabularData connectionInfos = (TabularData) getMbeanServer().getAttribute(databaseConnectionBeanName,
                                                                                "ConnectionInfos");
    getWriter().println("Free connections: " + numberOfFreeConnections);
    getWriter().println("Current pool size: " + poolSize);
    getWriter().println("Maximum pool size: " + poolMaximumSize);
    getWriter().println();
    getWriter().printTabularData(connectionInfos, "Connection", "Available");
  }

}
