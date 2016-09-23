package eu.w4.contrib.tools;

public class W4ReservedPoolMonitor extends W4DbPoolMonitor
{

  private static String RESERVED_DATABASE_CONNECTION_MBEAN = "eu.w4.engine:instance=%INSTANCE%,type=DatabaseConnectionPool,name=Engine-reserved";

  private String _instanceName;

  public W4ReservedPoolMonitor(final String instanceName)
  {
    super(instanceName);
    _instanceName = instanceName;
  }

  @Override
  protected String getMBeanName()
  {
    return RESERVED_DATABASE_CONNECTION_MBEAN.replaceAll("%INSTANCE%", _instanceName);
  }

  @Override
  protected String getTitle()
  {
    return "Reserved Pool";
  }
}
