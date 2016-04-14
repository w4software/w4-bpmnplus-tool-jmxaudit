package eu.w4.contrib.tools;

import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class JmxAuditLogger
{
  private static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  private String _host;

  private String _port;

  private String _login;

  private String _password;

  private MBeanServerConnection _mbeanServer;

  private JMXConnector _jmx;

  private JmxAuditWriter _output;

  private List<JmxMonitor> _monitors;

  public JmxAuditLogger(final String host, final String port, final String login, final String password)
  {
    _host = host;
    _port = port;
    _login = login;
    _password = password;
    _monitors = new ArrayList<JmxMonitor>();
    _output = new JmxAuditWriter(System.err);
    connect();
  }

  public void addMonitor(final JmxMonitor monitor)
  {
    monitor.setWriter(_output);
    monitor.setMbeanServer(_mbeanServer);
    _monitors.add(monitor);
  }

  private void connect()
  {
    try
    {
      final JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + _host + ":" + _port + "/jmxrmi");
      final Map<String, Object> jmxEnvironment = new HashMap<String, Object>();
      final String[] credentials = { _login, _password };
      jmxEnvironment.put(JMXConnector.CREDENTIALS, credentials);
      _jmx = JMXConnectorFactory.connect(url, jmxEnvironment);
      _mbeanServer = _jmx.getMBeanServerConnection();

      for (final JmxMonitor monitor : _monitors)
      {
        monitor.setMbeanServer(_mbeanServer);
      }
    }
    catch (final MalformedURLException e)
    {
    }
    catch (final IOException e)
    {
      _output.println("Communication error: " + e.getClass().getName() + " " + e.getMessage());
    }
  }

  public void startLog()
  {
    _output.println('-', 120);
    _output.println("Time: " + DATE_FORMAT.format(new Date()));
  }


  public void dump(final JmxMonitor monitor) throws IOException
  {
    try
    {
      monitor.dump();
    }
    catch (InstanceNotFoundException | MalformedObjectNameException | AttributeNotFoundException
      | ReflectionException | MBeanException e)
    {
      _output.println("Could not log [" + monitor.getClass().getSimpleName() + "] because of " + e.getClass() + " " + e.getMessage());
    }
  }

  public void log()
  {
    try
    {
      _output.println();
      _output.println();
      startLog();

      for (final JmxMonitor monitor : _monitors)
      {
        dump(monitor);
        _output.println();
        _output.println();
      }
    }
    catch (java.rmi.ConnectException | java.net.ConnectException e)
    {
      _output.println("Connection lost... trying to reconnect");
      connect();
    }
    catch (final IOException e)
    {
      _output.println("Could not log because of " + e.getClass() + " " + e.getMessage());
    }
  }

  public void setOutput(final PrintStream output)
  {
    _output = new JmxAuditWriter(output);
    for (final JmxMonitor monitor : _monitors)
    {
      monitor.setWriter(_output);
    }
  }

  public static void help()
  {
    System.out.println("This tool allows to output regular reports of main JMX informations");
    System.out.println("of W4 BPMN+ Engine");
    System.out.println();
    System.out.println("Accepted options");
    System.out.println();
    System.out.println("-s | --server   set BPMN+ Engine hostname           (default 'localhost')");
    System.out.println("-p | --port     set BPMN+ Engine RMI port           (default '7707')");
    System.out.println("-l | --login    set BPMN+ Engine login              (default 'admin')");
    System.out.println("-w | --password set BPMN+ Engine password           (default 'admin')");
    System.out.println("-f | --file     switch output to given file         (stdout if not specified)");
    System.out.println("-d | --delay    set delay in ms between two reports (default '5000')");
    System.out.println();
    System.out.println("Following switches can be enabled/disabled using either + or - prefixes");
    System.out.println();
    System.out.println("+/-mpool        audit database pool usage    (default: enabled)");
    System.out.println("+/-mprincipal   audit session principals     (default: enabled)");
    System.out.println("+/-mtransaction audit transaction principals (default: enabled)");
    System.out.println("+/-mthread      audit threads                (default: disabled)");
    System.out.println();
    System.out.println("Example");
    System.out.println();
    System.out.println("Audit pools, principals and threads using JMX on w4host:7707 every 60 seconds");
    System.out.println("-s w4host -d 60000 +mthread -mtransaction");
    System.exit(1);
  }

  public static void main(final String[] args)
    throws Exception
  {
    String host = "localhost";
    String port = "7707";
    String login = "admin";
    String password = "admin";
    String delay = "5000";
    String file = null;
    final Set<String> audits = new HashSet<String>(Arrays.asList("pool", "principal", "transaction"));
    final LinkedList<String> argList = new LinkedList<String>(Arrays.asList(args));
    while(!argList.isEmpty())
    {
      final String arg = argList.poll().toLowerCase();
      if ("-h".equals(arg) || "--help".equals(arg))
      {
        help();
      }
      else if ("-s".equals(arg) || "--server".equals(arg) || "--host".equals(arg))
      {
        host = argList.poll();
      }
      else if ("-p".equals(arg) || "--port".equals(arg))
      {
        port = argList.poll();
      }
      else if ("-l".equals(arg) || "--login".equals(arg))
      {
        login = argList.poll();
      }
      else if ("-w".equals(arg) || "--password".equals(arg))
      {
        password = argList.poll();
      }
      else if ("-d".equals(arg) || "--delay".equals(arg))
      {
        delay = argList.poll();
      }
      else if ("-f".equals(arg) || "--file".equals(arg))
      {
        file = argList.poll();
      }
      else if (arg.startsWith("+m"))
      {
        audits.add(arg.substring(2));
      }
      else if (arg.startsWith("-m"))
      {
        audits.remove(arg.substring(2));
      }
      else
      {
        System.err.println("Unrecognized option: " + arg);
        help();
      }
    }

    final JmxAuditLogger jmxAuditLogger = new JmxAuditLogger(host, port, login, password);
    if (file == null || "".equals(file) || "-".equals(file))
    {
      jmxAuditLogger.setOutput(System.out);
    }
    else
    {
      jmxAuditLogger.setOutput(new PrintStream(file));
    }

    if (audits.contains("pool"))
    {
      jmxAuditLogger.addMonitor(new W4DbPoolMonitor());
      jmxAuditLogger.addMonitor(new W4ReservedPoolMonitor());
    }

    if (audits.contains("transaction"))
    {
      jmxAuditLogger.addMonitor(new W4TransactionMonitor());
    }

    if (audits.contains("principal"))
    {
      jmxAuditLogger.addMonitor(new W4PrincipalsMonitor());
    }

    if (audits.contains("thread"))
    {
      jmxAuditLogger.addMonitor(new ThreadMonitor("eu.w4.", "leon"));
    }

    if (audits.contains("heap"))
    {
      jmxAuditLogger.addMonitor(new JavaMemoryMonitor());
    }

    final Timer timer = new Timer();
    timer.schedule(new TimerTask()
    {
      @Override
      public void run()
      {
        jmxAuditLogger.log();
      }
    }, 0, Integer.parseInt(delay));
  }
}
