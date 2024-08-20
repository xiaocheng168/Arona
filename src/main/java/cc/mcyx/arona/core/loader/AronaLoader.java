package cc.mcyx.arona.core.loader;

import sun.misc.Unsafe;

import java.io.File;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.net.URL;

public final class AronaLoader {

    public static final File ARONA_LIB_DIR = new File("arona", "lib");

    static {
        ARONA_LIB_DIR.mkdirs();
    }

    /**
     * 加载默认依赖
     */
    public static void loadDefaultLib() {
        try {
            loadCloudLib(new LibInfo("cn.hutool", "hutool-all", "5.8.29", LibInfo.Source.ALIBABA));
            loadCloudLib(new LibInfo("org.jetbrains.kotlin", "kotlin-stdlib", "2.0.0", LibInfo.Source.ALIBABA));
            loadCloudLib(new LibInfo("org.jetbrains.kotlin", "kotlin-reflect", "2.0.0", LibInfo.Source.ALIBABA));
            loadCloudLib(new LibInfo("org.jetbrains.kotlin", "kotlin-stdlib-common", "2.0.0", LibInfo.Source.ALIBABA));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 加载来自 Maven 仓库中的依赖
     * @param libInfo 依赖信息
     */
    public static void loadCloudLib(LibInfo libInfo) {
        libInfo.downloadAndLoad();
    }

    /**
     * 加载一个 Cloud URL 地址的 Lib
     * @param url 下载绝对路径
     */
    public static void loadCloudLib(String url) {
        new LibInfo(url).downloadAndLoad();
    }

    public static Field scanUcp(Class<?> c) {
        try {
            return c.getDeclaredField("ucp");
        } catch (Throwable e) {
            if (c.getSuperclass() != Object.class) scanUcp(c.getSuperclass());
        }
        throw new RuntimeException("没有找到 UCP " + c);
    }

    /**
     * 加载Jar
     *
     * @param jarFile 加载的jar文件
     */
    public static void loadJar(URL jarFile) throws Throwable {

        Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
        theUnsafe.setAccessible(true);
        Unsafe unSafe = (Unsafe) theUnsafe.get(null);

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Field ucp = scanUcp(classLoader.getClass());
        Object object = unSafe.getObject(classLoader, unSafe.objectFieldOffset(ucp));

        Field implLookup = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
        MethodHandles.Lookup lookup = (MethodHandles.Lookup) unSafe.getObject(unSafe.staticFieldBase(implLookup), unSafe.staticFieldOffset(implLookup));
        MethodHandle addURL = lookup.findVirtual(object.getClass(), "addURL", MethodType.methodType(void.class, URL.class));
        addURL.invoke(object, jarFile);

        System.out.printf("[ARONA] %s\n", "Load lib" + jarFile.getPath());
    }
}
