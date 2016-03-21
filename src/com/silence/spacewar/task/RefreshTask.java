package com.silence.spacewar.task;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

import com.silence.spacewar.MyPanel;
import com.silence.spacewar.domain.Ball;
import com.silence.spacewar.domain.Blood;
import com.silence.spacewar.domain.Bomb;
import com.silence.spacewar.domain.Boss;
import com.silence.spacewar.domain.Enemy;
import com.silence.spacewar.domain.Explosion;
import com.silence.spacewar.domain.MyPlane;
import com.silence.spacewar.utils.AudioUtil;

public class RefreshTask extends TimerTask {

	private JPanel panel;
	private Timer bloodTimer;

	public RefreshTask(JPanel panel) {
		this.panel = panel;
	}

	@Override
	public void run() {
		// 碰撞检测
		bombAndEnemy();
		ballAndMe();
		enemyAndMe();
		ballAndBomb();
		bloodAndMe();
		// 检测是否得分够进入Boss
		gotoBoss();
		// 检测是否过关
		checkPass();
		// 检测是否打开血包
		openBlood();
		// 刷新界面
		panel.repaint();
	}

	private void gotoBoss() {
		// 进入下一关界面
		int pScore = MyPanel.PASS_SCORE + MyPanel.passNum * 5;
		// TODO调试条件
		// if (MyPanel.myplane != null && MyPanel.passScore >= 3 &&
		// !MyPanel.isPause&&!MyPanel.isBoss)
		if (MyPanel.myplane != null && MyPanel.passScore >= pScore
				&& !MyPanel.isPause && !MyPanel.isBoss) {
			// 进入Boss
			MyPanel.isBoss = true;
			MyPanel.boss = new Boss(1);
			MyPanel.boss.setSpeed(Boss.BOSS_SPEED + MyPanel.passNum - 1);
			MyPanel.boss.life = Boss.BOSS_LIFE + MyPanel.passNum * 50;// Boss总血量
			MyPanel.bossBlood = Boss.BOSS_LIFE + MyPanel.passNum * 50;// 当前Boss血量
			// Boss出场，暂停游戏
			MyPanel.bossLoaded = false;

			// 重新设置Boss的子弹产生频率，增强Boss子弹发射频率
			MyPanel.enemyTimer.cancel();
			MyPanel.enemyTimer = null;
			MyPanel.enemyTimer = new Timer();
			MyPanel.enemyTimer.schedule(new EnemyTask(MyPanel.enemyList), 0,
					2000 - MyPanel.passNum * 120);
		}
	}

	private void openBlood() {
		// 开启血包
		if (MyPanel.myplane != null && MyPanel.myLife > 0 && !MyPanel.isPause) {
			// 关卡打了三分之一三分之二处出现血包
			if (MyPanel.passScore > (MyPanel.PASS_SCORE + MyPanel.passNum * 5)
					* MyPanel.lifeCount / 3) {
				// 若屏幕中有未吃掉的血包，这次不产生血包
				if (!MyPanel.bloodExist) {
					MyPanel.lifeCount++;
					// 产生血包
					Blood blood = new Blood();
					MyPanel.bloodList.add(blood);
					MyPanel.bloodExist = true;
					bloodTimer = new Timer();
					bloodTimer.schedule(new TimerTask() {

						@Override
						public void run() {
							bloodTimer.cancel();
							bloodTimer = null;
							MyPanel.bloodExist = false;
							// 声明血包位置
							for (int i = 0; i < MyPanel.bloodList.size(); i++) {
								MyPanel.bloodList.remove(i);
								i--;
							}
						}
					}, 10000);
				} else
					MyPanel.lifeCount++;
			}
		}
	}

	private void checkPass() {
		if (MyPanel.isPass) {
			MyPanel.isPass = false;
			if (MyPanel.passNum == 10)// 10关
			{
				// 重新初始化数据
				MyPanel.killTimer();
				MyPanel.myplane = new MyPlane(false);
				MyPanel.isPause = true;

				MyPanel.isStop = MyPanel.FLAG_RESTART;
				// 清屏
			}// if
			else {
				MyPanel.killTimer();
				MyPanel.isPause = true;
				// 保存所需数据
				int tScore = MyPanel.myScore + MyPanel.passScore;
				int tPassNum = MyPanel.passNum + 1;
				boolean tTest = MyPanel.test;
				int magic = MyPanel.magicCount;
				// 重新开始游戏
				MyPanel.Restart();
				MyPanel.myplane = new MyPlane(false);
				MyPanel.myScore = tScore;
				MyPanel.passNum = tPassNum;
				MyPanel.magicCount = magic;
				MyPanel.test = tTest;
			}// else
		}// if
	}

