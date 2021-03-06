(ns kuromoji-jmdictfurigana.core
  (:require [clojure.string :as string]
            [clojure.pprint :refer [pprint]]
            [cheshire.core :refer [generate-string]]
            [kuromoji-jmdictfurigana.kuromoji :as kuromoji]
            [kuromoji-jmdictfurigana.furigana :as furigana])
  (:gen-class))

(defn to-sorted [token-map]
  (into (sorted-map) token-map))

(defn -main
  [& args]
  (if args
    (let [s (-> (first args)
                slurp
                string/split-lines)
          lots (mapv (fn [s] (->> s
                                  kuromoji/parse
                                  (map to-sorted)
                                  generate-string))
                     s)]
      (doseq [a lots] (println (generate-string a))))

    (let [s "何できた？"
          ; all-results is a list of maps
          all-results (map to-sorted (kuromoji/parse s))
          ; top N results
          nbest (kuromoji/parse-nbest s 3)
          ]
      (println "SINGLE RESULT USING parse")
      (pprint all-results)
      (println "TOP-3 BEST RESULTS USING parse-nbest")
      (pprint (map (fn [v] (map #(-> %
                                     to-sorted
                                     (select-keys [:literal :lemma]))
                                v))
                   nbest)))))
