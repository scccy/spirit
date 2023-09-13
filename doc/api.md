### 推送接口

```
接口地址为 http://ip:port/api/processingVideos
参数为
     token   自己后台设置的
     video   链接或分享口令

请求类型 http post
```


### 获取视频列表接口

```
接口地址为 http://ip:port/api/findVideos
参数为
     token   自己后台设置的  必填
     pageNo 页数  必填
	 pageSize  每页数量   必填
     videodesc   选填
     videoname   选填
     videoplatform   选填
请求类型 http post
```