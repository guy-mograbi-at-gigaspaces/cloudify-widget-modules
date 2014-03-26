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
    SYSCONFIG_FILE=widget-pool-manager read_sysconfig
    check_exists INSTALL_LOCATION


    install_java

    install_mysql

    if [ ! -f INSTALL_LOCATION/buid/upgrade.sh ];then
        download_pool_manager
    else
        echo "manager already installed. skipping. "
    fi

    cd $INSTALL_LOCATION/build
    upgrade_main

    cd $CURRENT_DIRECTORY

    service widget-pool
}

download_pool_manager(){
    echo "downloading pool manager"
    URL=http://get.gsdev.info/cloudify-widget-pool-manager-website/1.0.0/cloudify-widget-pool-manager-website-1.0.0.tar

    TAR_FILENAME=cloudify-widget-pool-manager-website-1.0.0.tar
    wget --no-check-certificate "$URL" -O $TAR_FILENAME


    mkdir -p $INSTALL_LOCATION
    tar -xf $TAR_FILENAME -C $INSTALL_LOCATION
    mv $INSTALL_LOCATION/target* $INSTALL_LOCATION/website-1.0.0.jar
    chmod +x $INSTALL_LOCATION/**/*.sh
}



upgrade_main(){
    echo "installing gsat"
    eval "`wget --no-cache --no-check-certificate -O - http://get.gsdev.info/gsat/1.0.0/install_gsat.sh | dos2unix`"
    SYSCONFIG_FILE=widget-pool-manager read_sysconfig

    echo "installing the JAR file"
    download_pool_manager

    dos2unix nginx.conf
    source nginx.conf | dos2unix > /etc/nginx/sites-enabled/widget-pool-manager.conf

    echo "installing service script under widget-pool"
    SERVICE_NAME=widget-pool SERVICE_FILE=$INSTALL_LOCATION/build/service.sh install_initd_script
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

