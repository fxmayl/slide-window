基于redis实现的滑动窗口限流器

## 1. 简介

基于redis实现的滑动窗口限流器，支持自定义滑动窗口大小，但是大小不超过60，而且只能限制到分钟级别
例如：只能配置3分钟内最大访问次数为100

## 2. 实现

### 2.1、增加访问次数
com.my.slide.window.slidewindow.flow.SlideWindowCounter.increase
bucket：表示滑动窗口的桶，例如：3分钟内，桶为3
然后根据桶的信息，生成对应的访问key信息，将此key对应的值增加1


### 2.2、获取访问次数
com.my.slide.window.slidewindow.flow.SlideWindowCounter.count
首先获取最近windowSize个桶对应的key
然后通过这些key从redis中获取对应的值，将这些值加起来作为总访问次数

## 3. 注意事项

### 3.1、滑动窗口

滑动窗口的大小，目前总长度为60，即60分钟
限流操作时，目前制定了3，即3分钟内只能访问访问多少次