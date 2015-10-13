package eu.w4.contrib.tools;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;

public class JmxAuditWriter
{
  private static final int INDENT_SIZE = 4;
  private PrintStream _output;

  public JmxAuditWriter()
  {
    this(null);
  }

  public JmxAuditWriter(final PrintStream output)
  {
    if (output == null)
    {
      _output = System.out;
    }
    _output = output;
  }

  public void print(final String message)
  {
    _output.print(message);
  }

  public void print(final char c, final int n)
  {
    _output.print(nchar(c, n));
  }

  public void println()
  {
    _output.println();
  }

  public void println(final String message)
  {
    _output.println(message);
  }

  public void printIndent(final int indentCount, final String message)
  {
    _output.println(nchar(' ', indentCount * INDENT_SIZE) + message);
  }

  public void println(final char c, final int n)
  {
    _output.println(nchar(c, n));
  }

  private String nchar(final char c, final int n)
  {
    final char[] charArray = new char[n];
    Arrays.fill(charArray, c);
    return new String(charArray);
  }

  public void printTitle(final String title)
  {
    println();
    println();
    println(title);
    println(nchar('=', title.length()));
    println();
  }

  private String fixedFormat(final String value, final int width)
  {
    if (value.length() < width)
    {
      final String spaces = nchar(' ', width - value.length());
      return value + spaces;
    }
    else
    {
      return value;
    }
  }

  public void printTabularData(final TabularData tabularData, final String... columns)
  {
    final List<List<String>> table = new ArrayList<List<String>>();
    table.add(Arrays.asList(columns));
    for (final List<?> key : (Set<List<?>>) tabularData.keySet())
    {
      final CompositeData compositeData = tabularData.get(key.toArray());
      final List<String> row = new ArrayList<String>();
      for (final String column : columns)
      {
        final Object data = compositeData.get(column);
        if (data == null)
        {
          row.add("null");
        }
        else
        {
          row.add(data.toString());
        }
      }
      table.add(row);
    }
    printTable(table);
    println("Items: " + (table.size() - 1));
  }

  public void printTable(final List<List<String>> table)
  {
    int columns = 0;
    for (final List<String> row : table)
    {
      columns = Math.max(columns, row.size());
    }

    final int sizes[] = new int[columns];
    for (int i = 0 ; i < columns ; i++)
    {
      for (final List<String> row : table)
      {
        if (row.size() > i)
        {
          sizes[i] = Math.max(sizes[i], row.get(i).length());
        }
      }
    }
    for (final List<String> row : table)
    {
      print(" | ");
      for (int i = 0 ; i < columns ; i++)
      {
        if (row.size() > i)
        {
          print(fixedFormat(row.get(i), sizes[i]));
        }
        else
        {
          print(fixedFormat("", sizes[i]));
        }
        print(" | ");
      }
      println();
    }
  }
}
