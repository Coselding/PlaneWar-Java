package com.silence.spacewar.utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public class ImageUtil {

	/*
	 * 构建imageSrc的拷贝，象素颜色为mask的显示为透明
	 * 
	 * @param imageSrc 原始图像
	 * 
	 * @param mask 无论原始图像的色彩模式为何种模式，mask统一传入一个 Color类型的对象指定希望显示为透明的色彩值
	 * 
	 * @return 返回imageSrc的拷贝，象素颜色为mask的显示为透明
	 */
	public static BufferedImage createImageByMaskColorEx(
			BufferedImage imageSrc, Color mask) {
		int x, y;
		x = imageSrc.getWidth(null);
		y = imageSrc.getHeight(null);
		Raster rasterSrc = imageSrc.getRaster();
		BufferedImage imageDes = new BufferedImage(x, y,
				BufferedImage.TYPE_4BYTE_ABGR);
		WritableRaster rasterDes = imageDes.getRaster();

		int[] src = null;
		int[] des = new int[4];

		ColorModel cm = imageSrc.getColorModel();
		Color cmask = (Color) mask;
		Object data = null;
		int maskR, maskG, maskB;
		maskR = cmask.getRed();
		maskG = cmask.getGreen();
		maskB = cmask.getBlue();
		while (--x >= 0)
			for (int j = 0; j < y; ++j) {
				data = rasterSrc.getDataElements(x, j, null);
				int rgb = cm.getRGB(data);
				int sr, sg, sb;
				sr = (rgb & 0xFF0000) >> 16;
				sg = (rgb & 0xFF00) >> 8;
				sb = rgb & 0xFF;
				if (sr == maskR && sg == maskG && sb == maskB)
					des[3] = 0;
				else {
					des[0] = sr;
					des[1] = sg;
					des[2] = sb;
					des[3] = 255;
				}
				rasterDes.setPixel(x, j, des);
			}
		return imageDes;
	}

	// 把两份image图片首尾拼接成一份背景长图
	public static BufferedImage copyImage(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		int[] imgArray = new int[width * height];
		BufferedImage imgNew = new BufferedImage(width, height * 2,
				BufferedImage.TYPE_INT_RGB);
		image.getRGB(0, 0, width, height, imgArray, 0, width);
		imgNew.setRGB(0, 0, width, height, imgArray, 0, width);
		imgNew.setRGB(0, height, width, height, imgArray, 0, width);
		return imgNew;
	}
}
