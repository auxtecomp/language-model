package lm;

import java.util.Map;
import lm.models.*;
import lm.objects.*;

public class Perplexity {

	public static double computeUnigramPerplexity(
			Map<String, Unigram> unigramModel, String[] testset) {
		double logProbability = 0.0;
		double totalcount = testset.length;
		double templog, uniperplexity;

		for (int i = 0; i < testset.length; i++) {

			if (unigramModel.containsKey(testset[i])) {
				logProbability += Math.log(unigramModel.get(testset[i])
						.getProbability());
			} else {
				logProbability += Math.log(unigramModel.get("<UNK>")
						.getProbability());
			}
		}

		templog = -1.0 * logProbability;
		templog = Math.log(templog) - Math.log(totalcount);
		uniperplexity = Math.exp(Math.exp(templog));
		return uniperplexity;

	}

	public static double computeBigramPerplexity(BigramModel bigramModel,
			UnigramModel unigram, String[] testset) {
		double logProbability = 0.0;
		double totalcount = testset.length;
		double templog, biperplexity;

		String test = testset[0];
		if (!unigram.getUnigramModel().containsKey(test))
			test = "<UNK>";
		logProbability = Math.log(unigram.getUnigramModel().get(test)
				.getProbability());

		for (int i = 0; i < testset.length - 1; i++) {
			String first = testset[i];
			String second = testset[i + 1];

			if (!unigram.getUnigramModel().containsKey(first))
				first = "<UNK>";
			if (!unigram.getUnigramModel().containsKey(second))
				second = "<UNK>";

			logProbability += Math.log(bigramModel
					.getSmoothedBigramProbability(first + " " + second));

		}

		templog = -1.0 * logProbability;
		templog = templog / totalcount;
		biperplexity = Math.exp(templog);
		return biperplexity;

	}

	public static double computeTrigramPerplexity(TrigramModel trigramModel,
			UnigramModel unigram, String[] testset) {
		double logProbability = 0.0;
		double totalcount = testset.length;
		double templog, triperplexity;

		for (int i = 0; i < testset.length - 2; i++) {
			String first = testset[i];
			String second = testset[i + 1];
			String third = testset[i + 2];

			if (!unigram.getUnigramModel().containsKey(first))
				first = "<UNK>";
			if (!unigram.getUnigramModel().containsKey(second))
				second = "<UNK>";
			if (!unigram.getUnigramModel().containsKey(third))
				third = "<UNK>";

			logProbability += Math.log(trigramModel
					.getSmoothedTrigramProbability(first + " " + second + " "
							+ third));

		}

		templog = -1.0 * logProbability;
		templog = templog / totalcount;
		triperplexity = Math.exp(templog);
		return triperplexity;

	}

}