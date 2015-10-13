package eu.w4.contrib.tools;

import java.io.IOException;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.TabularData;

public class W4TransactionMonitor extends AbstractJmxMonitor
{

  private static String PRINCIPALS_TRANSACTION_MBEAN = "eu.w4.engine:instance=default,type=PrincipalsTransactions";

  @Override
  public void dump()
    throws IOException, ReflectionException, InstanceNotFoundException, MalformedObjectNameException,
    AttributeNotFoundException, MBeanException
  {
    getWriter().printTitle("Transaction Principals");
    final ObjectName principalsTransactionBeanName = new ObjectName(PRINCIPALS_TRANSACTION_MBEAN);
    final TabularData infos = (TabularData) getMbeanServer().getAttribute(principalsTransactionBeanName, "Infos");
    getWriter().printTabularData(infos,
                                 "01.Principal",
                                 "02.Connection",
                                 "03.Managed by client",
                                 "04.Principal type",
                                 "05.Date",
                                 "06.Formatted date");
  }
}
