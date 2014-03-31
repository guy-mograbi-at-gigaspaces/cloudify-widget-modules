Description
===========


A REST service that exposes pool management

Admin user can create new accounts.

Accounts can define pools.

Installation
============

The build is available at [http://get.gsdev.info/cloudify-widget-pool-manager-website/]

we are overriding existing builds, so when you point to a build, you might get

a different build each time.


## Installation Steps

 * Create a [manager.properties file](./build/website-context.example.xml) and put it 
 * Create an executable file /etc/sysconfig/widget-pool-manager
 * Have a look at [example file](./build/SYSCONF_EXAMPLE)
 * You can download the install file from get.gsdev.info, or you can run this one-liner to download it and execute it

````

yum -y install dos2unix && wget --no-cache --no-check-certificate -O - http://get.gsdev.info/cloudify-widget-pool-manager-website/1.0.0/install.sh | dos2unix | bash

````


API
====

TBD
