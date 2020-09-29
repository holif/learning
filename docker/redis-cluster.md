# 使用Docker搭建Redis三主三从集群

### 准备一个自定义网络做redis之间通讯
```
docker network create redisnet --subnet 172.38.0.0/16
```

### 循环创建6个redis容器目录,示例中将目录创建到宿主机的/opt/redis/
```
for port in $(seq 8010 1015);
do
mkdir -p /opt/redis/${port}/conf
mkdir -p /opt/redis/${port}/data
touch /opt/redis/${port}/conf/redis.conf
cat << EOF >/opt/redis/${port}/conf/redis.conf
port ${port}
cluster-enabled yes
cluster-config-file nodes.conf
cluster-node-timeout 5000
cluster-announce-ip 192.168.100.241
cluster-announce-port ${port}
cluster-announce-bus-port 1${port}
appendonly yes
masterauth 123456
requirepass 123456
EOF
done
```

### 循环启动redis实例
```
for port in $(seq 8010 8015); \
do \
   docker run -it -d -p ${port}:${port} -p 1${port}:1${port} \
  --privileged=true -v /opt/redis/${port}/conf/redis.conf:/usr/local/etc/redis/redis.conf \
  --privileged=true -v /opt/redis/${port}/data:/data \
  --restart always --name redis-${port} --net redisnet \
  --sysctl net.core.somaxconn=1024 redis redis-server /usr/local/etc/redis/redis.conf; \
done
```

### 进入一个redis容器内,创建集群
```
docker exec -it redis-8010 /bin/sh

redis-cli --cluster create 192.168.100.241:8010 192.168.100.241:8011 192.168.100.241:8012 192.168.100.241:8013 192.168.100.241:8014 192.168.100.241:8015
```

### 使用[AnotherRedisDesktopManager](https://github.com/qishibo/AnotherRedisDesktopManager)测试连接该集群