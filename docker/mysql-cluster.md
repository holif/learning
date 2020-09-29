# 使用Docker搭建MySQL主从复制集群
### 拉取并启动MySQL镜像
```
docker pull mysql
```
### 运行MySQL实例,并设置密码123456
```
docker run -p 3307:3306 --name mysql-master -e MYSQL_ROOT_PASSWORD=123456 -d mysql
docker run -p 3308:3306 --name mysql-slave1 -e MYSQL_ROOT_PASSWORD=123456 -d mysql
docker run -p 3309:3306 --name mysql-slave2 -e MYSQL_ROOT_PASSWORD=123456 -d mysql
```
### navicat连接不上,可进入容器,进入mysql查看root插件类型,修改plugin为mysql_native_password
```
select user,host,plugin,authentication_string from user;
```
### 修改plugin为mysql_native_password
```
ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY '123456';
```
### 修改master配置文件
```
#进入容器
docker exec -it mysql-master /bin/bash
#安装vim
apt update
apt install -y vim

[mysqld]
## 同一局域网内注意要唯一
server-id=100  
## 开启二进制日志功能，可以随便取（关键）
log-bin=mysql-bin
```
### 修改每个slave配置文件
```
[mysqld]
## 设置server_id,注意要唯一
server-id=101  
## 开启二进制日志功能，以备Slave作为其它Slave的Master时使用
log-bin=mysql-slave-bin   
## relay_log配置中继日志
relay_log=edu-mysql-relay-bin
```

### 在master中创建用户
```
CREATE USER 'slave'@'%' IDENTIFIED BY '123456';
GRANT REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'slave'@'%';
ALTER USER 'slave'@'%' IDENTIFIED WITH mysql_native_password BY '123456';
```
### 在master中查看,记录log_file和log_pos
```
show master status;
```
### 在宿主机执行,查看master的ip地址
```
docker inspect --format='{{.NetworkSettings.IPAddress}}' mysql-master
```
### 在每个slave中执行以下命令,host/log_file/log_pos均由以上查询得到
```
change master to master_host='172.17.0.2', master_user='slave', master_password='123456', master_port=3306, master_log_file='mysql-bin.000001', master_log_pos=994, master_connect_retry=30;
start slave;

show slave status \G;
#如结果中以下两栏为Yes则搭建完成，否则请查看log_file以及log_pos配置是否正确
Slave_IO_Running: Yes
Slave_SQL_Running: Yes
```