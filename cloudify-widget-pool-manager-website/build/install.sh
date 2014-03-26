main(){

    SYSCONFIG_FILE=/etc/sysconfig/widget-pool-manager
    if [ ! -f SYSCONFIG_FILE ];
        echo "missing file $SYSCONFIG_FILE"
        exit 1
    fi

    CURRENT_DIRECTORY=`pwd`
    cd "$(dirname "$0")"

    if [ ! -f /opt/gsat/gsui_functions.sh ]; then

        mkdir /opt/gsat
        echo "download gsui_functions.sh"
        wget --no-cache --no-check-certificate -O /opt/gsat/gsat.tar http://get.gsdev.info/gsat/1.0.0/gsat-1.0.0.tar
        tar -xvf /opt/gsat/gsat.tar

    fi

    echo "loading gsat functions"
    dos2unix *.sh /opt/gsat/*.sh
    chmod +x *.sh /opt/gsat/*.sh
    source /opt/gsat/gsui_functions.sh
    echo "gsat functions loaded"

    echo "reading sysconfig"
    SYSCONFIG_FILE=pool-manager read_sysconfig
    check_exists INSTALL_LOCATION


    install_java

    install_nginx
    source nginx.conf > /etc/nginx/sites-enabled/widget-pool-manager.conf

    install_mysql

    upgrade

    cd $CURRENT_DIRECTORY

    service widget-pool
}

set -e
main $*
set +e

