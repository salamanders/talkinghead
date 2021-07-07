# Kid Bot
LEGO BrickPi3 Kotlin on ev3dev

## Dev Notes

Make sure your bot is up to date:
    
1. [Fresh buster rpi2 image](https://oss.jfrog.org/list/oss-snapshot-local/org/ev3dev/brickstrap/)
2. All necessary config.txt changes
3. `touch ssh`
4. Plug into ethernet then [configure wifi](https://www.ev3dev.org/docs/tutorials/setting-up-wifi-using-the-command-line/)


    ssh robot@ev3dev  # pw: maker
    sudo apt-get update && sudo apt-get dist-upgrade -y
    sudo update-brickpi3-fw
    # check your python and java version
    # https://github.com/ev3dev-lang-java/installer
    sudo java -Xshare:dump
    
Stay up to date every few months:

    mvn versions:display-dependency-updates
    
To copy over all the jars if you are going to be doing a lot of iterate-deploy-test loops:   
 
    mvn dependency:copy-dependencies
    scp ./target/dependency/* robot@ev3dev:/home/robot/lib

Which will allow the pom's `assembly.skipAssembly` to only build the jar, without dependencies. (much faster!)
Then for each run:

    mvn deploy antrun:run
    
    
But if not everything exists locally:

    mvn org.apache.maven.plugins:maven-install-plugin:2.5.2:install-file \
    -Dfile=/Users/benhill/Desktop/workspace/thirdparty/caliko/caliko/target/caliko-1.3.8.jar

# TODO

-[ ] BrickPi3 needs to set the port to ev3-uart mode then write the driver name to set_device
-[ ] https://github.com/salamanders/waller/blob/master/src/main/kotlin/info/benjaminhill/waller/Waller.kt
-[ ] State botState loop independent of action loop
-[ ] Tacho to Gyro calibration to turn precise degrees.
-[ ] https://github.com/ev3dev-lang-java/lejos-navigation

# History

* [WatchService](https://docs.oracle.com/javase/7/docs/api/java/nio/file/WatchService.html#poll()) 
doesn't work for motor position updates.