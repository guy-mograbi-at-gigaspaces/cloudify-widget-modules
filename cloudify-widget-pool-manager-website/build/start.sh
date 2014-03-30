source /etc/sysconfig/widget-pool-manager

if [ -z PROPERTIES_RESOURCE ] || [ "$PROPERTIES_RESOURCE" = "" ];then
    echo "you must define $PROPERTIES_RESOURCE"
    exit 1
fi

VM_ARGS="-Dspring.profiles.active=manager-app"
VM_ARGS="$VM_ARGS  -DmanagerPropertiesLocation=$PROPERTIES_RESOURCE"


java $VM_ARGS -jar ../website-1.0.0.jar