### docker cli
#### 旧版本 0.0.3
```
docker run --name spirit -d -p 28081:28081 -v /home/spirit/log:/app/log -v /home/spirit/video:/app/resources/video -v /home/spirit/cover:/app/resources/cover -v /home/spirit/db:/app/db -v /home/spirit/tmp:/tmp qingfeng2336/spirit:latest
```
#### 新版本(latest,dev,steam) 
```
docker run --name spirit -d -p 28081:28081 -v /home/spirit/log:/app/log -v /home/spirit:/app/resources -v /home/spirit/db:/app/db -v /home/spirit/tmp:/tmp qingfeng2336/spirit:latest
```

### 普通分支
基于alpine镜像  支持多架构
### steamn分支介绍
```
注意此分支目前加入了 steamcmd 并且构建镜像从alpine变更为ubuntu 并且不再支持arm架构

此分支不支持 steam手机令牌验证  目前仅支持没有验证

部署之后进入容器 
执行 steamcmd
输入 login account password
登录完成后关闭终端

之后在挂载目录 的db 文件夹下新建account.txt文件
内容为
account:xxxxx
password:xxxxx
之后重启容器即可

之后像接口推送https://steamcommunity.com/sharedfiles/filedetails/?id=1234567这样的链接即可下载

可能需要账号内拥有wallpaper 
```

### 挂载目录及端口
docker 部署下 容器端口 为28081
0.0.3 版本号 挂载目录
```
/app/resources/video       请注意此处  挂载目录必须与系统配置用推送给Aria2的下载目录一致   
                           如果不使用Aria2 只使用内置http 下载器  就正常挂载此目录
/app/resources/cover
/app/db
/app/log
/tmp
```
latest,dev,steam 版本号 挂载目录
```
/app/resources
/app/resources
/app/db
/app/log
/tmp
```
### 后台默认账号及密码
```
后台地址是 http://ip:port/admin/login
默认帐号为admin 默认密码为123456
```