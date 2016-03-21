package com.silence.spacewar.domain;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

//游戏对象的父类，所有游戏对象都继承自它
abstract public class GameObject {

	protected Point point;// 对象在窗口的坐标

	public GameObject(int x, int y) {
		point = new Point(x, y);
	}

	// 绘制游戏对象
	abstract public boolean draw(Graphics g, JPanel panel, boolean pause);

	// 得到游戏对象的矩形，碰撞检测使用
	abstract public Rectangle getRect();

	public Point getPoint() {
		return point;
	}

	public void setPoint(Point point) {
		this.point = point;
	}

	// 加载游戏对象图片
	public static boolean loadImage(BufferedImage image, String source) {
		try {
			image = ImageIO.read(new File(source));
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}
