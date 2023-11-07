### Rabbit-cli: RabbitMQ Command Line Client
Rabbit-cli is a command-line interface (CLI) tool built on Spring Boot for interacting with RabbitMQ.      
It provides easy-to-use commands for both publishing and consuming messages from RabbitMQ queues.    

### Setup rabbit-cli
After you have downloaded the tarball file or built it from the source code, follow these steps:     
(Note: replace the `${v}` placeholder with the version of rabbit-cli you have)
```shell
mkdir ${HOME}/.local/share/rabbit-cli
cp rabbit-cli-${v}-tarball.tar.gz ${HOME}/.local/share/rabbit-cli
cd ${HOME}/.local/share/rabbit-cli
tar -xf rabbit-cli-*.tar.gz
# add rabbit-cli.sh to your PATH environment variable 
sudo ln -s ${HOME}/.local/share/rabbit-cli/rabbit-cli-${v}/bin/rabbit-cli.sh /usr/local/bin/rabbit-cli
```
Now, the `rabbit-cli` command should be accessible from your commandline.    

### Usage
Currently, the rabbit-cli command supports two operations:   
- consume
- publish
To subscribe to a queue and consume from it:    
```shell
rabbit-cli consume -q ${queueName}
```
Rabbit-cli also stores the consumed messages in an SQLite database residing in the "data" directory of the rabbit-cli home.

To publish to an exchange with a routingKey:
```shell
rabbit-cli publish -e ${exchangeName} -r ${routingKey} -p ${messagePayload}
```
To publish to the default rabbitMQ direct exchange: 
```shell
rabbit-cli publish -q ${queueName} -p ${messagePayload}
```
### Building from Source    
To build rabbit-cli from the source code, follow these steps:
```shell
git clone https://github.com/sadeq220/rabbit-cli.git   
cd rabbit-cli
mvn clean package
```
The rabbit-cli-${v}-tarball.tar.gz file should be created and reside in the target directory.