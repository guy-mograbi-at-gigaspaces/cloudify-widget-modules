PROPERTIES_FILE=/opt/pool-manager-website/manager.properties

if [ ! -f $PROPERTIES_FILE ];then
    echo "you must define $PROPERTIES_FILE"
    exit 1
fi

VM_ARGS="-Dspring.profiles.active=manager-app"
VM_ARGS="$VM_ARGS  -DmanagerPropertiesLocation=file:$PROPERTIES_FILE"


java $VM_ARGS -jar ../website-1.0.0.jar