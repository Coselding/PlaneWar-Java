package com.silence.spacewar.task;

import java.util.List;
import java.util.Random;
import java.util.TimerTask;

import com.silence.spacewar.MyPanel;
import com.silence.spacewar.domain.Ball;
import com.silence.spacewar.domain.Boss;
import com.silence.spacewar.domain.Enemy;
import com.silence.spacewar.utils.AudioUtil;

public class EnemyTask extends TimerTask {

	private List<Enemy> enemyList;

	public EnemyTask(List<Enemy> enemyList) {
		this.enemyList = enemyList;
	}

	@Override
	public void run() {
		if (MyPanel.isPause || MyPanel.myplane == null || !MyPanel.isStarted) {
			return;
		}
		// 根据关卡数产生敌机
		if (MyPanel.passNum <= 5) {
			// 前五关只有一个方向的敌机
			Enemy enemy = new Enemy(Enemy.ENEMY_SPEED, 1);// 设置敌机的方向，从上方飞出
			enemyList.add(enemy);// 随机产生敌机

			if (new Random().nextInt(2) == 0) {// 控制敌机炮弹发出频率
				Ball ball = new Ball(
						enemy.getPoint().x + Enemy.ENEMY_WIDTH / 2,
						enemy.getPoint().y + Enemy.ENEMY_HEIGHT,
						enemy.getDirection());
				ball.setBallSpeed(enemy.getSpeed()+2);
				MyPanel.ballList.add(ball);
				// 音效
				AudioUtil.play(AudioUtil.AUDIO_BALL);
			}
		} else if (MyPanel.passNum > 5) {// 第五关之后，两个方向的敌机
			Enemy enemy1 = new Enemy(Enemy.ENEMY_SPEED, 1);// 设置敌机的方向，从上方飞出
			enemy1.setSpeed(Enemy.ENEMY_SPEED
					+ (new Random().nextInt(2) + MyPanel.passNum - 1));
			enemyList.add(enemy1);

			Enemy enemy2 = new Enemy(Enemy.ENEMY_SPEED, -1);// 设置敌机的方向，从下方飞出
			enemy2.setSpeed(Enemy.ENEMY_SPEED
					+ (new Random().nextInt(2) + MyPanel.passNum - 1));
			enemyList.add(enemy2);

			int rand = new Random().nextInt(3);
			if (rand == 0) {// 控制敌机炮弹发出频率
				Ball ball = new Ball(enemy1.getPoint().x + Enemy.ENEMY_WIDTH
						/ 2, enemy1.getPoint().y + Enemy.ENEMY_HEIGHT,
						enemy1.getDirection());
				ball.setBallSpeed(enemy1.getSpeed()+2);
				MyPanel.ballList.add(ball);
				// 音效
				AudioUtil.play(AudioUtil.AUDIO_BALL);
			}
			if (rand == 1) {// 控制敌机炮弹发出频率
				Ball ball = new Ball(enemy2.getPoint().x + Enemy.ENEMY_WIDTH
						/ 2, enemy2.getPoint().y, enemy2.getDirection());
				ball.setBallSpeed(enemy2.getSpeed()+2);
				MyPanel.ballList.add(ball);
				// 音效
				AudioUtil.play(AudioUtil.AUDIO_BALL);
			}
		}
		if (MyPanel.isBoss) {
			// Boss发射子弹
			// 敌机炸弹产生定时器触发
			// 设置定时器产生敌机炸弹
			Ball ball1 = new Ball(MyPanel.boss.getPoint().x + Boss.BOSS_WIDTH
					/ 2, MyPanel.boss.getPoint().y + Boss.BOSS_HEIGHT, 1);
			ball1.setBallSpeed(Ball.BALL_SPEED + (MyPanel.passNum - 1) * 2);
			MyPanel.ballList.add(ball1);
			Ball ball2 = new Ball(MyPanel.boss.getPoint().x + 5,
					MyPanel.boss.getPoint().y + Boss.BOSS_HEIGHT, 1);
			ball2.setBallSpeed(Ball.BALL_SPEED + (MyPanel.passNum - 1) * 2);
			MyPanel.ballList.add(ball2);
			Ball ball3 = new Ball(MyPanel.boss.getPoint().x + Boss.BOSS_WIDTH
					- 5, MyPanel.boss.getPoint().y + Boss.BOSS_HEIGHT, 1);
			ball3.setBallSpeed(Ball.BALL_SPEED + (MyPanel.passNum - 1) * 2);
			MyPanel.ballList.add(ball3);
			Ball ball4 = new Ball(MyPanel.boss.getPoint().x + Boss.BOSS_WIDTH
					/ 2 + 85, MyPanel.boss.getPoint().y + Boss.BOSS_HEIGHT, 1);
			ball4.setBallSpeed(Ball.BALL_SPEED + (MyPanel.passNum - 1) * 2);
			MyPanel.ballList.add(ball4);
			Ball ball5 = new Ball(MyPanel.boss.getPoint().x + Boss.BOSS_WIDTH
					/ 2 - 85, MyPanel.boss.getPoint().y + Boss.BOSS_HEIGHT, 1);
			ball5.setBallSpeed(Ball.BALL_SPEED + (MyPanel.passNum - 1) * 2);
			MyPanel.ballList.add(ball5);
			// 音效
			AudioUtil.play(AudioUtil.AUDIO_BALL);
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}