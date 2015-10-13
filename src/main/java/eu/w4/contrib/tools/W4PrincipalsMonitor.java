package eu.w4.contrib.tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

public class W4PrincipalsMonitor extends AbstractJmxMonitor
{

  public static final String USERS_PRINCIPALS_MBEAN = "eu.w4.engine:instance=default,type=Users,name=Principals";

  @Override
  public void dump()
    throws IOException, ReflectionException, InstanceNotFoundException, MalformedObjectNameException,
    AttributeNotFoundException, MBeanException
  {
    getWriter().printTitle("Principals");
    final ObjectName usersPrincipalsBeanName = new ObjectName(USERS_PRINCIPALS_MBEAN);
    final Map<String, String> principals = (Map<String, String>) getMbeanServer().getAttribute(usersPrincipalsBeanName,
                                                                                               "Principals");
    final List<List<String>> table = new ArrayList<List<String>>();
    table.add(Arrays.asList("Principal ID", "Username"));
    for (final Map.Entry<String, String> e : principals.entrySet())
    {
      table.add(Arrays.asList(e.getKey(), e.getValue()));
    }
    getWriter().println("Principals");
    getWriter().printTable(table);
    getWriter().println();
  }
}
