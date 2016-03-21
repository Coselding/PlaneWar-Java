package com.silence.spacewar.domain;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import com.silence.spacewar.utils.ImageUtil;

public class Explosion extends GameObject {

	public static final int EXPLOSION_WIDTH = 66;
	public static final int EXPLOSION_NUM = 8;
	public static final int EXPLOSION_START = 0;

	// 炸弹的图片列表
	public static List<BufferedImage> images = new ArrayList<BufferedImage>();
	// 图像索引的步进计数
	public int imagesIndex;// 取值为0-7
	public boolean bossDie;// 标记该爆炸是Boss死亡爆炸

	public Explosion(int x, int y) {
		super(x, y);
		bossDie = false;
		imagesIndex = 0;
	}

	@Override
	public boolean draw(Graphics g, JPanel panel, boolean pause) {
		if (!pause) {
			if (imagesIndex < EXPLOSION_NUM) {
				boolean b = g.drawImage(images.get(imagesIndex), point.x,
						point.y, panel);
				imagesIndex++;
				return true;
			} else
				return false;
		} else
			return false;
	}

	public static boolean loadImage() {
		try {
			BufferedImage temp = ImageIO.read(new File("images/explosion.bmp"));
			temp = ImageUtil.createImageByMaskColorEx(temp, new Color(0, 0, 0));
			for (int i = 0; i < 8; i++) {
				BufferedImage image = temp.getSubimage(i * EXPLOSION_WIDTH, 0,
						EXPLOSION_WIDTH, EXPLOSION_WIDTH);
				images.add(image);
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public Rectangle getRect() {
		return new Rectangle(point.x, point.y, EXPLOSION_WIDTH, EXPLOSION_WIDTH);
	}

	public static List<BufferedImage> getImages() {
		return images;
	}

	public static void setImages(List<BufferedImage> images) {
		Explosion.images = images;
	}

	public int getImagesIndex() {
		return imagesIndex;
	}

	public void setImagesIndex(int imagesIndex) {
		this.imagesIndex = imagesIndex;
	}

	public boolean isBossDie() {
		return bossDie;
	}

	public void setBossDie(boolean bossDie) {
		this.bossDie = bossDie;
	}

}
