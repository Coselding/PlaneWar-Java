package com.silence.spacewar.domain;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.PrimitiveIterator.OfDouble;

import javax.imageio.ImageIO;

import com.silence.spacewar.SpaceWar;
import com.silence.spacewar.utils.AudioUtil;
import com.silence.spacewar.utils.ImageUtil;

//场景类
public class Scene {

	private int beginY;// 背景的Y坐标
	private List<BufferedImage> images;

	public Scene() {
		this.images = new ArrayList<BufferedImage>();
	}

	// 初始化场景
	public boolean initScene() {
		// 加载开始图片
		BufferedImage buffer;
		try {
			buffer = ImageUtil.copyImage(ImageIO.read(new File(
					"images/start.bmp")));
			this.images.add(buffer);
			// 如果加载失败, 返回false
			for (int i = 1; i <= 6; i++) {
				buffer = ImageUtil.copyImage(ImageIO.read(new File(
						"images/background" + i + ".bmp")));
				this.images.add(buffer);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		// 背景起始坐标为0
		this.beginY = 0;

		// 播放背景音乐
		AudioUtil.playBackground();
		return true;
	}

	// 绘制场景
	public void stickScene(Graphics graphics, int index, ImageObserver observer) {
		if (index == -1)
			index = 0;
		else
			index = index % 6 + 1;
		BufferedImage image = images.get(index);
		// 窗口滑在图片中间
		if (beginY >= 0
				&& beginY + SpaceWar.WINDOWS_HEIGHT <= image.getHeight()) {
			BufferedImage buffer = image.getSubimage(0, beginY,
					image.getWidth(), SpaceWar.WINDOWS_HEIGHT);
			graphics.drawImage(buffer, 0, 0, SpaceWar.WINDOWS_WIDTH,
					SpaceWar.WINDOWS_HEIGHT, observer);
		} else if (beginY < 0) {
			// 超出图片上界
			BufferedImage imageUp = image.getSubimage(0, image.getHeight()
					+ beginY, image.getWidth(), -beginY);
			graphics.drawImage(imageUp, 0, 0, SpaceWar.WINDOWS_WIDTH, -beginY,
					observer);
			graphics.drawImage(image, 0, -beginY, SpaceWar.WINDOWS_WIDTH,
					SpaceWar.WINDOWS_HEIGHT, observer);
			if (-beginY > SpaceWar.WINDOWS_HEIGHT) {
				beginY = image.getHeight() + beginY;
			}
		}
	}

	// 移动背景
	public void moveBg() {
		// 移动背景
		beginY -= 1;
	}

	// 释放内存资源
	public void releaseScene() {
		for (int i = 0; i < 7; i++)
			if (images.get(i) != null)
				images.get(i).flush();
		// 关闭背景音乐
		AudioUtil.stopBackground();
	}

	public int getBeginY() {
		return beginY;
	}

	public void setBeginY(int beginY) {
		this.beginY = beginY;
	}
}
