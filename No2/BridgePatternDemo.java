// BridgePatternDemo.java
// 集成桥接模式示例，统一使用 JPG 演示，同时保留所有格式代码，基于 OpenCV

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;

/** 实现者接口：Filter */
interface Filter {
    /** 对图像进行处理并返回处理后的 Mat 对象 */
    Mat apply(Mat src);
}

/** 木刻滤镜：Cutout */
class CutoutFilter implements Filter {
    @Override public Mat apply(Mat src) {
        Mat edges = new Mat();
        Imgproc.Canny(src, edges, 50, 150);
        Core.bitwise_not(edges, edges);
        return edges;
    }
}

/** 模糊滤镜：Blur */
class BlurFilter implements Filter {
    @Override public Mat apply(Mat src) {
        Mat dst = new Mat();
        Imgproc.GaussianBlur(src, dst, new Size(15, 15), 0);
        return dst;
    }
}

/** 锐化滤镜：Sharpen */
class SharpenFilter implements Filter {
    @Override public Mat apply(Mat src) {
        Mat dst = new Mat();
        Mat kernel = new Mat(3, 3, CvType.CV_32F);
        float[] data = {0f, -1f, 0f, -1f, 5f, -1f, 0f, -1f, 0f};
        kernel.put(0, 0, data);
        Imgproc.filter2D(src, dst, -1, kernel);
        return dst;
    }
}

/** 纹理滤镜：Texture (Laplace 边缘) */
class TextureFilter implements Filter {
    @Override public Mat apply(Mat src) {
        Mat gray = new Mat();
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
        Mat lap = new Mat();
        Imgproc.Laplacian(gray, lap, CvType.CV_32F);
        Mat dst = new Mat(); lap.convertTo(dst, CvType.CV_8U);
        return dst;
    }
}

/** 边缘处理滤镜：Canny */
class CannyFilter implements Filter {
    @Override public Mat apply(Mat src) {
        Mat gray = new Mat();
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
        Mat edges = new Mat();
        Imgproc.Canny(gray, edges, 100, 200);
        return edges;
    }
}

/** 抽象化：Image（桥接抽象） */
abstract class Image {
    protected Filter filter;
    protected String filename;

    public Image(Filter filter) { this.filter = filter; }
    public abstract void load(String filename);

    public void applyFilter(String outputPath) {
        try {
            File file = new File(filename);
            String absPath = file.getAbsolutePath();
            System.out.println("尝试加载路径: " + absPath);
            Mat src = Imgcodecs.imread(absPath);
            if (src == null || src.empty()) {
                System.err.println("无法加载图像（路径或文件错误）: " + absPath);
                return;
            }
            Mat result = filter.apply(src);
            Imgcodecs.imwrite(outputPath, result);
            System.out.println("已保存: " + outputPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

/** JPG 格式 */
class JPGImage extends Image {
    public JPGImage(Filter filter) { super(filter); }
    @Override public void load(String filename) {
        this.filename = filename;
        System.out.println("已注册 JPG 文件名: " + filename);
    }
}

/** BMP 格式 */
class BMPImage extends Image {
    public BMPImage(Filter filter) { super(filter); }
    @Override public void load(String filename) {
        this.filename = filename;
        System.out.println("已注册 BMP 文件名: " + filename);
    }
}

/** GIF 格式 */
class GIFImage extends Image {
    public GIFImage(Filter filter) { super(filter); }
    @Override public void load(String filename) {
        this.filename = filename;
        System.out.println("已注册 GIF 文件名: " + filename);
    }
}

/** 客户端演示：统一使用 input.jpg，保留所有格式类以备扩展 */
public class BridgePatternDemo {
    static { System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    public static void main(String[] args) {
        String input = "input.jpg";
        System.out.println("当前工作目录: " + System.getProperty("user.dir"));

        // 演示所有滤镜
        Image cutout = new JPGImage(new CutoutFilter()); cutout.load(input); cutout.applyFilter("output_cutout.jpg");
        Image blur    = new JPGImage(new BlurFilter());    blur.load(input);    blur.applyFilter("output_blur.jpg");
        Image sharpen = new JPGImage(new SharpenFilter()); sharpen.load(input); sharpen.applyFilter("output_sharpen.jpg");
        Image texture = new JPGImage(new TextureFilter()); texture.load(input); texture.applyFilter("output_texture.jpg");
        Image canny   = new JPGImage(new CannyFilter());   canny.load(input);   canny.applyFilter("output_canny.jpg");
    }
}
