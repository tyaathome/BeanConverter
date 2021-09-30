# BeanConverter

![Build](https://github.com/tyaathome/BeanConverter/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/PLUGIN_ID.svg)](https://plugins.jetbrains.com/plugin/PLUGIN_ID)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/PLUGIN_ID.svg)](https://plugins.jetbrains.com/plugin/PLUGIN_ID)

自动将Bean转换为ViewModel的Intellij Idea插件

### 使用方法

安装插件之后，选中目标java文件按右键，选择“Bean转换为ViewModel”，会弹出如下对话框

​    ![dialog](https://github.com/tyaathome/BeanConverter/blob/main/img/dialog.jpg?raw=true)

然后点击ok就可以在目标文件夹下生成文件

#### Feature

* 支持修改目标包名和类名
* 支持修改继承类(目前只能继承BaseViewModel和BaseItemViewModel)
* 支持修改需要添加的字段
* 支持修改字段名
* 支持修改注释

#### 未来功能

* 支持自定义添加集成类
* 支持修改字段类型

<!-- Plugin description -->
自动将Bean转换为ViewModel的Intellij Idea插件
<!-- Plugin description end -->