/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.ghost;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class SimpleDictionary implements GhostDictionary {
    private ArrayList<String> words;
    private Random random = new Random();

    public SimpleDictionary(InputStream wordListStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(wordListStream));
        words = new ArrayList<>();
        String line;
        while((line = in.readLine()) != null) {
            String word = line.trim();
            if (word.length() >= MIN_WORD_LENGTH)
                words.add(line.trim());
        }
        Collections.sort(words);
    }

    @Override
    public boolean isWord(String word) {
        return words.contains(word);
    }

    @Override
    public String getAnyWordStartingWith(String prefix) {
        if(prefix.length() == 0){
            return words.get(random.nextInt(words.size()));
        }else{
            int value = Collections.binarySearch(words, prefix);
            if(value >= 0){
                return words.get(value);
            }else{
                return null;
            }
        }
    }


    public String getGoodWordStartingWith(String prefix, boolean userTurn) {
        if(prefix.length() == 0 || prefix.equals(null)){
            return words.get(random.nextInt(words.size()));
        }

        int low = 0;
        int high = words.size()-1;
        int middle;

        ArrayList<String> evenWords = new ArrayList<>();
        ArrayList<String> oddWords = new ArrayList<>();

        while(low!=high){
//            Binary search beginning and end instead of using loops
            middle = (low+high)/2;
            if(words.get(middle).startsWith(prefix)){
                int start = middle, end = middle;
                while(words.get(start).startsWith(prefix)){
                    start--;
                }
                while(words.get(end).startsWith(prefix)){
                    end++;
                }

                for(int i = start; i <= end; i++){
                    if(words.get(i).length() %2 == 0){
                        evenWords.add(words.get(i));
                    }else{
                        oddWords.add(words.get(i));
                    }
                }
                if(evenWords.isEmpty() && oddWords.isEmpty()){
                    return null;
                }else if(evenWords.isEmpty() || oddWords.isEmpty()){
                    return (oddWords.isEmpty()) ?
                            evenWords.get(random.nextInt(evenWords.size())):
                            oddWords.get((random.nextInt(oddWords.size())));
                }
                if(userTurn){
                    return oddWords.get(random.nextInt(oddWords.size()));
                }else{
                    return evenWords.get(random.nextInt(evenWords.size()));
                }

            }else if(prefix.compareTo(words.get(middle))<0){
                high = middle-1;
            }else{
                low = middle+1;
            }
        }
        return null;
    }
}