	private void bloodAndMe() {
		if (MyPanel.myplane != null && !MyPanel.isPause) {
			// 吃到血包
			// 声明血包位置
			for (int i = 0; i < MyPanel.bloodList.size(); i++) {
				Blood blood = MyPanel.bloodList.get(i);
				// 获得血包矩形
				Rectangle bloodbRect = blood.getRect();
				// 获得战机矩形
				Rectangle planeRect = MyPanel.myplane.getRect();
				// 判断两个矩形区域是否有交接
				if (bloodbRect.intersects(planeRect)) {// 音效
					AudioUtil.play(AudioUtil.AUDIO_BLOOD);
					// 加血效果
					MyPanel.myLife += 5;
					if (MyPanel.myLife > MyPanel.DEFAULT_LIFE)
						MyPanel.myLife = MyPanel.DEFAULT_LIFE;
					// TODO 声音
					// 加血后血包删除
					MyPanel.bloodList.remove(i);
					i--;
					break;
				}// if
			}// for
		}
	}

	private void ballAndBomb() {
		if (MyPanel.myplane != null && !MyPanel.isPause) {
			// 敌机子弹和我方子弹碰撞
			for (int i = 0; i < MyPanel.bombList.size(); i++) {
				Bomb bomb = MyPanel.bombList.get(i);
				if (bomb == null)
					continue;
				Rectangle bombRectangle = bomb.getRect();
				for (int j = 0; j < MyPanel.ballList.size(); j++) {
					Ball ball = MyPanel.ballList.get(j);
					if (ball == null)
						continue;
					Rectangle ballRectangle = ball.getRect();
					if (bombRectangle.intersects(ballRectangle)) {
						Explosion explosion = new Explosion(
								(ball.getPoint().x + Ball.BALL_WIDTH / 2 - Explosion.EXPLOSION_WIDTH / 2),
								(ball.getPoint().y + Bomb.BOMB_HEIGHT / 2 - Explosion.EXPLOSION_WIDTH / 2));
						MyPanel.explosionList.add(explosion);
						// 音效
						AudioUtil.play(AudioUtil.AUDIO_EXPLOSION);
						// 爆炸后删除战机子弹
						MyPanel.bombList.remove(i);
						// 删除敌机炸弹
						MyPanel.ballList.remove(j);
						i--;
						j--;
						// 打掉敌机炮弹不加分
						// myScore++;
						// 战机炮弹释放，直接跳出本循环
						break;
					}
				}
			}
		}
	}

