package com.spider.util;
import com.sun.image.codec.jpeg.JPEGCodec;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import com.sun.image.codec.jpeg.JPEGImageDecoder;
/**
 * 旨在获取图片签名指纹(去掉重复的图片)
 */
public class PicTailUtil {
    private final  static int default_w =8;
    private final  static  int default_h =8;
    public static String produceFingerPrint(String ossUrl){
        return produceFingerPrint(ossUrl,default_w,default_h);
    }

    public static String produceFingerPrint(String ossUrl,int w,int h){
        BufferedImage source = readJPEGImage(ossUrl);
        if(source==null)return null;
        BufferedImage thumb = thumb(source,w,h);
        int[] pixels = new int[w * h];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                pixels[i * w + j] = rgbToGray(thumb.getRGB(i, j));
            }
        }
        int avgPixel = average(pixels);
        int[] comps = new int[w*h];
        for (int i = 0; i < comps.length; i++) {
            if (pixels[i] >= avgPixel) {
                comps[i] = 1;
            }else{
                comps[i] = 0;
            }
        }
        StringBuffer hashCode = new StringBuffer();
        for (int i = 0; i < comps.length; i+= 4) {
            int result = comps[i] * (int) Math.pow(2, 3) + comps[i + 1] * (int) Math.pow(2, 2) + comps[i + 2] * (int) Math.pow(2, 1) + comps[i + 2];
            hashCode.append(binaryToHex(result));
        }
        return hashCode.toString();
    }

    private static char binaryToHex(int binary){
        char ch = ' ';
        if(binary<10)return String.valueOf(binary).charAt(0);
        if(binary==10)return "a".charAt(0);
        if(binary==11)return "b".charAt(0);
        if(binary==12)return "c".charAt(0);
        if(binary==13)return "d".charAt(0);
        if(binary==14)return "e".charAt(0);
        if(binary==15)return "f".charAt(0);
        return ch;

    }

    private static int average(int[] pixels) {
        float m = 0;
        for (int i = 0; i < pixels.length; ++i) {
            m += pixels[i];
        }
        m = m / pixels.length;
        return (int) m;
    }

    public static int rgbToGray(int pixels){
        int _red = (pixels>>16)&0xFF;
        int _green = (pixels >> 8) & 0xFF;
        int _blue = (pixels) & 0xFF;
        return (int) (0.3 * _red + 0.59 * _green + 0.11 * _blue);
    }

    /**
     * 生产8*8的缩微图
     * @param source
     * @return
     */
    public static BufferedImage thumb(BufferedImage source,int w,int h){
        int type = source.getType();
        BufferedImage target = null;
        double sx = (double) w / source.getWidth();
        double sy = (double) h / source.getHeight();
        if(type==BufferedImage.TYPE_CUSTOM){
            ColorModel cm = source.getColorModel();
            WritableRaster raster = cm.createCompatibleWritableRaster(w, h);
            boolean alphaPremultiplied = cm.isAlphaPremultiplied();
            target = new BufferedImage(cm, raster, alphaPremultiplied, null);
        }else{
            target = new BufferedImage(w, h, type);
            Graphics2D g = target.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.drawRenderedImage(source, AffineTransform.getScaleInstance(sx, sy));
            g.dispose();
        }
        return target;
    }

    public static BufferedImage readJPEGImage(String ossUrl){
        URL url = null;
        try {
            url = new URL(ossUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(60 * 1000);
            InputStream inStream = conn.getInputStream();// 通过输入流获取图片数据
            JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder(inStream);
            return decoder.decodeAsBufferedImage();
        } catch (IOException e) {
            e.printStackTrace();
        }

              return null;
    }
    public static void print(String url){
        String printTail = produceFingerPrint(url);
        System.out.println(printTail);
    }

    public static void main(String[] args) {
        print("http://gd2.alicdn.com/imgextra/i4/2058024952/O1CN011mS3nCQ2XUkRCc0_!!2058024952.jpg");
        print("http://gd3.alicdn.com/imgextra/i2/2058024952/O1CN01EXmsVJ1mS3nTzru2m_!!2058024952.jpg");
    }
}
