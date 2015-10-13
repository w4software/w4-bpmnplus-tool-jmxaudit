package eu.w4.contrib.tools;

public class W4ReservedPoolMonitor extends W4DbPoolMonitor
{

  private static String RESERVED_DATABASE_CONNECTION_MBEAN = "eu.w4.engine:instance=default,type=DatabaseConnectionPool,name=Engine-reserved";

  @Override
  protected String getMBeanName()
  {
    return RESERVED_DATABASE_CONNECTION_MBEAN;
  }

  @Override
  protected String getTitle()
  {
    return "Reserved Pool";
  }
}
