package com.silence.spacewar.task;

import java.util.TimerTask;

import com.silence.spacewar.MyPanel;

public class MagicTask extends TimerTask {

	@Override
	public void run() {
		if (MyPanel.myplane != null && !MyPanel.isPause && MyPanel.isStarted) {
			// 防护罩和战机升级没打开，魔法值递增
			if (!MyPanel.isProtect && !MyPanel.isUpdate) {
				MyPanel.magicCount++;
				if (MyPanel.magicCount > 10)
					MyPanel.magicCount = 10;
			}
			// 判断是否打开防护罩
			if (MyPanel.isProtect) {
				// 开启防护罩魔法值递减
				MyPanel.magicCount--;
				if (MyPanel.magicCount <= 0) {
					MyPanel.magicCount = 0;
					MyPanel.isProtect = false;
				}
			}
			// 判断是否升级战机
			if (MyPanel.isUpdate) {
				// 战机升级，魔法值递减
				MyPanel.magicCount--;
				if (MyPanel.magicCount <= 0) {
					MyPanel.magicCount = 0;
					MyPanel.isUpdate = false;
					MyPanel.myplane.isUpdate = MyPanel.isUpdate;
				}
			}
		}
	}
}
