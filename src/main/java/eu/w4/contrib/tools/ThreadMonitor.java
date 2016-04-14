package eu.w4.contrib.tools;

import static java.lang.management.ManagementFactory.THREAD_MXBEAN_NAME;
import static java.lang.management.ManagementFactory.newPlatformMXBeanProxy;

import java.io.IOException;
import java.lang.management.LockInfo;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;
import java.util.List;

public class ThreadMonitor extends AbstractJmxMonitor
{
  private ThreadMXBean _threadMBean;
  private List<String> _patterns;

  public ThreadMonitor(final String... patterns)
  {
    _patterns = Arrays.asList(patterns);
  }

  @Override
  public void dump() throws IOException
  {
    getWriter().printTitle("Thread Stacks");
    this._threadMBean = newPlatformMXBeanProxy(getMbeanServer(),
                                               THREAD_MXBEAN_NAME,
                                               ThreadMXBean.class);
    dumpThread(_threadMBean.isObjectMonitorUsageSupported(), _threadMBean.isSynchronizerUsageSupported());
  }

  private boolean isThreadSelected(final ThreadInfo threadInfo)
  {
    if (_patterns == null || _patterns.isEmpty())
    {
      return true;
    }
    final StackTraceElement[] stacktrace = threadInfo.getStackTrace();
    for (final StackTraceElement element : stacktrace)
    {
      for (final String pattern : _patterns)
      {
        if (element.getClassName().startsWith(pattern))
        {
          return true;
        }
      }
    }
    return false;
  }

  private void dumpThread(final boolean includeMonitors, final boolean includeLocks)
  {
    final ThreadInfo[] threadInfos = _threadMBean.dumpAllThreads(includeLocks, includeLocks);
    for (final ThreadInfo threadInfo : threadInfos)
    {
      if (isThreadSelected(threadInfo))
      {
        printThreadHeader(threadInfo);
        printStackTrace(threadInfo, includeMonitors);
        if (includeLocks)
        {
          final LockInfo[] syncs = threadInfo.getLockedSynchronizers();
          printLockInfo(syncs);
        }
      }
    }
    getWriter().println();
  }

  private void printThreadHeader(final ThreadInfo threadInfo)
  {
    final StringBuilder sb = new StringBuilder();
    sb.append("Thread #");
    sb.append(threadInfo.getThreadId());
    sb.append(" \"").append(threadInfo.getThreadName()).append("\" ");
    sb.append("in ").append(threadInfo.getThreadState());
    if (threadInfo.isSuspended())
    {
      sb.append(" (suspended)");
    }
    if (threadInfo.isInNative())
    {
      sb.append(" (in native)");
    }
    getWriter().println(sb.toString());
    if (threadInfo.getLockName() != null)
    {
      getWriter().printIndent(1, " locked on [" + threadInfo.getLockName() + "]");
    }
    if (threadInfo.getLockOwnerName() != null)
    {
      getWriter().printIndent(1, " owned by #" + threadInfo.getLockOwnerId() + " \"" + threadInfo.getLockOwnerName() + "\"");
    }
  }

  private void printStackTrace(final ThreadInfo threadInfo, final boolean includeMonitors)
  {
    final StackTraceElement[] stacktrace = threadInfo.getStackTrace();
    final MonitorInfo[] monitors = threadInfo.getLockedMonitors();
    for (int i = 0 ; i < stacktrace.length ; i++)
    {
      final StackTraceElement element = stacktrace[i];
      getWriter().printIndent(1, "at " + element);
      if (includeMonitors)
      {
        for (final MonitorInfo monitor : monitors)
        {
          if (monitor.getLockedStackDepth() == i)
          {
            getWriter().printIndent(2, "- locked " + monitor.getClassName() + "@" + monitor.getIdentityHashCode());
          }
        }
      }
    }
    getWriter().println();
  }

  private void printLockInfo(final LockInfo[] locks)
  {
    if (locks.length > 0)
    {
      getWriter().printIndent(1, "Locked synchronizers:");
      for (final LockInfo lockInfo : locks)
      {
        getWriter().printIndent(1, "- " + lockInfo.getClassName() + "@" + lockInfo.getIdentityHashCode());
      }
      getWriter().println();
    }
  }

}
