package com.silence.spacewar;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.beans.PropertyChangeListener;
import java.io.IOException;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;

import com.silence.spacewar.domain.Ball;
import com.silence.spacewar.domain.Bomb;
import com.silence.spacewar.domain.Boss;
import com.silence.spacewar.domain.Enemy;
import com.silence.spacewar.domain.Explosion;
import com.silence.spacewar.domain.MyPlane;
import com.silence.spacewar.listener.MyKeyListener;
import com.silence.spacewar.utils.AudioUtil;

public class SpaceWar {

	// 全局窗口大小
	public static int WINDOWS_HEIGHT = 600;
	public static int WINDOWS_WIDTH = 900;
	private JFrame frame;
	// 单例模式
	static {
		new SpaceWar().start();
	}

	// 构造函数
	private SpaceWar() {
		gameInit();
	}

	private void start() {
		// 显示窗口
		frame.setVisible(true);
	}

	private void gameInit() {
		// 设置全局窗口大小
		WINDOWS_HEIGHT = 600;
		WINDOWS_WIDTH = 900;
		// 设置窗口初始化参数
		frame = new JFrame();
		frame.setSize(WINDOWS_WIDTH, WINDOWS_HEIGHT);
		frame.setLocation(200, 50);
		frame.setTitle("飞机大战      ———— 林宇强");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// 窗口添加显示面板
		MyPanel panel = new MyPanel();
		frame.add(panel);
		// 添加窗口事件监听
		addListener(frame);
	}

