(ns kuromoji-jmdictfurigana.kana
  (:require [clojure.string :as string]))

(def hiragana "ぁあぃいぅうぇえぉおかがきぎくぐけげこごさざしじすずせぜそぞただちぢっつづてでとどなにぬねのはばぱひびぴふぶぷへべぺほぼぽまみむめもゃやゅゆょよらりるれろゎわゐゑをんゔゕゖ")
(def katakana "ァアィイゥウェエォオカガキギクグケゲコゴサザシジスズセゼソゾタダチヂッツヅテデトドナニヌネノハバパヒビピフブプヘベペホボポマミムメモャヤュユョヨラリルレロヮワヰヱヲンヴヵヶ")

(def katakana-to-hiragana-map
  (into {} (map vector
                (string/split katakana #"")
                (string/split hiragana #""))))

(defn katakana->hiragana [text]
  ; need `str` because in Cljs, mapping over string gives you list of STRINGS. In
  ; CLJ, you get list of CHARS wtf.
  (string/join ""
               (map #(or (get katakana-to-hiragana-map (str %))
                         %)
                    text)))
