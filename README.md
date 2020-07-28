# UHF_NEW_UI
The new version of R2000 library is used, and the reading and writing of inventory data is returned through callbacks

## Import dependent libraries
**AndroidStudio** add in dependencies in build.gradle

```
//The outermost build.gradle
allprojects {
     repositories {
         jcenter()
         maven {url'https://jitpack.io'}
     }
}
```
```
  dependencies {
     implementation'com.github.SpeedataG:UHF:8.1.3'
     //Module power-on reference
     implementation'com.github.SpeedataG:Device:1.6.8'
   }
```
## High temperature prohibition instructions
* UHF is disabled when the module temperature is higher than 75℃

  
## API documentation

The detailed interface description is in showdoc, address: http://www.showdoc.cc/web/#/79868361520440?page_id=452063154391852

Beijing Spirit Technology Co., Ltd.

Website http://www.speedata.cn/

Technical Support Tel: 155 4266 8023

QQ: 2480737278
