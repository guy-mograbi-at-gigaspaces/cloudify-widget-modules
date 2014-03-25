

install_java(){
    echo "installing java"

    CURRENT_DIRECTORY=`pwd`

    cd "$(dirname "$0")"

    INSTALL_JAVA_DIR=/usr/lib/jvm
    mkdir -p $INSTALL_JAVA_DIR
    cd $INSTALL_JAVA_DIR
    wget -O jdk.bin --no-check-certificate --no-cookies --header "Cookie: gpw_e24=http%3A%2F%2Fwww.oracle.com%2Ftechnetwork%2Fjava%2Fjavase%2Fdownloads%2Fjdk6-downloads-1637591.html;" http://download.oracle.com/otn-pub/java/jdk/6u33-b03/jdk-6u33-linux-x64.bin
    chmod 755 jdk.bin
    echo "yes" | ./jdk.bin &>/dev/null
    export JAVA_HOME=/usr/lib/jvm/jdk1.6.0_33
    echo "JAVA_HOME is $JAVA_HOME"
    rm -f jdk.bin
    ln -Tfs $JAVA_HOME/bin/java /usr/bin/java
    ln -Tfs $JAVA_HOME/bin/javac /usr/bin/javac

    cd $CURRENT_DIRECTORY
}

read_sysconfig(){
    sysconfig_file = /etc/sysconfig/cwpm
    if [ ! -f $sysconfig_file ]; then
        echo "sysconfig file does not exists"
        mkdir -p /etc/sysconfig
        touch /etc/sysconfig/cwpm
    fi
    if [ -z $INSTALL_LOCATION ]; then
        echo "install location is undefined, setting default"
        echo "INSTALL_LOCATION=/opt/cwpm/cwpm.jar" >> $sysconfig_file
}

download_jar(){
    if [ "$1" = "latest" ];then
        URL=http://get.gsdev.info/cloudify-widget-pool-manager-website/1.0.0/website-1.0.0-LATEST.jar
    else
        URL=http://get.gsdev.info/cloudify-widget-pool-manager-website/1.0.0/website-1.0.0-SNAPSHOT.jar
    fi

    wget --no-check-certificate "$URL" -O /tmp/cwpm.jar
}

install_service(){
    wget --no-check-certificate "https://raw.githubusercontent.com/guy-mograbi-at-gigaspaces/cloudify-widget-modules/manager-website/build/service.sh" -O /etc/init.d/widget-pool
    chmod +x /etc/init.d/widget-pool
}



main(){
    echo "installing widget-pool"

    echo "reading sysconfig"
    read_sysconfig $*

    echo "installing java"
    install_java $*

    echo "installing the JAR file"
    download_jar $*

    echo "installing service script under widget-pool"
    install_service $*

    service widget-pool

}