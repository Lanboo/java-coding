```
-Djava.awt.headless=true -Dfile.encoding=UTF-8
-server -Xmx3g -Xms3g
-XX:NewRatio=3 -XX:SurvivorRatio=4
-XX:MetaspaceSize=256m -Xss256k
-XX:+DisableExplicitGC
-verbose:gc -XX:+PrintGC -XX:-PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintHeapAtGC -Xloggc:/data/logs/jvm/gc-8080.log
-XX:+UseConcMarkSweepGC

-XX:+CMSParallelRemarkEnabled
-XX:+UseCMSCompactAtFullCollection
-XX:CMSFullGCsBeforeCompaction=5
-XX:+UseCMSInitiatingOccupancyOnly
-XX:CMSInitiatingOccupancyFraction=70

-XX:LargePageSizeInBytes=128m -XX:+UseFastAccessorMethods

-Djava.rmi.server.hostname=127.0.0.1

-Dcom.sun.management.jmxremote
-Dcom.sun.management.jmxremote.port=7080
-Dcom.sun.management.jmxremote.ssl=false
-Dcom.sun.management.jmxremote.authenticate=false

-Ddubbo.registry.check=false
-Ddubbo.reference.check=false

-Ddisconf.env=online
-Ddisconf.conf_server_host=http://disconf.xych.com
-Ddisconf.user_define_download_dir=./myconfig
```
