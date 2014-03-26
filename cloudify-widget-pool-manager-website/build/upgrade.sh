

download_pool_manager(){
    URL=http://get.gsdev.info/cloudify-widget-pool-manager-website/1.0.0/cloudify-widget-pool-manager-website-1.0.0.tar

    TAR_FILENAME=cloudify-widget-pool-manager-website-1.0.0.tar
    wget --no-check-certificate "$URL" -O $TAR_FILENAME


    mkdir -p $INSTALL_LOCATION
    tar -xf $TAR_FILENAME -C $INSTALL_LOCATION
    mv $INSTALL_LOCATION/target* $INSTALL_LOCATION/website-1.0.0.jar
}



upgrade_main(){

    source /opt/gsat/gsui_functions.sh
    SYSCONFIG_FILE=pool-manager read_sysconfig

    echo "installing the JAR file"
    download_pool_manager

    echo "installing service script under widget-pool"
    SERVICE_NAME=pool-manager SERVICE_FILE=$INSTALL_LOCATION/build/service.sh install_initd_scriptinstall_service
}

upgrade_main