	// 添加事件监听
	private void addListener(JFrame frame) {
		// 按键监听
		frame.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyPressed(KeyEvent event) {
				if (MyPanel.myplane != null && !MyPanel.isPause) {
					switch (event.getKeyCode()) {
					case KeyEvent.VK_LEFT:
						int x = MyPanel.myplane.getPoint().x
								- MyPanel.DEFAULT_SPEED;
						if (x < 0)
							x = 0;
						MyPanel.myplane.setPoint(new Point(x, MyPanel.myplane
								.getPoint().y));
						break;
					case KeyEvent.VK_RIGHT:
						int x1 = MyPanel.myplane.getPoint().x
								+ MyPanel.DEFAULT_SPEED;
						if (x1 > SpaceWar.WINDOWS_WIDTH - Bomb.BOMB_WIDTH)
							x1 = SpaceWar.WINDOWS_WIDTH - Bomb.BOMB_WIDTH;
						MyPanel.myplane.setPoint(new Point(x1, MyPanel.myplane
								.getPoint().y));
						break;
					case KeyEvent.VK_UP:
						int y = MyPanel.myplane.getPoint().y
								- MyPanel.DEFAULT_SPEED;
						if (y < 0)
							y = 0;
						MyPanel.myplane.setPoint(new Point(MyPanel.myplane
								.getPoint().x, y));
						break;
					case KeyEvent.VK_DOWN:
						int y1 = MyPanel.myplane.getPoint().y
								+ MyPanel.DEFAULT_SPEED;
						if (y1 > SpaceWar.WINDOWS_HEIGHT)
							y1 = SpaceWar.WINDOWS_HEIGHT;
						MyPanel.myplane.setPoint(new Point(MyPanel.myplane
								.getPoint().x, y1));
						break;
					case KeyEvent.VK_SPACE:
						Bomb bomb1 = new Bomb(
								MyPanel.myplane.getPoint().x + 10,
								MyPanel.myplane.getPoint().y, 1,
								MyPanel.isUpdate);
						MyPanel.bombList.add(bomb1);
						Bomb bomb2 = new Bomb(MyPanel.myplane.getPoint().x
								+ MyPlane.PLANE_WIDTH - 40, MyPanel.myplane
								.getPoint().y, 1, MyPanel.isUpdate);
						MyPanel.bombList.add(bomb2);
						// 音效
						AudioUtil.play(AudioUtil.AUDIO_BOMB);
						break;
					case KeyEvent.VK_C:
						// 开启防护罩
						MyPanel.isProtect = true;
						// 音效
						AudioUtil.play(AudioUtil.AUDIO_PROTECT);
						break;
					case KeyEvent.VK_V:
						// 战机升级
						MyPanel.isUpdate = true;
						MyPanel.myplane.isUpdate = true;
						// 音效
						AudioUtil.play(AudioUtil.AUDIO_UPDATE);
						break;
					case KeyEvent.VK_Y:
						if (MyPanel.isStop == 0) {
							// 无敌模式开关
							if (MyPanel.test == false)
								MyPanel.test = true;
							else
								MyPanel.test = false;
						}
						break;
					case KeyEvent.VK_X:// 大招
						if (MyPanel.bossLoaded) {
							// 战机发大招
							if (MyPanel.magicCount >= 10) {
								MyPanel.magicCount -= 10;
								// 清空敌机
								for (int i = 0; i < MyPanel.enemyList.size(); i++) {
									Enemy enemy = MyPanel.enemyList.get(i);
									// 将爆炸对象添加到爆炸链表中
									Explosion explosion = new Explosion(
											(enemy.getPoint().x + Enemy.ENEMY_WIDTH / 2),
											(enemy.getPoint().y + Enemy.ENEMY_HEIGHT / 2));
									MyPanel.explosionList.add(explosion);

									// 删除敌机
									MyPanel.enemyList.remove(i);
									// 增加得分
									MyPanel.passScore++;
								}// for
								if (MyPanel.isBoss) {
									// 将爆炸对象添加到爆炸链表中
									Explosion explosion = new Explosion(
											MyPanel.boss.getPoint().x
													+ Boss.BOSS_WIDTH / 2,
											MyPanel.boss.getPoint().y
													+ Boss.BOSS_HEIGHT / 2);
									MyPanel.explosionList.add(explosion);
									MyPanel.bossBlood -= 50;
									if (MyPanel.bossBlood <= 0) {
										// boss死，过关
										// 过关的标志变量
										MyPanel.boss = null;
										// 过关的标志变量
										MyPanel.isPause = true;
										MyPanel.myplane = new MyPlane(false);
										MyPanel.isPass = true;
										MyPanel.isBoss = false;
									}
								}
								// 清空敌机炮弹
								for (int i = 0; i < MyPanel.ballList.size(); i++) {
									MyPanel.ballList.remove(i);
								}
								// 音效
								AudioUtil.play(AudioUtil.AUDIO_DAZHAO);
							}
						}
						break;
					default:
						break;
					}
				}
				// 暂停
				if (event.getKeyCode() == KeyEvent.VK_Z) {
					if (MyPanel.isPause)
						MyPanel.isPause = false;
					else
						MyPanel.isPause = true;
				}
				// 取消键N
				else if (event.getKeyCode() == KeyEvent.VK_N) {
					if (MyPanel.isStop != 0) {
						JDialog dialog = new JDialog(frame);
						dialog.setTitle("关于");
						dialog.setSize(400, 200);
						dialog.setLocation(450, 200);
						JButton button = new JButton();
						button.setText("确定");
						button.setSize(100, 50);
						button.setLocation(200, 120);

						button.addActionListener(new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent e) {
								System.exit(0);
							}
						});
						dialog.add(button);
						dialog.setVisible(true);
					}
				}
				// 确认键Y
				if (MyPanel.isStop != 0 && event.getKeyCode() == KeyEvent.VK_Y) {
					MyPanel.isStop = 0;
					MyPanel.Restart();
				}
				// 按空格进入游戏
				if (!MyPanel.isStarted
						&& event.getKeyCode() == KeyEvent.VK_SPACE) {
					MyPanel.isStarted = true;
					MyPanel.scene.setBeginY(0);
				}
			}
		});
		// 鼠标点击
		frame.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent arg0) {

			}

			@Override
			public void mousePressed(MouseEvent event) {
				if (MyPanel.myplane != null && !MyPanel.isPause) {
					if (event.getButton() == MouseEvent.BUTTON1) {
						// 左键
						if (MyPanel.myplane != null && !MyPanel.isPause) {
							Bomb bomb1 = new Bomb(
									MyPanel.myplane.getPoint().x + 10,
									MyPanel.myplane.getPoint().y, 1,
									MyPanel.isUpdate);
							MyPanel.bombList.add(bomb1);
							Bomb bomb2 = new Bomb(MyPanel.myplane.getPoint().x
									+ MyPlane.PLANE_WIDTH - 40, MyPanel.myplane
									.getPoint().y, 1, MyPanel.isUpdate);
							MyPanel.bombList.add(bomb2);
							// 音效
							AudioUtil.play(AudioUtil.AUDIO_BOMB);
						}

						if (!MyPanel.isStarted) {
							MyPanel.isStarted = true;
							MyPanel.scene.setBeginY(0);
						}
					}
				}
			}

			@Override
			public void mouseExited(MouseEvent arg0) {

			}

			@Override
			public void mouseEntered(MouseEvent arg0) {

			}

			@Override
			public void mouseClicked(MouseEvent arg0) {
			}
		});

		// 鼠标移动
		frame.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseMoved(MouseEvent arg0) {
				if (MyPanel.myplane != null && !MyPanel.isPause)
					MyPanel.myplane.setPoint(arg0.getPoint());
			}

			@Override
			public void mouseDragged(MouseEvent arg0) {

			}
		});
	}
}
