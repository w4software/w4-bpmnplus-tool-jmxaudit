package eu.w4.contrib.tools;

import java.io.IOException;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeData;

public class JavaMemoryMonitor extends AbstractJmxMonitor
{
  private static String MBEAN_NAME = "java.lang:type=Memory";

  protected String getMBeanName()
  {
    return MBEAN_NAME;
  }

  protected String getTitle()
  {
    return "Heap Memory";
  }

  @Override
  public void dump()
    throws IOException, ReflectionException, InstanceNotFoundException, MalformedObjectNameException,
    AttributeNotFoundException, MBeanException
  {
    getWriter().printTitle(getTitle());
    final ObjectName memoryConnectionBeanName = new ObjectName(MBEAN_NAME);
    final CompositeData memoryUsage = (CompositeData) getMbeanServer().getAttribute(memoryConnectionBeanName,
                                                                                    "HeapMemoryUsage");
    final long committed = ((long) memoryUsage.get("committed"));
    final long committedMega = committed / 1024 / 1024;
    final long used = (long) memoryUsage.get("used");
    final long usedMega = used / 1024 / 1024;
    final long max = (long) memoryUsage.get("max");
    getWriter().println("Committed: " + committedMega + " MB (" + (100*committed/max) + "% of maximum)");
    getWriter().println("Used: " + usedMega + " MB (" + (100*used/committed) + "% of committed) (" + (100*used/max) + "% of maximum)");
    getWriter().println();
  }

}
