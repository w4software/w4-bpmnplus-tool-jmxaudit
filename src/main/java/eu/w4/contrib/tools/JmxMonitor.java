package eu.w4.contrib.tools;

import java.io.IOException;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ReflectionException;

public interface JmxMonitor
{
  void dump()
    throws IOException,
           ReflectionException,
           InstanceNotFoundException,
           MalformedObjectNameException,
           AttributeNotFoundException,
           MBeanException;

  void setMbeanServer(final MBeanServerConnection mbeanServer);
  void setWriter(final JmxAuditWriter writer);
}
