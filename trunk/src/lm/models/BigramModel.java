package lm.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lm.objects.Bigram;
import lm.objects.Unigram;

public class BigramModel {

	private Map<String, Bigram> bigramModel;

	/**
	 * Creates a new BigramModel. If we want to handle unknown then the input
	 * vocabulary has to handle unknown as well.
	 * 
	 * @param vocabulary
	 * @param corpusContent
	 * @param handlingUnknown
	 */
	public BigramModel(Map<String, Unigram> vocabulary, String[] corpusContent,
			boolean handlingUnknown) {
		bigramModel = new HashMap<String, Bigram>();
		if (handlingUnknown) {
			populateModelHandlingUnknownWord(vocabulary, corpusContent);
		} else {
			populateModel(vocabulary, corpusContent);
		}
	}

	private void populateModel(Map<String, Unigram> unigrams, String[] tokens) {

		String lastToken = null;
		for (int i = 0; i < tokens.length; i++) {
			String token = tokens[i];
			// bigrams
			if (lastToken != null) {

				String bigramAsString = lastToken + " " + token;

				if (bigramModel.containsKey(bigramAsString)) {
					double count = bigramModel.get(bigramAsString).getCount();
					bigramModel.get(bigramAsString).setCount(count + 1);

				} else {
					Bigram bigram = new Bigram();
					bigram.setFirst(lastToken);
					bigram.setSecond(token);
					bigram.setCount(1);
					bigramModel.put(bigramAsString, bigram);
				}

			}
			lastToken = token;
		}
		setProbabilities(unigrams);

		// add the zeroes
		// for (String first : unigrams.keySet()) {
		// for (String second : unigrams.keySet()) {
		//
		// String bigram = first + " " + second;
		// if (!bigramModel.containsKey(bigram)) {
		// Bigram bigramObj = new Bigram();
		//
		// bigramObj.setFirst(first);
		// bigramObj.setCount(0);
		// bigramModel.put(bigram, bigramObj);
		// }
		// }
		//
		// }

	}

	private void populateModelHandlingUnknownWord(
			Map<String, Unigram> unigramsWithUnknown, String[] tokens) {

		int size = tokens.length;

		int trainingSize = 4 * size / 5;

		String lastToken = null;
		for (int i = 0; i < trainingSize; i++) {
			String token = tokens[i];
			// bigrams
			if (lastToken != null) {

				String bigramAsString = lastToken + " " + token;

				if (bigramModel.containsKey(bigramAsString)) {
					double count = bigramModel.get(bigramAsString).getCount();
					bigramModel.get(bigramAsString).setCount(count + 1);

				} else {
					Bigram bigram = new Bigram();
					bigram.setFirst(lastToken);
					bigram.setSecond(token);
					bigram.setCount(1);
					bigramModel.put(bigramAsString, bigram);
				}

			}
			lastToken = token;
		}

		// set bigram probabilities
		setProbabilities(unigramsWithUnknown);

	}

	public void setProbabilities(Map<String, Unigram> unigrams) {
		for (String bigram : bigramModel.keySet()) {
			String first = bigram.split(" ")[0];
			double probability = bigramModel.get(bigram).getCount()
					/ unigrams.get(first).getCount();// P(wn/wn-1)=C(wn-1wn)/C(wn-1)
			bigramModel.get(bigram).setProbability(probability);
		}
	}

	public Map<String, Bigram> getBigramModel() {
		return bigramModel;
	}

	public void setBigramModel(Map<String, Bigram> bigramModel) {
		this.bigramModel = bigramModel;
	}

	public List<Bigram> getBigramsByPrefix(String prefix) {
		List<Bigram> bigrams = new ArrayList<Bigram>();

		for (String bigram : bigramModel.keySet()) {
			if (bigram.startsWith(prefix)) {
				bigrams.add(bigramModel.get(bigram));
			}
		}
		return bigrams;

	}

	public Bigram generateNextBigram(String prefix) {

		List<Bigram> bigrams = getBigramsByPrefix(prefix);
		Collections.sort(bigrams);

		double random = Math.random();

		double probForGeneration = 0;
		Bigram lastBigram = null;
		for (Bigram bigram : bigrams) {
			lastBigram = bigram;

			double bigramProb = bigram.getProbability();
			probForGeneration = probForGeneration + bigramProb;

			if (random < probForGeneration) {
				return bigram;
			}
		}

		return lastBigram;
	}

	/**
	 * Returns the bigram count. As we dont's store bigrams with count 0 this
	 * method will check if bigram is in model and if yes will return the stored
	 * count, if not in model it will check first and second word in bigram in
	 * the vocabulary: if they both exist it returns 0 otherwise it will return
	 * -1.
	 * 
	 * @param bigram
	 *            a String representing a bigram
	 * @param vocabulary
	 *            a Set of Strings representing the vocabulary
	 * @return if bigram is in model and if yes will return the stored count, if
	 *         not in model it will check first and second word in bigram in the
	 *         vocabulary: if they both exist it returns 0 otherwise it will
	 *         return -1.
	 */
	public double getBigramCount(String bigram, Set<String> vocabulary) {
		double result = -1;
		if (bigramModel.containsKey(bigram)) {
			return bigramModel.get(bigram).getCount();
		} else {
			String[] items = bigram.split(" ");
			String first = items[0];
			String second = items[1];

			if (vocabulary.contains(first) && vocabulary.contains(second)) {
				return 0;
			}
		}
		return result;
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();
		for (String bigram : bigramModel.keySet()) {

			sb.append(bigram + " " + bigramModel.get(bigram).getCount());
			sb.append("\n");

		}

		return sb.toString();
	}
}