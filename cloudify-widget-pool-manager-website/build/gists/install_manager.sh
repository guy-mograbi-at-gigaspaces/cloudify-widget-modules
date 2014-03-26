set +e

echo "creating pool-manager-installation folder"
mkdir pool-manager-installation
cd pool-manager-installation


chmod +x install.sh
./install.sh



cd ..
rm -Rf pool-manager-installation



set -e