	private void enemyAndMe() {
		if (MyPanel.myplane != null && !MyPanel.isPause) {
			// 敌机战机碰撞
			for (int i = 0; i < MyPanel.enemyList.size(); i++) {
				Enemy enemy = MyPanel.enemyList.get(i);
				if (enemy == null)
					continue;
				Rectangle enemyRectangle = enemy.getRect();
				Rectangle meRectangle = MyPanel.myplane.getRect();
				if (meRectangle.intersects(enemyRectangle)) {
					Explosion explosion = new Explosion(
							MyPanel.myplane.getPoint().x + MyPlane.PLANE_WIDTH
									/ 2 - Explosion.EXPLOSION_WIDTH / 2,
							MyPanel.myplane.getPoint().y + MyPlane.PLANE_HEIGHT
									/ 2 - Explosion.EXPLOSION_WIDTH / 2);
					MyPanel.explosionList.add(explosion);
					// 音效
					AudioUtil.play(AudioUtil.AUDIO_EXPLOSION);
					if (!MyPanel.isProtect && !MyPanel.test)
						// 战机生命值减1
						MyPanel.myLife--;
					// 敌机生命值减少
					enemy.life--;
					if (enemy.life <= 0) {
						// 得分
						MyPanel.passScore++;
						// 删除敌机
						MyPanel.enemyList.remove(i);
						i--;
					}
					// 游戏结束
					if (MyPanel.myLife == 0) {
						MyPanel.lifeNum--;
						if (MyPanel.lifeNum <= 0) {
							// 删除战机对象
							MyPanel.myplane = null;
							MyPanel.gameOver();
							break;
						} else {
							MyPanel.myLife = MyPanel.DEFAULT_LIFE;
						}
					}// if
				}
			}
			// Boss和战机碰撞
			if (MyPanel.myplane != null && !MyPanel.isPause && MyPanel.isBoss) {
				// 获得战机的矩形区域
				Rectangle myPlaneRect = MyPanel.myplane.getRect();
				// Boss和战机相撞
				// 获得Boss的矩形区域
				Rectangle bossRect = MyPanel.boss.getRect();
				// 判断两个矩形区域是否有交接
				if (myPlaneRect.intersects(bossRect)) {
					// 将爆炸对象添加到爆炸链表中
					Explosion explosion = new Explosion(
							MyPanel.myplane.getPoint().x + MyPlane.PLANE_WIDTH
									/ 2 - Explosion.EXPLOSION_WIDTH / 2,
							MyPanel.myplane.getPoint().y + MyPlane.PLANE_HEIGHT
									/ 2 - Explosion.EXPLOSION_WIDTH / 2);
					MyPanel.explosionList.add(explosion);
					// 音效
					AudioUtil.play(AudioUtil.AUDIO_EXPLOSION);
					if (!MyPanel.isProtect && !MyPanel.test)
						// 战机生命值减1
						MyPanel.myLife--;
					// 是Boss，不删除敌机，只扣血
					MyPanel.bossBlood--;
					// MyPanel.myplane.setPoint(new Point(MyPlane.PLANE_X,
					// MyPlane.PLANE_Y));
					if (MyPanel.bossBlood <= 0) {
						Explosion explosion1 = new Explosion(
								MyPanel.boss.getPoint().x,
								MyPanel.boss.getPoint().y);
						MyPanel.explosionList.add(explosion1);
						Explosion explosion2 = new Explosion(
								(MyPanel.boss.getPoint().x + Boss.BOSS_WIDTH),
								(MyPanel.boss.getPoint().y + Boss.BOSS_HEIGHT));
						MyPanel.explosionList.add(explosion2);
						Explosion explosion3 = new Explosion(
								(MyPanel.boss.getPoint().x + Boss.BOSS_WIDTH),
								(MyPanel.boss.getPoint().y));
						MyPanel.explosionList.add(explosion3);
						Explosion explosion4 = new Explosion(
								(MyPanel.boss.getPoint().x),
								(MyPanel.boss.getPoint().y + Boss.BOSS_HEIGHT));
						MyPanel.explosionList.add(explosion4);
						Explosion explosion5 = new Explosion(
								(MyPanel.boss.getPoint().x + Boss.BOSS_WIDTH
										/ 2 - Explosion.EXPLOSION_WIDTH / 2),
								(MyPanel.boss.getPoint().y + Boss.BOSS_HEIGHT
										/ 2 - Explosion.EXPLOSION_WIDTH / 2));
						explosion5.setBossDie(true);// 标记最后一个炸弹，炸完之后跳入下一关
						MyPanel.explosionList.add(explosion5);
						// 音效
						AudioUtil.play(AudioUtil.AUDIO_EXPLOSION);
						MyPanel.boss = null;
						// 过关的标志变量
						// isPause = TRUE;
						// CMyPlane* temp = myplane;
						// myplane = new CMyPlane(FALSE);
						MyPanel.myplane = null;
						MyPanel.isPass = true;
						MyPanel.isBoss = false;
					}
					// 游戏结束
					if (MyPanel.myLife == 0) {
						MyPanel.lifeNum--;
						if (MyPanel.lifeNum <= 0) {
							// isPause = TRUE;
							// 删除战机对象
							MyPanel.myplane = null;

							MyPanel.gameOver();
						} else {
							MyPanel.myLife = MyPanel.DEFAULT_LIFE;
							// 删除原战机对象
						}
					}// if
				}// if
			}
		}
	}

	private void ballAndMe() {
		if (MyPanel.myplane != null && !MyPanel.isPause) {
			// 敌机子弹打中战机
			for (int i = 0; i < MyPanel.ballList.size(); i++) {
				Ball ball = MyPanel.ballList.get(i);
				if (ball == null)
					continue;
				Rectangle ballRectangle = ball.getRect();
				Rectangle meRectangle = MyPanel.myplane.getRect();
				if (meRectangle.intersects(ballRectangle)) {
					Explosion explosion = new Explosion(
							(ball.getPoint().x + Ball.BALL_WIDTH / 2 - Explosion.EXPLOSION_WIDTH / 2),
							(ball.getPoint().y + Ball.BALL_HEIGHT / 2 - Explosion.EXPLOSION_WIDTH / 2));
					MyPanel.explosionList.add(explosion);
					// 音效
					AudioUtil.play(AudioUtil.AUDIO_EXPLOSION);
					if (!MyPanel.isProtect && !MyPanel.test)
						// 战机生命值减1
						MyPanel.myLife--;
					// 删除敌机炸弹
					MyPanel.ballList.remove(i);
					i--;
					// 游戏结束
					if (MyPanel.myLife == 0) {
						MyPanel.lifeNum--;
						if (MyPanel.lifeNum <= 0) {
							// 删除战机对象
							MyPanel.myplane = null;
							MyPanel.gameOver();
							break;
						} else {
							MyPanel.myLife = MyPanel.DEFAULT_LIFE;
						}
					}// if
				}
			}
		}
	}

