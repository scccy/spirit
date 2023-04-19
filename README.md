```
请勿滥用，本项目仅用于学习和测试！请勿滥用.
```

### 为啥要做这个

```
啊  我喜欢的抖音视频怎么被下架了！怎么失效了！可恶  手机内存又满了！
```

### 简介

```
一个视频下载汇聚类功能  
将视频下载地址推送给 应用目前已经支持的下载器 目前支持http 及外置Aria2 
并建立视频资源缓存 后台可进行管理查询查看等 
后期预计增加视频墙功能和APP端播放视频功能
```

### 技术框架

```
spring boot 2.7.10
spring boot jpa
sqlite
htmlunit等
```

### 功能点

- [x] 配置下载器

- [x] HTTP下载

- [x] 推送Aria2

- [x] 多用户管理

- [x] 视频资源缓存查看

- [x] 视频墙

- [ ] 视频解析

- [x]           抖音

- [x]           哔哩

- [ ] APP或小程序播放视频

- [ ] 其他功能

### 预计目标实现及支持平台

```
    支持平台AMD64及ARM
    可解析平台
        抖音
        B站
        待总结
```

### 部署

##### 手动部署

```
待后续完善  可在任意存在java1.8以上环境部署
```

##### docker部署

###### docker cli

```
docker run --name spirit -d -p 28081:28081 -v /home/spirit/log:/app/log -v /home/spirit/video:/app/resources/video -v /home/spirit/cover:/app/resources/cover -v /home/spirit/db:/app/db -v /home/spirit/tmp:/tmp qingfeng2336/spirit:latest
```

###### 手动方式

    

```
docker 部署下 容器端口 为20801
/app/resources/video       请注意此处  挂载目录必须与系统配置用推送给Aria2的下载目录一致   
                           如果不使用Aria2 只使用内置http 下载器  就正常挂载此目录
/app/resources/cover
/app/db
/tmp

```

### 后台默认帐号密码

```
后台地址是 http://ip:port/admin/login
默认帐号为admin 默认密码为123456
```

### 意见反馈

### 更新方向

```
暂无
```

## 参考资料

   https://github.com/SocialSisterYi/bilibili-API-collect

   https://toscode.gitee.com/zong_zh/parsing-tiktok-video
