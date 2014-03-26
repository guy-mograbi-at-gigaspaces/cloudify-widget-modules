read_sysconfig(){



    if [ ! -f $SYSCONFIG_FILE ]; then
        echo "sysconfig file does not exists"
        touch /etc/sysconfig/cwpm
    fi
    if [ -z $INSTALL_LOCATION ]; then
        echo "install location is undefined, setting default"
        echo "INSTALL_LOCATION=/opt/cwpm/cwpm.jar" >> $SYSCONFIG_FILE
    fi
}

download_pool_manager(){
    URL=http://get.gsdev.info/cloudify-widget-pool-manager-website/1.0.0/cloudify-widget-pool-manager-website-1.0.0.tar

    TAR_FILENAME=cloudify-widget-pool-manager-website-1.0.0.tar
    wget --no-check-certificate "$URL" -O $TAR_FILENAME

    OPT_LOCATION=/opt/cloudify-widget-pool-manager
    mkdir -p $OPT_LOCATION
    tar -xf $TAR_FILENAME -C $OPT_LOCATION
    mv $OPT_LOCATION/target* $OPT_LOCATION/website-1.0.0.jar
}

install_service(){
    SERVICE_NAME=widget-pool
    INITD_LOCATION=/etc/init.d/$SERVICE_NAME
    SERVICE_FILE=$OPT_LOCATION/build/service.sh

    cp $SERVICE_FILE $INITD_LOCATION
    chmod +x $INITD_LOCATION
}







main(){

    SYSCONFIG_FILE=/etc/sysconfig/widget-pool-manager
    if [ ! -f SYSCONFIG_FILE ];
        echo "missing file $SYSCONFIG_FILE"
        exit 1
    fi

    CURRENT_DIRECTORY=`pwd`
    cd "$(dirname "$0")"

    if [ ! -f gsui_functions.sh ]; then

        echo "download gsui_functions.sh"
        wget --no-cache --no-check-certificate -O gsat.tar http://get.gsdev.info/gsat/1.0.0/gsat-1.0.0.tar

        tar -xvf gsat.tar


    fi

    echo "loading gsat functions"
    dos2unix *.sh
    chmod +x *.sh
    source ./gsui_functions.sh
    echo "gsat functions loaded"

    echo "reading sysconfig"
    read_sysconfig $*


    install_java

    install_nginx

    install_mysql

    echo "installing the JAR file"
    download_pool_manager $*

    echo "installing service script under widget-pool"
    install_service $*

    cd $CURRENT_DIRECTORY

    service widget-pool
}

set -e
main $*
set +e