	private void bombAndEnemy() {
		if (MyPanel.myplane != null && !MyPanel.isPause) {
			// 子弹打中敌机
			boolean flag = false;
			for (int i = 0; i < MyPanel.bombList.size(); i++) {
				Bomb bomb = MyPanel.bombList.get(i);
				if (bomb == null)
					continue;
				Rectangle bombRectangle = bomb.getRect();
				for (int j = 0; j < MyPanel.enemyList.size(); j++) {
					Enemy enemy = MyPanel.enemyList.get(j);
					if (enemy == null)
						continue;
					Rectangle enemyRectangle = enemy.getRect();
					if (enemyRectangle.intersects(bombRectangle)) {
						Explosion explosion = new Explosion(
								(bomb.getPoint().x + Bomb.BOMB_WIDTH / 2 - Explosion.EXPLOSION_WIDTH / 2),
								(bomb.getPoint().y + Bomb.BOMB_HEIGHT / 2 - Explosion.EXPLOSION_WIDTH / 2));
						MyPanel.explosionList.add(explosion);
						// 音效
						AudioUtil.play(AudioUtil.AUDIO_EXPLOSION);
						// 爆炸后删除子弹
						MyPanel.bombList.remove(i);
						i--;
						// 敌机生命值减少
						enemy.life -= MyPanel.isUpdate ? 2 : 1;
						if (enemy.life <= 0) {
							// 增加得分
							MyPanel.passScore++;
							// 删除敌机
							MyPanel.enemyList.remove(j);
							j--;
						}
						// 炮弹已删除，直接跳出本循环
						flag = true;
						break;
					}
				}
				if (flag)
					continue;
				if (MyPanel.isBoss && bomb != null) {
					// 获得战机子弹的矩形区域
					Rectangle bombRect = bomb.getRect();
					// 获得Boss的矩形区域
					Rectangle bossRect = MyPanel.boss.getRect();
					// 判断两个矩形区域是否有交接
					if (bombRect.intersects(bossRect)) {
						// 将爆炸对象添加到爆炸链表中
						Explosion explosion = new Explosion(
								(bomb.getPoint().x + Bomb.BOMB_WIDTH / 2 - Explosion.EXPLOSION_WIDTH / 2),
								(bomb.getPoint().y + Bomb.BOMB_HEIGHT / 2 - Explosion.EXPLOSION_WIDTH / 2));
						MyPanel.explosionList.add(explosion);
						// 音效
						AudioUtil.play(AudioUtil.AUDIO_EXPLOSION);
						// 爆炸后删除子弹
						MyPanel.bombList.remove(i);
						i--;
						bomb = null;
						// 是Boss，不删除敌机，只扣血
						MyPanel.bossBlood -= MyPanel.isUpdate ? 2 : 1;
						if (MyPanel.bossBlood <= 0) {
							Explosion explosion1 = new Explosion(
									MyPanel.boss.getPoint().x,
									MyPanel.boss.getPoint().y);
							MyPanel.explosionList.add(explosion1);
							Explosion explosion2 = new Explosion(
									(MyPanel.boss.getPoint().x + Boss.BOSS_WIDTH),
									(MyPanel.boss.getPoint().y + Boss.BOSS_HEIGHT));
							MyPanel.explosionList.add(explosion2);
							Explosion explosion3 = new Explosion(
									(MyPanel.boss.getPoint().x + Boss.BOSS_WIDTH),
									(MyPanel.boss.getPoint().y));
							MyPanel.explosionList.add(explosion3);
							Explosion explosion4 = new Explosion(
									(MyPanel.boss.getPoint().x),
									(MyPanel.boss.getPoint().y + Boss.BOSS_HEIGHT));
							MyPanel.explosionList.add(explosion4);
							Explosion explosion5 = new Explosion(
									(MyPanel.boss.getPoint().x
											+ Boss.BOSS_WIDTH / 2 - Explosion.EXPLOSION_WIDTH / 2),
									(MyPanel.boss.getPoint().y
											+ Boss.BOSS_HEIGHT / 2 - Explosion.EXPLOSION_WIDTH / 2));
							explosion5.setBossDie(true);// 标记最后一个炸弹，炸完之后跳入下一关
							MyPanel.explosionList.add(explosion5);

							MyPanel.boss = null;
							// 过关的标志变量
							// isPause = TRUE;
							// CMyPlane* temp = myplane;
							// myplane = new CMyPlane(FALSE);
							MyPanel.myplane = null;
							MyPanel.isPass = true;
							MyPanel.isBoss = false;
						}
					}
				}
			}
		}
	}
}