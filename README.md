# wiki
该系统是前后端分离开发模式，SpringBoot+Vue全家桶；包含拦截器、过滤器、异步化、定时任务、WebSocket等算法
## 1. 主页面
### 1.1 从最左侧可以看到全部大分类的书籍，并且在主页面显示了总浏览量以及点赞量等信息，并结合了Echart进行可视化
### 1.2 在阅读量&点赞量开发时用到了定时任务框架：quartz、WebScoket、MQ（防止一部请求时间过长占用资源！！）
![image](https://github.com/z-s920/wiki/assets/75167800/5621c588-1178-4cc5-a30c-82e09a21839e)
## 2. 用户管理
### 2.1 可以对任意用户进行重置密码、编辑以及删除操作 <br>
其中对用户密码进行了加密存储和传输（MDS）
![image](https://github.com/z-s920/wiki/assets/75167800/0068621c-dfe4-4544-ad34-e2f5d1755431)
## 3. 电子书管理
## 3.1 在各个管理页面集成了PageHelper分页插件，并使用了雪花算法生成不规则id
![image](https://github.com/z-s920/wiki/assets/75167800/8d9bd36f-d7fe-4ec0-b6dd-f87bf775edb2)

## 4. 分类管理
这里采用的是树形结构！
![image](https://github.com/z-s920/wiki/assets/75167800/efd0ebf1-05bf-48fb-b91c-1310a6d36be8)
## 5. 知识库浏览
可以点击主页面的左侧分类找到任意一本知识总结进行浏览！每个用户可以对任意小节进行点赞一次。
![image](https://github.com/z-s920/wiki/assets/75167800/79ed6814-2cb1-48c7-839c-ebf942688ea3)
## 6. 登录功能
