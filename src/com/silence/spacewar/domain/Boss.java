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

import com.silence.spacewar.SpaceWar;
import com.silence.spacewar.utils.ImageUtil;

public class Boss extends Enemy {

	public static final int BOSS_HEIGHT = 250;
	public static final int BOSS_WIDTH = 360;
	public static final int BOSS_SPEED = 3;
	public static final int BOSS_LIFE = 100;

	boolean bossLoadOK;// Boss是否出场完毕
	// Boss图像
	static List<BufferedImage> imagesBoss = new ArrayList<BufferedImage>();

	public Boss(int direction) {
		super(BOSS_SPEED, direction);
		this.life = BOSS_LIFE;
		point.x = SpaceWar.WINDOWS_WIDTH / 2;
		point.y = -BOSS_HEIGHT;
		bossLoadOK = false;
	}

	@Override
	public boolean draw(Graphics g, JPanel panel, boolean pause) {
		if (!pause) {
			// 绘制Boss
			if (bossLoadOK == false) {
				// 还未出场完毕，只改纵坐标
				point.y += speed;
				if (point.y >= 30)
					bossLoadOK = true;
				g.drawImage(imagesBoss.get(0), point.x, point.y, panel);
				return false;
			} else {
				// Boss出场完毕，只改横坐标
				point.x += BOSS_SPEED * direction;
				if (point.x > SpaceWar.WINDOWS_WIDTH - BOSS_WIDTH) {
					direction = -1;
					point.x = SpaceWar.WINDOWS_WIDTH - BOSS_WIDTH;
				}
				if (point.x < 0) {
					direction = 1;
					point.x = 0;
				}
				g.drawImage(imagesBoss.get(0), point.x, point.y, panel);
				return true;
			}
		} else
			return false;
	}

	public boolean draw(Graphics g, JPanel panel, int passNum, boolean pause) {
		if (!pause) {
			int index = passNum % 5;
			// 绘制Boss
			if (bossLoadOK == false) {
				// 还未出场完毕，只改纵坐标
				point.y += speed;
				if (point.y >= 30)
					bossLoadOK = true;
				g.drawImage(imagesBoss.get(index), point.x, point.y, panel);
				return false;
			} else {
				// Boss出场完毕，只改横坐标
				point.x += BOSS_SPEED * direction;
				if (point.x > SpaceWar.WINDOWS_WIDTH - BOSS_WIDTH) {
					direction = -1;
					point.x = SpaceWar.WINDOWS_WIDTH - BOSS_WIDTH;
				}
				if (point.x < 0) {
					direction = 1;
					point.x = 0;
				}
				g.drawImage(imagesBoss.get(index), point.x, point.y, panel);
				return true;
			}
		} else
			return false;
	}

	public static boolean loadImageBoss() {
		try {
			BufferedImage temp = ImageIO.read(new File("images/boss.bmp"));
			temp = ImageUtil.createImageByMaskColorEx(temp, new Color(0, 0, 0));
			for (int i = 0; i < 5; i++) {
				BufferedImage image = temp.getSubimage(i * BOSS_WIDTH, 0,
						BOSS_WIDTH, BOSS_HEIGHT);
				imagesBoss.add(image);
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	};

	@Override
	public Rectangle getRect() {
		return new Rectangle(point.x, point.y, BOSS_WIDTH, BOSS_HEIGHT);
	}

	public boolean isBossLoadOK() {
		return bossLoadOK;
	}

	public void setBossLoadOK(boolean bossLoadOK) {
		this.bossLoadOK = bossLoadOK;
	}

	public static List<BufferedImage> getImagesBoss() {
		return imagesBoss;
	}

	public static void setImagesBoss(List<BufferedImage> imagesBoss) {
		Boss.imagesBoss = imagesBoss;
	}

}
