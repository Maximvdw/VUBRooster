# VUBRooster Daemon
This daemon is a J2EE (Java Enterprise) application that will scrape all faculties, student groups and timetables.
It should be run in a JBOSS container such as Wildfly (9/10).

The following instructions will provide you more detail on how to get started
## Getting started
First you will need the following
1. JBOSS server (Tested on Wildfly 9 and 10)
2. Database to add to the JBOSS server (Tested with MySQL)

For these instructions we will install Wildfly 10 and LAMP
on an Ubuntu server
### Installing Wildfly 10
Instructions on how to install Wildfly can be found here:
https://gesker.wordpress.com/2016/02/09/wildfly-10-on-ubuntu-15-10/

After installing you will have to
1. Add a new management account (https://docs.jboss.org/author/display/WFLY8/add-user+utility)
2. Make sure port 9990 is open for easy deployment (or VPN)
3. Add the MySQL driver (http://giordanomaestro.blogspot.be/2015/02/install-jdbc-driver-on-wildfly.html)
### Configuring Wildfly 10
The Daemon will check for a **JTA** database with data source `java:/vubrooster`
You can use whatever SQL database for this, but it is tested for MySQL.

To add this data source go to the configuration panel
`http://yoururl.com:9990/`
Navigate to `Configuration > Subsystems > Datasources > Non-XA`
and click *Add datasource*
