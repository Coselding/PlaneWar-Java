package com.silence.spacewar;

import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import com.silence.spacewar.domain.Ball;
import com.silence.spacewar.domain.Blood;
import com.silence.spacewar.domain.Bomb;
import com.silence.spacewar.domain.Boss;
import com.silence.spacewar.domain.Enemy;
import com.silence.spacewar.domain.Explosion;
import com.silence.spacewar.domain.MyPlane;
import com.silence.spacewar.domain.Scene;
import com.silence.spacewar.task.EnemyTask;
import com.silence.spacewar.task.MagicTask;
import com.silence.spacewar.task.RefreshTask;
import com.silence.spacewar.utils.AudioUtil;
import com.silence.spacewar.utils.ImageUtil;

public class MyPanel extends JPanel {

	public static final int DEFAULT_SPEED = 30;
	public static final int DEFAULT_LIFE = 10;
	public static final int DEFAULT_LIFE_COUNT = 3;
	public static final int DEFAULT_PASS = 1;
	public static final int BOMB_DISTANCE = 35;
	public static final int STEP = 30;
	public static final int PASS_SCORE = 20;
	public static final int FLAG_RESTART = 2;
	public static final int FLAG_STOP = 3;

	// Scene scene;//场景

	// 创建各游戏对象
	public static MyPlane myplane = null;
	Enemy enemy = null;
	public static Boss boss = null;
	Bomb bomb = null;
	Ball ball = null;
	Explosion explosion = null;
	Blood blood = null;

	// 创建存储游戏对象的对象列表
	public static List<Enemy> enemyList = new ArrayList<Enemy>();
	public static List<MyPlane> meList = new ArrayList<MyPlane>();
	public static List<Bomb> bombList = new ArrayList<Bomb>();
	public static List<Ball> ballList = new ArrayList<Ball>();
	public static List<Explosion> explosionList = new ArrayList<Explosion>();
	public static List<Blood> bloodList = new ArrayList<Blood>();

	int speed;// 战机的速度，方向键控制
	public static int myLife;// 为战机设置生命值
	public static int lifeNum;// 战机命条数
	public static int myScore;// 战机的得分
	public static int passScore;// 当前关卡得分数
	public static int lifeCount;// 血包产生控制参数
	public static boolean bloodExist;// 标记屏幕中是否存在血包
	public static int magicCount;// 魔法值，控制能否发大招
	public static int bossBlood;// Boss血量

	public static int passNum;// 记录当前关卡
	public static boolean isPass;// 是否通关的标志
	public static boolean isPause;// 是否暂停
	public static boolean isBoss;// 标记是否进入Boss
	public static boolean bossLoaded;// 标记Boss出场完成
	public static boolean isProtect;// 标记是否开启防护罩
	public static boolean isUpdate;// 标记战机是否升级
	public static boolean test;// 无敌模式参数
	public static int isStop;// 标记游戏停止
	public static boolean isStarted;// 标记欢迎界面是否加载完成

	public static Timer enemyTimer;
	public static Timer painTimer;
	public static Timer magicTimer;

	private static BufferedImage titleImage;
	public static Scene scene;

	static {
		// 加载标题图片
		try {
			titleImage = ImageUtil.createImageByMaskColorEx(ImageIO
					.read(new File("images/title.bmp")), new Color(0, 0, 0));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	// List<BufferedImage> startIMG;
	public MyPanel() {
		// -----------初始化工作------------
		// 加载游戏对象图片
		MyPlane.loadImage();
		Enemy.loadImage();
		Boss.loadImageBoss();
		Ball.loadImage();
		Bomb.loadImage();
		Explosion.loadImage();
		Blood.loadImage();

		// 滚动背景
		scene = new Scene();
		// 场景初始化失败
		if (!scene.initScene()) {
			JDialog dialog = new JDialog();
			dialog.setTitle("图片加载失败！！！");
			dialog.setLocation(300, 150);
			dialog.setSize(200, 100);
			JButton button = new JButton("确定");
			button.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					System.exit(0);
				}
			});
			dialog.add(button);
			dialog.setVisible(true);
		}

