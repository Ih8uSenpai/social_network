package com.example.social_network.utils;

import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TagExtractor {

    public List<String> extractTags(String text) throws Exception {
        InputStream tokenModelIn = getClass().getResourceAsStream("/models/opennlp-en-ud-ewt-tokens-1.0-1.9.3.bin");
        TokenizerModel tokenModel = new TokenizerModel(tokenModelIn);
        TokenizerME tokenizer = new TokenizerME(tokenModel);

        String[] tokens = tokenizer.tokenize(text);

        InputStream posModelIn = getClass().getResourceAsStream("/models/opennlp-en-ud-ewt-pos-1.0-1.9.3.bin");
        POSModel posModel = new POSModel(posModelIn);
        POSTaggerME tagger = new POSTaggerME(posModel);

        String[] tags = tagger.tag(tokens);

        for (int i = 0; i < tokens.length; i++) {
            System.out.println("Token: " + tokens[i] + " -> Tag: " + tags[i]);
        }

        Set<String> keywords = new HashSet<>();
        for (int i = 0; i < tags.length; i++) {
            if (tags[i].equals("NOUN") || tags[i].equals("PROPN")) { // Существительные и собственные имена
                keywords.add(tokens[i]);
            }
        }

        // Поиск всех слов, начинающихся с '#'
        Pattern hashtagPattern = Pattern.compile("#([^\\s#]+)");
        Matcher matcher = hashtagPattern.matcher(text);
        while (matcher.find()) {
            String hashtag = matcher.group().substring(1);
            keywords.add(hashtag);
        }

        return new ArrayList<>(keywords);
    }
}