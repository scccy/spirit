### 更新记录


```
2023/09/12
优化以下两个目录
/app/resources/video
/app/resources/cover

优化成
/app/resources/{platform}/{yyyy}/{MM}/{name}/{name}.mp4
/app/resources/{platform}/{yyyy}/{MM}/{name}/{name}.jpg

也就是说将 图片和视频 放在一个文件夹下 方便手工管理
新增steam分支

```

```
修复抖音下载问题 如需使用抖音必须填写登录后的cookie 否则无法下载.移除远程服务器生成Xbogus
如果不填写COOKIE 单个视频将使用htmlclient方式获视频信息,然后就会有内存占用高的问题
所以建议填写Cookie 使用接口获取视频信息
```


```
新增java 生成Xbogus  不再需要调用node的接口  后续稳定后会合并到latest分支  默认情况下 会先本地生成
本地生成无效是会在调用api类型  不过应该不会 所以基本上只会走本地调用 所以 解析地址和qingfeng2336/spirit-assist-node无需在关注
```

```
忘记哪天的了
vercel函数部署的接口 由于所在服务器地址问题可自行搭建 并在系统配置中修改解析地址http://ip:port
docker run --name spirit-assist -d -p 53123:80 qingfeng2336/spirit-assist-node:latest
```

```
20230511一支持哔哩收藏夹下载 同时优化了系统日志 方便排错  优化了提交状态

有点前言要说 本项目目前处于一天一更的状态,如果使用的话 尽可能的每天都看一下docker状态
作为一个解析类项目 我觉得下载任务还是有必要完全交给下载器去做  所以在最新的版本中 Aria2也支持Bili的下载
最新版本中已经移除Bili对htmlunit的依赖 所以只有解析抖音的时候才会增加内存开销
20230408最新版本中 dy解析方式 已变更为优先API解析 API解析失败是才会调用htmlunit
为了不给docker中在加一个node环境   API 为 nodejs  vercel函数部署
后续会考虑出一个 docker 中自带node 并部署解析接口API
有问题请提交issues
```