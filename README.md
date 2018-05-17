# java-memtest
A demonstration of java memory management.

# to run:
mvn clean install ; java -Xmx1024m -cp target/classes memtest.Main

# to use:
size (mb): <size of object to allocate>
count: <how many objects to allocate>
ttl (sec): <time to leave> (0 for no expiration)

# to monitor
use jvisualvm to see the memory consumption and run GC while the memtest is running. Accomodate jvisualvm with visual gc plugin to visually see eden and old generations.

