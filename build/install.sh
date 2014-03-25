read_sysconfig(){
    SYSCONFIG_FILE=/etc/sysconfig/cwpm
    if [ ! -f $SYSCONFIG_FILE ]; then
        echo "sysconfig file does not exists"
        touch /etc/sysconfig/cwpm
    fi
    if [ -z $INSTALL_LOCATION ]; then
        echo "install location is undefined, setting default"
        echo "INSTALL_LOCATION=/opt/cwpm/cwpm.jar" >> $SYSCONFIG_FILE
    fi
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
    CURRENT_DIRECTORY=`pwd`
    cd "$(dirname "$0")"

    if [ ! -f gsui_functions.sh ]; then

        echo "download gsui_functions.sh"
        wget --no-cache --no-check-certificate -O gsat.tar http://get.gsdev.info/gsat/1.0.0/gsat-1.0.0.tar

        tar -xvf gsat.tar

        source gsui_functions.sh
    fi

    echo "installing widget-pool from workspace `pwd`"

    echo "reading sysconfig"
    read_sysconfig $*

    echo "installing java"
    install_java $*

    echo "installing the JAR file"
    download_jar $*

    echo "installing service script under widget-pool"
    install_service $*

    cd $CURRENT_DIRECTORY

    service widget-pool
}

set -e
main $*
set +e

