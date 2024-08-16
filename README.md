# ARONA 阿诺娜

<div style="text-align: center">
    <img src="arona.png" alt="Arona~"  style="border-radius: 25px;">
</div>

### Arona 是继 [Paimon](https://github.com/xiaocheng168/Paimon) 重写的一个新框架

### 改用模块化装载

### 偏向注解开发,让开发更容易看懂与使用

## 功能与预计功能

- [x] Bukkit 监听器快速监听
- [x] Bukkit 命令快捷注册
- [x] Config 配置自动重载
- [ ] 虚拟 GUI 界面处理
- [ ] 监听玩家数据包
- [ ] NBT 编辑

## 1 如何使用

```shell
  gradlew publishToMavenLocal
```

## 2 模块引用

```kotlin
val inAronaModule = listOf(
    aronaModule("Arona"),
    aronaModule("Arona-GUI"),
    aronaModule("Arona-NMS"),
    aronaModule("Arona-Config"),
)
dependencies {
    inAronaModule.forEach { implementation(it) }
}
```

## 推荐

推荐直接 clone 案例项目进行开发
[AronaDemo](https://github.com/xiaocheng168/AronaDemo)


Arona PNG [pixiv](https://www.pixiv.net/artworks/120849416)