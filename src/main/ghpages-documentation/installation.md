This document refers to release v${project.version}

GPS Survey is provided as a jar file which can be downloaded
from [here](http://repository.rlinsdale.org.uk/uk/org/rlinsdale/gpssurvey/${project.version}/gpssurvey-${project.version}.jar).
The jar file can be placed anywhere in the filesystem on your RPi machine.

The software requires Java 8 to run, so please ensure that the
Java 8 JRE has been installed, and that it is the default Java
environment on your machine. If you need more assistance on
installing Java 8 on your RPi machine please look
at [this documentation](http://richard-linsdale.github.io/install-java8.html).

To execute the solution the following command line should be given:

    java -cp gpssurveyt-${project.version}.jar <command line options>