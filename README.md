# BewBluetoothStrap
H8手表蓝牙协议文档：https://nigh.github.io/Cool.Romi/page/#!dev/classB/index.md

# 包说明

Google Play和微信平台用的是app这个mode

安智和华为用的是Old这个mode

app用的是app下的guider签名 , 别名密码见gradle

old用的是old下的beraceSport签名 , 别名密码见gradle


# 其他说明
- 在主分支上孙工那天提交了一些修改 , 他说是吴工这边提的 , 我还没有拉下来 , 应该是关于一些BUG的修复.
- DeviceActivity 是测量的入口 , start() 方法则是启动 , 加载m500系列还是加载无创手机版, 在这个里面加载就行.
- app Module下的B31HomeActivity,在点击运动的时候是跳转到 DeviceActivity 里的.
- ChooseDeviceFragment 是m500 的设备列表页
- AttentionInfo 是无创的启动页.