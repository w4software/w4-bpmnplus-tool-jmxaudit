package eu.w4.contrib.tools;

import javax.management.MBeanServerConnection;

public abstract class AbstractJmxMonitor implements JmxMonitor
{
  private MBeanServerConnection _mbeanServer;
  private JmxAuditWriter _writer;

  public MBeanServerConnection getMbeanServer()
  {
    return _mbeanServer;
  }

  public void setMbeanServer(final MBeanServerConnection mbeanServer)
  {
    _mbeanServer = mbeanServer;
  }

  public JmxAuditWriter getWriter()
  {
    return _writer;
  }

  public void setWriter(final JmxAuditWriter writer)
  {
    _writer = writer;
  }

}
