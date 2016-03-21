package com.silence.spacewar.domain;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import com.silence.spacewar.SpaceWar;
import com.silence.spacewar.MyPanel;
import com.silence.spacewar.utils.ImageUtil;

public class Ball extends GameObject {

	public static final int BALL_SPEED = 6;
	public static final int BALL_HEIGHT = 40;
	public static final int BALL_WIDTH = 15;
	// 向上和向下的敌机炮弹
	public static List<BufferedImage> imagesUp = new ArrayList<BufferedImage>();
	public static List<BufferedImage> imagesDown = new ArrayList<BufferedImage>();
	public int direction;// 控制炸弹的飞行方向
	public int ballSpeed;// 炸弹的速度
	private int currentIndex;

	public Ball(int x, int y, int direction) {
		super(x, y);
		this.direction = direction;
		this.ballSpeed = BALL_SPEED;
	}

	@Override
	public boolean draw(Graphics g, JPanel panel, boolean pause) {
		if (!pause) {
			// 炮弹纵向飞行，只需要更改纵坐标，先只实现炮弹由上到下飞行
			point.y += ballSpeed * this.direction;
			if (point.y < 0 || point.y > SpaceWar.WINDOWS_HEIGHT) {
				MyPanel.ballList.remove(currentIndex);
				return false;
			}
			// Boss炮弹随机变化
			int index = new Random().nextInt(5);
			if (direction == 1) {
				g.drawImage(imagesDown.get(0), point.x, point.y, panel);
			} else {
				g.drawImage(imagesUp.get(0), point.x, point.y, panel);
			}
			return true;
		} else
			return false;
	}

	public boolean draw(Graphics g, JPanel panel, int passNum, boolean pause) {
		// 敌机炮弹随关卡变化
		int index = (passNum - 1) / 2;
		if (!pause) {
			// 炮弹纵向飞行，只需要更改纵坐标，先只实现炮弹由上到下飞行
			point.y += ballSpeed * this.direction;
			if (point.y < 0 || point.y > SpaceWar.WINDOWS_HEIGHT) {
				MyPanel.ballList.remove(currentIndex);
				return false;
			}
			if (direction == 1) {
				g.drawImage(imagesDown.get(index), point.x, point.y, panel);
			} else {
				g.drawImage(imagesUp.get(index), point.x, point.y, panel);
			}
			return true;
		} else
			return false;
	}

	public static boolean loadImage() {
		try {
			BufferedImage temp = ImageIO.read(new File("images/balldown.bmp"));
			temp = ImageUtil.createImageByMaskColorEx(temp, new Color(0, 0, 0));
			for (int i = 0; i < 5; i++) {
				BufferedImage image = temp.getSubimage(i * BALL_WIDTH, 0,
						BALL_WIDTH, BALL_HEIGHT);
				imagesDown.add(image);
			}
			temp = ImageIO.read(new File("images/ballup.bmp"));
			temp = ImageUtil.createImageByMaskColorEx(temp, new Color(0, 0, 0));
			for (int i = 0; i < 5; i++) {
				BufferedImage image = temp.getSubimage(i * BALL_WIDTH, 0,
						BALL_WIDTH, BALL_HEIGHT);
				imagesUp.add(image);
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public Rectangle getRect() {
		return new Rectangle(point.x, point.y, BALL_WIDTH, BALL_HEIGHT);
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public int getBallSpeed() {
		return ballSpeed;
	}

	public void setBallSpeed(int ballSpeed) {
		this.ballSpeed = ballSpeed;
	}

	public int getCurrentIndex() {
		return currentIndex;
	}

	public void setCurrentIndex(int currentIndex) {
		this.currentIndex = currentIndex;
	}

}
