package cc.mcyx.arona.core.loader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class LibInfo {
    private final String groupId;
    private final String artifactId;
    private final String version;
    private final Source source;


    public LibInfo(String groupId, String artifactId, String version, Source source) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.source = source;
    }


    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersion() {
        return version;
    }

    public Source getSource() {
        return source;
    }

    /**
     * 下载该依赖并且加载
     * @return 返回依赖实例
     */
    public LibInfo downloadAndLoad() {
        String downloadURL = source.url + "/" + this.getGroupId().replace(".", "/") + "/" + this.getArtifactId() + "/" + this.getVersion() + "/" + (this.getArtifactId() + "-" + this.getVersion() + ".jar");
        try {
            URL url = new URL(downloadURL);
            URLConnection urlConnection = url.openConnection();
            HttpURLConnection connection = (HttpURLConnection) urlConnection;
            connection.setReadTimeout(10000);
            InputStream inputStream = connection.getInputStream();
            byte[] bytes = new byte[1024];
            File jar = new File(AronaLoader.ARONA_LIB_DIR, new File(url.getFile()).getName());
            FileOutputStream fileOutputStream = new FileOutputStream(jar);
            int read;
            while ((read = inputStream.read(bytes)) != -1) {
                fileOutputStream.write(bytes, 0, read);
            }
            fileOutputStream.close();
            inputStream.close();
            // 加载
            AronaLoader.loadJar(jar.toURI().toURL());
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return this;
    }

    public enum Source {
        ALIBABA("https://maven.aliyun.com/repository/public");

        final String url;

        Source(String url) {
            this.url = url;
        }
    }
}
