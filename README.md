
```
请勿滥用，本项目仅用于学习和测试！请勿滥用.
请勿滥用，本项目仅用于学习和测试！请勿滥用.
```

### 停更
    由于个人问题 项目暂停更新 仅保持已经实现的功能
    如算法更新 等会尽量及时修复
    暂停新功能开发及处理issues 具体恢复时间 暂时未知

### 项目介绍([历史README文档](https://github.com/lemon8866/spirit/blob/main/doc/README.md "历史README文档"))
    将短视频等下载到自己指定的环境 如NAS等 目前支持 抖音 哔哩哔哩   steam的 wallpaper
    可通过配套小程序发送 也可以通过[接口](https://github.com/lemon8866/spirit/blob/main/doc/api.md "接口")自行实现推送

    将视频下载地址推送给 应用目前已经支持的下载器 目前支持http 及外置Aria2 
    并建立视频资源缓存 后台可进行管理查询查看等 

    latest 支持多架构
    steam  仅支持amd64

### 版本区别及遗留版本问题说明
0.0.3版本
```
/app/resources/video
/app/resources/cover
```

资源挂载目录 为做出变动
latest steam dev 等分支 资源目录进行调整
优化成
```
/app/resources/{platform}/{yyyy}/{MM}/{name}/{name}.mp4
/app/resources/{platform}/{yyyy}/{MM}/{name}/{name}.jpg
```
#### steam分支
新增容器内新增steamcmd 用于下载steam wallpaper 壁纸 没需要请使用latest分支
支持两种方式 请查看 [部署方式](https://github.com/lemon8866/spirit/blob/main/doc/deployment.md "查看部署方式中 steam分支相关内容") 相关区别

由于0.0.3 存在很多bug 但是代码没有分支 故不再修复  全都移到latest进行修复
例如 哔哩哔哩 某些情况下分辨率降级异常造成下载失败或者收藏夹下载中断等问题

### 更新记录

[更新记录](https://github.com/lemon8866/spirit/blob/main/doc/updaterecords.md "尽量记录每次更新")

### 更新计划
- [ ] 修复后台端 由于文件名称存在中文等特殊符号无法显示缩略图的问题
- [ ] 支持对网页内m3u8资源进行嗅探 并下载

### 部署文档
详情 请查看[部署方式](https://github.com/lemon8866/spirit/blob/main/doc/deployment.md "部署文档")

#### 客户端配套
###### 手机配套端

```
    安卓用户可以有两种选择 一个是我打包好的apk 一个是自己发布一个小程序
    apk
      https://github.com/Confession1995/spirit/blob/main/app/release/apk/apk.apk

    apk 请注意 这个是升级版 支持查看视频的 如果没有手机查看视频的需求 直接下上边那一个就可以了
     https://github.com/Confession1995/spirit/blob/main/app/release/apk/apk_update_video_play.apk
     https://github.com/lemon8866/spirit/blob/main/app/release/apk/apk_update_video_play-fix.apk
    ios用户 仅有一种选择是自己发布一个小程序
    
    小程序基于uniapp开发  直接发布一个体验版本的小程序即可  不要推正式版
    并打开小程序开发模式  跳过域名校验  即可使用
```
###### 电脑配套端
[win 桌面客户端](https://github.com/lemon8866/spirit/blob/main/app/desktop/spirit-app/build/spirit-app%200.0.1.exe "基于electron-vue")


###### 小程序界面或APP界面

服务器地址 服务器端口 服务器token 仅需要填写一次  填写后点击保存服务器

小程序或者APP监听剪贴板 会自动复制到第一个大方框里

点击推送 将分享链接推送给后台 将进入排队 进行处理  设置了线程1 所以会排队

<img src="https://s2.loli.net/2023/04/19/GlmrVTWEe8AyYR2.jpg" title="" alt="微信图片_20230419093629.jpg" width="251">


#### 服务端 docker cli

```
docker run --name spirit -d -p 28081:28081 -v /home/spirit/log:/app/log -v /home/spirit:/app/resources -v /home/spirit/db:/app/db -v /home/spirit/tmp:/tmp qingfeng2336/spirit:latest
```

### 技术框架
```
spring boot 2.7.10、spring boot、jpa、sqlite、htmlunit等
```

## docker hub

[Docker](https://hub.docker.com/r/qingfeng2336/spirit)


### github

[GitHub - lemon8866/spirit: spirit](https://github.com/lemon8866/spirit/)


### 参考资料

https://github.com/SocialSisterYi/bilibili-API-collect

https://toscode.gitee.com/zong_zh/parsing-tiktok-video

https://github.com/Johnserf-Seed/TikTokDownload


### 致谢
 [JetBrains](https://jb.gg/OpenSourceSupport/?from=https://github.com/lemon8866/spirit) 提供 [Licenses for Open Source Development](https://www.jetbrains.com/community/opensource/#support)

[![JetBrains](https://resources.jetbrains.com/storage/products/company/brand/logos/jb_beam.svg)](https://www.jetbrains.com/?from=https://github.com/lemon8866/spirit)