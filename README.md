w4-bpmnplus-tool-jmxaudit
=========================

This tools allow to periodically log main JMX infos from a W4 BPMN+ Server.

Logged data contains informations like database pools status, currently opened
sessions, client transactions, thread stacks.


Download
--------

The binary package is available from the [release page](https://github.com/w4software/w4-bpmnplus-tool-jmxaudit/releases)


Installation
------------

### Requirements

Java JRE 1.6 is required to run this tool.


### Extraction

Extract the package, from either format (zip or tar.gz)


Usage
-----

Use the following command to run the utility

    java -jar bpmnplus-tool-jmxaudit-1.0.jar [options]

### Accepted options

    -s | --server   set BPMN+ Engine hostname           (default 'localhost')
    -p | --port     set BPMN+ Engine RMI port           (default '7707')
    -l | --login    set BPMN+ Engine login              (default 'admin')
    -w | --password set BPMN+ Engine password           (default 'admin')
    -f | --file     switch output to given file         (stdout if not specified)
    -d | --delay    set delay in ms between two reports (default '5000')


### Switches

Following switches can be enabled/disabled using either + or - prefixes

    +/-mpool        audit database pool usage          (default: enabled)
    +/-mprincipal   audit session principals           (default: enabled)
    +/-mtransaction audit transaction principals       (default: enabled)
    +/-mthread      audit threads (with w4 code only)  (default: disabled)
    +/-mallthread   audit *all* threads                (default: disabled)
    +/-mheap        audit heap memory                  (default: disabled)


### Examples

Audit pools, principals and threads using JMX on w4host:7707 every 60 seconds

    java -jar bpmnplus-tool-jmxaudit-1.0.jar -s w4host -d 60000 +mthread -mtransaction


### Example output

On the desired periodicity, jmxaudit tool will issue traces like the following ones
on stdout or in a log file.

    Database Connections
    ====================

    Free connections: 4
    Current pool size: 4
    Maximum pool size: 20

     | Connection                       | Available | 
     | eu.w4.engine.core.sql.q@164f0719 | true      | 
     | eu.w4.engine.core.sql.q@3f87e1a3 | true      | 
     | eu.w4.engine.core.sql.q@13d17b9  | true      | 
     | eu.w4.engine.core.sql.q@441b1664 | true      | 
    Items: 4


    Reserved Pool
    =============

    Free connections: 4
    Current pool size: 4
    Maximum pool size: 20

     | Connection                       | Available | 
     | eu.w4.engine.core.sql.q@164f0719 | true      | 
     | eu.w4.engine.core.sql.q@3f87e1a3 | true      | 
     | eu.w4.engine.core.sql.q@13d17b9  | true      | 
     | eu.w4.engine.core.sql.q@441b1664 | true      | 
    Items: 4


    Principals
    ==========

    Principals
     | Principal ID                         | Username | 
     | 4c95e0d4-0b3e-4751-ae44-d3cb04e72fdf | admin    | 
     | 6089575e-9abd-4d22-8d57-8c99e09005ff | admin    | 
     | 69f60d8b-1c06-43ab-8d46-ce4f4f7c26d7 | admin    | 
     | f81878f4-9c07-4947-afce-9cf1eb1a9210 | admin    | 



License
-------

Copyright (c) 2015-2016, W4 S.A. 

This project is licensed under the terms of the MIT License (see LICENSE file)

Ce projet est licencié sous les termes de la licence MIT (voir le fichier LICENSE)