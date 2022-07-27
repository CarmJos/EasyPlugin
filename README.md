```text
    ______                    ____  __            _     
   / ____/___ ________  __   / __ \/ /_  ______ _(_)___ 
  / __/ / __ `/ ___/ / / /  / /_/ / / / / / __ `/ / __ \
 / /___/ /_/ (__  ) /_/ /  / ____/ / /_/ / /_/ / / / / /
/_____/\__,_/____/\__, /  /_/   /_/\__,_/\__, /_/_/ /_/ 
                 /____/                 /____/          
```

# EasyPlugin

[![version](https://img.shields.io/github/v/release/CarmJos/EasyPlugin)](https://github.com/CarmJos/EasyPlugin/releases)
[![License](https://img.shields.io/github/license/CarmJos/EasyPlugin)](https://opensource.org/licenses/MIT)
[![workflow](https://github.com/CarmJos/EasyPlugin/actions/workflows/maven.yml/badge.svg?branch=master)](https://github.com/CarmJos/EasyPlugin/actions/workflows/maven.yml)
[![CodeFactor](https://www.codefactor.io/repository/github/carmjos/EasyPlugin/badge)](https://www.codefactor.io/repository/github/carmjos/EasyPlugin)
![CodeSize](https://img.shields.io/github/languages/code-size/CarmJos/EasyPlugin)
![](https://visitor-badge.glitch.me/badge?page_id=EasyPlugin.readme)

轻松(做)插件，旨在于方便快捷的使用Bukkit实现MC中的一些功能。

## 优势

- 轻便独立的功能模块，按需使用，避免大量打包！
- 详细的Javadoc与使用文档，轻松上手，方便使用！
- 持续的更新与优化，需求不止，更新不止！
    - 如需新功能支持，请通过 [Issues](https://github.com/CarmJos/EasyPlugin/issues) 提交功能需求。

## 内容

项目初创不久，加 * 的仍在开发更新中...欢迎各路大佬帮助提供本项目的开发文档~

### 集合部分 (`/collection`)

- All [`easyplugin-all`](collection/all)
- Common [`easyplugin-common`](collection/common)

### 主要部分 (`/base`)

- Main [`easyplugin-main`](base/main)
  -  主要接口模块，提供了方便的插件入口类与相关工具类。
- Command [`easyplugin-command`](base/command)
  - 指令接口模块，便于快速进行子指令的实现，并提供单独的TabComplete方法。
  - 随本项目提供了 `SimpleCompleter` 类，用于快速创建补全的内容列表。
- GUI [`easyplugin-gui`](base/main)
  - 简单便捷的箱子GUI接口，可以快速实现GUI中不同图标的点击功能。
  - 随本项目提供了 `AutoPagedGUI` 等翻页GUI抽象类。
- Storage [`easyplugin-storage`](base/storage)
  - 抽象存储管理器，便于实现不同的存储类型。 
  - 随本项目提供了 `FileBasedStorage`、`FolderBasedStorage` 等常用存储抽象方法。

> 以下项目均已独立出单独项目，仅仅是按照包名规则打包到EasyPlugin的子项目中。
> 
> 如需使用，**强烈建议自行引用对应的项目**，以支持完整的Javadoc并获取源码内容！

- _Listener_ [`easyplugin-listener`](base/listener) (打包自 [**EasyListener**](https://github.com/CarmJos/EasyListener))
- _Configuration_ [`easyplugin-conf`](base/conf) (打包自 [**MineConfiguration**](https://github.com/CarmJos/MineConfiguration))
- _Database_ [`easyplugin-database`](base/database)  (打包自 [**EasySQL**](https://github.com/CarmJos/EasySQL))
- _Messages*_ [`easyplugin-message`](base/message) (打包自 [**EasyMessages**](https://github.com/CarmJos/EasyMessages))

### 附属部分 (`/extension`)

- [PlaceholderAPI](https://www.spigotmc.org/resources/6245/)* [`easyplugin-placeholderapi`](extension/papi)
- [Vault](https://github.com/MilkBowl/VaultAPI)* [`easyplugin-vault`](extension/vault)

## 开发

详细开发介绍请 [点击这里](.documentation/README.md) , JavaDoc(最新Release) 请 [点击这里](https://carmjos.github.io/EasyPlugin) 。

### 示例代码

您可以 [点击这里](https://github.com/CarmJos/UltraDepository) 查看实例项目演示，更多演示详见 [开发介绍](.documentation/README.md) 。

### 依赖方式

<details>
<summary>展开查看 Maven 依赖方式</summary>

```xml

<project>
    <repositories>

        <repository>
            <!--采用github-repo依赖库(推荐)-->
            <id>EasyPlugin</id>
            <name>GitHub Packages</name>
            <url>https://raw.githubusercontent.com/CarmJos/EasyPlugin/repo/</url>
        </repository>

        <repository>
            <!--采用我的私人依赖库，简单方便，但可能因为变故而无法使用-->
            <id>carm-repo</id>
            <name>Carm's Repo</name>
            <url>https://repo.carm.cc/repository/maven-public/</url>
        </repository>

    </repositories>

    <dependencies>
        <!--大全集版本，包含项目内所有模块-->
        <dependency>
            <groupId>cc.carm.lib</groupId>
            <artifactId>easyplugin-all</artifactId>
            <version>[LATEST RELEASE]</version>
            <scope>compile</scope>
        </dependency>

        <!--常用接口集，包含除附属插件模块外的所有模块-->
        <dependency>
            <groupId>cc.carm.lib</groupId>
            <artifactId>easyplugin-common</artifactId>
            <version>[LATEST RELEASE]</version>
            <scope>compile</scope>
        </dependency>

        <!--插件主要接口模块，包含方便的插件入口类与相关工具类-->
        <dependency>
            <groupId>cc.carm.lib</groupId>
            <artifactId>easyplugin-main</artifactId>
            <version>[LATEST RELEASE]</version>
            <scope>compile</scope>
        </dependency>

        <!-- 其他模块自行选择，详见 “内容”-->

    </dependencies>
</project>
```

</details>

<details>
<summary>展开查看 Gradle 依赖方式</summary>

```groovy
repositories {
    // 采用github依赖库，安全稳定，但需要配置 (推荐)
    maven { url 'https://raw.githubusercontent.com/CarmJos/EasyPlugin/repo/' }

    // 采用我的私人依赖库，简单方便，但可能因为变故而无法使用
    maven { url 'https://repo.carm.cc/repository/maven-public/' }
}

dependencies {

    //大全集版本，包含项目内所有模块
    api "cc.carm.lib:easyplugin-all:[LATEST RELEASE]"

    //常用接口集，包含除附属插件模块外的所有模块
    api "cc.carm.lib:easyplugin-common:[LATEST RELEASE]"

    //插件主要接口模块，包含方便的插件入口类与相关工具类
    api "cc.carm.lib:easyplugin-main:[LATEST RELEASE]"

    // 其他模块自行选择，详见 “内容”

}
```

</details>

## 支持与捐赠

若您觉得本插件做的不错，您可以通过捐赠支持我！

感谢您对开源项目的支持！

<img height=25% width=25% src="https://raw.githubusercontent.com/CarmJos/CarmJos/main/img/donate-code.jpg"  alt=""/>

## 开源协议

本项目源码采用 [The MIT License](https://opensource.org/licenses/MIT) 开源协议。
<details>
<summary>关于 MIT 协议</summary>

> MIT 协议可能是几大开源协议中最宽松的一个，核心条款是：
>
> 该软件及其相关文档对所有人免费，可以任意处置，包括使用，复制，修改，合并，发表，分发，再授权，或者销售。唯一的限制是，软件中必须包含上述版 权和许可提示。
>
> 这意味着：
> - 你可以自由使用，复制，修改，可以用于自己的项目。
> - 可以免费分发或用来盈利。
> - 唯一的限制是必须包含许可声明。
>
> MIT 协议是所有开源许可中最宽松的一个，除了必须包含许可声明外，再无任何限制。
>
> *以上文字来自 [五种开源协议GPL,LGPL,BSD,MIT,Apache](https://www.oschina.net/question/54100_9455) 。*
</details>