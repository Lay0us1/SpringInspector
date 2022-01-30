# SpringInspector

![](https://img.shields.io/badge/build-passing-brightgreen)
![](https://img.shields.io/badge/ASM-9.2-blue)
![](https://img.shields.io/badge/Java-8-red)
![](https://img.shields.io/badge/Line-5208-yellow)

一个Java自动代码审计工具，尤其针对Spring框架，也可自行改造以适配其他情况

提供一个SpringBoot的Jar包即可进行自动代码审计，底层技术基于字节码分析

能够生成跨越接口和实现的方法调用关系图（CallGraph）并模拟JVM栈帧实现简单的数据流分析

注意：该工具不能确定存在漏洞，只能证明某条调用链上存在危险操作，所以建议结合人工做进一步分析

![](/img/1.png)

支持漏洞类型：

- SQL注入检测
- 服务器端请求伪造漏洞检测
- XML外部实体注入漏洞检测
- 远程命令执行漏洞检测
- 针对Java的拒绝服务漏洞检测
- URL重定向漏洞检测
- 日志注入漏洞检测

## 快速上手

示例：针对我写好的 [靶机](https://github.com/EmYiQing/SIDemo) 进行SSRF漏洞的检测

配置靶机下载地址：[SIDemo](https://github.com/EmYiQing/SIDemo/releases/download/1.0/SIDemo.jar)

命令：`java -jar SpringInspector.jar sidemo.jar --springboot --package org.sec --module SSRF`

该工具扫描速度极快，只需要几秒，将会扫描到以下四条链

```text
......
14:03:55 [INFO] [SSRFService] start analysis ssrf
14:03:55 [INFO] [SSRFService] detect jdk ssrf
JDK SSRF
	org/sec/sidemo/web/SSRFController.ssrf1
	org/sec/sidemo/service/SSRFService.ssrf1
	org/sec/sidemo/service/impl/SSRFServiceImpl.ssrf1

14:03:55 [INFO] [SSRFService] detect apache ssrf
Apache SSRF
	org/sec/sidemo/web/SSRFController.ssrf2
	org/sec/sidemo/service/SSRFService.ssrf2
	org/sec/sidemo/service/impl/SSRFServiceImpl.ssrf2

14:03:55 [INFO] [SSRFService] detect socket ssrf
Socket SSRF
	org/sec/sidemo/web/SSRFController.ssrf3
	org/sec/sidemo/service/SSRFService.ssrf3
	org/sec/sidemo/service/impl/SSRFServiceImpl.ssrf3

14:03:55 [INFO] [SSRFService] detect okhttp ssrf
Okhttp SSRF
	org/sec/sidemo/web/SSRFController.ssrf4
	org/sec/sidemo/service/SSRFService.ssrf4
	org/sec/sidemo/service/impl/SSRFServiceImpl.ssrf4
......
```

可选参数说明

|      参数      |          参数说明           |  参数类型   | 是否必须 |
|:------------:|:-----------------------:|:-------:|:----:|
|   xxx.jar    |        检测Jar文件路径        | String  |  是   |
| --springboot |  针对SpringBoot对Jar进行分析   | Boolean |  是   |
|  --package   |    设置SpringBoot项目的包名    | String  |  是   |
|   --module   |    设置使用的检测模块（可包含多个）     | String  |  否   |
|   --debug    |   设置使用调试模式（保存一些临时数据）    | Boolean |  否   |
|    --jdk     | 加入JDK中的rj.jar进行分析（可能耗时） | Boolean |  否   |
|    --all     | 加入SpringBoot的其他依赖（可能耗时） | Boolean |  否   |
|   --output   | 漏洞报告输出文件（默认result.txt）  | String  |  否   |

注意：

- 其中类型为`String`的需要在`flag`之后加入字符串参数（例如`--package org.sec`）
- 类型为`Boolean`直接加入`flag`即可（例如`--debug`或`--jdk`）
- 项目包名参数必须设置（例如`org.sec`或`com.xxx`等）
- 可选检测模块用`|`分割可包含多个（例如`--module SSRF|SQLI`）

## SQL注入

开启检测模块关键字：SQLI

|                   Sink类                    |     Sink方法     |
|:------------------------------------------:|:--------------:|
|             java/sql/Statement             |    execute     |
|             java/sql/Statement             |  executeQuery  |
|             java/sql/Statement             | executeUpdate  |
| org/springframework/jdbc/core/JdbcTemplate |     update     |
| org/springframework/jdbc/core/JdbcTemplate |    execute     |
| org/springframework/jdbc/core/JdbcTemplate |     query      |
| org/springframework/jdbc/core/JdbcTemplate | queryForStream |
| org/springframework/jdbc/core/JdbcTemplate |  queryForList  |
| org/springframework/jdbc/core/JdbcTemplate |  queryForMap   |
| org/springframework/jdbc/core/JdbcTemplate | queryForObject |

检测说明：

1. Source是Controller输入的String型请求参数
2. 该参数通过字符串拼接得到了SQL语句
3. SQL语句进入了Sink方法

## XXE

开启检测模块关键字：XXE

|                     Sink类                     |        Sink方法         |
|:---------------------------------------------:|:---------------------:|
|          org/jdom2/input/SAXBuilder           |         build         |
|          javax/xml/parsers/SAXParser          |         parse         |
| javax/xml/transform/sax/SAXTransformerFactory | newTransformerHandler |
|      javax/xml/validation/SchemaFactory       |       newSchema       |
|        javax/xml/transform/Transformer        |       transform       |
|        javax/xml/validation/Validator         |       validate        |
|             org/xml/sax/XMLReader             |         parse         |

检测说明：

Sink方法的参数有多种重载，已针对这些类型做处理（污点传递）

1. `java/lang/String`
2. `java/io/File`
3. `java/io/FileInputStream`
4. `org/xml/sax/InputSource`
5. `javax/xml/transform/stream/StreamSource`

## RCE

开启检测模块关键字：RCE

|          Sink类           |  Sink方法  |
|:------------------------:|:--------:|
|    java/lang/Runtime     |   exec   |
| java/lang/ProcessBuilder |  start   |
| groovy/lang/GroovyShell  | evaluate |

检测说明：

1. 简单的命令执行，判断整条链中参数是否能进入危险方法
2. 其中`ProcessBuilder`类初始化需要处理数组情况的污点传递

## DOS

开启检测模块关键字：DOS

|    漏洞名    |                漏洞细节                |
|:---------:|:----------------------------------:|
|  RE DOS   |      Pattern.matches(str,str)      |
|  FOR DOS  |       for(int i=0;i<int;i++)       |
| ARRAY DOS | object[] array = new object\[int\] |
| LIST DOS  |   List list = new ArrayList(int)   |
|  MAP DOS  |     Map map = new HashMap(int)     |

检测说明：

1. 其中的`RE DOS`模块曾经发现某开源组件的`RE DOS`([参考文章](https://4ra1n.love/post/TVE_41PT3/))
2. 如果传入的参数是`int`类型且作为数组或集合的初始化长度认为可能存在拒绝服务

## URL重定向

开启检测模块关键字：REDIRECT

|       漏洞名        |                漏洞细节                |
|:----------------:|:----------------------------------:|
| SERVLET REDIRECT |       response.sendRedirect        |
| SPRING REDIRECT  |      return "redirect://str"       |
| SPRING REDIRECT  | new ModelAndView("redirect://str") |

检测说明：

1. 对于SPRING型，解决字符串拼接和包含`redirect://`问题
2. 这里为了方便只分析了`Controller`层，实际中也大都在这里做重定向

## LOG注入

开启检测模块关键字：LOG

|              Sink类              | Sink方法 |
|:-------------------------------:|:------:|
|        org/slf4j/Logger         |  log   |
|        org/slf4j/Logger         | error  |
|        org/slf4j/Logger         |  warn  |
| org/apache/logging/log4j/Logger |  log   |
| org/apache/logging/log4j/Logger | error  |
| org/apache/logging/log4j/Logger |  warn  |

检测说明：

1. 关于日志注入的说明参考：[OWASP](https://owasp.org/www-community/attacks/Log_Injection)
2. 需要处理可能存在的字符串拼接问题

## 优点

该项目的优点如下：

1. 速度较快，一次分析大致需要几秒，曾经`CodeInspector`跑一次需要三分钟以上
2. 不依赖于源码，直接提供SpringBoot的Jar基于字节码分析
3. 实现了简单的数据流分析和模拟栈帧的动态分析，不同于传统的正则等方式
4. ......（后续补充）

## 缺陷

缺陷有很多，大概如下：

1. 没有加入返回值的分析
2. 目前判断规则比较直接，应该参考实际的项目改善
3. 污点传递比较简单，应该结合实际情况改善
4. 仅针对于SpringMVC的Source，可以加入Servlet等情况
5. 数据流分析并不是真正的数据流分析，是一种快速简单但不准确的分析
6. ......（后续补充）

## TODO

1. 处理BUG
2. 完善污点传递
3. 找实际的CMS进行测试
4. 能否加入一种表达式或语言以实现动态配置
5. 找大佬一起做

## 参考

关于该工具原理我写了五篇文章

1. 我的博客：[4ra1n](https://4ra1n.love/)
2. [深入分析GadgetInspector核心代码](https://xz.aliyun.com/t/10363)
3. [Java自动代码审计工具实现](https://xz.aliyun.com/t/10433)
4. [详解Java自动代码审计工具实现](https://tttang.com/archive/1334/)
5. [基于污点分析的JSP Webshell检测](https://xz.aliyun.com/t/10622)
6. [加载恶意字节码Webshell的检测](https://xz.aliyun.com/t/10636)

## 其他

- 欢迎大佬们提出建议和意见
- 二次开发请注明来自于：[4ra1n](https://github.com/EmYiQing)
- 工具仅用于安全研究，使用工具造成的任何后果使用者负责，与作者无关