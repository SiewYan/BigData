# Create your docker with spark and mesos library. 


1. [Overview](#overview)
2. [How to create the container](#container-creation)
   * [Install Docker in your host](#install-docker)
   * [Creating a Spark image with Dockerfile](#dockerfile)
   * [Important conf file and script](#conffileandscrpt)
   * [Build the Docker](#BuildDocker)
3. [Use the Spark with Mesos](#usesparkmesos)
4. [Documentation](#documentation)

## Overview

We suppose to have a mesos cluster composed by 1 master and more slaves or 3 master in HA and more slaves. In this documentation we will explain how to create the Dockerfile that will be used by the Spark Driver and Spark Executor. For our example, we will consider that the Docker image should provide the CentOS7 distro along with additional Mesos and spark libraries. So, in a nutshell, the Docker image must have the following features:
 * The version of libmesos should be compatible with the version of the Mesos master and slave. For example, /usr/lib/libmesos-0.26.0.so
 * It should have a valid JDK
 * It should have the Python packages 
 * It should have a version of Spark, we will choose 2.1.0
 * It should have the hadoop libraries.

## How to create the container

### Install Docker in your host

We followed the Docker documentation on installing Docker in CentOS. I suggest that you do the same. In a nutshell, we executed the following:

    $ sudo yum update
    $ sudo tee /etc/yum.repos.d/docker.repo <<-'EOF'
    $ [dockerrepo]
    $ name=Docker Repository
    $ baseurl=https://yum.dockerproject.org/repo/main/centos/$releasever/
    $ enabled=1
    $ gpgcheck=1
    $ gpgkey=https://yum.dockerproject.org/gpg
    $ EOF
    $ sudo yum install docker-engine
    $ sudo service docker start

If the preceding code succeeded, you should be able to do a docker ps as well as a docker search ipython/scipystack successfully.

### Creating a Spark image with Dockerfile

Create the Doker file similar the one in the folder. Rember to START FROM one exixting container (ubuntu, centos, or straldi/mesoshadoopspark) and then add your libraies and so on. The example we committed start from centos7 and mesos will be installed, also hadoop and spark and after that a run script and configuration file will be customized in the container execution.

### Important conf file and script 
Let’s explain some very important files that will be available in the Docker image according to the Dockerfile mentioned earlier:

The spark-conf/spark-env.sh, as mentioned in the Spark docs, will be used to set the location of the Mesos libmesos.so:

    $ export MESOSNATIVEJAVALIBRARY=${MESOSNATIVEJAVALIBRARY:-/usr/lib/libmesos.so}export SPARKLOCALIP=${SPARKLOCALIP:-"127.0.0.1"}export SPARKPUBLICDNS=${SPARKPUBLICDNS:-"127.0.0.1"}

The spark-conf/spark-defaults.conf serves as the definition of the default configuration for our Spark jobs within the container, the contents are as follows:

    $ spark.master  SPARKMASTER
    $ spark.mesos.mesosExecutor.cores   MESOSEXECUTORCORE
    $ spark.mesos.executor.docker.image SPARKIMAGE
    $ spark.mesos.executor.home /opt/spark
    $ spark.driver.host CURRENTIP
    $ spark.executor.extraClassPath /opt/spark/custom/lib/*
    $ spark.driver.extraClassPath   /opt/spark/custom/lib/*

Note that the use of environment variables such as SPARKMASTER and SPARKIMAGE are critical since this will allow us to customize how the Spark application interacts with the Mesos Docker integration.

We have Docker's entry point script. The script, showcased below, will populate the spark-defaults.conf file.

Now, let’s define the Dockerfile entry point such that it lets us define some basic options that will get passed to the Spark command, for example, spark-shell, spark-submit or pyspark (script/RUN.sh)

### Build the Docker

Let’s build the image so we can start using it.

    $ docker build -t mysparkmesoshadoop . && \
    $ docker tag -f mysparkmesoshadoop:latest mysparkmesoshadoop:latest

## Use the Spark with Mesos

    $ docker run -it -e SPARK_MASTER=mesos://10.64.22.79:5050 -e SPARK_IMAGE=straldi/sparkhdfsmesos --name mySparkShell --net host --privileged --pid host straldi/sparkhdfsmesos /opt/spark/bin/spark-shell --master mesos://10.64.22.79:5050

Where 10.64.22.79 is the host ip where master mesos is running. 5050 the default mesos port. straldi/sparkhdfsmesos is the containr we just crerate and upload to docker repository (straldi is my own repository you could create yours). mySparkShell is the name of the app in mesos. This following command /opt/spark/bin/spark-shell --master mesos://10.64.22.79:5050 is the real command executed.

A spark-shell will be opened.

## Documentation

* [Mesos - Spark](https://spark.apache.org/docs/latest/running-on-mesos.html)
* [Docker](https://www.docker.com/)
