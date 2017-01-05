(ns kuromoji-jmdictfurigana.furigana
  (:require [clojure.string :as string]
            [clojure.java.io :refer [resource]]))

; General purpose utility that we use to replace characters in a kanji with an
; object and nils
(defn replace-subvec [v start how-many to-replace]
  "Replace a sub-vector with another vector"
  (let [[left right] (split-at start v)
        right (drop how-many right)]
    (concat left (take how-many to-replace) right)))

(defn update-kanji-strvec-with-reading [kanji-vec [idx-str reading]]
  "Given a single vector of characters, updates it with a single furigana"
  (let [[start end] (string/split idx-str #"-")
        start (Integer. start)
        how-many (if end
                   (inc (- (Integer. end) start)) ;"1-2" should yield 2
                   1)
        kanji (apply str (subvec kanji-vec start (+ how-many start)))]
    (into []
          (replace-subvec kanji-vec
                          start
                          how-many
                          (concat [{:ruby kanji :rt reading}]
                                  (repeat nil))))))

; Run the above function on all furiganas for this kanji/reading tuple
(def update-kanji-strvec-with-readings (partial reduce update-kanji-strvec-with-reading))

(defn combine-strings [v]
  "If a vector contains adjacent strings, merge them."
  (reduce (fn [acc curr]
            (cond
              (empty? acc)
              [curr]

              (and (string? (peek acc)) (string? curr))
              (conj (pop acc) (str (peek acc) curr)) ; Note [🐻]

              :else
              (conj acc curr)))
          []
          v))
; Note [🐻]: don’t use `into` here: `(into [] "s")] => [\s]`! Use `conj`.

(defn format-furigana [kanji reading furigana]
  (->> (update-kanji-strvec-with-readings (string/split kanji #"")
                                          (->> (string/split furigana #";")
                                               (map #(string/split % #":") ,)))
       (filter identity) ; remove nils
       combine-strings))
; (format-furigana "z1art23x" "oneatwentythree" "1:one;5-6:twentythree")

(defn map-vals [f m]
  (zipmap (keys m) (map f (vals m))))

(defn debom [s]
  "Strips byte-order mark (BOM) from head of string if present"
  (if (= (first s) \uFEFF)
    (subs s 1)
    s))

(defn map-vals-kv [f m]
  (into {}
        (map (fn [[k v]]
               [k (f k v)])
             m)))

(def allf (->> "JmdictFurigana.txt"
               resource
               slurp
               debom
               string/trim
               string/split-lines
               (map #(string/split % #"\|"))
               ; Vec KanjiKanaFuriTuple

               (group-by first)
               ; Dict KanjiString (Vec KanjiKanaFuriTuple)

               (map-vals #(->> (group-by second %)))
               ; Dict KanjiString (Dict KanaString (Vec (Vec KanjiKanaFuriTuple)))
               ;   We could remove the double-Vec nesting here, and save memory,
               ;   but I think it’s a bit clearer to do that in the fn below.

               ; Dict KanjiString (Dict KanaString FuriganaVec)
               (map-vals-kv (fn [kanji r-f-map]
                              (map-vals-kv (fn [r [[_ _ f]]]
                                             (format-furigana kanji r f))
                                           r-f-map)))))

(defn kanji-reading->furigana [kanji reading]
  (-> (get allf kanji)
      (get reading)))
