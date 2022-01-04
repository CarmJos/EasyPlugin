package cc.carm.lib.easyplugin.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Callable;

@SuppressWarnings("DuplicatedCode")
public class SchedulerUtils {

	private final JavaPlugin plugin;

	public SchedulerUtils(JavaPlugin plugin) {
		this.plugin = plugin;
	}

	private JavaPlugin getPlugin() {
		return plugin;
	}


	/**
	 * 在服务端主线程中执行一个任务
	 *
	 * @param runnable 需要执行的任务
	 */
	public void run(Runnable runnable) {
		Bukkit.getScheduler().runTask(getPlugin(), runnable);
	}


	/**
	 * 异步执行一个任务。
	 *
	 * @param runnable 需要执行的任务
	 */
	public void runAsync(Runnable runnable) {
		Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), runnable);
	}
	
	/**
	 * 在主线程延时执行一个任务。
	 *
	 * @param delay    延迟的ticks
	 * @param runnable 需要执行的任务
	 */
	public void runLater(long delay, Runnable runnable) {
		Bukkit.getScheduler().runTaskLater(getPlugin(), runnable, delay);
	}

	/**
	 * 异步延时执行一个任务。
	 *
	 * @param delay    延迟的ticks
	 * @param runnable 需要执行的任务
	 */
	public void runLaterAsync(long delay, Runnable runnable) {
		Bukkit.getScheduler().runTaskLaterAsynchronously(getPlugin(), runnable, delay);
	}

	/**
	 * 间隔一段时间按顺序执行列表中的任务
	 *
	 * @param interval 间隔时间
	 * @param tasks    任务列表
	 */
	public void runAtInterval(long interval, Runnable... tasks) {
		runAtInterval(0L, interval, tasks);
	}


	/**
	 * 间隔一段时间按顺序执行列表中的任务
	 *
	 * @param delay    延迟时间
	 * @param interval 间隔时间
	 * @param tasks    任务列表
	 */
	public void runAtInterval(long delay, long interval, Runnable... tasks) {
		new BukkitRunnable() {
			private int index;

			@Override
			public void run() {
				if (this.index >= tasks.length) {
					this.cancel();
					return;
				}

				tasks[index].run();
				index++;
			}
		}.runTaskTimer(getPlugin(), delay, interval);
	}

	/**
	 * 间隔一段时间按顺序异步执行列表中的任务
	 *
	 * @param interval 间隔时间
	 * @param tasks    任务列表
	 */
	public void runAtIntervalAsync(long interval, Runnable... tasks) {
		runAtIntervalAsync(0L, interval, tasks);
	}

	/**
	 * 间隔一段时间按顺序异步执行列表中的任务
	 *
	 * @param delay    延迟时间
	 * @param interval 间隔时间
	 * @param tasks    任务列表
	 */
	public void runAtIntervalAsync(long delay, long interval, Runnable... tasks) {
		new BukkitRunnable() {
			private int index;

			@Override
			public void run() {
				if (this.index >= tasks.length) {
					this.cancel();
					return;
				}

				tasks[index].run();
				index++;
			}
		}.runTaskTimerAsynchronously(getPlugin(), delay, interval);
	}

	/**
	 * 重复执行一个任务。
	 *
	 * @param repetitions 重复次数
	 * @param interval    间隔时间
	 * @param task        任务
	 * @param onComplete  结束时执行的任务
	 */
	public void repeat(int repetitions, long interval, Runnable task, Runnable onComplete) {
		new BukkitRunnable() {
			private int index;

			@Override
			public void run() {
				index++;
				if (this.index >= repetitions) {
					this.cancel();
					if (onComplete == null) {
						return;
					}

					onComplete.run();
					return;
				}

				task.run();
			}
		}.runTaskTimer(getPlugin(), 0L, interval);
	}

	/**
	 * 重复执行一个任务。
	 *
	 * @param repetitions 重复次数
	 * @param interval    间隔时间
	 * @param task        任务
	 * @param onComplete  结束时执行的任务
	 */
	public void repeatAsync(int repetitions, long interval, Runnable task, Runnable onComplete) {
		new BukkitRunnable() {
			private int index;

			@Override
			public void run() {
				index++;
				if (this.index >= repetitions) {
					this.cancel();
					if (onComplete == null) {
						return;
					}

					onComplete.run();
					return;
				}

				task.run();
			}
		}.runTaskTimerAsynchronously(getPlugin(), 0L, interval);
	}

	/**
	 * 在满足某个条件时，重复执行一个任务。
	 *
	 * @param interval   重复间隔时间
	 * @param predicate  条件
	 * @param task       任务
	 * @param onComplete 结束时执行的任务
	 */
	public void repeatWhile(long interval, Callable<Boolean> predicate, Runnable task, Runnable onComplete) {
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					if (!predicate.call()) {
						this.cancel();
						if (onComplete == null) {
							return;
						}

						onComplete.run();
						return;
					}

					task.run();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.runTaskTimer(getPlugin(), 0L, interval);
	}

	/**
	 * 在满足某个条件时，重复执行一个任务。
	 *
	 * @param interval   重复间隔时间
	 * @param predicate  条件
	 * @param task       任务
	 * @param onComplete 结束时执行的任务
	 */
	public void repeatWhileAsync(long interval, Callable<Boolean> predicate, Runnable task, Runnable onComplete) {
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					if (!predicate.call()) {
						this.cancel();
						if (onComplete == null) {
							return;
						}

						onComplete.run();
						return;
					}

					task.run();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.runTaskTimerAsynchronously(getPlugin(), 0L, interval);
	}

	public interface Task {
		void start(Runnable onComplete);
	}

	public class TaskBuilder {
		private final Queue<Task> taskList;

		public TaskBuilder() {
			this.taskList = new LinkedList<>();
		}

		public TaskBuilder append(TaskBuilder builder) {
			this.taskList.addAll(builder.taskList);
			return this;
		}

		public TaskBuilder appendDelay(long delay) {
			this.taskList.add(onComplete -> SchedulerUtils.this.runLater(delay, onComplete));
			return this;
		}

		public TaskBuilder appendTask(Runnable task) {
			this.taskList.add(onComplete ->
			{
				task.run();
				onComplete.run();
			});

			return this;
		}

		public TaskBuilder appendTask(Task task) {
			this.taskList.add(task);
			return this;
		}

		public TaskBuilder appendDelayedTask(long delay, Runnable task) {
			this.taskList.add(onComplete -> SchedulerUtils.this.runLater(delay, () ->
			{
				task.run();
				onComplete.run();
			}));

			return this;
		}

		public TaskBuilder appendTasks(long delay, long interval, Runnable... tasks) {
			this.taskList.add(onComplete ->
			{
				Runnable[] runnables = Arrays.copyOf(tasks, tasks.length + 1);
				runnables[runnables.length - 1] = onComplete;
				SchedulerUtils.this.runAtInterval(delay, interval, runnables);
			});

			return this;
		}

		public TaskBuilder appendRepeatingTask(int repetitions, long interval, Runnable task) {
			this.taskList.add(onComplete -> SchedulerUtils.this.repeat(repetitions, interval, task, onComplete));
			return this;
		}

		public TaskBuilder appendConditionalRepeatingTask(long interval, Callable<Boolean> predicate, Runnable task) {
			this.taskList.add(onComplete -> SchedulerUtils.this.repeatWhile(interval, predicate, task, onComplete));
			return this;
		}

		public TaskBuilder waitFor(Callable<Boolean> predicate) {
			this.taskList.add(onComplete -> new BukkitRunnable() {
				@Override
				public void run() {
					try {
						if (!predicate.call()) {
							return;
						}

						this.cancel();
						onComplete.run();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}.runTaskTimer(getPlugin(), 0L, 1L));
			return this;
		}

		public void runTasks() {
			this.startNext();
		}

		private void startNext() {
			Task task = this.taskList.poll();
			if (task == null) {
				return;
			}

			task.start(this::startNext);
		}
	}
}
