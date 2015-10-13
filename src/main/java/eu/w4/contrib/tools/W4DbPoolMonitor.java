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
  private static String DATABASE_CONNECTION_MBEAN = "eu.w4.engine:instance=default,type=DatabaseConnectionPool,name=Engine";

  protected String getMBeanName()
  {
    return DATABASE_CONNECTION_MBEAN;
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
    final ObjectName databaseConnectionBeanName = new ObjectName(DATABASE_CONNECTION_MBEAN);
    final int numberOfFreeConnections = (int) getMbeanServer().getAttribute(databaseConnectionBeanName,
                                                                            "NumberOfFreeConnections");
    final int poolMaximumSize = (int) getMbeanServer().getAttribute(databaseConnectionBeanName, "PoolMaximumSize");
    final int poolSize = (int) getMbeanServer().getAttribute(databaseConnectionBeanName, "PoolSize");
    final TabularData connectionInfos = (TabularData) getMbeanServer().getAttribute(databaseConnectionBeanName,
                                                                                "ConnectionInfos");
    getWriter().println("Free connections: " + numberOfFreeConnections);
    getWriter().println("Current pool size: " + poolSize);
    getWriter().println("Maximum pool size: " + poolMaximumSize);
    getWriter().println();
    getWriter().printTabularData(connectionInfos, "Connection", "Available");
  }

}
