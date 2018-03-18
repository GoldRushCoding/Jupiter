# Jupiter - Nukkit Fork for 1.2.x

# Project Freezed [プロジェクト凍結]
This project is freezed. Now, thare are no plans to develop further.
<!-- ![jupiter](https://github.com/JupiterDevelopmentTeam/JupiterDevelopmentTeam/blob/master/Banner.jpg) -->

Jupiterについて / About Jupiter
--------------------

Jupiterは無料のMinecraft Pocket Edition用のサーバーソフトウェアです。コードの多くは[Nukkit](https://github.com/Nukkit/Nukkit/)のものを使用しています。

* 日本人開発者チームが日本のユーザーのために作っています

* Nukkitプラグインはもちろん、Jupiterの独自メソッドを使ったプラグインも使用できます   
  
なお、WindowsとMacOSで正常な動作を確認しております。  
  
Jupiterの機能の詳細については[Wiki](https://github.com/JupiterDevelopmentTeam/Jupiter/wiki)をご覧ください。  
  
ビルドステータス
--------------------
<!-- [![travisCI](https://travis-ci.org/JupiterDevelopmentTeam/Jupiter.svg?branch=master)](https://travis-ci.org/JupiterDevelopmentTeam/Jupiter) -->
[![CircleCI](https://circleci.com/gh/JupiterDevelopmentTeam/Jupiter/tree/master.svg?style=svg)](https://circleci.com/gh/JupiterDevelopmentTeam/Jupiter/tree/master)
  
開発に至ったきっかけ
--------------------
日本ではPHPのサーバーソフトを使ったサーバーが主流です。  
Javaのものはほとんど使われていなく、Java(Nukkit)を使ったサーバーは人数が少なくなるという傾向がありました。  
こういったイメージを払拭すべく、そして日本でもっとJavaのサーバーソフトを普及させるべく、このプロジェクトが立ち上がりました。  
Jupiterはこのようなきっかけを持つために、__日本語に特化__ させています。そのかわり、いろいろな機能をどんどん追加していきます。  

ダウンロード & インフォメーション / Download & Information
-------------

* __[最新ビルドのjarファイルをダウンロード](https://circleci.com/gh/JupiterDevelopmentTeam/Jupiter/tree/master)__

* __[公式サイト](https://jupiterdevelopmentteam.github.io/)__
* __[Lobiグループ](https://web.lobi.co/group/5f56c6d4c43cdb8c63541731b2ea8533ac4b50f1)__
* __[公式プラグイン](https://github.com/JupiterDevelopmentTeam/Plugins)__
* __[JupiterをWindows10で動かす！(Youtube)](https://www.youtube.com/watch?v=bHTzzD6z4pw)__
* __[Travis Ci](https://travis-ci.org/JupiterDevelopmentTeam/Jupiter)__
* __[Circle Ci](https://circleci.com/gh/JupiterDevelopmentTeam/Jupiter/tree/master)__
* __[Jupiterプラグインコードエディタ「EarthOne」](http://itsuplugin.web.fc2.com/earthone.html)__


Mavenリポジトリ / Maven Repositories
--------------------

Mavenリポジトリはありませんが、プラグインを作ることはできます。  

__Jupiterプラグインの作り方__
  
作り方は非常に簡単です。ビルドパスにJupiterを追加するだけで作成することができます。
  
  
#### ローカルMavenリポジトリの構築  
```xml:pom.xml
<dependencies>
    <dependency>
        <groupId>cn.nukkit</groupId>
        <artifactId>nukkit</artifactId>
        <version>1.0-SNAPSHOT</version>
        <scope>system</scope>
        <systemPath>\path\jupiter.jar</systemPath>
    </dependency>
</dependencies>
```