		// 参数初始化
		myplane = new MyPlane(false);
		isBoss = false;
		speed = DEFAULT_SPEED;
		myLife = DEFAULT_LIFE;
		lifeNum = DEFAULT_LIFE_COUNT;
		lifeCount = 1;
		passScore = 0;
		myScore = 0;
		bossLoaded = true;
		passNum = DEFAULT_PASS;
		isPass = false;
		isPause = false;
		magicCount = 0;
		bloodExist = false;
		bossBlood = Boss.BOSS_LIFE;
		isProtect = false;
		isUpdate = false;
		test = false;
		isStop = 0;
		boss = null;
		isStarted = false;
		// 界面刷新计时器
		painTimer = new Timer();
		painTimer.schedule(new RefreshTask(this), 0, 15);// 刷新界面定时器
		// 其他计时器
		initTimer();
	}

	private static void initTimer() {
		enemyTimer = new Timer();
		enemyTimer.schedule(new EnemyTask(enemyList), 0, 400 - passNum * 30);// 产生敌机定时器
		magicTimer = new Timer();
		magicTimer.schedule(new MagicTask(), 0, 2000);// 控制魔法值变化频率
	}

	public static void killTimer() {
		enemyTimer.cancel();
		// painTimer.cancel();
		magicTimer.cancel();
		enemyTimer = null;
		// painTimer = null;
		magicTimer = null;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		// 背景图片
		// g.drawImage(bkImage, 0, 0, App.WINDOWS_WIDTH, App.WINDOWS_HEIGHT,
		// this);
		// 欢迎界面
		if (!isStarted) {
			// 滚动背景
			scene.stickScene(g, -1, this);
			scene.moveBg();
			// 飞机大战标题图片
			g.drawImage(titleImage, SpaceWar.WINDOWS_WIDTH / 2 - 173, 100,
					titleImage.getWidth(), titleImage.getHeight(), this);
			// 文字说明
			Font font = new Font("宋体", Font.PLAIN, 12);
			g.setColor(new Color(128, 128, 0));
			g.setFont(font);
			g.drawString("方向控制：方向键、ASDW、鼠标", SpaceWar.WINDOWS_WIDTH / 2 - 150,
					210);
			g.drawString("射击：空格键、鼠标左键", SpaceWar.WINDOWS_WIDTH / 2 - 150, 225);
			g.drawString("暂停：Z键", SpaceWar.WINDOWS_WIDTH / 2 - 150, 240);
			g.drawString("大招：X键", SpaceWar.WINDOWS_WIDTH / 2 - 150, 255);
			g.drawString("防护罩：C键", SpaceWar.WINDOWS_WIDTH / 2 - 150, 270);
			g.drawString("战机升级：V键", SpaceWar.WINDOWS_WIDTH / 2 - 150, 285);
			g.drawString("无敌模式：Y键", SpaceWar.WINDOWS_WIDTH / 2 - 150, 300);
			g.drawString("初始生命值：10", SpaceWar.WINDOWS_WIDTH / 2 - 150, 315);
			g.drawString("初始魔法值：0", SpaceWar.WINDOWS_WIDTH / 2 - 150, 330);
			g.drawString("敌机生命值：2", SpaceWar.WINDOWS_WIDTH / 2 - 150, 345);
			g.drawString("消灭一个敌机加1分，如果分数达到要求即可进入Boss模式，打赢Boss即可进入下一关。",
					SpaceWar.WINDOWS_WIDTH / 2 - 150, 360);
			g.drawString("魔法值随着游戏进程增加，可通过使用魔法值使用防护罩、战机升级、战机大招的使用。",
					SpaceWar.WINDOWS_WIDTH / 2 - 150, 375);
			g.drawString("游戏过程中会有一定程度的血包出现以恢复生命值。",
					SpaceWar.WINDOWS_WIDTH / 2 - 150, 390);
			g.drawString("随着关卡增多，敌机、炮弹速度和数量均增加，通过10关即可通关！",
					SpaceWar.WINDOWS_WIDTH / 2 - 150, 405);
			font = new Font("黑体", Font.BOLD, 24);
			g.setFont(font);
			g.setColor(Color.red);
			g.drawString("点击鼠标左键或空格键开始游戏", SpaceWar.WINDOWS_WIDTH / 2 - 150,
					450);
			return;
		} else {
			scene.stickScene(g, passNum, this);
			scene.moveBg();
		}

		// 显示暂停信息
		if (myplane != null && isPause && isStop != 0 && isStop == 0) {
			Font textFont = new Font("宋体", Font.BOLD, 20);
			g.setFont(textFont);
			g.setColor(Color.red);
			// 设置透明背景
			// cdc.SetBkMode(TRANSPARENT);
			g.drawString("暂停", SpaceWar.WINDOWS_WIDTH / 2 - 10, 150);
			return;
		}

		// 游戏界面输出该游戏当前信息
		if (myplane != null) {
			Font textFont = new Font("宋体", Font.BOLD, 15);
			g.setFont(textFont);
			g.setColor(Color.red);
			// 设置透明背景
			// cdc.SetBkMode(TRANSPARENT);
			g.drawString("当前关卡:" + passNum, 10, 20);
			g.drawString("当前命数:" + lifeNum, 110, 20);
			g.drawString("当前得分:" + passScore, 10, 35);
			if (test) {
				g.drawString("无敌模式！！！", 10, 220);
			}

			textFont = new Font("宋体", Font.BOLD, 12);
			g.setFont(textFont);
			g.drawString("血量：",
					SpaceWar.WINDOWS_WIDTH - 12 * DEFAULT_LIFE - 45, 20);

			// 输出血条
			g.setColor(Color.red);
			int leftPos, topPos = 10, width, height = 12;
			leftPos = SpaceWar.WINDOWS_WIDTH - 12 * DEFAULT_LIFE;
			width = 12 * myLife;
			g.fillRect(leftPos, topPos, width, height);

			textFont = new Font("宋体", Font.BOLD, 12);
			g.setFont(textFont);
			g.setColor(Color.blue);
			g.drawString("魔法：",
					SpaceWar.WINDOWS_WIDTH - 12 * DEFAULT_LIFE - 45, 35);

			// 输出魔法值
			g.setColor(Color.blue);
			topPos = 25;
			height = 12;
			leftPos = SpaceWar.WINDOWS_WIDTH - 12 * DEFAULT_LIFE;
			width = 12 * magicCount;
			g.fillRect(leftPos, topPos, width, height);

			// 输出Boss血条
			if (isBoss) {
				g.setColor(new Color(128, 0, 128));
				topPos = 10;
				height = 15;
				leftPos = SpaceWar.WINDOWS_WIDTH / 2 - 100;
				width = bossBlood / (boss.life / 10) * 20;
				g.fillRect(leftPos, topPos, width, height);
			}

			// 输出血条中的详细血值
			textFont = new Font("宋体", Font.BOLD, 12);
			g.setFont(textFont);
			g.setColor(Color.white);
			g.drawString(DEFAULT_LIFE + "/" + myLife, SpaceWar.WINDOWS_WIDTH
					- 12 * DEFAULT_LIFE + 48, 20);
			g.drawString(DEFAULT_LIFE + "/" + magicCount,
					SpaceWar.WINDOWS_WIDTH - 12 * DEFAULT_LIFE + 48, 35);
			if (isBoss) {
				g.drawString(boss.life + "/" + bossBlood,
						SpaceWar.WINDOWS_WIDTH / 2 - 20, 22);
			}

			// 显示当前能发动的道具
			textFont = new Font("宋体", Font.BOLD, 24);
			g.setFont(textFont);
			g.setColor(Color.white);
			if (magicCount > 0) {
				g.drawString("按C可打开防护罩", 0, SpaceWar.WINDOWS_HEIGHT - 100);
				g.drawString("按V可升级战机", 0, SpaceWar.WINDOWS_HEIGHT - 75);
			}
			if (magicCount >= 10) {
				g.drawString("按X可使用战机大招", 0, SpaceWar.WINDOWS_HEIGHT - 50);
			}
		}// if

		// 游戏停止和重开状态信息
		if (isStop == FLAG_RESTART) {
			Font textFont = new Font("宋体", Font.BOLD, 20);
			g.setFont(textFont);
			// 设置透明背景
			// cdc.SetBkMode(TRANSPARENT);
			g.setColor(Color.red);
			g.drawString("哇，恭喜你已通关！", SpaceWar.WINDOWS_WIDTH / 2 - 100,
					SpaceWar.WINDOWS_HEIGHT / 2 - 30);
			g.drawString("您的得分为：" + myScore, SpaceWar.WINDOWS_WIDTH / 2 - 100,
					SpaceWar.WINDOWS_HEIGHT / 2 - 10);
			g.drawString("COME ON ！重新开始？Y/N", SpaceWar.WINDOWS_WIDTH / 2 - 100,
					SpaceWar.WINDOWS_HEIGHT / 2 + 10);
			return;
		} else if (isStop == FLAG_STOP) {
			Font textFont = new Font("宋体", Font.BOLD, 20);
			g.setFont(textFont);
			// 设置透明背景
			// cdc.SetBkMode(TRANSPARENT);
			g.setColor(Color.red);
			// 显示最后结果
			g.drawString("GAME OVER！", SpaceWar.WINDOWS_WIDTH / 2 - 100,
					SpaceWar.WINDOWS_HEIGHT / 2 - 30);
			g.drawString("您的得分为：" + myScore, SpaceWar.WINDOWS_WIDTH / 2 - 100,
					SpaceWar.WINDOWS_HEIGHT / 2 - 10);
			g.drawString("COME ON ！重新开始？Y/N", SpaceWar.WINDOWS_WIDTH / 2 - 100,
					SpaceWar.WINDOWS_HEIGHT / 2 + 10);
			return;
		}

		// 显示Boss
		if (myplane != null && boss != null && !isPause && isBoss) {
			boolean status = boss.draw(g, this, passNum, isPause);
			if (status)
				bossLoaded = true;
		}

		// 刷新显示战机
		if (myplane != null) {
			myplane.draw(g, this, isPause, isProtect);
		}
		// 显示敌机
		for (int i = 0; i < enemyList.size(); i++) {
			enemy = enemyList.get(i);
			if (enemy == null)
				continue;
			enemy.setCurrentIndex(i);
			if (!enemy.draw(g, this, passNum, isPause))
				i--;
		}
		// 显示敌机子弹
		for (int i = 0; i < ballList.size(); i++) {
			ball = ballList.get(i);
			if (ball == null)
				continue;
			ball.setCurrentIndex(i);
			if (!ball.draw(g, this, passNum, isPause))
				i--;
		}
		// 显示我方子弹
		for (int i = 0; i < bombList.size(); i++) {
			bomb = bombList.get(i);
			if (bomb == null)
				continue;
			bomb.setCurrentIndex(i);
			bomb.isUpdate = isUpdate;
			if (!bomb.draw(g, this, isPause))
				i--;
		}
		// 显示爆炸效果
		for (int i = 0; i < explosionList.size(); i++) {
			explosion = explosionList.get(i);
			if (explosion == null)
				continue;
			boolean b = explosion.draw(g, this, isPause);
			if (!b) {
				explosionList.remove(i);
				i--;
			}
		}
		// 显示血包
		if (myplane != null && !isPause) {
			// 检索血包链表，非空时在所在位置显示
			int i = 0;
			while (i < bloodList.size()) {
				blood = bloodList.get(i);
				if (blood == null)
					continue;
				blood.draw(g, this, false);
				i++;
			}// while
		}
	}

	// 生命值归零，游戏结束
	public static void gameOver() {
		// 结束游戏界面
		// 释放计时器
		killTimer();
		// 计算最后得分
		myScore += passScore;
		// 播放游戏结束音乐
		// 清屏
		// 音效
		AudioUtil.play(AudioUtil.AUDIO_GAMEOVER);
		isStop = FLAG_STOP;
		// TODO
		System.out.println("-----------gameOver");
	}

	// 游戏重新开始
	public static void Restart() {
		// TODO: 在此处添加游戏重新开始初始化参数
		// 战机重新加载
		MyPanel.myplane = new MyPlane(false);

		scene.setBeginY(0);
		// 清空敌机链表
		if (MyPanel.enemyList.size() > 0)
			MyPanel.enemyList.removeAll(MyPanel.enemyList);
		// 清空战机链表
		if (MyPanel.meList.size() > 0)
			MyPanel.meList.removeAll(MyPanel.meList);
		// 清空战机子弹链表
		if (MyPanel.bombList.size() > 0)
			MyPanel.bombList.removeAll(MyPanel.bombList);
		// 清空敌机炸弹链表
		if (MyPanel.ballList.size() > 0)
			MyPanel.ballList.removeAll(MyPanel.ballList);
		// 清空爆炸链表
		if (MyPanel.explosionList.size() > 0)
			MyPanel.explosionList.removeAll(MyPanel.explosionList);
		// 清空血包列表
		if (MyPanel.bloodList.size() > 0)
			MyPanel.bloodList.removeAll(MyPanel.bloodList);

		// 参数重新初始化
		MyPanel.myLife = DEFAULT_LIFE;
		MyPanel.lifeNum = DEFAULT_LIFE_COUNT;
		MyPanel.myScore = 0;
		MyPanel.passScore = 0;
		MyPanel.passNum = DEFAULT_PASS;
		MyPanel.isPass = false;
		MyPanel.isPause = false;
		MyPanel.lifeCount = 1;
		MyPanel.magicCount = 0;
		MyPanel.bloodExist = false;
		MyPanel.bossBlood = Boss.BOSS_LIFE;
		MyPanel.isBoss = false;
		MyPanel.bossLoaded = true;
		MyPanel.isProtect = false;
		MyPanel.isUpdate = false;
		MyPanel.test = false;
		MyPanel.boss = null;
		// isStarted = FALSE;
		initTimer();
	}
}
