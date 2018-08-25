package hichang.audio;

public class YIN {

	private float threshold = 0.15f;

	// 只有前这么多个数据会被处理, 其余的全部丢弃
	private int bufferSize = 160; // 222;

	// 采样频率
	private float sampleRate = 8000; // 11050;

	// 音高(频率)
	private float pitchInHertz;

	// 音频原始数据
	private float[] inputBuffer = new float[bufferSize];

	// 音频分析数据
	private float[] yinBuffer = new float[bufferSize / 2];

	// 传入一个音频数据相关的数组
	public YIN(float[] inputBuffer) {
		for (int i = 0; i < inputBuffer.length; i++) {
			this.inputBuffer[i] = inputBuffer[i];
		}
	}

	private void difference() {
		int j = 0;
		int tau = 0;
		float delta = 0.0f;

		// yinBuffer数组清零
		for (tau = 0; tau < yinBuffer.length; tau++) {
			yinBuffer[tau] = 0;
		}

		// 算法
		for (tau = 1; tau < yinBuffer.length; tau++) {
			for (j = 0; j < yinBuffer.length; j++) {
				delta = inputBuffer[j] - inputBuffer[j + tau];
				yinBuffer[tau] += delta * delta;
			}
		}
	}

	private void cumulativeMeanNormalizedDifference() {
		int tau = 0;
		yinBuffer[0] = 1;

		float runningSum = yinBuffer[1];

		yinBuffer[1] = 1;

		for (tau = 2; tau < yinBuffer.length; tau++) {
			runningSum = runningSum + yinBuffer[tau];
			yinBuffer[tau] = yinBuffer[tau] * tau / runningSum;
		}
	}

	private int absoluteThreshold() {

		for (int tau = 1; tau < yinBuffer.length; tau++) {
			if (yinBuffer[tau] < threshold) {
				while (tau + 1 < yinBuffer.length
						&& yinBuffer[tau + 1] < yinBuffer[tau]) {
					tau++;
				}
				return tau;
			}
		}
		return -1;
	}

	private float parabolicInterpolation(int tauEstimate) {
		float s0;
		float s1;
		float s2;
		int x0 = (tauEstimate < 1) ? tauEstimate : tauEstimate - 1;
		int x2 = (tauEstimate + 1 < yinBuffer.length) ? tauEstimate + 1
				: tauEstimate;
		if (x0 == tauEstimate) {
			return (yinBuffer[tauEstimate] <= yinBuffer[x2]) ? tauEstimate : x2;
		}
		if (x2 == tauEstimate) {
			return (yinBuffer[tauEstimate] <= yinBuffer[x0]) ? tauEstimate : x0;
		}
		s0 = yinBuffer[x0];
		s1 = yinBuffer[tauEstimate];
		s2 = yinBuffer[x2];

		return tauEstimate + 0.5f * (s2 - s0) / (2.0f * s1 - s2 - s0);
	}

	public float getPitch() {
		int tauEstimate = -1;
		pitchInHertz = 0;

		difference();

		cumulativeMeanNormalizedDifference();

		tauEstimate = absoluteThreshold();

		if (tauEstimate != -1) {
			float betterTau = parabolicInterpolation(tauEstimate);

			pitchInHertz = sampleRate / betterTau;
		}

		this.inputBuffer = null;

		return pitchInHertz;
	}
}