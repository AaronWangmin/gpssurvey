[**GPS Survey**](index.html)&nbsp;&nbsp;
[Installation Instructions](install.html)
[**User Documentation**](user.html)&nbsp;&nbsp;
[**Developer Documentation**](developer.html)&nbsp;&nbsp;
[**Release Notes**](release.html)

GPS Survey is provided as a jar file which can be downloaded
[from Maven Central](http://central.maven.org/maven2/uk/theretiredprogrammer/gpssurvey/2.0.0-SNAPSHOT/gpssuvey-2.0.0-SNAPSHOT.jar).
The jar file can be placed anywhere in the filesystem on your RPi machine.

The software requires Java 8 to run, so please ensure that the
Java 8 JRE has been installed, and that it is the default Java
environment on your machine. If you need more assistance on
installing Java 8 on your RPi machine please look
at [this documentation](http://www.theretiredprogrammer.uk/commondocumentation/install-java8.html).

To execute the solution the following command line should be given:

~~~ bash
    java -jar gpssurvey-2.0.0-SNAPSHOT.jar <command line options>
~~~