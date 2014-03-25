set +e

echo "creating pool-manager-installation folder"
mkdir pool-manager-installation
cd pool-manager-installation

wget --no-check-certificate "https://raw.githubusercontent.com/guy-mograbi-at-gigaspaces/cloudify-widget-modules/manager-website/build/install.sh" -O install.sh
chmod +x install.sh
./install.sh


cd ..
rm -Rf pool-manager-installation



set -e