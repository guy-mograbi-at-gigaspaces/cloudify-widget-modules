
VM_ARGS="-Dspring.profiles.active=manager-app"
VM_ARGS="$VM_ARGS  -DmanagerPropertiesLocation=../manager.properties"


java -jar ../website-1.0.0.jar $VM_ARGS