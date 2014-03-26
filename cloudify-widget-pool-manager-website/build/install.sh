# use this script by running
# wget --no-cache --no-check-certificate -O - http://get.gsdev.info/cloudify-widget-pool-manager-website/1.0.0/install.sh | dos2unix | bash

install_main(){

    SYSCONFIG_FILE=/etc/sysconfig/widget-pool-manager
    if [ ! -f $SYSCONFIG_FILE ]; then
        echo "missing file $SYSCONFIG_FILE"
        exit 1
    fi

    CURRENT_DIRECTORY=`pwd`
    cd "$(dirname "$0")"

    eval "`wget --no-cache --no-check-certificate -O - http://get.gsdev.info/gsat/1.0.0/install_gsat.sh | dos2unix`"


    echo "reading sysconfig"
    SYSCONFIG_FILE=pool-manager read_sysconfig
    check_exists INSTALL_LOCATION


    install_java

    install_nginx
    source nginx.conf > /etc/nginx/sites-enabled/widget-pool-manager.conf

    install_mysql

    upgrade_main

    cd $CURRENT_DIRECTORY

    service widget-pool
}

download_pool_manager(){
    URL=http://get.gsdev.info/cloudify-widget-pool-manager-website/1.0.0/cloudify-widget-pool-manager-website-1.0.0.tar

    TAR_FILENAME=cloudify-widget-pool-manager-website-1.0.0.tar
    wget --no-check-certificate "$URL" -O $TAR_FILENAME


    mkdir -p $INSTALL_LOCATION
    tar -xf $TAR_FILENAME -C $INSTALL_LOCATION
    mv $INSTALL_LOCATION/target* $INSTALL_LOCATION/website-1.0.0.jar
}



upgrade_main(){

    eval "`wget --no-cache --no-check-certificate -O - http://get.gsdev.info/gsat/1.0.0/install_gsat.sh | dos2unix`"
    SYSCONFIG_FILE=pool-manager read_sysconfig

    echo "installing the JAR file"
    download_pool_manager

    echo "installing service script under widget-pool"
    SERVICE_NAME=pool-manager SERVICE_FILE=$INSTALL_LOCATION/build/service.sh install_initd_script
}


set -e
if [ "$1" = "upgrade" ];then
    echo "running upgrade"
    upgrade_main $*
else
    echo "running install"
    install_main $*
fi
set +e

